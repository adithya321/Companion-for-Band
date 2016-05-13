package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;

import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class GyroscopeEventListener implements BandGyroscopeEventListener {
    @Override
    public void onBandGyroscopeChanged(final BandGyroscopeEvent bandGyroscopeEvent) {
        if (bandGyroscopeEvent != null) {
            if (SensorsFragment.chart_spinner.getSelectedItem().toString().equals("Angular Velocity X")) {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorsFragment.mChartAdapter.add(bandGyroscopeEvent.getAngularVelocityX());
                    }
                });
            }
            SensorsFragment.appendToUI(String.format("X = %.3f (m/s²) \nY = %.3f (m/s²)\nZ = %.3f (m/s²)\n" +
                            "X = %.3f (°/sec)\nY = %.3f (°/sec)\nZ = %.3f (°/sec)",
                    bandGyroscopeEvent.getAccelerationX(),
                    bandGyroscopeEvent.getAccelerationY(),
                    bandGyroscopeEvent.getAccelerationZ(),
                    bandGyroscopeEvent.getAngularVelocityX(),
                    bandGyroscopeEvent.getAngularVelocityY(),
                    bandGyroscopeEvent.getAngularVelocityZ()),
                    SensorsFragment.gyroscopeTV);
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Gyroscope");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Gyroscope" + File.separator + "Gyroscope_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(bandGyroscopeEvent.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Gyroscope" + File.separator + "Gyroscope_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{String.valueOf(bandGyroscopeEvent.getTimestamp()),
                                    str, String.valueOf(bandGyroscopeEvent.getAccelerationX()),
                                    String.valueOf(bandGyroscopeEvent.getAccelerationY()),
                                    String.valueOf(bandGyroscopeEvent.getAccelerationZ()),
                                    String.valueOf(bandGyroscopeEvent.getAngularVelocityX()),
                                    String.valueOf(bandGyroscopeEvent.getAngularVelocityY()),
                                    String.valueOf(bandGyroscopeEvent.getAngularVelocityZ())});
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Gyroscope" + File.separator + "Gyroscope_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), "X m/s²", "Y", "Z", "X °/sec", "Y", "Z"});
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
