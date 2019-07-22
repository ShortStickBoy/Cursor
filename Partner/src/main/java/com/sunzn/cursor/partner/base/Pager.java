package com.sunzn.cursor.partner.base;

public abstract class Pager {

    protected static final float DEFAULT_WIDTH = 1.f;

    private final String title;
    private final float width;

    protected Pager(String title, float width) {
        this.title = title;
        this.width = width;
    }

    public String getTitle() {
        return title;
    }

    public float getWidth() {
        return width;
    }

}
