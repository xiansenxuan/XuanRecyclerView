package com.xuan.recycler;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class XuanRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH> /*implements XuanRecyclerViewHeaderInter<RecyclerView.ViewHolder>*/{

	protected class VIEW_TYPES {
		public static final int NORMAL = 0;
		public static final int HEADER = 1;
		public static final int FOOTER = 2;
	}

	protected View customLoadMoreView = null;
	private View tempCustomLoadMoreView=null;
	private ProgressBar pb;
	private TextView tv;
	
	protected XuanRecyclerView.CustomRelativeWrapper customHeaderView = null;

	public XuanRecyclerViewAdapter() {
		super();
	}
	
	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		if (/*footerType==bottomFooter && */viewType == VIEW_TYPES.FOOTER) {
			VH viewHolder = getViewHolder(customLoadMoreView);
			if (getAdapterItemCount() == 0)
				viewHolder.itemView.setVisibility(View.GONE);
				return viewHolder;
		}else if (viewType == VIEW_TYPES.HEADER) {
            if (customHeaderView != null)
                return getViewHolder(customHeaderView);
        }

		return onCreateViewHolder(parent);
	}
	
	public void removeCustomLoadMoreView(){
		if(customLoadMoreView!=null){
			tempCustomLoadMoreView=customLoadMoreView;
		}
		customLoadMoreView=null;
		this.notifyDataSetChanged();
	}
	
	public void reEnableCustomLoadMoreView(){
		if(customLoadMoreView==null && tempCustomLoadMoreView!=null){
			customLoadMoreView=tempCustomLoadMoreView;
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == getItemCount() - 1 && customLoadMoreView != null) {
			return VIEW_TYPES.FOOTER;
		} else if (position == 0 && customHeaderView != null) {
            return VIEW_TYPES.HEADER;
        } else {
            return VIEW_TYPES.NORMAL;
        }
	}
	
    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        int headerOrFooter = 0;
        if (customHeaderView != null) headerOrFooter++;
        if (customLoadMoreView != null) headerOrFooter++;
        return getAdapterItemCount() + headerOrFooter;
    }

	public abstract VH getViewHolder(View view);

	public abstract VH onCreateViewHolder(ViewGroup parent);
	
	public abstract void onBindNormalViewHolder(VH holder, int position);
	
	private OnItemClickListener listener;
	
	public void addOnItemClickListener(OnItemClickListener listener){
		this.listener=listener;
	}
	
	@Override
	public void onBindViewHolder(VH holder, int position) {
			if (position < getItemCount() && (customHeaderView != null ? position <= getAdapterItemCount() : position < getAdapterItemCount()) && (customHeaderView != null ? position > 0 : true)) {
				position = customHeaderView != null ? position - 1 : position;
				
				if(listener!=null){
					holder.itemView.setOnClickListener(new XuanOnClickListener(position));
					holder.itemView.setOnLongClickListener(new XuanOnLongClickListener(position));
				}
				
				onBindNormalViewHolder(holder, position);
			}

	}
	
	private class XuanOnClickListener implements OnClickListener{
		private int position;

		public XuanOnClickListener(int position) {
			this.position=position;
		}

		@Override
		public void onClick(View v) {
			listener.onItemClick(v, position);
		}
		
	}
	
	private class XuanOnLongClickListener implements OnLongClickListener{
		private int position;
		
		public XuanOnLongClickListener(int position) {
			this.position=position;
		}

		@Override
		public boolean onLongClick(View v) {
			listener.onItemLongClick(v, position);
			return false;
		}
		
	}
	
    public View getCustomLoadMoreView() {
        return customLoadMoreView;
    }
    
    /**
     * Using a custom LoadMoreView
     * 
     * @param context
     *            the inflated view
     */
    public void setDefaultCustomLoadMoreView(Context context) {
		// 自定义加载更多样式
		setCustomLoadMoreView(LayoutInflater.from(context).inflate(R.layout.recycler_load_more_progress, null));
		pb = (ProgressBar)customLoadMoreView.findViewById(R.id.load_more_progressBar);
		tv = (TextView)customLoadMoreView.findViewById(R.id.load_more_text);
		// 设置居中
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		customLoadMoreView.setLayoutParams(params);
		// 设置google样式
		pb.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
    }

	/**
	 * Using a custom LoadMoreView
	 * 
	 * @param customview
	 *            the inflated view
	 */
	public void setCustomLoadMoreView(View customview) {
		customLoadMoreView = customview;
	}
	
	public void isHasMoreData(boolean flag){
		if(pb!=null && tv!=null){
			if(flag){
				pb.setVisibility(View.VISIBLE);
				tv.setVisibility(View.GONE);
			}else{
				pb.setVisibility(View.GONE);
				tv.setVisibility(View.VISIBLE);
			}
		}
	}
	
    /**
     * Set the header view of the adapter.
     */
    public void setCustomHeaderView(XuanRecyclerView.CustomRelativeWrapper customHeaderView) {
        this.customHeaderView = customHeaderView;
    }
    
    public XuanRecyclerView.CustomRelativeWrapper getCustomHeaderView() {
        return customHeaderView;
    }
    

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
        	final GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
 			//确保头部在顶部而不是gridview的左边
 			 gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
 				
 				@Override
 				public int getSpanSize(int position) {
 					//头部的角标为0 那么让它横跨二列 其他的正常就是一列
 					//尾部角标为最大 那么让它横跨二列 其他的正常就是一列
 					if(customHeaderView!=null && position==0){
 						return gridLayoutManager.getSpanCount();
 					}else if(customHeaderView==null ? position==getAdapterItemCount() : position==getAdapterItemCount()+1){
 						return gridLayoutManager.getSpanCount();
 					}else{
 						return 1;
 					}
 				}
 			});
        }
	}
	
	@Override
	public void onViewAttachedToWindow(VH holder) {
		super.onViewAttachedToWindow(holder);
		   if (isStaggeredGridLayout(holder)) {
	            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
	        }
	}

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }
 
    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
    	if(getItemViewType(position)== VIEW_TYPES.FOOTER || getItemViewType(position)== VIEW_TYPES.HEADER){
    		  StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
    	      layoutParams.setFullSpan(true);
    	}
      
    }

	/**
	 * Returns the number of items in the adapter bound to the parent
	 * RecyclerView.
	 * 
	 * @return The number of items in the bound adapter
	 */
	public abstract int getAdapterItemCount();
}
