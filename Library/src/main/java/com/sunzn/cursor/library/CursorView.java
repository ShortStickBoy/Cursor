package com.sunzn.cursor.library;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CursorView extends CursorBase {

    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener viewPagerPageChangeListener;

    public CursorView(Context context) {
        super(context);
    }

    public CursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CursorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getCurrentItem() {
        return viewPager == null ? -1 : viewPager.getCurrentItem();
    }

    @Override
    protected void setCurrentItem(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        tabStrip.removeAllViews();

        this.viewPager = viewPager;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    @Override
    protected void populateTabStrip() {
        final PagerAdapter adapter = viewPager.getAdapter();

        int size = adapter == null ? 0 : adapter.getCount();

        for (int i = 0; i < size; i++) {

            CharSequence title = adapter.getPageTitle(i);

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

            if (i == viewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }

            if (isMakeTabTextTypeface() && tabView instanceof TextView) {
                if (i == viewPager.getCurrentItem()) {
                    ((TextView) tabView).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    ((TextView) tabView).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        }
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link CursorView} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPagerPageChangeListener = listener;
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {

        private int scrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = tabStrip.getChildCount();
            if ((position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            tabStrip.onViewPagerPageChanged(position, positionOffset);

            scrollToTab(position, positionOffset);

            if (viewPagerPageChangeListener != null) {
                viewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;

            if (viewPagerPageChangeListener != null) {
                viewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                tabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
                tabStrip.getChildAt(i).setSelected(position == i);
                if (isMakeTabTextTypeface() && tabStrip.getChildAt(i) instanceof TextView) {
                    if (position == i) {
                        ((TextView) tabStrip.getChildAt(i)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    } else {
                        ((TextView) tabStrip.getChildAt(i)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    }
                }
            }

            if (viewPagerPageChangeListener != null) {
                viewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

}
