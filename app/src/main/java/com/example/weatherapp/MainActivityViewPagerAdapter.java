package com.example.weatherapp;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewPagerAdapter extends FragmentPagerAdapter {
    private List<MainActivityFragment> mFragmentList;

    public MainActivityViewPagerAdapter(FragmentManager fm, List<MainActivityFragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public MainActivityFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
