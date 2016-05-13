package com.pimp.companionforband.utils.band.subscription;

import android.os.AsyncTask;

import com.microsoft.band.BandException;
import com.microsoft.band.sensors.SampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.BandUtils;
import com.pimp.companionforband.utils.band.listeners.AccelerometerEventListener;
import com.pimp.companionforband.utils.band.listeners.CaloriesEventListener;
import com.pimp.companionforband.utils.band.listeners.ContactEventListener;
import com.pimp.companionforband.utils.band.listeners.DistanceEventListener;
import com.pimp.companionforband.utils.band.listeners.GyroscopeEventListener;
import com.pimp.companionforband.utils.band.listeners.PedometerEventListener;
import com.pimp.companionforband.utils.band.listeners.SkinTemperatureEventListener;
import com.pimp.companionforband.utils.band.listeners.UVEventListener;

public class Band1SubscriptionTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                switch (MainActivity.sharedPreferences.getInt("acc_hz", R.id.accelerometer_ms128)) {
                    case R.id.accelerometer_ms16:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(new AccelerometerEventListener(), SampleRate.MS16);
                        break;
                    case R.id.accelerometer_ms32:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(new AccelerometerEventListener(), SampleRate.MS32);
                        break;
                    default:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(new AccelerometerEventListener(), SampleRate.MS128);
                }

                switch (MainActivity.sharedPreferences.getInt("gyr_hz", R.id.gyroscope_ms128)) {
                    case R.id.gyroscope_ms16:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(new GyroscopeEventListener(), SampleRate.MS16);
                        break;
                    case R.id.gyroscope_ms32:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(new GyroscopeEventListener(), SampleRate.MS32);
                        break;
                    default:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(new GyroscopeEventListener(), SampleRate.MS128);
                }

                MainActivity.client.getSensorManager().registerCaloriesEventListener(new CaloriesEventListener());
                MainActivity.client.getSensorManager().registerContactEventListener(new ContactEventListener());
                MainActivity.client.getSensorManager().registerDistanceEventListener(new DistanceEventListener());
                MainActivity.client.getSensorManager().registerPedometerEventListener(new PedometerEventListener());
                MainActivity.client.getSensorManager().registerSkinTemperatureEventListener(new SkinTemperatureEventListener());
                MainActivity.client.getSensorManager().registerUVEventListener(new UVEventListener());
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
