package com.sunzn.cursor.partner.common;

import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.sunzn.cursor.partner.base.Holder;

public class CommonHolder extends Holder<CommonPager> {

    public CommonHolder(Context context) {
        super(context);
    }

    public static Creator with(Context context) {
        return new Creator(context);
    }

    public static class Creator {

        private final CommonHolder items;

        public Creator(Context context) {
            items = new CommonHolder(context);
        }

        public Creator add(@StringRes int title, @LayoutRes int resource) {
            return add(CommonPager.of(items.getContext().getString(title), resource));
        }

        public Creator add(@StringRes int title, float width, @LayoutRes int resource) {
            return add(CommonPager.of(items.getContext().getString(title), width, resource));
        }

        public Creator add(String title, @LayoutRes int resource) {
            return add(CommonPager.of(title, resource));
        }

        public Creator add(CommonPager item) {
            items.add(item);
            return this;
        }

        public CommonHolder create() {
            return items;
        }

    }

}
