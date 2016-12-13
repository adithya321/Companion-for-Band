/*
 * Companion for Band
 * Copyright (C) 2016  Adithya J
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
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

public class CaloriesEventListener implements BandCaloriesEventListener {

    private TextView textView;
    private boolean graph;

    public void setViews(TextView textView, boolean graph) {
        this.textView = textView;
        this.graph = graph;
    }

    @Override
    public void onBandCaloriesChanged(final BandCaloriesEvent bandCaloriesEvent) {
        if (bandCaloriesEvent != null) {
            if (graph)
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SensorActivity.chartAdapter.add((float) bandCaloriesEvent.getCalories());
                    }
                });

            if (MainActivity.band2) {
                try {
                    SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.calories_today) + " = " + bandCaloriesEvent.getCaloriesToday() + " kCal\n" +
                            MainActivity.sContext.getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", textView);
                } catch (Exception e) {
                    SensorsFragment.appendToUI(e.toString(), textView);
                }
            } else {
                SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", textView);
            }

            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                MainActivity.bandSensorData.setCalorieData(bandCaloriesEvent);

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Calories");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Calories" + File.separator + "Calories_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(bandCaloriesEvent.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Calories" + File.separator + "Calories_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2) {
                                try {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandCaloriesEvent.getTimestamp()),
                                            str, String.valueOf(bandCaloriesEvent.getCaloriesToday()),
                                            String.valueOf(bandCaloriesEvent.getCalories())});
                                } catch (Exception e) {
                                    SensorsFragment.appendToUI(e.toString(), textView);
                                }
                            } else {
                                csvWriter.writeNext(new String[]{String.valueOf(bandCaloriesEvent.getTimestamp()),
                                        str, String.valueOf(bandCaloriesEvent.getCalories())});
                            }
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Calories" + File.separator + "Calories_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2)
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.calories_today), MainActivity.sContext.getString(R.string.calories)});
                            else
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.calories)});
                            csvWriter.close();
                        }
                    } catch (IOException paramAnonymousBandCaloriesEvent) {
                        Log.e("CSV", paramAnonymousBandCaloriesEvent.toString());
                    }
                } else {
                    file.mkdirs();
                }
            }
        }
    }
}
