package com.sunzn.cursor.partner.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.sunzn.cursor.partner.base.Pager;

public class CommonPager extends Pager {

    private final int resource;

    protected CommonPager(String title, float width, @LayoutRes int resource) {
        super(title, width);
        this.resource = resource;
    }

    public static CommonPager of(String title, @LayoutRes int resource) {
        return of(title, DEFAULT_WIDTH, resource);
    }

    public static CommonPager of(String title, float width, @LayoutRes int resource) {
        return new CommonPager(title, width, resource);
    }

    public View initiate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(resource, container, false);
    }

}
