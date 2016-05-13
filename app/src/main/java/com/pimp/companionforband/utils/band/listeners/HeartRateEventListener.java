package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;

import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class HeartRateEventListener implements BandHeartRateEventListener {
    @Override
    public void onBandHeartRateChanged(final BandHeartRateEvent event) {
        if (event != null) {
            if (SensorsFragment.chart_spinner.getSelectedItem().toString().equals("Heart Rate")) {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorsFragment.mChartAdapter.add((float) event.getHeartRate());
                    }
                });
            }
            SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.heart_rate) + String.format(" = %d ", event.getHeartRate())
                    + MainActivity.sContext.getString(R.string.beats_per_minute) + "\n" + MainActivity.sContext.getString(R.string.quality)
                    + String.format(" = %s", event.getQuality()), SensorsFragment.heartRateTV);
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "HeartRate");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "HeartRate" + File.separator + "HeartRate_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "HeartRate" + File.separator + "HeartRate_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()),
                                    str, String.valueOf(event.getHeartRate()), String.valueOf(event.getQuality())});
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "HeartRate" + File.separator + "HeartRate_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.heart_rate), MainActivity.sContext.getString(R.string.quality)});
                            csvWriter.close();
                        }
                    } catch (IOException e) {
                        //
                    }
                } else {
                    file.mkdirs();
                }
            }
        }
    }
}
