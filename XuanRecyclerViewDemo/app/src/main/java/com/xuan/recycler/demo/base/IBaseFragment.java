package com.xuan.recycler.demo.base;

import android.view.View;

/**
 * Fragment接口
 * @author 曾繁添
 * @version 1.0
 *
 */
public interface IBaseFragment {

	/**
	 * 绑定渲染视图的布局文件
	 * @return 布局文件资源id
	 */
	public int bindLayout();
	
	/**
	 * 不需要刷新的操作 （控件初始化 设置适配器 等操作）
	 */
	public void initView(final View view);
	
	/**
	 * 需要刷新界面数据的操作 （网络请求 标题栏刷新 等操作）
	 * @param mContext  当前Activity对象
	 */
	public void doBusiness();
	
}
