package com.pimp.companionforband.utils.band.subscription;

import android.os.AsyncTask;

import com.microsoft.band.BandException;
import com.microsoft.band.sensors.GsrSampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.utils.band.BandUtils;
import com.pimp.companionforband.utils.band.listeners.AltimeterEventListener;
import com.pimp.companionforband.utils.band.listeners.AmbientLightEventListener;
import com.pimp.companionforband.utils.band.listeners.BarometerEventListener;
import com.pimp.companionforband.utils.band.listeners.GsrEventListener;

public class Band2SubscriptionTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                if (MainActivity.band2) {
                    MainActivity.client.getSensorManager().registerAltimeterEventListener(new AltimeterEventListener());
                    MainActivity.client.getSensorManager().registerAmbientLightEventListener(new AmbientLightEventListener());
                    MainActivity.client.getSensorManager().registerBarometerEventListener(new BarometerEventListener());

                    if (MainActivity.sharedPreferences.getInt("gsr_hz", R.id.gsr_ms200) == R.id.gsr_ms200)
                        MainActivity.client.getSensorManager().registerGsrEventListener(new GsrEventListener(), GsrSampleRate.MS200);
                    else
                        MainActivity.client.getSensorManager().registerGsrEventListener(new GsrEventListener(), GsrSampleRate.MS5000);

                    SensorsFragment.appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                            + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await(), SensorsFragment.band2TV);
                } else {
                    SensorsFragment.appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                            + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await()
                            + "\n" + MainActivity.sContext.getString(R.string.band_2_required), SensorsFragment.band2TV);
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
