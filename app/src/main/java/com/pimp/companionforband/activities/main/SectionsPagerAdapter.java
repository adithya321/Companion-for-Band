package com.pimp.companionforband.activities.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pimp.companionforband.R;
import com.pimp.companionforband.fragments.extras.ExtrasFragment;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.fragments.theme.ThemeFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ThemeFragment.newInstance();
            case 1:
                return SensorsFragment.newInstance();
            case 2:
                return ExtrasFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return MainActivity.sContext.getString(R.string.theme);
            case 1:
                return MainActivity.sContext.getString(R.string.sensors);
            case 2:
                return MainActivity.sContext.getString(R.string.extras);
        }
        return null;
    }
}