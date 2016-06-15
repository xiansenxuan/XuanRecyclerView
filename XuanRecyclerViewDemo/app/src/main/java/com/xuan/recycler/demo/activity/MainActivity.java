package com.xuan.recycler.demo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.xuan.recycler.demo.R;
import com.xuan.recycler.demo.base.BaseFragmentPagerAdapter;
import com.xuan.recycler.demo.fragment.GridFragment;
import com.xuan.recycler.demo.fragment.LinearFragment;
import com.xuan.recycler.demo.fragment.StaggeredGridFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.view_pager)
    ViewPager view_pager;
    @Bind(R.id.smart_tab_layout)SmartTabLayout smart_tab_layout;

    private ArrayList <Fragment> fragments;
    private String mTitles[] = {"LinearLayout", "GridLayout", "StaggeredGridLayout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setBottomTab(savedInstanceState);
    }

    private void setBottomTab(Bundle savedInstanceState) {
        //初始化fragment数据
        if(fragments==null){
            fragments = new ArrayList<>();

            fragments.add(LinearFragment.newInstance());
            fragments.add(GridFragment.newInstance());
            fragments.add(StaggeredGridFragment.newInstance());

            view_pager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments, mTitles));

            smart_tab_layout.setViewPager(view_pager);

            //设置vp预加载所有的view但是不会加载数据
            view_pager.setOffscreenPageLimit(fragments.size());
        }
    }
}
