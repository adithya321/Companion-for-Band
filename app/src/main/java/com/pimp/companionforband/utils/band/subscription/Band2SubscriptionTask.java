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

import com.microsoft.band.BandException;
import com.microsoft.band.sensors.GsrSampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.utils.band.BandUtils;

public class Band2SubscriptionTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                if (MainActivity.band2) {
                    if (MainActivity.sharedPreferences.getBoolean("Altimeter", true))
                        MainActivity.client.getSensorManager().registerAltimeterEventListener(SensorsFragment.bandAltimeterEventListener);
                    if (MainActivity.sharedPreferences.getBoolean("Ambient Light", true))
                        MainActivity.client.getSensorManager().registerAmbientLightEventListener(SensorsFragment.bandAmbientLightEventListener);
                    if (MainActivity.sharedPreferences.getBoolean("Barometer", true))
                        MainActivity.client.getSensorManager().registerBarometerEventListener(SensorsFragment.bandBarometerEventListener);

                    if (MainActivity.sharedPreferences.getBoolean("GSR", true))
                        if (MainActivity.sharedPreferences.getInt("gsr_hz", 31) == 31)
                            MainActivity.client.getSensorManager().registerGsrEventListener(SensorsFragment.bandGsrEventListener, GsrSampleRate.MS200);
                        else
                            MainActivity.client.getSensorManager().registerGsrEventListener(SensorsFragment.bandGsrEventListener, GsrSampleRate.MS5000);

                    SensorsFragment.appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                            + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await(), SensorsFragment.band2TV);
                } else {
                    SensorsFragment.appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                            + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await()
                            + "\n\n" + MainActivity.sContext.getString(R.string.band_2_required), SensorsFragment.band2TV);
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
