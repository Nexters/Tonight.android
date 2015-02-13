package com.teamnexters.tonight;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


public class MainActivity extends FragmentActivity {

    private static final String MSG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return HomeFragment.newInstance("HomeFragment, Instance 1");
                case 1: return RecordFragment.newInstance("RecordFragment, Instance 2");
                default: return HomeFragment.newInstance("HomeFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
