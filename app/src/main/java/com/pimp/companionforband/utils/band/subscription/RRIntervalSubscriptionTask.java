package com.pimp.companionforband.utils.band.subscription;

import android.os.AsyncTask;

import com.microsoft.band.BandException;
import com.microsoft.band.UserConsent;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;
import com.pimp.companionforband.utils.band.BandUtils;

public class RRIntervalSubscriptionTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (BandUtils.getConnectedBandClient()) {
                if (MainActivity.band2) {
                    if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent()
                            == UserConsent.GRANTED) {
                        MainActivity.client.getSensorManager()
                                .registerRRIntervalEventListener(SensorsFragment.bandRRIntervalEventListener);
                    } else {
                        SensorsFragment.appendToUI(MainActivity.sContext
                                        .getString(R.string.heart_rate_consent) + "\n",
                                SensorsFragment.rrTV);
                    }
                }
            } else {
                MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_not_found),
                        "Style.ALERT");
            }
        } catch (BandException e) {
            BandUtils.handleBandException(e);
        } catch (Exception e) {
            MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
        }
        return null;
    }
}
