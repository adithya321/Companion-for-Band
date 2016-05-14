package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;

import com.jjoe64.graphview.series.DataPoint;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class DistanceEventListener implements BandDistanceEventListener {
    @Override
    public void onBandDistanceChanged(final BandDistanceEvent bandDistanceEvent) {
        if (bandDistanceEvent != null) {
            if (SensorsFragment.chart_spinner.getSelectedItem().toString().equals("Pace")) {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorsFragment.series1.appendData(new DataPoint(SensorsFragment.graphLastValueX,
                                (double) bandDistanceEvent.getPace()), true, 100);
                        SensorsFragment.graphLastValueX += 1;
                    }
                });
            }
            if (MainActivity.band2) {
                try {
                    SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.motion_type) + " = " + bandDistanceEvent.getMotionType() +
                            "\n" + MainActivity.sContext.getString(R.string.pace) + " (ms/m) = " + bandDistanceEvent.getPace() +
                            "\n" + MainActivity.sContext.getString(R.string.speed) + " (cm/s) = " + bandDistanceEvent.getSpeed() +
                            "\n" + MainActivity.sContext.getString(R.string.distance_today) + " = " + bandDistanceEvent.getDistanceToday() / 100000L +
                            " km\n" + MainActivity.sContext.getString(R.string.total_distance) + " = " + bandDistanceEvent.getTotalDistance() / 100000L +
                            " km", SensorsFragment.distanceTV);
                } catch (Exception e) {
                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.distanceTV);
                }
            } else {
                SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.motion_type) + " = " + bandDistanceEvent.getMotionType() +
                        "\n" + MainActivity.sContext.getString(R.string.pace) + " (ms/m) = " + bandDistanceEvent.getPace() +
                        "\n" + MainActivity.sContext.getString(R.string.speed) + " (cm/s) = " + bandDistanceEvent.getSpeed() +
                        "\n" + MainActivity.sContext.getString(R.string.total_distance) + " = " + bandDistanceEvent.getTotalDistance() / 100000L +
                        " km", SensorsFragment.distanceTV);
            }
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Distance");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Distance" + File.separator + "Distance_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(bandDistanceEvent.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Distance" + File.separator + "Distance_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2) {
                                try {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandDistanceEvent.getTimestamp()),
                                            str, String.valueOf(bandDistanceEvent.getMotionType()),
                                            String.valueOf(bandDistanceEvent.getPace()),
                                            String.valueOf(bandDistanceEvent.getSpeed()),
                                            String.valueOf(bandDistanceEvent.getDistanceToday()),
                                            String.valueOf(bandDistanceEvent.getTotalDistance())});
                                } catch (Exception e) {
                                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.distanceTV);
                                }
                            } else {
                                csvWriter.writeNext(new String[]{String.valueOf(bandDistanceEvent.getTimestamp()),
                                        str, String.valueOf(bandDistanceEvent.getMotionType()),
                                        String.valueOf(bandDistanceEvent.getPace()),
                                        String.valueOf(bandDistanceEvent.getSpeed()),
                                        String.valueOf(bandDistanceEvent.getTotalDistance())});
                            }
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Distance" + File.separator + "Distance_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2)
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.motion_type), MainActivity.sContext.getString(R.string.pace), MainActivity.sContext.getString(R.string.speed), MainActivity.sContext.getString(R.string.distance_today), MainActivity.sContext.getString(R.string.total_distance)});
                            else
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.motion_type), MainActivity.sContext.getString(R.string.pace), MainActivity.sContext.getString(R.string.speed), MainActivity.sContext.getString(R.string.total_distance)});
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