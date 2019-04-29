package com.sunzn.cursor.partner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunzn.cursor.partner.base.Pager;

public class FragmentPager extends Pager {

    private static final String TAG = "FragmentPagerItem";
    private static final String KEY_POSITION = TAG + ":Position";

    private final String className;
    private final Bundle args;

    protected FragmentPager(CharSequence title, float width, String className, Bundle args) {
        super(title, width);
        this.className = className;
        this.args = args;
    }

    public static FragmentPager of(CharSequence title, Class<? extends Fragment> clazz) {
        return of(title, DEFAULT_WIDTH, clazz);
    }

    public static FragmentPager of(CharSequence title, Class<? extends Fragment> clazz, Bundle args) {
        return of(title, DEFAULT_WIDTH, clazz, args);
    }

    public static FragmentPager of(CharSequence title, float width, Class<? extends Fragment> clazz) {
        return of(title, width, clazz, new Bundle());
    }

    public static FragmentPager of(CharSequence title, float width, Class<? extends Fragment> clazz, Bundle args) {
        return new FragmentPager(title, width, clazz.getName(), args);
    }

    public static boolean hasPosition(Bundle args) {
        return args != null && args.containsKey(KEY_POSITION);
    }

    public static int getPosition(Bundle args) {
        return (hasPosition(args)) ? args.getInt(KEY_POSITION) : 0;
    }

    static void setPosition(Bundle args, int position) {
        args.putInt(KEY_POSITION, position);
    }

    public Fragment instantiate(Context context, int position) {
        setPosition(args, position);
        return Fragment.instantiate(context, className, args);
    }

}
