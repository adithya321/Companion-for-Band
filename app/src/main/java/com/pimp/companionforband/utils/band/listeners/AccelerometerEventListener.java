package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class AccelerometerEventListener implements BandAccelerometerEventListener {

    TextView textView;

    public AccelerometerEventListener(TextView textView) {
        this.textView = textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
        if (event != null) {
            /*if (SensorsFragment.chart_spinner.getSelectedItem().toString().equals("Acceleration")) {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorsFragment.series1.appendData(new DataPoint(SensorsFragment.graphLastValueX,
                                (double) event.getAccelerationX()), true, 100);
                        SensorsFragment.series2.appendData(new DataPoint(SensorsFragment.graphLastValueX,
                                (double) event.getAccelerationY()), true, 100);
                        SensorsFragment.series3.appendData(new DataPoint(SensorsFragment.graphLastValueX,
                                (double) event.getAccelerationZ()), true, 100);
                        SensorsFragment.graphLastValueX += 1;
                    }
                });
            }*/
            SensorsFragment.appendToUI(String.format(" X = %.3f (m/s²) \n Y = %.3f (m/s²)\n Z = %.3f (m/s²)",
                    event.getAccelerationX(),
                    event.getAccelerationY(),
                    event.getAccelerationZ()), textView);
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Accelerometer");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Accelerometer" + File.separator + "Accelerometer_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Accelerometer" + File.separator + "Accelerometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()),
                                    str, String.valueOf(event.getAccelerationX()),
                                    String.valueOf(event.getAccelerationY()),
                                    String.valueOf(event.getAccelerationZ())});

                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Accelerometer" + File.separator + "Accelerometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), "X", "Y", "Z"});
                            csvWriter.close();
                        }
                    } catch (IOException e) {
                        Log.e("CSV", e.toString());
                    }
                } else {
                    file.mkdirs();
                }
            }
        }
    }
}
