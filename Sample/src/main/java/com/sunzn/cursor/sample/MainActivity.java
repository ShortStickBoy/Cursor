package com.sunzn.cursor.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.sunzn.cursor.library.CursorView;
import com.sunzn.cursor.partner.fragment.FragmentAdapter;
import com.sunzn.cursor.partner.fragment.FragmentHolder;
import com.sunzn.cursor.partner.fragment.FragmentPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CursorView mCursorView = findViewById(R.id.fragment_nav_push_tab);
        mViewPager = findViewById(R.id.fragment_nav_push_pager);

        FragmentHolder pagers = new FragmentHolder(this);
        for (int i = 0; i < 10; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("Value", i);
            pagers.add(FragmentPager.from(getName(i), SampleFragment.class, bundle));
        }
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), pagers);
        mViewPager.setAdapter(adapter);
        mCursorView.setViewPager(mViewPager);
        mCursorView.setOnTabClickListener(new CursorView.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                mViewPager.setCurrentItem(position, false);
            }
        });
    }

    private String getName(int i) {
        switch (i) {
            case 0:
                return "推荐";
            case 1:
                return "文艺";
            case 2:
                return "诸子百家";
            default:
                return "默认";
        }
    }
}
