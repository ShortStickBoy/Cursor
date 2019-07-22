package com.sunzn.cursor.partner.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sunzn.cursor.partner.base.Pager;

public class FragmentPager extends Pager {

    private static final String TAG = "FragmentPagerItem";
    private static final String KEY_POSITION = TAG + ":Position";

    private final String className;
    private final Bundle bundle;

    protected FragmentPager(String title, float width, String className, Bundle bundle) {
        super(title, width);
        this.className = className;
        this.bundle = bundle;
    }

    public static FragmentPager from(String title, Class<? extends Fragment> clazz) {
        return from(title, DEFAULT_WIDTH, clazz);
    }

    public static FragmentPager from(String title, Class<? extends Fragment> clazz, Bundle args) {
        return from(title, DEFAULT_WIDTH, clazz, args);
    }

    public static FragmentPager from(String title, float width, Class<? extends Fragment> clazz) {
        return from(title, width, clazz, new Bundle());
    }

    public static FragmentPager from(String title, float width, Class<? extends Fragment> clazz, Bundle args) {
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

    public Fragment instantiate(FragmentManager manager, Context context, int position) {
        setPosition(bundle, position);
//        return Fragment.instantiate(context, className, bundle);
        Fragment fragment = manager.getFragmentFactory().instantiate(context.getClassLoader(), className);
        fragment.setArguments(bundle);
        return fragment;
    }

}
