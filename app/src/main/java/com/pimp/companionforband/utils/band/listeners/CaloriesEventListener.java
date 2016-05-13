package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.util.Log;

import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class CaloriesEventListener implements BandCaloriesEventListener {
    @Override
    public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
        if (bandCaloriesEvent != null) {
            if (MainActivity.band2) {
                try {
                    SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.calories_today) + " = " + bandCaloriesEvent.getCaloriesToday() + " kCal\n" +
                            MainActivity.sContext.getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", SensorsFragment.caloriesTV);
                } catch (Exception e) {
                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.caloriesTV);
                }
            } else {
                SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", SensorsFragment.caloriesTV);
            }
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
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
                                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.caloriesTV);
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
