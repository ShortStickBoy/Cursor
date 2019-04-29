package com.sunzn.cursor.partner.base;

import android.content.Context;

import java.util.ArrayList;

public abstract class Holder<T extends Pager> extends ArrayList<T> {

    private final Context context;

    protected Holder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

}
