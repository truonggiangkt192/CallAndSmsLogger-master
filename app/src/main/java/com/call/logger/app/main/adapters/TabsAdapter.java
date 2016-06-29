package com.call.logger.app.main.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, OnPageChangeListener {
    private final Context context;
    private final TabHost tabHost;
    private final ViewPager viewPager;
    private final List<TabInfo> mTabs = new ArrayList<TabInfo>();

    // TODO: of changes  
    static final class TabInfo {
        final String tag;
        private final Class<?> clss;
        private final Bundle bundle;

        TabInfo(final String tag, final Class<?> clss, final Bundle bundle) {
            this.tag = tag;
            this.clss = clss;
            this.bundle = bundle;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context context;

        public DummyTabFactory(final Context context) {
            this.context = context;
        }

        @Override
        public View createTabContent(final String tag) {
            View v = new View(context);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabsAdapter(final FragmentActivity activity, final TabHost tabHost, final ViewPager viewPager) {
        super(activity.getSupportFragmentManager());
        context = activity;
        this.tabHost = tabHost;
        this.viewPager = viewPager;
        this.tabHost.setOnTabChangedListener(this);
        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);
    }

    public void addTab(final TabHost.TabSpec tabSpec, final Class<?> clss, final Bundle args) {
        tabSpec.setContent(new DummyTabFactory(context));
        String tag = tabSpec.getTag();
        TabInfo info = new TabInfo(tag, clss, args);
        mTabs.add(info);
        tabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(final int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(context, info.clss.getName(), info.bundle);
    }

    @Override
    public void onTabChanged(final String tabId) {
        int position = tabHost.getCurrentTab();
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        TabWidget widget = tabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        tabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
    }
}
