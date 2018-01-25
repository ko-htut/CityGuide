package com.albertotorresgi.cityguide.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.albertotorresgi.cityguide.fragments.PlaceFragment;
import com.albertotorresgi.cityguide.models.PlaceType;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int page) {
        return PlaceFragment.newInstance(PlaceType.values()[page]);
    }

    @Override
    public int getCount() {
        return PlaceType.values().length;
    }
}
