package com.jiang.musicplayer.widget.refresh;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jiang.musicplayer.R;

/**
 * Created by Administrator on 2015/7/9 0009.
 */
public class SwipeRefreshListView extends SwipeRefreshAdapterViewBase<ListView> {
    protected View mLoadMoreBar;
    protected ProgressBar mLoadMorePb;
    private boolean mHasLoadMore;

    public SwipeRefreshListView(Context context) {
        super(context);
        init();
    }

    public SwipeRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        this.mLoadMoreBar = LayoutInflater.from(getContext()).inflate(R.layout.layout_swipe_load_more_footer, null);
        this.mLoadMorePb = ((ProgressBar) this.mLoadMoreBar.findViewById(R.id.swipe_load_more_pb));
        this.mLoadMorePb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.MULTIPLY);
        getRefreshableView().setDivider(null);
    }

    @Override
    protected void addFooterView() {

    }

    @Override
    protected void removeFooterView() {
        ((InternalListView) getRefreshableView()).removeFooterView();
    }

    @Override
    protected void showChildLoadMoreBar(boolean paramBoolean) {
        if (paramBoolean) {
            this.mLoadMorePb.setVisibility(VISIBLE);
            return;
        }
        this.mLoadMorePb.setVisibility(GONE);
    }

    @Override
    protected ListView createRefreshableView() {
        return new InternalListView(getContext());
    }


    private class InternalListView extends ListView {
        private boolean mAddedFooterView = false;

        public InternalListView(Context context) {
            super(context);
        }

        public InternalListView(Context context, AttributeSet atts) {
            super(context, atts);
        }

        public void addFooterView() {
            if (!this.mAddedFooterView) {
                this.mAddedFooterView = true;
                addFooterView(SwipeRefreshListView.this.mLoadMoreBar, null, false);
            }
        }

        public void removeFooterView() {
            if (this.mAddedFooterView) {
                this.mAddedFooterView = false;
                removeFooterView(SwipeRefreshListView.this.mLoadMoreBar);
            }
        }

        public void setAdapter(ListAdapter paramListAdapter) {
            if ((SwipeRefreshListView.this.mHasLoadMore) && (SwipeRefreshListView.this.mLoadMoreBar != null) && (!this.mAddedFooterView))
                addFooterView();
            super.setAdapter(paramListAdapter);
            if (paramListAdapter != null)
                paramListAdapter.registerDataSetObserver(new DataSetObserver() {
                    public void onChanged() {
                        super.onChanged();
                        SwipeRefreshListView.InternalListView localInternalListView = SwipeRefreshListView.InternalListView.this;
                        Runnable local1 = new Runnable() {
                            public void run() {
                                if (SwipeRefreshListView.InternalListView.this.mAddedFooterView) {
                                    if (1 + SwipeRefreshListView.InternalListView.this.getLastVisiblePosition() >= SwipeRefreshListView.InternalListView.this.getAdapter().getCount())
                                        SwipeRefreshListView.this.showLoadMoreBar(false);
                                } else
                                    return;
                                SwipeRefreshListView.this.showLoadMoreBar(true);
                            }
                        };
                        long l;
                        if (SwipeRefreshListView.InternalListView.this.getLastVisiblePosition() > 0) {
                            l = 10L;
                            localInternalListView.postDelayed(local1, l);
                            return;
                        }
                    }
                });
        }
    }
}
