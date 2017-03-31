package com.twinc.halmato.lottogo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Tiaan on 3/31/2017.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter
{
    private static final int CAMERA_FRAGMENT_POSITION = 0;
    private static final int DRAW_SELECTIONS_FRAGMENT_POSITION = 1;
    private static final String[] PAGE_TITLES = {"CAMERA", "My Draws", "Stats"};

    public CustomPagerAdapter(FragmentManager manager){
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        if(position == CAMERA_FRAGMENT_POSITION) {

            fragment = new CameraFragment();

        } else if(position == DRAW_SELECTIONS_FRAGMENT_POSITION) {

            fragment = new DrawSelectionsFragment();

        } else {

            fragment = ViewPagerItemFragment.getInstanceOfFragment(PAGE_TITLES[position]);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return PAGE_TITLES[position];
    }
}