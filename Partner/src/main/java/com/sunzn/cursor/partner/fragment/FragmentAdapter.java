package com.sunzn.cursor.partner.fragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

public class FragmentAdapter extends FragmentPagerAdapter {

    interface PositionHelper {

        int onGetPosition(@NonNull Object object);

    }

    private FragmentHolder pages;
    private PositionHelper helper;
    private FragmentManager manager;
    private SparseArrayCompat<WeakReference<Fragment>> holder;

    public FragmentAdapter(FragmentManager fm, FragmentHolder pages) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.manager = fm;
        this.pages = pages;
        this.holder = new SparseArrayCompat<>(pages.size());
    }

    public void setPages(FragmentHolder pages) {
        this.pages = pages;
    }

    public void setPositionHelper(PositionHelper helper) {
        this.helper = helper;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return getPagerItem(position).instantiate(manager, pages.getContext(), position);
    }

    @Override
    public long getItemId(int position) {
        return getPageTitle(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (helper != null) {
            return helper.onGetPosition(object);
        } else {
            return super.getItemPosition(object);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<>((Fragment) item));
        }
        return item;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        holder.remove(position);
        super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        return getPagerItem(position).getTitle();
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    public Fragment getPage(int position) {
        final WeakReference<Fragment> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

    protected FragmentPager getPagerItem(int position) {
        return pages.get(position);
    }

}
