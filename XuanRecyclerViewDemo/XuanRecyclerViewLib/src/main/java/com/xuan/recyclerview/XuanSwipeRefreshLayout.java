package com.xuan.recyclerview;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

/**
 * @author xiansenxuan
 */
public class XuanSwipeRefreshLayout extends SwipeRefreshLayout {

    private View view;
    public XuanSwipeRefreshLayout(Context context) {
        super(context);
    }

    public XuanSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewGroup(View view) {
        this.view = view;
    }

    @Override
    public boolean canChildScrollUp() {
        if (view != null && view instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) view;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        }
        return super.canChildScrollUp();
    }
}