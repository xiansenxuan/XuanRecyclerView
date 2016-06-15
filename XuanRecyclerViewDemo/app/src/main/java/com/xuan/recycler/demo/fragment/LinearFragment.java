package com.xuan.recycler.demo.fragment;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xuan.recycler.demo.R;
import com.xuan.recycler.demo.base.BaseFragmentV4;
import com.xuan.recycler.demo.manager.ContactManager;
import com.xuan.recycler.demo.manager.RecyclerViewManager;
import com.xuan.recyclerview.OnItemClickListener;
import com.xuan.recyclerview.XuanRecyclerView;
import com.xuan.recyclerview.XuanRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Title :
 * Description :
 * Author : xiansenxuan
 * Data : 2016/5/25 0025 15:42
 * Version : 1.0.0
 */
public class LinearFragment extends BaseFragmentV4 {

    @Bind(R.id.recycler_view)
    XuanRecyclerView recycler_view;
    @Bind(R.id.sw_layout)
    SwipeRefreshLayout sw_layout;
    @Bind(R.id.recycler_empty_view)
    View recycler_empty_view;

    private LinearLayoutManager layoutManager;
    private MyRecyclerViewAdapter adapter;

    private int index = 0;

    public LinearFragment() {
        super();
    }

    public static LinearFragment newInstance() {
        LinearFragment fragment = new LinearFragment();
        return fragment;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_linear;
    }

    @Override
    public void doBusiness() {
        Log.i("print", "doBusiness  " + this.getClass().getSimpleName());

        queryData(ContactManager.onRefresh);
    }

    @Override
    public void initView(View view) {
        Log.i("print", "initView  " + this.getClass().getSimpleName());

        setRecyclerView();

        actionFirstBusiness(true);
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
    public void onDestroy() {
        super.onDestroy();
    }

    private void queryData(final int state) {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                ArrayList<String> tempList = new ArrayList<>();
                if (state == ContactManager.onRefresh) {
                    index = 0;
                    tempList.clear();

                    for (int i = index; i < index + 20; i++) {
                        tempList.add("index " + i);
                    }
                } else if (state == ContactManager.onLoadMore) {

                    for (int i = index; i < index + 20; i++) {
                        tempList.add("index " + i);
                    }
                }
                index += 20;

                RecyclerViewManager.onRefresh(state,tempList,recycler_view,sw_layout,adapter);
            }
        }, 2000);


    }

    /**
     * 设置setRecyclerView
     */
    private void setRecyclerView() {
        // 设置布局管理器
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(layoutManager);

        adapter = new MyRecyclerViewAdapter();

        RecyclerViewManager.initRecyclerView(getActivity(), recycler_view, sw_layout, recycler_empty_view, adapter,
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        queryData(ContactManager.onRefresh);
                    }
                }, new XuanRecyclerView.OnLoadMoreListener() {
                    @Override
                    public void loadMore() {
                        queryData(ContactManager.onLoadMore);
                    }
                },
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 适配器
     */
    class MyRecyclerViewAdapter extends XuanRecyclerViewAdapter<MyRecyclerViewAdapter.MyRecyclerViewHolder> {
        class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView text_view;

            public MyRecyclerViewHolder(View view) {
                super(view);
                this.view = view;
                text_view = (TextView) view.findViewById(R.id.text_view);
            }

        }

        @Override
        // 获取viewholder在创建之后执行
        public MyRecyclerViewHolder getViewHolder(View view) {
            return new MyRecyclerViewHolder(view);
        }

        @Override
        // 创建viewholder
        public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
            MyRecyclerViewHolder holder = new MyRecyclerViewHolder(view);
            return holder;
        }

        @Override
        // 设置数据
        public void onBindNormalViewHolder(MyRecyclerViewHolder holder, int position) {
            String data = (String) itemList.get(position);

            holder.text_view.setText(data);
        }

    }

}
