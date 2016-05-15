package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
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

public class RRIntervalEventListener implements BandRRIntervalEventListener {

    TextView textView;
    boolean graph;

    public void setViews(TextView textView, boolean graph) {
        this.textView = textView;
        this.graph = graph;
    }

    @Override
    public void onBandRRIntervalChanged(final BandRRIntervalEvent event) {
        if (event != null) {
            if (graph)
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorActivity.series1.appendData(new DataPoint(SensorActivity.graphLastValueX,
                                event.getInterval()), true, 30);
                        SensorActivity.graphLastValueX += 1;
                    }
                });

            SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.rr)
                    + String.format(" = %.3f s\n", event.getInterval()), textView);

            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "RRInterval");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "RRInterval" + File.separator + "RRInterval_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "RRInterval" + File.separator + "RRInterval_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()), str,
                                    String.valueOf(event.getInterval())});
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "RRInterval" + File.separator + "RRInterval_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.rr)});
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
