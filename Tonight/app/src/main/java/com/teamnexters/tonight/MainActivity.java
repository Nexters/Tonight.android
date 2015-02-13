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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
<<<<<<< HEAD
=======
import android.view.ViewGroup;
>>>>>>> 2b1571c7a20325bdc5778224aabbcd015455fd2b
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
<<<<<<< HEAD

=======
        pager.setCurrentItem(1);//초기 페이지 설정
>>>>>>> 2b1571c7a20325bdc5778224aabbcd015455fd2b
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private ViewGroup container;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0: return OnairFragment.newInstance("OnairFragment, Instance 0");
                case 1: return HomeFragment.newInstance("HomeFragment, Instance 1");
                case 2: return RecordFragment.newInstance("RecordFragment, Instance 2");
                default: return HomeFragment.newInstance("HomeFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
