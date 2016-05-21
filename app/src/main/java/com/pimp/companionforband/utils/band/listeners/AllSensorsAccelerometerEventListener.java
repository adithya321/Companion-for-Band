package com.pimp.companionforband.utils.band.listeners;

import android.os.Environment;
import android.util.Log;

import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.opencsv.CSVWriter;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Date;

public class AllSensorsAccelerometerEventListener implements BandAccelerometerEventListener {

    @Override
    public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
        if (event != null) {
            if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "CompanionForBand" + File.separator + "All-in-One";
                File file = new File(folderPath);
                if (file.exists() || file.isDirectory()) {
                    try {
                        Date date = new Date();
                        String filePath = folderPath + File.separator + "All-in-One_"
                                + DateFormat.getDateInstance().format(date) + ".csv";
                        if (new File(filePath).exists()) {
                            String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true));
                            if (MainActivity.band2) {
                                csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()), str,
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationX()),
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationY()),
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationZ()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getTotalGainToday()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getTotalGain()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getTotalLoss()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getSteppingGain()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getSteppingLoss()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getStepsAscended()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getStepsDescended()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getRate()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getFlightsAscendedToday()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getFlightsAscended()),
                                        String.valueOf(MainActivity.bandSensorData.getAltimeterData().getFlightsDescended()),
                                        String.valueOf(MainActivity.bandSensorData.getAmbientLightData().getBrightness()),
                                        String.valueOf(MainActivity.bandSensorData.getBarometerData().getAirPressure()),
                                        String.valueOf(MainActivity.bandSensorData.getBarometerData().getTemperature()),
                                        String.valueOf(MainActivity.bandSensorData.getCalorieData().getCaloriesToday()),
                                        String.valueOf(MainActivity.bandSensorData.getCalorieData().getCalories()),
                                        String.valueOf(MainActivity.bandSensorData.getContactData().getContactState()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getMotionType()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getPace()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getSpeed()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getDistanceToday()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getTotalDistance()),
                                        String.valueOf(MainActivity.bandSensorData.getGsrData().getResistance()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationX()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationY()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationZ()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityX()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityY()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityZ()),
                                        String.valueOf(MainActivity.bandSensorData.getHeartRateData().getHeartRate()),
                                        String.valueOf(MainActivity.bandSensorData.getHeartRateData().getQuality()),
                                        String.valueOf(MainActivity.bandSensorData.getPedometerData().getStepsToday()),
                                        String.valueOf(MainActivity.bandSensorData.getPedometerData().getTotalSteps()),
                                        String.valueOf(MainActivity.bandSensorData.getRrIntervalData().getInterval()),
                                        String.valueOf(MainActivity.bandSensorData.getSkinTemperatureData().getTemperature()),
                                        String.valueOf(MainActivity.bandSensorData.getUvIndexData().getUVExposureToday()),
                                        String.valueOf(MainActivity.bandSensorData.getUvIndexData().getUVIndexLevel())});
                            } else {
                                csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()), str,
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationX()),
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationY()),
                                        String.valueOf(MainActivity.bandSensorData.getAccelerometerData().getAccelerationZ()),
                                        "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only",
                                        "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only",
                                        "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only", "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getCalorieData().getCalories()),
                                        String.valueOf(MainActivity.bandSensorData.getContactData().getContactState()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getMotionType()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getPace()),
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getSpeed()),
                                        "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getDistanceData().getTotalDistance()),
                                        "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationX()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationY()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAccelerationZ()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityX()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityY()),
                                        String.valueOf(MainActivity.bandSensorData.getGyroscopeData().getAngularVelocityZ()),
                                        String.valueOf(MainActivity.bandSensorData.getHeartRateData().getHeartRate()),
                                        String.valueOf(MainActivity.bandSensorData.getHeartRateData().getQuality()),
                                        "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getPedometerData().getTotalSteps()),
                                        "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getSkinTemperatureData().getTemperature()),
                                        "Band 2 only",
                                        String.valueOf(MainActivity.bandSensorData.getUvIndexData().getUVIndexLevel())});
                            }
                            csvWriter.close();
                        } else {
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true));
                            csvWriter.writeNext(new String[]{MainActivity.sContext.getString(R.string.timestamp),
                                    MainActivity.sContext.getString(R.string.date_time), "X", "Y", "Z",
                                    MainActivity.sContext.getString(R.string.total_gain_today),
                                    MainActivity.sContext.getString(R.string.total_gain),
                                    MainActivity.sContext.getString(R.string.total_loss),
                                    MainActivity.sContext.getString(R.string.stepping_gain),
                                    MainActivity.sContext.getString(R.string.stepping_loss),
                                    MainActivity.sContext.getString(R.string.steps_ascended),
                                    MainActivity.sContext.getString(R.string.steps_descended),
                                    MainActivity.sContext.getString(R.string.alt_rate),
                                    MainActivity.sContext.getString(R.string.stairs_ascended_today),
                                    MainActivity.sContext.getString(R.string.stairs_ascended),
                                    MainActivity.sContext.getString(R.string.stairs_descended),
                                    MainActivity.sContext.getString(R.string.brightness),
                                    MainActivity.sContext.getString(R.string.air_pressure),
                                    MainActivity.sContext.getString(R.string.air_temperature),
                                    MainActivity.sContext.getString(R.string.calories_today),
                                    MainActivity.sContext.getString(R.string.calories),
                                    MainActivity.sContext.getString(R.string.contact_status),
                                    MainActivity.sContext.getString(R.string.motion_type),
                                    MainActivity.sContext.getString(R.string.pace),
                                    MainActivity.sContext.getString(R.string.speed),
                                    MainActivity.sContext.getString(R.string.distance_today),
                                    MainActivity.sContext.getString(R.string.total_distance),
                                    MainActivity.sContext.getString(R.string.resistance),
                                    "X m/s²", "Y", "Z", "X °/sec", "Y", "Z",
                                    MainActivity.sContext.getString(R.string.heart_rate),
                                    MainActivity.sContext.getString(R.string.quality),
                                    MainActivity.sContext.getString(R.string.steps_today),
                                    MainActivity.sContext.getString(R.string.total_steps),
                                    MainActivity.sContext.getString(R.string.rr),
                                    MainActivity.sContext.getString(R.string.temperature),
                                    MainActivity.sContext.getString(R.string.uv_today),
                                    MainActivity.sContext.getString(R.string.uv_index)});
                            csvWriter.close();
                        }
                    } catch (Exception e) {
                        Log.e("All-in-One CSV", e.toString());
                    }
                } else {
                    file.mkdirs();
                }
            }
        }
    }
}
