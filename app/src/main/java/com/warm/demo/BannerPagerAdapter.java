package com.warm.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * 作者：warm
 * 时间：2018-10-12 09:52
 * 描述：
 */
public class BannerPagerAdapter extends PagerAdapter {
    private List<Integer> mColors;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public BannerPagerAdapter(Context context, List<Integer> banners) {
        mContext = context;
        mColors = banners;
    }


    @Override
    public int getCount() {
        return mColors.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.viewholder_banner, container, false);

        ImageView imageView = view.findViewById(R.id.iv_banner);
        imageView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
//        GlideImageLoader.loadImage(mContext, imageView, mBanners.get(position));
        imageView.setBackgroundColor(ContextCompat.getColor(container.getContext(),mColors.get(position)));
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}