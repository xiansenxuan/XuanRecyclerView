package com.xuan.recycler.demo.manager;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.xuan.recyclerview.OnItemClickListener;
import com.xuan.recyclerview.XuanRecyclerView;
import com.xuan.recyclerview.XuanRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Title : 管理下拉菜单
 * Description :
 * Author : xiansenxuan
 * Data : 2016/4/20 0006 09:53
 * Version : 1.0.0
 * Copyright : Copyright(c) 浙江蘑菇加电子商务有限公司 2015 ~ 2016 版权所有
 */
public class RecyclerViewManager {
    public static void initRecyclerView(Context context, XuanRecyclerView recycler_view, SwipeRefreshLayout sw_layout, View emptyView, XuanRecyclerViewAdapter adapter
            , SwipeRefreshLayout.OnRefreshListener onRefresh, XuanRecyclerView.OnLoadMoreListener loadMore
            , OnItemClickListener onItemListener) {

        // 设置recyclerview适配器
        recycler_view.setAdapter(adapter);

        // 设置ultimateRecyclerView 使RecyclerView保持固定的大小，该信息被用于自身的优化
        recycler_view.setHasFixedSize(true);

        if(emptyView!=null){
            recycler_view.setEmptyView(emptyView);
        }

        if (onRefresh != null && sw_layout != null) {
            recycler_view.setSwipeRefreshLayout(sw_layout);
            sw_layout.setOnRefreshListener(onRefresh);
        }


        if (loadMore != null) {
            adapter.setDefaultCustomLoadMoreView(context);
            recycler_view.setEnableLoadMore(true);
            recycler_view.setOnLoadMoreListener(loadMore);
        }

        if (onItemListener != null) {
            adapter.addOnItemClickListener(onItemListener);
        }
    }

    public static <T> void onRefresh(int refreshOrLoadMore, ArrayList<T> tempList
            , XuanRecyclerView recycler_view, SwipeRefreshLayout sw_layout, final XuanRecyclerViewAdapter adapter) {

        if (refreshOrLoadMore == ContactManager.onRefresh) {

            if (tempList == null || tempList.size() <= 0) {
                //当前刷新没有数据 清空原来的数据 刷新列表
                adapter.clear();
                //重新开启加载更多
                sw_layout.setRefreshing(false);
                recycler_view.reEnableLoadMore();
            }
            //刷新操作
            adapter.clear();
            adapter.addItem(tempList);

            //重新开启加载更多
            sw_layout.setRefreshing(false);
            recycler_view.reEnableLoadMore();

        } else if (refreshOrLoadMore == ContactManager.onLoadMore) {
            if (tempList == null || tempList.size() <= 0) {
                recycler_view.setEnableLoadMore(false);//关闭加载更多
            }
            //加载
            adapter.addItem(tempList);
        }

        adapter.notifyDataSetChanged();
    }
}
