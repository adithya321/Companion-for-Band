package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.widget.TextView;

import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class GyroscopeEventListener implements BandGyroscopeEventListener {

    private TextView textView;
    private boolean graph;

    public void setViews(TextView textView, boolean graph) {
        this.textView = textView;
        this.graph = graph;
    }

    @Override
    public void onBandGyroscopeChanged(final BandGyroscopeEvent bandGyroscopeEvent) {
        if (bandGyroscopeEvent != null) {
            if (graph)
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorActivity.chartAdapter.add(bandGyroscopeEvent.getAngularVelocityX());
                    }
                });

            SensorsFragment.appendToUI(String.format("X = %.3f (m/s²) \nY = %.3f (m/s²)\nZ = %.3f (m/s²)\n" +
                            "X = %.3f (°/sec)\nY = %.3f (°/sec)\nZ = %.3f (°/sec)",
                    bandGyroscopeEvent.getAccelerationX(),
                    bandGyroscopeEvent.getAccelerationY(),
                    bandGyroscopeEvent.getAccelerationZ(),
                    bandGyroscopeEvent.getAngularVelocityX(),
                    bandGyroscopeEvent.getAngularVelocityY(),
                    bandGyroscopeEvent.getAngularVelocityZ()),
                    textView);

            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                MainActivity.bandSensorData.setGyroscopeData(bandGyroscopeEvent);

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
