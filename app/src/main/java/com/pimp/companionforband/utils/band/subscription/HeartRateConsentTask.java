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

import android.app.Activity;
import android.os.AsyncTask;

import com.microsoft.band.BandException;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.BandUtils;

import java.lang.ref.WeakReference;

public class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {

    @Override
    protected Void doInBackground(WeakReference<Activity>... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {

                if (params[0].get() != null) {
                    MainActivity.client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                        @Override
                        public void userAccepted(boolean consentGiven) {
                            new HeartRateSubscriptionTask().execute();
                        }
                    });
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
