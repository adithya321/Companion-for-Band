package com.pimp.companionforband;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandAmbientLightEventListener;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandBarometerEventListener;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;
import com.opencsv.CSVWriter;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

public class SensorsFragment extends Fragment {
    Spinner chart_spinner;
    WeakReference<Activity> reference;
    TextView band2TV;
    TextView logTV, backlogTV, accelerometerTV, altimeterTV, ambientLightTV, barometerTV, caloriesTV, contactTV,
            distanceTV, gsrTV, gyroscopeTV, heartRateTV, pedometerTV, rrTV, skinTempTV, uvTV;
    int permissionCheck;
    ChartAdapter mChartAdapter;
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Acceleration X")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add(event.getAccelerationX());
                        }
                    });
                }
                appendToUI(String.format(" X = %.3f (m/s²) \n Y = %.3f (m/s²)\n Z = %.3f (m/s²)",
                        event.getAccelerationX(),
                        event.getAccelerationY(),
                        event.getAccelerationZ()), accelerometerTV);
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
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), "X", "Y", "Z"});
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
    };
    private BandAltimeterEventListener mAltimeterEventListener = new BandAltimeterEventListener() {
        @Override
        public void onBandAltimeterChanged(final BandAltimeterEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Altimeter")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add(event.getRate());
                        }
                    });
                }
                try {
                    appendToUI(new StringBuilder()
                                    .append(getString(R.string.total_gain_today))
                                    .append(String.format(" = %d cm\n", event.getTotalGainToday()))
                                    .append(getString(R.string.total_gain))
                                    .append(String.format(" = %d cm\n", event.getTotalGain()))
                                    .append(getString(R.string.total_loss))
                                    .append(String.format(" = %d cm\n", event.getTotalLoss()))
                                    .append(getString(R.string.stepping_gain))
                                    .append(String.format(" = %d cm\n", event.getSteppingGain()))
                                    .append(getString(R.string.stepping_loss))
                                    .append(String.format(" = %d cm\n", event.getSteppingLoss()))
                                    .append(getString(R.string.steps_ascended))
                                    .append(String.format(" = %d\n", event.getStepsAscended()))
                                    .append(getString(R.string.steps_descended))
                                    .append(String.format(" = %d\n", event.getStepsDescended()))
                                    .append(getString(R.string.alt_rate))
                                    .append(String.format(" = %f cm/s\n", event.getRate()))
                                    .append(getString(R.string.stairs_ascended_today))
                                    .append(String.format(" = %d\n", event.getFlightsAscendedToday()))
                                    .append(getString(R.string.stairs_ascended))
                                    .append(String.format(" = %d\n", event.getFlightsAscended()))
                                    .append(getString(R.string.stairs_descended))
                                    .append(String.format(" = %d\n", event.getFlightsDescended())).toString()
                            , altimeterTV);

                    if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Altimeter");
                        if (file.exists() || file.isDirectory()) {
                            try {
                                Date date = new Date();
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Altimeter" + File.separator + "Altimeter_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                    String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                                    CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Altimeter" + File.separator + "Altimeter_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                    csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()), str,
                                            String.valueOf(event.getTotalGainToday()),
                                            String.valueOf(event.getTotalGain()),
                                            String.valueOf(event.getTotalLoss()),
                                            String.valueOf(event.getSteppingGain()),
                                            String.valueOf(event.getSteppingLoss()),
                                            String.valueOf(event.getStepsAscended()),
                                            String.valueOf(event.getStepsDescended()),
                                            String.valueOf(event.getRate()),
                                            String.valueOf(event.getFlightsAscendedToday()),
                                            String.valueOf(event.getFlightsAscended()),
                                            String.valueOf(event.getFlightsDescended())});
                                    csvWriter.close();
                                } else {
                                    CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Altimeter" + File.separator + "Altimeter_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.total_gain_today),
                                            getString(R.string.total_gain), getString(R.string.total_loss), getString(R.string.stepping_gain),
                                            getString(R.string.stepping_loss), getString(R.string.steps_ascended), getString(R.string.steps_descended),
                                            getString(R.string.alt_rate), getString(R.string.stairs_ascended_today), getString(R.string.stairs_ascended), getString(R.string.stairs_descended)});
                                    csvWriter.close();
                                }
                            } catch (IOException e) {
                                Log.e("CSV", e.toString());
                            }
                        } else {
                            file.mkdirs();
                        }
                    }
                } catch (Exception e) {
                    appendToUI(e.toString(), altimeterTV);
                }
            }
        }
    };
    private BandAmbientLightEventListener mAmbientLightEventListener = new BandAmbientLightEventListener() {
        @Override
        public void onBandAmbientLightChanged(final BandAmbientLightEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Ambient Light")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add((float) event.getBrightness());
                        }
                    });
                }
                appendToUI(getString(R.string.brightness) + String.format(" = %d lux\n", event.getBrightness()), ambientLightTV);
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "AmbientLight");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "AmbientLight" + File.separator + "AmbientLight_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "AmbientLight" + File.separator + "AmbientLight_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()),
                                        str, String.valueOf(event.getBrightness())});
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "AmbientLight" + File.separator + "AmbientLight_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.brightness)});
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
    };
    private BandBarometerEventListener mBarometerEventListener = new BandBarometerEventListener() {
        @Override
        public void onBandBarometerChanged(final BandBarometerEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Barometer")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add((float) event.getAirPressure());
                        }
                    });
                }
                appendToUI(getString(R.string.air_pressure) + String.format(" = %.3f hPa\n", event.getAirPressure())
                                + getString(R.string.air_temperature) + String.format(" = %.2f °C = %.2f F",
                        event.getTemperature(), 1.8 * event.getTemperature() + 32),
                        barometerTV);
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Barometer");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Barometer" + File.separator + "Barometer_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Barometer" + File.separator + "Barometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()),
                                        str, String.valueOf(event.getAirPressure()),
                                        String.valueOf(event.getTemperature())});
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Barometer" + File.separator + "Barometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.air_pressure), getString(R.string.air_temperature)});
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
    };
    private BandCaloriesEventListener bandCaloriesEventListener = new BandCaloriesEventListener() {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
            if (bandCaloriesEvent != null) {
                if (MainActivity.band2) {
                    try {
                        appendToUI(getString(R.string.calories_today) + " = " + bandCaloriesEvent.getCaloriesToday() + " kCal\n" +
                                getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", caloriesTV);
                    } catch (Exception e) {
                        appendToUI(e.toString(), caloriesTV);
                    }
                } else {
                    appendToUI(getString(R.string.calories) + " = " + bandCaloriesEvent.getCalories() + " kCal", caloriesTV);
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
                                        appendToUI(e.toString(), caloriesTV);
                                    }
                                } else {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandCaloriesEvent.getTimestamp()),
                                            str, String.valueOf(bandCaloriesEvent.getCalories())});
                                }
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Calories" + File.separator + "Calories_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                if (MainActivity.band2)
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.calories_today), getString(R.string.calories)});
                                else
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.calories)});
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
    };
    private BandContactEventListener bandContactEventListener = new BandContactEventListener() {
        @Override
        public void onBandContactChanged(BandContactEvent bandContactEvent) {
            if (bandContactEvent != null) {
                appendToUI(getString(R.string.contact_status) + " = " + bandContactEvent.getContactState(), contactTV);
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Contact");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "Contact" + File.separator + "Contact_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(bandContactEvent.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Contact" + File.separator + "Contact_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{String.valueOf(bandContactEvent.getTimestamp()), str,
                                        String.valueOf(bandContactEvent.getContactState())});
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Contact" + File.separator + "Contact_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.contact_status)});
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
    };
    private BandDistanceEventListener bandDistanceEventListener = new BandDistanceEventListener() {
        @Override
        public void onBandDistanceChanged(final BandDistanceEvent bandDistanceEvent) {
            if (bandDistanceEvent != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Pace")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add(bandDistanceEvent.getPace());
                        }
                    });
                }
                if (MainActivity.band2) {
                    try {
                        appendToUI(getString(R.string.motion_type) + " = " + bandDistanceEvent.getMotionType() +
                                "\n" + getString(R.string.pace) + " (ms/m) = " + bandDistanceEvent.getPace() +
                                "\n" + getString(R.string.speed) + " (cm/s) = " + bandDistanceEvent.getSpeed() +
                                "\n" + getString(R.string.distance_today) + " = " + bandDistanceEvent.getDistanceToday() / 100000L +
                                " km\n" + getString(R.string.total_distance) + " = " + bandDistanceEvent.getTotalDistance() / 100000L +
                                " km", distanceTV);
                    } catch (Exception e) {
                        appendToUI(e.toString(), distanceTV);
                    }
                } else {
                    appendToUI(getString(R.string.motion_type) + " = " + bandDistanceEvent.getMotionType() +
                            "\n" + getString(R.string.pace) + " (ms/m) = " + bandDistanceEvent.getPace() +
                            "\n" + getString(R.string.speed) + " (cm/s) = " + bandDistanceEvent.getSpeed() +
                            "\n" + getString(R.string.total_distance) + " = " + bandDistanceEvent.getTotalDistance() / 100000L +
                            " km", distanceTV);
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
                                        appendToUI(e.toString(), distanceTV);
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
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.motion_type), getString(R.string.pace), getString(R.string.speed), getString(R.string.distance_today), getString(R.string.total_distance)});
                                else
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.motion_type), getString(R.string.pace), getString(R.string.speed), getString(R.string.total_distance)});
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
    };
    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("GSR")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add((float) event.getResistance());
                        }
                    });
                }
                appendToUI(getString(R.string.resistance) + String.format(" = %d kOhms\n", event.getResistance()), gsrTV);
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "GSR");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "GSR" + File.separator + "GSR_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(event.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "GSR" + File.separator + "GSR_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{String.valueOf(event.getTimestamp()),
                                        str, String.valueOf(event.getResistance())});
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "GSR" + File.separator + "GSR_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.resistance)});
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
    };
    private BandGyroscopeEventListener bandGyroscopeEventListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(final BandGyroscopeEvent bandGyroscopeEvent) {
            if (bandGyroscopeEvent != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Angular Velocity X")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add(bandGyroscopeEvent.getAngularVelocityX());
                        }
                    });
                }
                appendToUI(String.format("X = %.3f (m/s²) \nY = %.3f (m/s²)\nZ = %.3f (m/s²)\n" +
                                "X = %.3f (°/sec)\nY = %.3f (°/sec)\nZ = %.3f (°/sec)",
                        bandGyroscopeEvent.getAccelerationX(),
                        bandGyroscopeEvent.getAccelerationY(),
                        bandGyroscopeEvent.getAccelerationZ(),
                        bandGyroscopeEvent.getAngularVelocityX(),
                        bandGyroscopeEvent.getAngularVelocityY(),
                        bandGyroscopeEvent.getAngularVelocityZ()),
                        gyroscopeTV);
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
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), "X m/s²", "Y", "Z", "X °/sec", "Y", "Z"});
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
    };
    View.OnClickListener sampleRateRadioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (v.getId()) {
                    case R.id.accelerometer_ms16:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                mAccelerometerEventListener, SampleRate.MS16);
                        editor.putInt("acc_hz", R.id.accelerometer_ms16);
                        break;
                    case R.id.accelerometer_ms32:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                mAccelerometerEventListener, SampleRate.MS32);
                        editor.putInt("acc_hz", R.id.accelerometer_ms32);
                        break;
                    case R.id.accelerometer_ms128:
                        MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                mAccelerometerEventListener, SampleRate.MS128);
                        editor.putInt("acc_hz", R.id.accelerometer_ms128);
                        break;
                    case R.id.gyroscope_ms16:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                bandGyroscopeEventListener, SampleRate.MS16);
                        editor.putInt("gyr_hz", R.id.gyroscope_ms16);
                        break;
                    case R.id.gyroscope_ms32:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                bandGyroscopeEventListener, SampleRate.MS32);
                        editor.putInt("gyr_hz", R.id.gyroscope_ms32);
                        break;
                    case R.id.gyroscope_ms128:
                        MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                bandGyroscopeEventListener, SampleRate.MS128);
                        editor.putInt("gyr_hz", R.id.gyroscope_ms128);
                        break;
                    case R.id.gsr_ms200:
                        if (MainActivity.band2)
                            MainActivity.client.getSensorManager().registerGsrEventListener(
                                    mGsrEventListener, GsrSampleRate.MS200);
                        editor.putInt("gsr_hz", R.id.gsr_ms200);
                        break;
                    case R.id.gsr_ms5000:
                        if (MainActivity.band2)
                            MainActivity.client.getSensorManager().registerGsrEventListener(
                                    mGsrEventListener, GsrSampleRate.MS5000);
                        editor.putInt("gsr_hz", R.id.gsr_ms5000);
                        break;
                }
                editor.apply();
            } catch (Exception e) {
                //
            }
        }
    };
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Heart Rate")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add((float) event.getHeartRate());
                        }
                    });
                }
                appendToUI(getString(R.string.heart_rate) + String.format(" = %d ", event.getHeartRate())
                        + getString(R.string.beats_per_minute) + "\n" + getString(R.string.quality)
                        + String.format(" = %s", event.getQuality()), heartRateTV);
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
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.heart_rate), getString(R.string.quality)});
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
    };
    private BandPedometerEventListener bandPedometerEventListener = new BandPedometerEventListener() {
        @Override
        public void onBandPedometerChanged(BandPedometerEvent bandPedometerEvent) {
            if (bandPedometerEvent != null) {
                if (MainActivity.band2) {
                    try {
                        appendToUI(getString(R.string.steps_today) + " = " + bandPedometerEvent.getStepsToday() + "\n" +
                                getString(R.string.total_steps) + " = " + bandPedometerEvent.getTotalSteps(), pedometerTV);
                    } catch (Exception e) {
                        appendToUI(e.toString(), pedometerTV);
                    }
                } else {
                    appendToUI(getString(R.string.total_steps) + " = " + bandPedometerEvent.getTotalSteps(), pedometerTV);
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
                                        appendToUI(e.toString(), pedometerTV);
                                    }
                                } else {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandPedometerEvent.getTimestamp()),
                                            str, String.valueOf(bandPedometerEvent.getTotalSteps())});
                                }
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "Pedometer" + File.separator + "Pedometer_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                if (MainActivity.band2)
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.steps_today), getString(R.string.total_steps)});
                                else
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.total_steps)});
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
    };
    private BandRRIntervalEventListener mRRIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(final BandRRIntervalEvent event) {
            if (event != null) {
                if (chart_spinner.getSelectedItem().toString().equals("RR Interval")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add((float) event.getInterval());
                        }
                    });
                }
                appendToUI(getString(R.string.rr) + String.format(" = %.3f s\n", event.getInterval()), rrTV);
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
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.rr)});
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
    };
    private BandSkinTemperatureEventListener bandSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(final BandSkinTemperatureEvent bandSkinTemperatureEvent) {
            if (bandSkinTemperatureEvent != null) {
                if (chart_spinner.getSelectedItem().toString().equals("Skin Temperature")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChartAdapter.add(bandSkinTemperatureEvent.getTemperature());
                        }
                    });
                }
                appendToUI(getString(R.string.temperature) + String.format(" = " + bandSkinTemperatureEvent.getTemperature() + " °C" + " = %.2f F",
                        1.8 * bandSkinTemperatureEvent.getTemperature() + 32), skinTempTV);
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "SkinTemperature");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "SkinTemperature" + File.separator + "SkinTemperature_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(bandSkinTemperatureEvent.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "SkinTemperature" + File.separator + "SkinTemperature_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{String.valueOf(bandSkinTemperatureEvent.getTimestamp()),
                                        str, String.valueOf(bandSkinTemperatureEvent.getTemperature())});
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "SkinTemperature" + File.separator + "SkinTemperature_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.temperature)});
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
    };
    private BandUVEventListener bandUVEventListener = new BandUVEventListener() {
        @Override
        public void onBandUVChanged(final BandUVEvent bandUVEvent) {
            if (bandUVEvent != null) {
                if (MainActivity.band2) {
                    try {
                        appendToUI(getString(R.string.uv_today) + " = " + bandUVEvent.getUVExposureToday() + "\n" +
                                getString(R.string.uv_index) + " = " + bandUVEvent.getUVIndexLevel(), uvTV);
                    } catch (Exception e) {
                        appendToUI(e.toString(), uvTV);
                    }
                } else {
                    appendToUI(getString(R.string.uv_index) + " = " + bandUVEvent.getUVIndexLevel(), uvTV);
                }
                if (MainActivity.sharedPreferences.getBoolean("log", false)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "UV");
                    if (file.exists() || file.isDirectory()) {
                        try {
                            Date date = new Date();
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CompanionForBand" + File.separator + "UV" + File.separator + "UV_" + DateFormat.getDateInstance().format(date) + ".csv").exists()) {
                                String str = DateFormat.getDateTimeInstance().format(bandUVEvent.getTimestamp());
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "UV" + File.separator + "UV_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                if (MainActivity.band2) {
                                    try {
                                        csvWriter.writeNext(new String[]{String.valueOf(bandUVEvent.getTimestamp()),
                                                str, String.valueOf(bandUVEvent.getUVExposureToday()), String.valueOf(bandUVEvent.getUVIndexLevel())});
                                    } catch (Exception e) {
                                        appendToUI(e.toString(), uvTV);
                                    }
                                } else {
                                    csvWriter.writeNext(new String[]{String.valueOf(bandUVEvent.getTimestamp()),
                                            str, String.valueOf(bandUVEvent.getUVIndexLevel())});
                                }
                                csvWriter.close();
                            } else {
                                CSVWriter csvWriter = new CSVWriter(new FileWriter(path + File.separator + "CompanionForBand" + File.separator + "UV" + File.separator + "UV_" + DateFormat.getDateInstance().format(date) + ".csv", true));
                                if (MainActivity.band2)
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.uv_today), getString(R.string.uv_index)});
                                else
                                    csvWriter.writeNext(new String[]{getString(R.string.timestamp), getString(R.string.date_time), getString(R.string.uv_index)});
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
    };
    private TextView scrubInfoTextView;

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        logTV = (TextView) view.findViewById(R.id.logStatus);
        if (permissionCheck == PackageManager.PERMISSION_DENIED)
            logTV.setText(getResources().getString(R.string.permit_log));
        else
            logTV.setText(getResources().getString(R.string.log_text));

        backlogTV = (TextView) view.findViewById(R.id.backlogStatus);
        band2TV = (TextView) view.findViewById(R.id.band2TxtStatus);
        accelerometerTV = (TextView) view.findViewById(R.id.accelerometerStatus);
        altimeterTV = (TextView) view.findViewById(R.id.altimeterStatus);
        ambientLightTV = (TextView) view.findViewById(R.id.lightStatus);
        barometerTV = (TextView) view.findViewById(R.id.barometerStatus);
        caloriesTV = (TextView) view.findViewById(R.id.caloriesStatus);
        contactTV = (TextView) view.findViewById(R.id.contactStatus);
        distanceTV = (TextView) view.findViewById(R.id.distanceStatus);
        gsrTV = (TextView) view.findViewById(R.id.gsrStatus);
        gyroscopeTV = (TextView) view.findViewById(R.id.gyroscopeStatus);
        heartRateTV = (TextView) view.findViewById(R.id.heartStatus);
        pedometerTV = (TextView) view.findViewById(R.id.pedometerStatus);
        rrTV = (TextView) view.findViewById(R.id.rrStatus);
        skinTempTV = (TextView) view.findViewById(R.id.temperatureStatus);
        uvTV = (TextView) view.findViewById(R.id.UVStatus);

        final Switch SheartStatus = (Switch) view.findViewById(R.id.heart_rate_switch);
        SheartStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SheartStatus.isChecked()) {
                    MainActivity.editor.putBoolean("heart", false);
                    MainActivity.editor.apply();
                    heartRateTV.setVisibility(View.GONE);
                    try {
                        MainActivity.client.getSensorManager().unregisterHeartRateEventListeners();
                    } catch (Exception e) {
                        //
                    }
                } else {
                    MainActivity.editor.putBoolean("heart", true);
                    MainActivity.editor.apply();
                    heartRateTV.setVisibility(View.VISIBLE);
                    new HeartRateSubscriptionTask().execute();
                }
            }
        });

        final Switch SrrStatus = (Switch) view.findViewById(R.id.rr_switch);
        SrrStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SrrStatus.isChecked()) {
                    MainActivity.editor.putBoolean("rr", false);
                    MainActivity.editor.apply();
                    rrTV.setVisibility(View.GONE);
                    try {
                        MainActivity.client.getSensorManager().unregisterRRIntervalEventListeners();
                    } catch (Exception e) {
                        //
                    }
                } else {
                    MainActivity.editor.putBoolean("rr", true);
                    MainActivity.editor.apply();
                    rrTV.setVisibility(View.VISIBLE);
                    new RRIntervalSubscriptionTask().execute();
                }
            }
        });

        ((Switch) view.findViewById(R.id.log_switch)).setChecked(MainActivity.sharedPreferences.getBoolean("log", false));
        if (!MainActivity.sharedPreferences.getBoolean("log", false))
            view.findViewById(R.id.logStatus).setVisibility(View.GONE);
        else {
            view.findViewById(R.id.logStatus).setVisibility(View.VISIBLE);
            view.findViewById(R.id.backlog_switch).setVisibility(View.VISIBLE);
        }
        ((Switch) view.findViewById(R.id.backlog_switch)).setChecked(MainActivity.sharedPreferences.getBoolean("backlog", false));
        if (!MainActivity.sharedPreferences.getBoolean("backlog", false))
            view.findViewById(R.id.backlogStatus).setVisibility(View.GONE);
        else
            view.findViewById(R.id.backlogStatus).setVisibility(View.VISIBLE);

        ((Switch) view.findViewById(R.id.heart_rate_switch)).setChecked(MainActivity.sharedPreferences.getBoolean("heart", true));
        if (!MainActivity.sharedPreferences.getBoolean("heart", true))
            view.findViewById(R.id.heartStatus).setVisibility(View.GONE);
        else {
            new HeartRateSubscriptionTask().execute();
            view.findViewById(R.id.heartStatus).setVisibility(View.VISIBLE);
        }
        ((Switch) view.findViewById(R.id.rr_switch)).setChecked(MainActivity.sharedPreferences.getBoolean("rr", true));
        if (!MainActivity.sharedPreferences.getBoolean("rr", true))
            view.findViewById(R.id.rrStatus).setVisibility(View.GONE);
        else {
            new RRIntervalSubscriptionTask().execute();
            view.findViewById(R.id.rrStatus).setVisibility(View.VISIBLE);
        }

        int[] ids = {R.id.altimeter_switch, R.id.light_switch, R.id.barometer_switch,
                R.id.calories_switch, R.id.contact_switch, R.id.distance_switch,
                R.id.pedometer_switch, R.id.temperature_switch, R.id.UV_switch};
        String[] strings = {"alt", "light", "bar", "cal", "con", "dis", "ped", "tem", "uv"};
        TextView[] textViews = {altimeterTV, ambientLightTV, barometerTV, caloriesTV, contactTV,
                distanceTV, pedometerTV, skinTempTV, uvTV};

        for (int i = 0; i < ids.length; i++) setSwitch(view, ids[i], strings[i], textViews[i]);
        reference = new WeakReference<Activity>(getActivity());

        int[] hzIds = {R.id.accelerometer_switch, R.id.gyroscope_switch, R.id.gsr_switch};
        String[] hzStrings = {"acc", "gyr", "gsr"};
        TextView[] hzTextViews = {accelerometerTV, gyroscopeTV, gsrTV};
        int[] hzRadioGroups = {R.id.accelerometer_radioGroup, R.id.gyroscope_radioGroup, R.id.gsr_radioGroup};
        for (int i = 0; i < hzIds.length; i++)
            setSwitch(view, hzIds[i], hzStrings[i], hzTextViews[i], hzRadioGroups[i]);

        new Band2SubscriptionTask().execute();
        new Band1SubscriptionTask().execute();

        SparkView sparkView = (SparkView) view.findViewById(R.id.sparkview);

        mChartAdapter = new ChartAdapter();
        sparkView.setAdapter(mChartAdapter);
        sparkView.setScrubListener(new SparkView.OnScrubListener() {
            @Override
            public void onScrubbed(Object value) {
                if (value == null) {
                    scrubInfoTextView.setText(R.string.scrub_empty);
                } else {
                    scrubInfoTextView.setText(getString(R.string.scrub_format, value));
                }
            }
        });

        scrubInfoTextView = (TextView) view.findViewById(R.id.scrub_info_textview);
        chart_spinner = (Spinner) view.findViewById(R.id.chart_spinner);

        view.findViewById(R.id.accelerometer_ms16).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.accelerometer_ms32).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.accelerometer_ms128).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.gyroscope_ms16).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.gyroscope_ms32).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.gyroscope_ms128).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.gsr_ms200).setOnClickListener(sampleRateRadioButtonClickListener);
        view.findViewById(R.id.gsr_ms5000).setOnClickListener(sampleRateRadioButtonClickListener);
    }

    private void appendToUI(final String string, final TextView textView) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(string);
                }
            });
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
        if (!sharedPreferences.getBoolean("backlog", false)) {
            if (MainActivity.client != null) {
                try {
                    MainActivity.client.getSensorManager().unregisterAllListeners();
                } catch (BandIOException e) {
                    MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = MainActivity.sharedPreferences;

        if (sharedPreferences.getBoolean("rr", true))
            new RRIntervalSubscriptionTask().execute();

        if (sharedPreferences.getBoolean("heart", true))
            new HeartRateSubscriptionTask().execute();

        new Band1SubscriptionTask().execute();
        new Band2SubscriptionTask().execute();
    }

    private void setSwitch(View view, int id, String string, TextView textView) {
        ((Switch) view.findViewById(id)).setChecked(MainActivity.sharedPreferences.getBoolean(string, true));
        if (!MainActivity.sharedPreferences.getBoolean(string, true))
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);
    }

    private void setSwitch(View view, int id, String string, TextView textView, int radioGroupId) {
        int radioButtonId = MainActivity.sharedPreferences.getInt(string + "_hz", R.id.accelerometer_ms128);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(radioGroupId);
        RadioButton radioButton = (RadioButton) view.findViewById(radioButtonId);
        radioButton.setChecked(true);

        ((Switch) view.findViewById(id)).setChecked(MainActivity.sharedPreferences.getBoolean(string, true));
        if (!MainActivity.sharedPreferences.getBoolean(string, true)) {
            textView.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
        }
    }

    public static class ChartAdapter extends SparkAdapter {
        private final float[] yData;
        int i = 0;

        public ChartAdapter() {
            yData = new float[50];
        }

        public void add(Float y) {
            yData[i] = y;
            i = (i < 49) ? i + 1 : 0;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }

        @Override
        public float getX(int index) {
            return super.getX(index);
        }

        @Override
        public RectF getDataBounds() {
            return super.getDataBounds();
        }

        @Override
        public boolean hasBaseLine() {
            return false;
        }

        @Override
        public float getBaseLine() {
            return super.getBaseLine();
        }
    }

    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (MainActivity.getConnectedBandClient()) {
                    if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        MainActivity.client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void run() {
                                new HeartRateConsentTask().execute(reference);
                            }
                        });
                        appendToUI(getString(R.string.heart_rate_consent) + "\n", heartRateTV);
                    }
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }
            } catch (BandException e) {
                MainActivity.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (MainActivity.getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        MainActivity.client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }
            } catch (BandException e) {
                MainActivity.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class RRIntervalSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (MainActivity.getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(MainActivity.client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                            MainActivity.client.getSensorManager().registerRRIntervalEventListener(mRRIntervalEventListener);
                        } else {
                            appendToUI(getString(R.string.heart_rate_consent) + "\n", rrTV);
                        }
                    }
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }
            } catch (BandException e) {
                MainActivity.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class Band1SubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (MainActivity.getConnectedBandClient()) {
                    switch (MainActivity.sharedPreferences.getInt("acc_hz", R.id.accelerometer_ms128)) {
                        case R.id.accelerometer_ms16:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
                            break;
                        case R.id.accelerometer_ms32:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS32);
                            break;
                        default:
                            MainActivity.client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
                    }

                    switch (MainActivity.sharedPreferences.getInt("gyr_hz", R.id.gyroscope_ms128)) {
                        case R.id.gyroscope_ms16:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(bandGyroscopeEventListener, SampleRate.MS16);
                            break;
                        case R.id.gyroscope_ms32:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(bandGyroscopeEventListener, SampleRate.MS32);
                            break;
                        default:
                            MainActivity.client.getSensorManager().registerGyroscopeEventListener(bandGyroscopeEventListener, SampleRate.MS128);
                    }

                    MainActivity.client.getSensorManager().registerCaloriesEventListener(bandCaloriesEventListener);
                    MainActivity.client.getSensorManager().registerContactEventListener(bandContactEventListener);
                    MainActivity.client.getSensorManager().registerDistanceEventListener(bandDistanceEventListener);
                    MainActivity.client.getSensorManager().registerPedometerEventListener(bandPedometerEventListener);
                    MainActivity.client.getSensorManager().registerSkinTemperatureEventListener(bandSkinTemperatureEventListener);
                    MainActivity.client.getSensorManager().registerUVEventListener(bandUVEventListener);
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }
            } catch (BandException e) {
                MainActivity.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }

    private class Band2SubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (MainActivity.getConnectedBandClient()) {
                    if (MainActivity.band2) {
                        MainActivity.client.getSensorManager().registerAltimeterEventListener(mAltimeterEventListener);
                        MainActivity.client.getSensorManager().registerAmbientLightEventListener(mAmbientLightEventListener);
                        MainActivity.client.getSensorManager().registerBarometerEventListener(mBarometerEventListener);

                        if (MainActivity.sharedPreferences.getInt("gsr_hz", R.id.gsr_ms200) == R.id.gsr_ms200)
                            MainActivity.client.getSensorManager().registerGsrEventListener(mGsrEventListener, GsrSampleRate.MS200);
                        else
                            MainActivity.client.getSensorManager().registerGsrEventListener(mGsrEventListener, GsrSampleRate.MS5000);

                        appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                                + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await(), band2TV);
                    } else {
                        appendToUI("Firmware Version : " + MainActivity.client.getFirmwareVersion().await()
                                + "\nHardware Version : " + MainActivity.client.getHardwareVersion().await()
                                + "\n" + getString(R.string.band_2_required), band2TV);
                    }
                } else {
                    MainActivity.appendToUI(getString(R.string.band_not_found), "Style.ALERT");
                }
            } catch (BandException e) {
                MainActivity.handleBandException(e);
            } catch (Exception e) {
                MainActivity.appendToUI(e.getMessage(), "Style.ALERT");
            }
            return null;
        }
    }
}