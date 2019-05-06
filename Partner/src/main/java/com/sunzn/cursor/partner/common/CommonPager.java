package com.sunzn.cursor.partner.common;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunzn.cursor.partner.base.Pager;

public class CommonPager extends Pager {

    private final int resource;

    protected CommonPager(CharSequence title, float width, @LayoutRes int resource) {
        super(title, width);
        this.resource = resource;
    }

    public static CommonPager of(CharSequence title, @LayoutRes int resource) {
        return of(title, DEFAULT_WIDTH, resource);
    }

    public static CommonPager of(CharSequence title, float width, @LayoutRes int resource) {
        return new CommonPager(title, width, resource);
    }

    public View initiate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(resource, container, false);
    }

}
