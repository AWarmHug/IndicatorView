package com.warm.indicatorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 作者：warm
 * 时间：2018-10-11 10:30
 * 描述：
 */
public class IndicatorView extends LinearLayout {
    private static final String TAG = "IndicatorView";

    private Drawable mIndicator;

    private Drawable mSelectedIndicator;

    private int mSpace;

    public static final int DEFAULT_SPACE = 4;


    private AdapterChangeListener mAdapterChangeListener;

    ViewPager mViewPager;

    private int mSelectPosition;


    private PagerAdapter mPagerAdapter;

    private DataSetObserver mDataSetObserver;

    private ArrayList<OnIndicatorSelectedListener> mSelectedListeners = new ArrayList<>();

    private ViewPagerOnIndicatorSelectedListener mCurrentVpSelectedListener;

    private IndicatorOnPageChangeListener mPageChangeListener;


    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView, defStyleAttr, 0);
        mIndicator = array.getDrawable(R.styleable.IndicatorView_indicator);
        mSelectedIndicator = array.getDrawable(R.styleable.IndicatorView_selectedIndicator);
        mSpace = array.getDimensionPixelOffset(R.styleable.IndicatorView_space, dp2px(DEFAULT_SPACE));
        array.recycle();
    }


    public void addOnIndicatorSelectedListener(OnIndicatorSelectedListener onIndicatorSelectedListener) {
        mSelectedListeners.add(onIndicatorSelectedListener);
    }

    public void removeOnIndicatorSelectedListener(OnIndicatorSelectedListener onIndicatorSelectedListener) {
        mSelectedListeners.remove(onIndicatorSelectedListener);
    }

    private void dispatchSelected(final int position) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onSelected(position);
        }
    }

    private void dispatchUnselected(final int position) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onUnselected(position);
        }
    }

    private void dispatchReselected(final int position) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onReselected(position);
        }
    }


    public void select(int position) {
        ((ImageView) getChildAt(mSelectPosition)).setImageDrawable(mIndicator);
        this.mSelectPosition = position;
        ((ImageView) getChildAt(mSelectPosition)).setImageDrawable(mSelectedIndicator);

        dispatchSelected(position);
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable final ViewPager viewPager, boolean autoRefresh) {
        setupWithViewPager(viewPager, autoRefresh, false);
    }

    private void setupWithViewPager(@Nullable final ViewPager viewPager, boolean autoRefresh,
                                    boolean implicitSetup) {
        if (mViewPager != null) {

            if (mPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mPageChangeListener);
            }
            if (mAdapterChangeListener != null) {
                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
            }
        }

        if (mCurrentVpSelectedListener != null) {
            removeOnIndicatorSelectedListener(mCurrentVpSelectedListener);
            mCurrentVpSelectedListener = null;
        }

        if (viewPager != null) {
            mViewPager = viewPager;

            if (mPageChangeListener == null) {
                mPageChangeListener = new IndicatorView.IndicatorOnPageChangeListener(this);
            }

            viewPager.addOnPageChangeListener(mPageChangeListener);

            mCurrentVpSelectedListener = new ViewPagerOnIndicatorSelectedListener(viewPager);

            addOnIndicatorSelectedListener(mCurrentVpSelectedListener);

            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                setPagerAdapter(adapter, autoRefresh);
            }

            if (mAdapterChangeListener == null) {
                mAdapterChangeListener = new AdapterChangeListener();
            }
            mAdapterChangeListener.setAutoRefresh(autoRefresh);

            viewPager.addOnAdapterChangeListener(mAdapterChangeListener);
            if (adapter != null && adapter.getCount() != 0) {
                select(viewPager.getCurrentItem());
            }
        } else {
            mViewPager = null;
            setPagerAdapter(null, false);
        }

    }

    private void setPagerAdapter(PagerAdapter adapter, boolean addObserver) {
        if (mPagerAdapter != null && mDataSetObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        if (adapter != null && addObserver) {
            if (mDataSetObserver == null) {
                mDataSetObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mDataSetObserver);
        }

        mPagerAdapter = adapter;
        populateFromPagerAdapter();

    }

    private void populateFromPagerAdapter() {
        removeAllViews();
        if (mPagerAdapter != null) {
            int count = mPagerAdapter.getCount();

            for (int i = 0; i < count; i++) {
                ImageView indicator = createIndicator(i);
                addView(indicator);
            }

            if (mViewPager != null && count > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectPosition() && curItem < getChildCount()) {
                    select(curItem);
                }
            }
        }
    }

    public void setCount(int count) {

        removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView indicator = createIndicator(i);
            addView(indicator);
        }
    }

    private ImageView createIndicator(final int position) {
        ImageView indicator = new ImageView(getContext());
        indicator.setImageDrawable(mIndicator);
        indicator.setPadding(mSpace / 2, 0, mSpace / 2, 0);
//        indicator.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                select(position);
//            }
//        });
        return indicator;
    }


    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }


    private static class IndicatorOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<IndicatorView> mIndicatorRef;


        public IndicatorOnPageChangeListener(IndicatorView indicatorView) {
            mIndicatorRef = new WeakReference<>(indicatorView);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {

        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                                   final int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {

            final IndicatorView indicatorView = mIndicatorRef.get();
            if (indicatorView != null && indicatorView.getSelectPosition() != position
                    && position < indicatorView.getChildCount()) {
                indicatorView.select(position);
            }
        }


    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
        private boolean mAutoRefresh;

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (mViewPager == viewPager) {
                setPagerAdapter(newAdapter, mAutoRefresh);
            }
        }

        public void setAutoRefresh(boolean autoRefresh) {
            this.mAutoRefresh = autoRefresh;
        }
    }


    private static class ViewPagerOnIndicatorSelectedListener implements OnIndicatorSelectedListener {
        private ViewPager mViewPager;

        public ViewPagerOnIndicatorSelectedListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onSelected(int position) {
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onUnselected(int position) {

        }

        @Override
        public void onReselected(int position) {

        }
    }

    public int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public interface OnIndicatorSelectedListener {

        void onSelected(int position);


        void onUnselected(int position);


        void onReselected(int position);
    }


}
