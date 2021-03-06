package com.sunzn.cursor.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public abstract class CursorBase extends HorizontalScrollView {

    private static final boolean DEFAULT_DISTRIBUTE_EVENLY = false;
    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TITLE_OFFSET_AUTO_CENTER = -1;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final boolean TAB_VIEW_TEXT_ALL_CAPS = true;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;
    private static final int TAB_VIEW_TEXT_COLOR = 0xFC000000;
    private static final int TAB_VIEW_TEXT_MIN_WIDTH = 0;
    private static final boolean TAB_TEXT_TYPEFACE = true;
    private static final boolean TAB_CLICKABLE = true;

    protected final CursorStrip tabStrip;
    private int titleOffset;
    private int tabViewBackgroundResId;
    private boolean tabViewTextAllCaps;
    private boolean makeTabTextTypeface;
    private ColorStateList normTabTextColors;
    protected float normTabTextSize;
    private int tabViewTextHorizontalPadding;
    private int tabViewTextMinWidth;
    private OnScrollChangeListener onScrollChangeListener;
    protected TabProvider tabProvider;
    protected InternalTabClickListener internalTabClickListener;
    private OnTabClickListener onTabClickListener;
    protected boolean distributeEvenly;

    public CursorBase(Context context) {
        this(context, null);
    }

    public CursorBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public CursorBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float density = dm.density;

        int tabBackgroundResId = NO_ID;
        boolean textAllCaps = TAB_VIEW_TEXT_ALL_CAPS;
        ColorStateList normTextColors;
        float normTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP, dm);
        int textHorizontalPadding = (int) (TAB_VIEW_PADDING_DIPS * density);
        int textMinWidth = (int) (TAB_VIEW_TEXT_MIN_WIDTH * density);
        boolean distributeEvenly = DEFAULT_DISTRIBUTE_EVENLY;
        int customTabLayoutId = NO_ID;
        int customTabTextViewId = NO_ID;
        boolean typeface = TAB_TEXT_TYPEFACE;
        boolean clickable = TAB_CLICKABLE;
        int titleOffset = (int) (TITLE_OFFSET_DIPS * density);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CursorView, defStyle, 0);
        tabBackgroundResId = a.getResourceId(R.styleable.CursorView_cv_normTabBackground, tabBackgroundResId);
        textAllCaps = a.getBoolean(R.styleable.CursorView_cv_normTabTextAllCaps, textAllCaps);
        normTextColors = a.getColorStateList(R.styleable.CursorView_cv_normTabTextColor);
        normTextSize = a.getDimension(R.styleable.CursorView_cv_normTabTextSize, normTextSize);
        textHorizontalPadding = a.getDimensionPixelSize(R.styleable.CursorView_cv_normTabTextHorizontalPadding, textHorizontalPadding);
        textMinWidth = a.getDimensionPixelSize(R.styleable.CursorView_cv_normTabTextMinWidth, textMinWidth);
        typeface = a.getBoolean(R.styleable.CursorView_cv_makeTabTextTypeface, typeface);
        customTabLayoutId = a.getResourceId(R.styleable.CursorView_cv_customTabTextLayoutId, customTabLayoutId);
        customTabTextViewId = a.getResourceId(R.styleable.CursorView_cv_customTabTextViewId, customTabTextViewId);
        distributeEvenly = a.getBoolean(R.styleable.CursorView_cv_distributeEvenly, distributeEvenly);
        clickable = a.getBoolean(R.styleable.CursorView_cv_clickable, clickable);
        titleOffset = a.getLayoutDimension(R.styleable.CursorView_cv_titleOffset, titleOffset);
        a.recycle();

        this.titleOffset = titleOffset;
        this.tabViewBackgroundResId = tabBackgroundResId;
        this.tabViewTextAllCaps = textAllCaps;
        this.normTabTextColors = (normTextColors != null) ? normTextColors : ColorStateList.valueOf(TAB_VIEW_TEXT_COLOR);
        this.normTabTextSize = normTextSize;
        this.tabViewTextHorizontalPadding = textHorizontalPadding;
        this.tabViewTextMinWidth = textMinWidth;
        this.makeTabTextTypeface = typeface;
        this.internalTabClickListener = clickable ? new InternalTabClickListener() : null;
        this.distributeEvenly = distributeEvenly;

        if (customTabLayoutId != NO_ID) {
            setCustomTabView(customTabLayoutId, customTabTextViewId);
        }

        this.tabStrip = new CursorStrip(context, attrs);

        if (distributeEvenly && tabStrip.isIndicatorAlwaysInCenter()) {
            throw new UnsupportedOperationException("'distributeEvenly' and 'indicatorAlwaysInCenter' both use does not support");
        }

        // Make sure that the Tab Strips fills this View
        setFillViewport(!tabStrip.isIndicatorAlwaysInCenter());

        addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, oldl);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (tabStrip.isIndicatorAlwaysInCenter() && tabStrip.getChildCount() > 0) {
            View firstTab = tabStrip.getChildAt(0);
            View lastTab = tabStrip.getChildAt(tabStrip.getChildCount() - 1);
            int start = (w - CursorUtils.getMeasuredWidth(firstTab)) / 2 - CursorUtils.getMarginStart(firstTab);
            int end = (w - CursorUtils.getMeasuredWidth(lastTab)) / 2 - CursorUtils.getMarginEnd(lastTab);
            tabStrip.setMinimumWidth(tabStrip.getMeasuredWidth());
            ViewCompat.setPaddingRelative(this, start, getPaddingTop(), end, getPaddingBottom());
            setClipToPadding(false);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // Ensure first scroll
        if (changed) {
            scrollToTab(getCurrentItem(), 0);
        }
    }

    /**
     * Set the behavior of the Indicator scrolling feedback.
     *
     * @param interpolator {@link CursorInterpolator}
     */
    public void setIndicationInterpolator(CursorInterpolator interpolator) {
        tabStrip.setIndicationInterpolator(interpolator);
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     * <p>
     * If you only require simple customisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        tabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * Set the color used for styling the tab text.
     *
     * @param color to use for tab text
     */
    public void setDefaultTabTextColor(int color) {
        normTabTextColors = ColorStateList.valueOf(color);
    }

    /**
     * Sets the colors used for styling the tab text.
     *
     * @param colors ColorStateList to use for tab text
     */
    public void setDefaultTabTextColor(ColorStateList colors) {
        normTabTextColors = colors;
    }

    /**
     * Set the same weight for tab
     */
    public void setDistributeEvenly(boolean distributeEvenly) {
        this.distributeEvenly = distributeEvenly;
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        tabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        tabStrip.setDividerColors(colors);
    }

    /**
     * Set {@link OnScrollChangeListener} for obtaining values of scrolling.
     *
     * @param listener the {@link OnScrollChangeListener} to set
     */
    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        onScrollChangeListener = listener;
    }

    /**
     * Set {@link OnTabClickListener} for obtaining click event.
     *
     * @param listener the {@link OnTabClickListener} to set
     */
    public void setOnTabClickListener(OnTabClickListener listener) {
        onTabClickListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        tabProvider = new SimpleTabProvider(getContext(), layoutResId, textViewId);
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param provider {@link TabProvider}
     */
    public void setCustomTabView(TabProvider provider) {
        tabProvider = provider;
    }

    /**
     * Returns the view at the specified position in the tabs.
     *
     * @param position the position at which to get the view from
     * @return the view at the specified position or null if the position does not exist within the
     * tabs
     */
    public View getTabAt(int position) {
        return tabStrip.getChildAt(position);
    }

    public boolean isMakeTabTextTypeface() {
        return makeTabTextTypeface;
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(CharSequence title, int position) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText(title);
        textView.setTextColor(normTabTextColors);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, normTabTextSize);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        if (tabViewBackgroundResId != NO_ID) {
            textView.setBackgroundResource(tabViewBackgroundResId);
        } else {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
        textView.setAllCaps(tabViewTextAllCaps);

        textView.setPadding(tabViewTextHorizontalPadding, 0, tabViewTextHorizontalPadding, 0);

        if (tabViewTextMinWidth > 0) {
            textView.setMinWidth(tabViewTextMinWidth);
        }

        return textView;
    }

    protected abstract int getCurrentItem();

    protected abstract void setCurrentItem(int position);

    protected abstract void populateTabStrip();

    protected void scrollToTab(int tabIndex, float positionOffset) {
        final int tabStripChildCount = tabStrip.getChildCount();
        if (tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        final boolean isLayoutRtl = CursorUtils.isLayoutRtl(this);
        View selectedTab = tabStrip.getChildAt(tabIndex);
        int widthPlusMargin = CursorUtils.getWidth(selectedTab) + CursorUtils.getMarginHorizontally(selectedTab);
        int extraOffset = (int) (positionOffset * widthPlusMargin);

        if (tabStrip.isIndicatorAlwaysInCenter()) {

            if (0f < positionOffset && positionOffset < 1f) {
                View nextTab = tabStrip.getChildAt(tabIndex + 1);
                int selectHalfWidth = CursorUtils.getWidth(selectedTab) / 2 + CursorUtils.getMarginEnd(selectedTab);
                int nextHalfWidth = CursorUtils.getWidth(nextTab) / 2 + CursorUtils.getMarginStart(nextTab);
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth));
            }

            View firstTab = tabStrip.getChildAt(0);
            int x;
            if (isLayoutRtl) {
                int first = CursorUtils.getWidth(firstTab) + CursorUtils.getMarginEnd(firstTab);
                int selected = CursorUtils.getWidth(selectedTab) + CursorUtils.getMarginEnd(selectedTab);
                x = CursorUtils.getEnd(selectedTab) - CursorUtils.getMarginEnd(selectedTab) - extraOffset;
                x -= (first - selected) / 2;
            } else {
                int first = CursorUtils.getWidth(firstTab) + CursorUtils.getMarginStart(firstTab);
                int selected = CursorUtils.getWidth(selectedTab) + CursorUtils.getMarginStart(selectedTab);
                x = CursorUtils.getStart(selectedTab) - CursorUtils.getMarginStart(selectedTab) + extraOffset;
                x -= (first - selected) / 2;
            }

            scrollTo(x, 0);
            return;

        }

        int x;
        if (titleOffset == TITLE_OFFSET_AUTO_CENTER) {

            if (0f < positionOffset && positionOffset < 1f) {
                View nextTab = tabStrip.getChildAt(tabIndex + 1);
                int selectHalfWidth = CursorUtils.getWidth(selectedTab) / 2 + CursorUtils.getMarginEnd(selectedTab);
                int nextHalfWidth = CursorUtils.getWidth(nextTab) / 2 + CursorUtils.getMarginStart(nextTab);
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth));
            }

            if (isLayoutRtl) {
                x = -CursorUtils.getWidthWithMargin(selectedTab) / 2 + getWidth() / 2;
                x -= CursorUtils.getPaddingStart(this);
            } else {
                x = CursorUtils.getWidthWithMargin(selectedTab) / 2 - getWidth() / 2;
                x += CursorUtils.getPaddingStart(this);
            }

        } else {

            if (isLayoutRtl) {
                x = (tabIndex > 0 || positionOffset > 0) ? titleOffset : 0;
            } else {
                x = (tabIndex > 0 || positionOffset > 0) ? -titleOffset : 0;
            }

        }

        int start = CursorUtils.getStart(selectedTab);
        int startMargin = CursorUtils.getMarginStart(selectedTab);
        if (isLayoutRtl) {
            x += start + startMargin - extraOffset - getWidth() + CursorUtils.getPaddingHorizontally(this);
        } else {
            x += start - startMargin + extraOffset;
        }

        scrollTo(x, 0);

    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);

    }

    /**
     * Interface definition for a callback to be invoked when the scroll position of a view changes.
     */
    public interface OnScrollChangeListener {

        /**
         * Called when the scroll position of a view changes.
         *
         * @param scrollX    Current horizontal scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         */
        void onScrollChanged(int scrollX, int oldScrollX);
    }

    /**
     * Interface definition for a callback to be invoked when a tab is clicked.
     */
    public interface OnTabClickListener {

        /**
         * Called when a tab is clicked.
         *
         * @param position tab's position
         */
        void onTabClicked(int position);
    }

    /**
     * Create the custom tabs in the tab layout. Set with
     * {@link #setCustomTabView(TabProvider)}
     */
    public interface TabProvider {

        /**
         * @return Return the View of {@code position} for the Tabs
         */
        View createTabView(ViewGroup container, int position, @Nullable CharSequence title);

    }

    private static class SimpleTabProvider implements TabProvider {

        private final LayoutInflater inflater;
        private final int tabViewLayoutId;
        private final int tabViewTextViewId;

        private SimpleTabProvider(Context context, int layoutResId, int textViewId) {
            inflater = LayoutInflater.from(context);
            tabViewLayoutId = layoutResId;
            tabViewTextViewId = textViewId;
        }

        @Override
        public View createTabView(ViewGroup container, int position, @Nullable CharSequence title) {
            View tabView = null;
            TextView tabTitleView = null;

            if (tabViewLayoutId != NO_ID) {
                tabView = inflater.inflate(tabViewLayoutId, container, false);
            }

            if (tabViewTextViewId != NO_ID && tabView != null) {
                tabTitleView = tabView.findViewById(tabViewTextViewId);
            }

            if (tabTitleView == null && tabView instanceof TextView) {
                tabTitleView = (TextView) tabView;
            }

            if (tabTitleView != null) {
                tabTitleView.setText(title);
            }

            return tabView;
        }

    }

    private class InternalTabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                if (v == tabStrip.getChildAt(i)) {
                    if (onTabClickListener != null) {
                        onTabClickListener.onTabClicked(i);
                    }
                    setCurrentItem(i);
                    return;
                }
            }
        }
    }

}
