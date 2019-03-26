package com.warm.demo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.warm.indicatorview.IndicatorView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private IndicatorView indicator;
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = findViewById(R.id.vp);
        List<Integer> banners = new ArrayList<>();
        banners.add(R.color.color0);
        banners.add(R.color.color1);
        banners.add(R.color.color2);
        banners.add(R.color.color3);
        banners.add(R.color.color4);
        banners.add(R.color.color5);
        banners.add(R.color.color6);

        BannerPagerAdapter mPagerAdapter = new BannerPagerAdapter(this, banners);
        vp.setAdapter(mPagerAdapter);
        indicator = findViewById(R.id.indicator);
        indicator.setupWithViewPager(vp);

        list = findViewById(R.id.list);

        list.setAdapter(new ListAdapter());
        list.setLayoutManager(new LinearLayoutManager(this));

    }
}
