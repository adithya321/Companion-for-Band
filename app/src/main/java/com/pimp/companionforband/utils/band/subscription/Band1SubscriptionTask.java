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
                switch (MainActivity.sharedPreferences.getInt("acc_hz", R.id.accelerometer_ms128)) {
                    case R.id.accelerometer_ms16:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(SensorsFragment.bandAccelerometerEventListener, SampleRate.MS16);
                        break;
                    case R.id.accelerometer_ms32:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(SensorsFragment.bandAccelerometerEventListener, SampleRate.MS32);
                        break;
                    default:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(SensorsFragment.bandAccelerometerEventListener, SampleRate.MS128);
                }

                switch (MainActivity.sharedPreferences.getInt("gyr_hz", R.id.gyroscope_ms128)) {
                    case R.id.gyroscope_ms16:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(SensorsFragment.bandGyroscopeEventListener, SampleRate.MS16);
                        break;
                    case R.id.gyroscope_ms32:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(SensorsFragment.bandGyroscopeEventListener, SampleRate.MS32);
                        break;
                    default:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(SensorsFragment.bandGyroscopeEventListener, SampleRate.MS128);
                }

                MainActivity.client.getSensorManager().registerCaloriesEventListener(SensorsFragment.bandCaloriesEventListener);
                MainActivity.client.getSensorManager().registerContactEventListener(SensorsFragment.bandContactEventListener);
                MainActivity.client.getSensorManager().registerDistanceEventListener(SensorsFragment.bandDistanceEventListener);
                MainActivity.client.getSensorManager().registerPedometerEventListener(SensorsFragment.bandPedometerEventListener);
                MainActivity.client.getSensorManager().registerSkinTemperatureEventListener(SensorsFragment.bandSkinTemperatureEventListener);
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
