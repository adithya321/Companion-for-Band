package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;

import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.fragments.sensors.SensorsFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class PedometerEventListener implements BandPedometerEventListener {
    @Override
    public void onBandPedometerChanged(BandPedometerEvent bandPedometerEvent) {
        if (bandPedometerEvent != null) {
            if (MainActivity.band2) {
                try {
                    SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.steps_today) + " = " + bandPedometerEvent.getStepsToday() + "\n" +
                            MainActivity.sContext.getString(R.string.total_steps) + " = " + bandPedometerEvent.getTotalSteps(), SensorsFragment.pedometerTV);
                } catch (Exception e) {
                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.pedometerTV);
                }
            } else {
                SensorsFragment.appendToUI(MainActivity.sContext.getString(R.string.total_steps) + " = " + bandPedometerEvent.getTotalSteps(), SensorsFragment.pedometerTV);
            }
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Pedometer");
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Pedometer" + File.separator + "Pedometer_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                            String str = DateFormat.getDateTimeInstance().format(bandPedometerEvent.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Pedometer" + File.separator + "Pedometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2) {
                                try {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandPedometerEvent.getTimestamp()),
                                            str, String.valueOf(bandPedometerEvent.getStepsToday()), String.valueOf(bandPedometerEvent.getTotalSteps())});
                                } catch (Exception e) {
                                    SensorsFragment.appendToUI(e.toString(), SensorsFragment.pedometerTV);
                                }
                            } else {
                                csvWriter.writeNext(new String[]{String.valueOf(bandPedometerEvent.getTimestamp()),
                                        str, String.valueOf(bandPedometerEvent.getTotalSteps())});
                            }
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Pedometer" + File.separator + "Pedometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                            if (MainActivity.band2)
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.steps_today), MainActivity.sContext.getString(R.string.total_steps)});
                            else
                                csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp), MainActivity.sContext.getString(R.string.date_time), MainActivity.sContext.getString(R.string.total_steps)});
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
