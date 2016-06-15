package com.xuan.recycler.demo.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;

/**
 * Fragment基类(V4包)
 * 
 * @author 曾繁添
 * @version 1.0
 * 
 */
public abstract class BaseFragmentV4 extends Fragment implements IBaseFragment {

	/** 当前Fragment渲染的视图View **/
	private View mContextView = null;
	private boolean actionBusiness=false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 渲染视图View(防止切换时重绘View)
		if (null != mContextView) {
			ViewGroup parent = (ViewGroup) mContextView.getParent();
			if (null != parent) {
				parent.removeView(mContextView);
			}
		} else {
			mContextView = inflater.inflate(bindLayout(), container, false);
			// 控件初始化
			ButterKnife.bind(this, mContextView);
			initView(mContextView);
		}
		
		isPrepared=true;
		
		actionFirstBusiness(actionBusiness);

		return mContextView;
	}

	/**
	 * fragment视图是否显示
	 */
	protected boolean isVisible=false;
	/**
	 * 是否初始化完成，即onCreateView过，才可以doBusiness，否则会nullpoint
	 */
	protected boolean isPrepared=false;
	/**
	 * 在onCreateView之前调用
	 * 在这里实现Fragment数据的缓加载
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}
	
	/**
	 * 首次进入是否在initView后执行一次初始化
	 */
	public void actionFirstBusiness(boolean actionBusiness){
		if(actionBusiness && isPrepared){
			doBusiness();
		}
		if(actionBusiness && !isPrepared){
			setActionBusiness(true);
		}
	}
	
	public void setActionBusiness(boolean actionBusiness){
		this.actionBusiness=actionBusiness;
	}
	

	@Override
	public int bindLayout() {
		return 0;
	}

	@Override
	public void initView(View view) {
	}

	@Override
	public void doBusiness() {
	}

	protected void onVisible() {
		// 业务处理
		if(isPrepared && isVisible){
			doBusiness();
		}
	}

	protected void onInvisible(){
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//解绑butterknife
		ButterKnife.unbind(this);
	}

}
