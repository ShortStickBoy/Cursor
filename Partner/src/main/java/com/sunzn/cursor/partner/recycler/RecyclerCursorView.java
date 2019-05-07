package com.sunzn.cursor.partner.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sunzn.cursor.library.CursorBase;

public class RecyclerCursorView extends CursorBase {

    private final InternalOnScrollListener mInternalOnScrollListener = new InternalOnScrollListener();
    @Nullable
    private RecyclerView mRecyclerView;
    @Nullable
    private SnapHelper mSnapHelper;

    public RecyclerCursorView(Context context) {
        super(context);
    }

    public RecyclerCursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerCursorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (mRecyclerView != recyclerView) {
            if (mRecyclerView != null) {
                mRecyclerView.removeOnScrollListener(mInternalOnScrollListener);
            }
            mRecyclerView = recyclerView;
            if (mRecyclerView != null) {
                if (mSnapHelper == null) {
                    RecyclerView.OnFlingListener flingListener = recyclerView.getOnFlingListener();
                    if (flingListener instanceof SnapHelper) {
                        mSnapHelper = (SnapHelper) flingListener;
                    }
                }
                recyclerView.addOnScrollListener(mInternalOnScrollListener);
            }
            populateTabStrip();
        }
    }

    public int getSnapPosition(@Nullable RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null || mSnapHelper == null) {
            return RecyclerView.NO_POSITION;
        }
        View snapView = mSnapHelper.findSnapView(layoutManager);
        if (snapView == null) {
            return RecyclerView.NO_POSITION;
        }
        return layoutManager.getPosition(snapView);
    }

    @Override
    protected void populateTabStrip() {
        tabStrip.removeAllViews();
        if (mRecyclerView == null) {
            return;
        }

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (!(adapter instanceof RecyclerPagerAdapter)) {
            return;
        }

        int count = adapter.getItemCount();

        for (int i = 0; i < count; i++) {
            CharSequence title = ((RecyclerPagerAdapter) adapter).getPageTitle(i);

            final View tabView = (tabProvider == null) ? createDefaultTabView(title, i) : tabProvider.createTabView(tabStrip, i, title);

            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }

            if (distributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            if (internalTabClickListener != null) {
                tabView.setOnClickListener(internalTabClickListener);
            }

            tabStrip.addView(tabView);

            if (i == getCurrentItem()) {
                tabView.setSelected(true);
            }
        }
    }

    @Override
    protected int getCurrentItem() {
        if (mRecyclerView == null) {
            return RecyclerView.NO_POSITION;
        }
        return getSnapPosition(mRecyclerView.getLayoutManager());
    }

    @Override
    protected void setCurrentItem(int position) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(position);
        }
    }

    private class InternalOnScrollListener extends SnapPageScrollListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (mSnapHelper == null) {
                mSnapHelper = snapHelper;
            }
            scrollToTab(position, positionOffset);
            tabStrip.onViewPagerPageChanged(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
                tabStrip.getChildAt(i).setSelected(position == i);
            }
        }
    }

    private final RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mRecyclerView == null) {
                return;
            }

            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            int newCount = adapter != null ? adapter.getItemCount() : 0;
            int currentCount = tabStrip.getChildCount();
            if (newCount == currentCount) {
                // No change
                return;
            }

            populateTabStrip();
            if (mInternalOnScrollListener.currentPosition < newCount) {
                mInternalOnScrollListener.currentPosition = getSnapPosition(mRecyclerView.getLayoutManager());
                mInternalOnScrollListener.onPageScrolled(mInternalOnScrollListener.currentPosition, 0, 0);
            } else {
                mInternalOnScrollListener.currentPosition = RecyclerView.NO_POSITION;
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            onChanged();
        }
    };

    public RecyclerView.AdapterDataObserver getAdapterDataObserver() {
        return mAdapterDataObserver;
    }

}