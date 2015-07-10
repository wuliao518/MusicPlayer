package com.jiang.musicplayer.widget.refresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Wuliao on 2015/7/8 0008.
 */
public abstract class SwipeRefreshBase<T extends View> extends FrameLayout {

    private SwipeRefreshLayout mSwipeParent;
    private OnRefreshListener mListener;
    private T mRefreshableView;
    public SwipeRefreshBase(Context context) {
        this(context, null);
    }

    public SwipeRefreshBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRefreshBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        this.mSwipeParent = new SwipeRefreshLayout(paramContext, paramAttributeSet);
        addView(this.mSwipeParent, -1, new FrameLayout.LayoutParams(-1, -1));
        this.mSwipeParent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (SwipeRefreshBase.this.mListener != null)
                    SwipeRefreshBase.this.mListener.onRefresh();
            }
        });
        this.mSwipeParent.setProgressViewOffset(true, 0, 100);
        this.mRefreshableView = createRefreshableView();
        if (this.mRefreshableView != null)
            addRefreshableView(this.mRefreshableView);
    }

    private void addRefreshableView(T mRefreshableView) {
        mSwipeParent.addView(mRefreshableView, -1, new ViewGroup.LayoutParams(-1, -1));
    }

    protected abstract T createRefreshableView();

    public void setRefreshing(boolean paramBoolean) {
        this.mSwipeParent.setRefreshing(paramBoolean);
    }

    public static abstract interface OnLoadMoreListener {
        public abstract void onLoadMore();
    }

    public static abstract interface OnRefreshListener {
        public abstract void onRefresh();
    }

    public OnRefreshListener getOnRefreshListener() {
        return this.mListener;
    }

    public T getRefreshableView() {
        return this.mRefreshableView;
    }

    public void setOnRefreshListener(OnRefreshListener paramOnRefreshListener) {
        this.mListener = paramOnRefreshListener;
    }

}
