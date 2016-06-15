package com.xuan.recycler.demo.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * @author xiansenxuan fragment组成的多个pager页面适配器
 */
public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> fragments;
	private ArrayList<String> titles;
	private String[] mTitles;

	public BaseFragmentPagerAdapter(FragmentManager fm,
									ArrayList<Fragment> fragments, ArrayList<String> titles) {
		super(fm);
		this.fragments = fragments;
		this.titles=titles;
	}

	public BaseFragmentPagerAdapter(FragmentManager fm,
									ArrayList<Fragment> fragments, String[] mTitles) {
		super(fm);
		this.fragments = fragments;
		this.mTitles=mTitles;
	}

	@Override
	// 返回的fragment页面
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
    @Override//返回的tabs标签文本
    public CharSequence getPageTitle(int position) {
		if(titles==null){
			return mTitles==null ? "" : mTitles[position];
		}else if(mTitles==null){
			return titles==null ? "" : titles.get(position);
		}else{
			return "";
		}
    }
}
