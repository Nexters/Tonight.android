package com.teamnexters.tonight;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.lang.reflect.Type;


public class MainActivity extends FragmentActivity {

    private static final String MSG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);//초기 페이지 설정
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private ViewGroup container;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0: return OnAirFragment.newInstance("OnairFragment, Instance 0");
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
