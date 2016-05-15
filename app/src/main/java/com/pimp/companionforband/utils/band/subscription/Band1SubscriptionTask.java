package com.pimp.companionforband.utils.band.subscription;

import android.os.AsyncTask;

import com.microsoft.band.BandException;
import com.microsoft.band.sensors.SampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.utils.band.BandUtils;

public class Band1SubscriptionTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                if (MainActivity.sharedPreferences.getBoolean("Accelerometer", true)) {
                    switch (MainActivity.sharedPreferences.getInt("acc_hz", 13)) {
                        case 11:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                    SensorsFragment.bandAccelerometerEventListener, SampleRate.MS16);
                            break;
                        case 12:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                    SensorsFragment.bandAccelerometerEventListener, SampleRate.MS32);
                            break;
                        default:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                    SensorsFragment.bandAccelerometerEventListener, SampleRate.MS128);
                    }
                }

                if (MainActivity.sharedPreferences.getBoolean("Gyroscope", true)) {
                    switch (MainActivity.sharedPreferences.getInt("gyr_hz", 23)) {
                        case 21:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                    SensorsFragment.bandGyroscopeEventListener, SampleRate.MS16);
                            break;
                        case 22:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                    SensorsFragment.bandGyroscopeEventListener, SampleRate.MS32);
                            break;
                        default:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                    SensorsFragment.bandGyroscopeEventListener, SampleRate.MS128);
                    }
                }

                if (MainActivity.sharedPreferences.getBoolean("Calories", true))
                    MainActivity.client.getSensorManager().registerCaloriesEventListener(SensorsFragment.bandCaloriesEventListener);
                if (MainActivity.sharedPreferences.getBoolean("Contact", true))
                    MainActivity.client.getSensorManager().registerContactEventListener(SensorsFragment.bandContactEventListener);
                if (MainActivity.sharedPreferences.getBoolean("Distance", true))
                    MainActivity.client.getSensorManager().registerDistanceEventListener(SensorsFragment.bandDistanceEventListener);
                if (MainActivity.sharedPreferences.getBoolean("Pedometer", true))
                    MainActivity.client.getSensorManager().registerPedometerEventListener(SensorsFragment.bandPedometerEventListener);
                if (MainActivity.sharedPreferences.getBoolean("Skin Temperature", true))
                    MainActivity.client.getSensorManager().registerSkinTemperatureEventListener(SensorsFragment.bandSkinTemperatureEventListener);
                if (MainActivity.sharedPreferences.getBoolean("UV", true))
                    MainActivity.client.getSensorManager().registerUVEventListener(SensorsFragment.bandUVEventListener);
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
