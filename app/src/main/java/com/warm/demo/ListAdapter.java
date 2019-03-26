package com.warm.demo;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warm.indicatorview.IndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：warm
 * 时间：2019-03-26 14:19
 * 描述：
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    List<Integer> colors = new ArrayList<>();

    public ListAdapter() {
        colors.add(R.color.color0);
        colors.add(R.color.color1);
        colors.add(R.color.color2);
        colors.add(R.color.color3);
        colors.add(R.color.color4);
        colors.add(R.color.color5);
        colors.add(R.color.color6);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.vp.setCurrentItem(0,false);

    }


    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ViewPager vp;
        private IndicatorView indicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vp = itemView.findViewById(R.id.vp);
            BannerPagerAdapter mPagerAdapter = new BannerPagerAdapter(itemView.getContext(), colors);
            vp.setAdapter(mPagerAdapter);
            indicator = itemView.findViewById(R.id.indicator);
            indicator.setupWithViewPager(vp);
        }
    }

}
