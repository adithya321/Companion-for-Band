package com.pimp.companionforband.utils.band;

import android.os.AsyncTask;

import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.ConnectionState;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;

public class BandUtils extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (getConnectedBandClient()) {
                MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_connected),
                        "Style.CONFIRM");
                MainActivity.band2 = Integer.parseInt(MainActivity.client
                        .getHardwareVersion().await()) >= 20;
                MainActivity.editor.putString("device_name", MainActivity.devices[0].getName());
                MainActivity.editor.putString("device_mac", MainActivity.devices[0].getMacAddress());
                MainActivity.editor.apply();
            } else {
                MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_not_found),
                        "Style.ALERT");
            }
        } catch (BandException e) {
            handleBandException(e);
        } catch (Exception e) {
            MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
        }
        return null;
    }

    public static boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (MainActivity.client == null) {
            MainActivity.devices = BandClientManager.getInstance().getPairedBands();
            if (MainActivity.devices.length == 0) {
                MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_not_paired),
                        "Style.ALERT");
                return false;
            }
            MainActivity.client = BandClientManager.getInstance().create(MainActivity.sContext,
                    MainActivity.devices[0]);
        } else if (ConnectionState.CONNECTED == MainActivity.client.getConnectionState()) {
            return true;
        }

        MainActivity.appendToUI(MainActivity.sContext.getString(R.string.band_connecting),
                "Style.INFO");
        return ConnectionState.CONNECTED == MainActivity.client.connect().await();
    }

    public static void handleBandException(BandException e) {
        String exceptionMessage;
        switch (e.getErrorType()) {
            case DEVICE_ERROR:
                exceptionMessage = MainActivity.sContext.getString(R.string.band_not_found);
                break;
            case UNSUPPORTED_SDK_VERSION_ERROR:
                exceptionMessage = MainActivity.sContext.getString(R.string.band_unsupported_sdk);
                break;
            case SERVICE_ERROR:
                exceptionMessage = MainActivity.sContext.getString(R.string.band_service_unavailable);
                break;
            case BAND_FULL_ERROR:
                exceptionMessage = MainActivity.sContext.getString(R.string.band_full);
                break;
            default:
                exceptionMessage = MainActivity.sContext.getString(R.string.band_unknown_error)
                        + " : " + e.getMessage();
                break;
        }
        MainActivity.appendToUI(exceptionMessage, "Style.ALERT");
    }
}