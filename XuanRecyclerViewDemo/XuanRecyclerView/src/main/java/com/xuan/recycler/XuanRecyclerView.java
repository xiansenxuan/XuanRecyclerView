package com.xuan.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class XuanRecyclerView extends RecyclerView {

	public static enum LAYOUT_MANAGER_TYPE {
		LINEAR, GRID, STAGGERED_GRID
	}

	protected View mEmpty;
	protected LAYOUT_MANAGER_TYPE layoutManagerType;
	private boolean isLoadingMore = false;
	private int loadMoreMinSize;
	private int spanSize = 1;

	private XuanRecyclerViewAdapter adapter;
	private SwipeRefreshLayout sw_layout;

	public void setAdapter(XuanRecyclerViewAdapter adapter) {
		this.adapter = adapter;
		this.loadMoreMinSize = 20;

		registerAdapterDataObserver();

		super.setAdapter(adapter);
	}
	
	/**
	 * @param sw_layout
	 * 	防止未到顶部下滑触发刷新
	 */
	public void setSwipeRefreshLayout(SwipeRefreshLayout sw_layout){
		this.sw_layout=sw_layout;
	}

	private void registerAdapterDataObserver() {
		if (adapter != null)
			adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
				@Override
				public void onItemRangeChanged(int positionStart, int itemCount) {
					super.onItemRangeChanged(positionStart, itemCount);
					updateHelperDisplays();
					updateHelperEmpty();
				}

				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					super.onItemRangeInserted(positionStart, itemCount);
					updateHelperDisplays();
					updateHelperEmpty();
				}

				@Override
				public void onItemRangeRemoved(int positionStart, int itemCount) {
					super.onItemRangeRemoved(positionStart, itemCount);
					updateHelperDisplays();
					updateHelperEmpty();
				}

				@Override
				public void onItemRangeMoved(int fromPosition, int toPosition,
						int itemCount) {
					super.onItemRangeMoved(fromPosition, toPosition, itemCount);
					updateHelperDisplays();
					updateHelperEmpty();
				}

				@Override
				public void onChanged() {
					super.onChanged();
					updateHelperDisplays();
					updateHelperEmpty();
				}
			});
	}

	protected void updateHelperEmpty() {
		if (mEmpty == null)
			return;
		if ((adapter.getCustomLoadMoreView() == null && adapter.getItemCount() == 0)
				|| (adapter.getCustomLoadMoreView() != null && adapter
						.getItemCount() == 1)) {
			mEmpty.setVisibility(View.VISIBLE);
			this.setVisibility(View.GONE);
		} else {
			mEmpty.setVisibility(View.GONE);
			this.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 初始化一个空数据布局
	 */
	public void setEmptyView(View mEmpty) {
		this.mEmpty = mEmpty;
		mEmpty.setVisibility(View.GONE);
//		if (((adapter.getCustomLoadMoreView() == null && adapter.getItemCount() == 0) || (adapter
//				.getCustomLoadMoreView() != null && adapter.getItemCount() == 1))
//				&& mEmpty != null) {
//			mEmpty.setVisibility(View.VISIBLE);
//		}

	}

	protected void updateHelperDisplays() {
		isLoadingMore = false;
		if (adapter == null)
			return;

		if (adapter.getCustomLoadMoreView() == null)
			return;
		if (adapter.getAdapterItemCount() >= loadMoreMinSize
				&& adapter.getCustomLoadMoreView().getVisibility() == View.GONE) {
			adapter.getCustomLoadMoreView().setVisibility(View.VISIBLE);
		}
		if (adapter.getAdapterItemCount() < loadMoreMinSize) {
			adapter.getCustomLoadMoreView().setVisibility(View.GONE);
		}
	}

	/**
	 * 是否开启加载更多功能
	 */
	private boolean isEnableLoadMore = false;

	private int mVisibleItemCount = 0;

	public boolean isEnableLoadMore() {
		return isEnableLoadMore;
	}

	private int mTotalItemCount = 0;
	private int previousTotal = 0;
	private int mFirstVisibleItem;
	private int lastVisibleItemPosition;
	private int[] lastPositions;
	// private MyRecyclerViewAdapter adapter;

	private OnLoadMoreListener onLoadMoreListener;

	public XuanRecyclerView(Context context) {
		super(context);
	}

	public XuanRecyclerView(Context arg0, @Nullable AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public XuanRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	private AppBarLayout.OnOffsetChangedListener offListen;
	/**
	 * 有AppBarLayout头部时此处判断是否需要刷新
	 * @param appBarHeader
	 */
	public void setAppBarHeader(AppBarLayout appBarHeader){
		if(appBarHeader!=null){
			if(offListen==null){
				offListen=new AppBarLayout.OnOffsetChangedListener() {

					@Override
					public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
						if(sw_layout!=null){
							sw_layout.setEnabled(verticalOffset >= 0);
						}
					}
				};
			}

			appBarHeader.addOnOffsetChangedListener(offListen);
		}
	}


	@Override
	public void onScrolled(int dx, int dy) {
		super.onScrolled(dx, dy);

		//没有AppBarLayout 此处判断是否到达顶部 可以刷新
		if(offListen==null){
			int topRowVerticalPosition = (this == null || this.getChildCount() == 0)  ? 0 : this.getChildAt(0).getTop();
			if(sw_layout!=null){
				sw_layout.setEnabled(topRowVerticalPosition >= 0);
			}
		}


        if (mHeader != null) {
            mTotalYScrolled += dy;
            if (isParallaxHeader){
            	translateHeader(mTotalYScrolled);
            }
        }

		if (!isEnableLoadMore) {
			return;
		}

		switchPositionIsLoadMore();

		// 不满一屏数据 隐藏加载更多
		if (adapter.getAdapterItemCount() < loadMoreMinSize) {
			isEnableLoadMore = false;
			removeLoadMore();
			return;
		} else {
			adapter.isHasMoreData(true);
		}

		if (isLoadingMore) {
			// todo: there are some bugs needs to be adjusted for admob adapter
			if (mTotalItemCount > previousTotal) {
				isLoadingMore = false;
				previousTotal = mTotalItemCount;
			}
		}

		if (!isLoadingMore
				&& (mTotalItemCount - mVisibleItemCount) <= mFirstVisibleItem
				&& mTotalItemCount > 1) {
			// todo: there are some bugs needs to be adjusted for admob adapter
			if(onLoadMoreListener!=null){
				onLoadMoreListener.loadMore();
			}
			isLoadingMore = true;
			previousTotal = mTotalItemCount;

		}
	}

	private void switchPositionIsLoadMore() {
		RecyclerView.LayoutManager layoutManager = this.getLayoutManager();

		if (layoutManagerType == null) {
			if (layoutManager instanceof GridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
			} else if (layoutManager instanceof LinearLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
			} else if (layoutManager instanceof StaggeredGridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
			} else {
				throw new RuntimeException(
						"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
			}
		}

		switch (layoutManagerType) {
		case LINEAR:
			lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			mFirstVisibleItem = ((LinearLayoutManager) layoutManager)
					.findFirstVisibleItemPosition();

			mVisibleItemCount = ((LinearLayoutManager) layoutManager)
					.getChildCount();
			mTotalItemCount = ((LinearLayoutManager) layoutManager)
					.getItemCount();
			
//			MyLogger.xuanLog().i("lastVisibleItemPosition="+lastVisibleItemPosition);
//			MyLogger.xuanLog().i("mFirstVisibleItem="+mFirstVisibleItem);
//			MyLogger.xuanLog().i("mVisibleItemCount="+mVisibleItemCount);
//			MyLogger.xuanLog().i("mTotalItemCount="+mTotalItemCount);
			
			break;
		case GRID:
			spanSize = ((GridLayoutManager) layoutManager).getSpanCount();

			lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			mFirstVisibleItem = ((GridLayoutManager) layoutManager)
					.findFirstVisibleItemPosition();

			mVisibleItemCount = ((GridLayoutManager) layoutManager)
					.getChildCount();
			mTotalItemCount = ((GridLayoutManager) layoutManager)
					.getItemCount();

			break;
		case STAGGERED_GRID:
			StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;

			spanSize = staggeredGridLayoutManager.getSpanCount();

			if (lastPositions == null)
				lastPositions = new int[staggeredGridLayoutManager
						.getSpanCount()];

			staggeredGridLayoutManager
					.findLastVisibleItemPositions(lastPositions);
			lastVisibleItemPosition = findMax(lastPositions);

			staggeredGridLayoutManager
					.findFirstVisibleItemPositions(lastPositions);
			mFirstVisibleItem = findMin(lastPositions);

			mVisibleItemCount = staggeredGridLayoutManager.getChildCount();
			mTotalItemCount = staggeredGridLayoutManager.getItemCount();

			break;
		}
	}
	
	/**
	 * 
	 */
	public void removeLoadMore(){
		adapter.removeCustomLoadMoreView();
		//防止刷新脚布局的时候 重新刷新空数据布局 导致空数据布局显示  隐藏空数据布局
		if(mEmpty!=null){
			mEmpty.setVisibility(View.GONE);
		}
	}

	/**
	 * @param isEnableLoadMore
	 *            是否启用
	 */
	public void setEnableLoadMore(boolean isEnableLoadMore) {
		this.isEnableLoadMore = isEnableLoadMore;

		if (adapter != null && adapter.getCustomLoadMoreView() != null) {
			if (isEnableLoadMore) {
				// 不满一屏数据 隐藏加载更多
				if (adapter.getAdapterItemCount() < loadMoreMinSize) {
					isEnableLoadMore = false;
					removeLoadMore();
					return;
				} else {
					previousTotal = 0;
					adapter.isHasMoreData(true);
				}
			} else {
				removeLoadMore();
			}
		}
	}

	/**
	 * 重新启用
	 */
	public void reEnableLoadMore() {
		this.isEnableLoadMore = true;

		this.onScrolled(0, 0);
		
		//重新添加上原来的加载更多布局
		adapter.reEnableCustomLoadMoreView();

		if (adapter != null && adapter.getCustomLoadMoreView() != null) {
			if (isEnableLoadMore) {
				// 不满一屏数据 隐藏加载更多
				if (adapter.getAdapterItemCount() < loadMoreMinSize) {
					isEnableLoadMore = false;
					removeLoadMore();
					return;
				} else {
					previousTotal = 0;
					adapter.isHasMoreData(true);
				}
			} else {
				removeLoadMore();
			}
		}
	}
	
	private CustomRelativeWrapper mHeader;
	private boolean isParallaxHeader = false;
    private int mTotalYScrolled;
    private final float SCROLL_MULTIPLIER = 0.5f;
    private OnParallaxScroll mParallaxScroll;
    
    public void setIsParallaxHeader(boolean isParallaxHeader){
    	this.isParallaxHeader=isParallaxHeader;
    }
    public boolean getIsParallaxHeader(){
    	return isParallaxHeader;
    }
	
    /**
     * Set the normal header of the recyclerview
     *
     * @param header
     */
    public void setNormalHeader(View header) {
        setParallaxHeader(header);
        isParallaxHeader = false;
    }
    /**
     * Set the parallax header of the recyclerview
     * 
     * 有一个未修复的bug 使用viewpager+fragment的时候视图重新加载导致头部丢失的问题
     * 解决办法：使用setOffscreenPageLimit(fragments.size()) 即可
     *
     * @param header the view
     */
    public void setParallaxHeader(View header) {
        mHeader = new CustomRelativeWrapper(header.getContext());
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (adapter != null){
        	adapter.setCustomHeaderView(mHeader);
        }
        isParallaxHeader = true;
    }
    
    private void translateHeader(float of) {
        float ofCalculated = of * SCROLL_MULTIPLIER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Logs.d("ofCalculated    " + ofCalculated+"   "+mHeader.getHeight());
            mHeader.setTranslationY(ofCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(ofCalculated));
        if (mParallaxScroll != null) {
            float left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
            mParallaxScroll.onParallaxScroll(left, of, mHeader);
        }
    }
    
    /**
     * Set the on scroll method of parallax header
     *
     * @param parallaxScroll
     */
    public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
        mParallaxScroll = parallaxScroll;
        mParallaxScroll.onParallaxScroll(0, 0, mHeader);
    }
    

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	private int findMax(int[] lastPositions) {
		int max = Integer.MIN_VALUE;
		for (int value : lastPositions) {
			if (value > max)
				max = value;
		}
		return max;
	}

	private int findMin(int[] lastPositions) {
		int min = Integer.MAX_VALUE;
		for (int value : lastPositions) {
			if (value != RecyclerView.NO_POSITION && value < min)
				min = value;
		}
		return min;
	}

	public interface OnLoadMoreListener {
		void loadMore();
	}
	
    public interface OnParallaxScroll {
        void onParallaxScroll(float percentage, float offset, View parallax);
    }
	
	/**
     * Custom layout for the Parallax Header.
     */
    public class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;

        public CustomRelativeWrapper(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (isParallaxHeader){
            	canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            }
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }


}
