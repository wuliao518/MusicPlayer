package com.jiang.musicplayer.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * Created by Administrator on 2015/7/8 0008.
 */
public abstract class SwipeRefreshAdapterViewBase<T extends AbsListView> extends SwipeRefreshBase<T> implements AbsListView.OnScrollListener {
    private int mLastVisibleItem;
    private OnLoadMoreListener mListener;
    private AbsListView.OnScrollListener mChildOnScrollListener;
    private boolean mHasLoadMore;

    public SwipeRefreshAdapterViewBase(Context context) {
        super(context);
        init();
    }

    public SwipeRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        ((AbsListView) getRefreshableView()).setOnScrollListener(this);
    }
    protected abstract void addFooterView();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if ((this.mHasLoadMore) && (scrollState == 0) && (this.mLastVisibleItem == -1 + view.getCount()) && (this.mListener != null))
            this.mListener.onLoadMore();
        if (this.mChildOnScrollListener != null)
            this.mChildOnScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mLastVisibleItem = (-1 + (firstVisibleItem + visibleItemCount));
        if (this.mChildOnScrollListener != null)
            this.mChildOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    protected abstract void removeFooterView();

    public void setHasLoadMore(boolean mHasLoadMore) {
        showLoadMoreBar(mHasLoadMore);
        this.mHasLoadMore = mHasLoadMore;
    }

    public void setOnLoadMoreListener(SwipeRefreshBase.OnLoadMoreListener paramOnLoadMoreListener) {
        this.mListener = paramOnLoadMoreListener;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener paramOnScrollListener) {
        this.mChildOnScrollListener = paramOnScrollListener;
    }

    protected abstract void showChildLoadMoreBar(boolean paramBoolean);

    public void showLoadMoreBar(boolean paramBoolean) {
        if (paramBoolean) {
            addFooterView();
            showChildLoadMoreBar(true);
            return;
        }
        showChildLoadMoreBar(false);
        removeFooterView();
    }
}
