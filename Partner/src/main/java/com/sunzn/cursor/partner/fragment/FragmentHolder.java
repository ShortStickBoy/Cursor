package com.sunzn.cursor.partner.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.sunzn.cursor.partner.base.Holder;

public class FragmentHolder extends Holder<FragmentPager> {

    public FragmentHolder(Context context) {
        super(context);
    }

    public static Creator with(Context context) {
        return new Creator(context);
    }

    public static class Creator {

        private final FragmentHolder items;

        public Creator(Context context) {
            items = new FragmentHolder(context);
        }

        public Creator add(@StringRes int title, Class<? extends Fragment> clazz) {
            return add(FragmentPager.from(items.getContext().getString(title), clazz));
        }

        public Creator add(@StringRes int title, Class<? extends Fragment> clazz, Bundle args) {
            return add(FragmentPager.from(items.getContext().getString(title), clazz, args));
        }

        public Creator add(@StringRes int title, float width, Class<? extends Fragment> clazz) {
            return add(FragmentPager.from(items.getContext().getString(title), width, clazz));
        }

        public Creator add(@StringRes int title, float width, Class<? extends Fragment> clazz, Bundle args) {
            return add(FragmentPager.from(items.getContext().getString(title), width, clazz, args));
        }

        public Creator add(CharSequence title, Class<? extends Fragment> clazz) {
            return add(FragmentPager.from(title, clazz));
        }

        public Creator add(CharSequence title, Class<? extends Fragment> clazz, Bundle args) {
            return add(FragmentPager.from(title, clazz, args));
        }

        public Creator add(FragmentPager item) {
            items.add(item);
            return this;
        }

        public FragmentHolder create() {
            return items;
        }

    }

}
