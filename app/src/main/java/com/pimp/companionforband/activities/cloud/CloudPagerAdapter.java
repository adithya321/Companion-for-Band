package com.pimp.companionforband.activities.cloud;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.cloud.ActivitiesFragment;
import com.pimp.companionforband.fragments.cloud.ProfileFragment;
import com.pimp.companionforband.fragments.cloud.SummariesFragment;

public class CloudPagerAdapter extends FragmentPagerAdapter {

    public CloudPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new ActivitiesFragment();
            case 2:
                return new SummariesFragment();
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
                return MainActivity.sContext.getString(R.string.cloud_profile);
            case 1:
                return MainActivity.sContext.getString(R.string.cloud_activites);
            case 2:
                return MainActivity.sContext.getString(R.string.cloud_summaries);
        }
        return null;
    }
}