package com.call.logger.app.main;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;
import com.call.logger.app.main.adapters.TabsAdapter;
import com.call.logger.app.main.services.SmsService;
import com.call.logger.app.main.settings.SetLocale;

public class MainFragmentActivity extends FragmentActivity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);
        new SetLocale().set(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabsAdapter tabsAdapter = new TabsAdapter(this, tabHost, viewPager);
        tabsAdapter.addTab(tabHost.newTabSpec("calls").setIndicator("calls"), CallFragment.class, null);
        tabsAdapter.addTab(tabHost.newTabSpec("sms").setIndicator("sms"), SmsFragment.class, null);

        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        if (!isMyServiceRunning()) {
            Intent service = new Intent(this, SmsService.class);
            startService(service);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.call.logger.app.main.services.SmsService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}