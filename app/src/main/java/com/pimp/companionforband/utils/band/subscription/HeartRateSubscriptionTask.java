/*
 * Companion for Band
 * Copyright (C) 2016  Adithya J
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.pimp.companionforband.utils.band.subscription;

import android.os.AsyncTask;
import android.widget.TextView;

import com.microsoft.band.BandException;
import com.microsoft.band.UserConsent;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.utils.band.BandUtils;

public class HeartRateSubscriptionTask extends AsyncTask<TextView, Void, Void> {
    @Override
    protected Void doInBackground(TextView... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                if (MainActivity.sharedPreferences.getBoolean("Heart Rate", true))
                    if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        MainActivity.client.getSensorManager().registerHeartRateEventListener(SensorsFragment.bandHeartRateEventListener);
                    } else {
                        MainActivity.sActivity.runOnUiThread(new Runnable() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void run() {
                                new HeartRateConsentTask().execute(SensorsFragment.reference);
                            }
                        });
                        SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.heart_rate_consent) + "\n", params[0]);
                    }
            } else {
                MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_not_found), "Style.ALERT");
            }
        } catch (BandException e) {
            BandUtils.handleBandException(e);
        } catch (Exception e) {
            MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
        }
        return null;
    }
}
