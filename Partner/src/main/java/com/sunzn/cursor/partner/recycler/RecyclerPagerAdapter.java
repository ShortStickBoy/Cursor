package com.sunzn.cursor.partner.recycler;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerPagerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    @Nullable
    public CharSequence getPageTitle(int position) {
        return null;
    }

}