package com.xuan.recyclerview;

import android.view.View;

/**
 * @author xiansenxuan
 */
public interface OnItemClickListener {
	 void onItemClick(View view, int position);

	 void onItemLongClick(View view, int position);
}
