package com.pimp.companionforband.activities.cloud;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.cloud.ProfileFragment;
import com.pimp.companionforband.fragments.cloud.SummariesFragment;
import com.pimp.companionforband.fragments.cloud.WebviewFragment;

public class CloudPagerAdapter extends FragmentPagerAdapter {

    public CloudPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WebviewFragment();
            case 1:
                return new SummariesFragment();
            case 2:
                return new ProfileFragment();
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
                return MainActivity.sContext.getString(R.string.cloud_activites);
            case 1:
                return MainActivity.sContext.getString(R.string.cloud_summaries);
            case 2:
                return MainActivity.sContext.getString(R.string.cloud_profile);
        }
        return null;
    }
}