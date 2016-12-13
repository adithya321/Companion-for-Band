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

package com.pimp.companionforband.fragments.sensors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.SampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.listeners.AccelerometerEventListener;
import com.pimp.companionforband.utils.band.listeners.AltimeterEventListener;
import com.pimp.companionforband.utils.band.listeners.AmbientLightEventListener;
import com.pimp.companionforband.utils.band.listeners.BarometerEventListener;
import com.pimp.companionforband.utils.band.listeners.CaloriesEventListener;
import com.pimp.companionforband.utils.band.listeners.ContactEventListener;
import com.pimp.companionforband.utils.band.listeners.DistanceEventListener;
import com.pimp.companionforband.utils.band.listeners.GsrEventListener;
import com.pimp.companionforband.utils.band.listeners.GyroscopeEventListener;
import com.pimp.companionforband.utils.band.listeners.HeartRateEventListener;
import com.pimp.companionforband.utils.band.listeners.PedometerEventListener;
import com.pimp.companionforband.utils.band.listeners.RRIntervalEventListener;
import com.pimp.companionforband.utils.band.listeners.SkinTemperatureEventListener;
import com.pimp.companionforband.utils.band.listeners.UVEventListener;
import com.pimp.companionforband.utils.band.subscription.HeartRateConsentTask;
import com.robinhood.spark.SparkView;

public class SensorActivity extends AppCompatActivity {

    public static ChartAdapter chartAdapter;
    String sensorName;
    TextView nameTV, dataTV, detailsTV, scrubInfoTextView;
    CardView optionsCV, graphCV;
    SparkView sparkView;
    RadioButton option1, option2, option3;
    View.OnClickListener optionsRadioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (v.getId()) {
                    case R.id.option_1:
                        switch (sensorName) {
                            case "Accelerometer":
                                MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                        SensorsFragment.bandAccelerometerEventListener, SampleRate.MS16);
                                editor.putInt("acc_hz", 11);
                                break;
                            case "Gyroscope":
                                MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                        SensorsFragment.bandGyroscopeEventListener, SampleRate.MS16);
                                editor.putInt("gyr_hz", 21);
                                break;
                            case "GSR":
                                if (MainActivity.band2)
                                    MainActivity.client.getSensorManager().registerGsrEventListener(
                                            SensorsFragment.bandGsrEventListener, GsrSampleRate.MS200);
                                editor.putInt("gsr_hz", 31);
                                break;
                        }
                        editor.apply();
                        break;

                    case R.id.option_2:
                        switch (sensorName) {
                            case "Accelerometer":
                                MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                        SensorsFragment.bandAccelerometerEventListener, SampleRate.MS32);
                                editor.putInt("acc_hz", 12);
                                break;
                            case "Gyroscope":
                                MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                        SensorsFragment.bandGyroscopeEventListener, SampleRate.MS32);
                                editor.putInt("gyr_hz", 22);
                                break;
                            case "GSR":
                                if (MainActivity.band2)
                                    MainActivity.client.getSensorManager().registerGsrEventListener(
                                            SensorsFragment.bandGsrEventListener, GsrSampleRate.MS5000);
                                editor.putInt("gsr_hz", 32);
                                break;
                        }
                        editor.apply();
                        break;

                    case R.id.option_3:
                        switch (sensorName) {
                            case "Accelerometer":
                                MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                                        SensorsFragment.bandAccelerometerEventListener, SampleRate.MS128);
                                editor.putInt("acc_hz", 13);
                                break;
                            case "Gyroscope":
                                MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                                        SensorsFragment.bandGyroscopeEventListener, SampleRate.MS128);
                                editor.putInt("gyr_hz", 23);
                                break;
                        }
                        editor.apply();
                        break;
                }
            } catch (Exception e) {
                //
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
        setSensorConfiguration();
    }

    private void getInitialConfiguration() {
        sensorName = getIntent().getStringExtra("sensor_name");
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (toolbar != null)
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
    }

    private void setScreenElements() {
        nameTV = (TextView) findViewById(R.id.sensor_name);
        dataTV = (TextView) findViewById(R.id.sensor_data);
        optionsCV = (CardView) findViewById(R.id.options_card);
        option1 = (RadioButton) findViewById(R.id.option_1);
        option2 = (RadioButton) findViewById(R.id.option_2);
        option3 = (RadioButton) findViewById(R.id.option_3);
        graphCV = (CardView) findViewById(R.id.graph_card);
        sparkView = (SparkView) findViewById(R.id.sensor_graph);
        detailsTV = (TextView) findViewById(R.id.sensor_details);

        nameTV.setText(sensorName);

        option1.setOnClickListener(optionsRadioButtonClickListener);
        option2.setOnClickListener(optionsRadioButtonClickListener);
        option3.setOnClickListener(optionsRadioButtonClickListener);

        switch (sensorName) {
            case "Accelerometer":
                optionsCV.setVisibility(View.VISIBLE);
                option1.setChecked(MainActivity.sharedPreferences.getInt("acc_hz", 13) == 11);
                option2.setChecked(MainActivity.sharedPreferences.getInt("acc_hz", 13) == 12);
                option3.setChecked(MainActivity.sharedPreferences.getInt("acc_hz", 13) == 13);
                break;
            case "Gyroscope":
                optionsCV.setVisibility(View.VISIBLE);
                option1.setChecked(MainActivity.sharedPreferences.getInt("gyr_hz", 23) == 21);
                option2.setChecked(MainActivity.sharedPreferences.getInt("gyr_hz", 23) == 22);
                option3.setChecked(MainActivity.sharedPreferences.getInt("gyr_hz", 23) == 23);
                break;
            case "GSR":
                optionsCV.setVisibility(View.VISIBLE);
                option1.setOnClickListener(optionsRadioButtonClickListener);
                option1.setChecked(MainActivity.sharedPreferences.getInt("gsr_hz", 31) == 31);
                option2.setChecked(MainActivity.sharedPreferences.getInt("gsr_hz", 31) == 32);
                option2.setOnClickListener(optionsRadioButtonClickListener);
                option1.setText("200 Hz");
                option2.setText("5000 Hz");
                option3.setVisibility(View.GONE);
                break;
        }

        scrubInfoTextView = (TextView) findViewById(R.id.scrub_info_textview);
        chartAdapter = new ChartAdapter();
        sparkView.setAdapter(chartAdapter);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    void setAccelerometerConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.accelerometer_details));
        ((AccelerometerEventListener) SensorsFragment.bandAccelerometerEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            switch (MainActivity.sharedPreferences.getInt("acc_hz", 13)) {
                case 11:
                    MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                            SensorsFragment.bandAccelerometerEventListener, SampleRate.MS16);
                    break;
                case 12:
                    MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                            SensorsFragment.bandAccelerometerEventListener, SampleRate.MS32);
                    break;
                default:
                    MainActivity.client.getSensorManager().registerAccelerometerEventListener(
                            SensorsFragment.bandAccelerometerEventListener, SampleRate.MS128);
            }
    }

    void setAltimeterConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.altimeter_details));
        ((AltimeterEventListener) SensorsFragment.bandAltimeterEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerAltimeterEventListener(
                    SensorsFragment.bandAltimeterEventListener);
    }

    void setAmbientLightConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.ambientLight_details));
        ((AmbientLightEventListener) SensorsFragment.bandAmbientLightEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerAmbientLightEventListener(
                    SensorsFragment.bandAmbientLightEventListener);
    }

    void setBarometerConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.barometer_details));
        ((BarometerEventListener) SensorsFragment.bandBarometerEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerBarometerEventListener(
                    SensorsFragment.bandBarometerEventListener);
    }

    void setCaloriesConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.calories_details));
        ((CaloriesEventListener) SensorsFragment.bandCaloriesEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerCaloriesEventListener(
                    SensorsFragment.bandCaloriesEventListener);
    }

    void setContactConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.contact_details));
        ((ContactEventListener) SensorsFragment.bandContactEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerContactEventListener(
                    SensorsFragment.bandContactEventListener);
    }

    void setDistanceConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.distance_details));
        ((DistanceEventListener) SensorsFragment.bandDistanceEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerDistanceEventListener(
                    SensorsFragment.bandDistanceEventListener);
    }

    void setGsrConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.gsr_details));
        ((GsrEventListener) SensorsFragment.bandGsrEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            if (MainActivity.sharedPreferences.getInt("gsr_hz", 31) == 31)
                MainActivity.client.getSensorManager().registerGsrEventListener(
                        SensorsFragment.bandGsrEventListener, GsrSampleRate.MS200);
            else
                MainActivity.client.getSensorManager().registerGsrEventListener(
                        SensorsFragment.bandGsrEventListener, GsrSampleRate.MS5000);
    }

    void setGyroscopeConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.gyroscope_details));
        ((GyroscopeEventListener) SensorsFragment.bandGyroscopeEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            switch (MainActivity.sharedPreferences.getInt("gyr_hz", 23)) {
                case 21:
                    MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                            SensorsFragment.bandGyroscopeEventListener, SampleRate.MS16);
                    break;
                case 22:
                    MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                            SensorsFragment.bandGyroscopeEventListener, SampleRate.MS32);
                    break;
                default:
                    MainActivity.client.getSensorManager().registerGyroscopeEventListener(
                            SensorsFragment.bandGyroscopeEventListener, SampleRate.MS128);
            }
    }

    void setHeartRateConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.heartRate_details));
        ((HeartRateEventListener) SensorsFragment.bandHeartRateEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                MainActivity.client.getSensorManager().registerHeartRateEventListener(SensorsFragment.bandHeartRateEventListener);
            } else {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        new HeartRateConsentTask().execute(SensorsFragment.reference);
                    }
                });
                detailsTV.setText(getString(R.string.heart_rate_consent));
            }
    }

    void setPedometerConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.pedometer_details));
        ((PedometerEventListener) SensorsFragment.bandPedometerEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerPedometerEventListener(
                    SensorsFragment.bandPedometerEventListener);
    }

    void setRRIntervalConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.rrInterval_details));
        ((RRIntervalEventListener) SensorsFragment.bandRRIntervalEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            if (MainActivity.client.getSensorManager().getCurrentHeartRateConsent()
                    == UserConsent.GRANTED) {
                MainActivity.client.getSensorManager()
                        .registerRRIntervalEventListener(SensorsFragment.bandRRIntervalEventListener);
            } else {
                MainActivity.sActivity.runOnUiThread(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        new HeartRateConsentTask().execute(SensorsFragment.reference);
                    }
                });
                detailsTV.setText(getString(R.string.heart_rate_consent));
            }
    }

    void setSkinTemperatureConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.skinTemperature_details));
        ((SkinTemperatureEventListener) SensorsFragment.bandSkinTemperatureEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerSkinTemperatureEventListener(
                    SensorsFragment.bandSkinTemperatureEventListener);
    }

    void setUVConfiguration() throws Exception {
        detailsTV.setText(getString(R.string.uv_details));
        ((UVEventListener) SensorsFragment.bandUVEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true))
            MainActivity.client.getSensorManager().registerUVEventListener(
                    SensorsFragment.bandUVEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (MainActivity.sharedPreferences.getBoolean(sensorName, true)) {
            menu.findItem(R.id.action_hide)
                    .setIcon(getResources().getDrawable(R.drawable.ic_visibility_off_white_48dp));
            menu.findItem(R.id.action_hide).setTitle(getString(R.string.action_hide));
        } else {
            menu.findItem(R.id.action_hide)
                    .setIcon(getResources().getDrawable(R.drawable.ic_visibility_white_48dp));
            menu.findItem(R.id.action_hide).setTitle(getString(R.string.action_show));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hide:
                if (item.getTitle().equals(getString(R.string.action_hide))) {
                    MainActivity.editor.putBoolean(sensorName, false);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_visibility_white_48dp));
                    item.setTitle(getString(R.string.action_show));
                } else {
                    MainActivity.editor.putBoolean(sensorName, true);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_visibility_off_white_48dp));
                    item.setTitle(getString(R.string.action_hide));
                }
                MainActivity.editor.apply();
                try {
                    MainActivity.client.getSensorManager().unregisterAllListeners();
                } catch (Exception e) {
                    //
                }
                setSensorConfiguration();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setSensorConfiguration() {
        try {
            switch (sensorName) {
                case "Accelerometer":
                    setAccelerometerConfiguration();
                    break;
                case "Altimeter":
                    setAltimeterConfiguration();
                    break;
                case "Ambient Light":
                    setAmbientLightConfiguration();
                    break;
                case "Barometer":
                    setBarometerConfiguration();
                    break;
                case "Calories":
                    setCaloriesConfiguration();
                    break;
                case "Contact":
                    setContactConfiguration();
                    break;
                case "Distance":
                    setDistanceConfiguration();
                    break;
                case "GSR":
                    setGsrConfiguration();
                    break;
                case "Gyroscope":
                    setGyroscopeConfiguration();
                    break;
                case "Heart Rate":
                    setHeartRateConfiguration();
                    break;
                case "Pedometer":
                    setPedometerConfiguration();
                    break;
                case "RR Interval":
                    setRRIntervalConfiguration();
                    break;
                case "Skin Temperature":
                    setSkinTemperatureConfiguration();
                    break;
                case "UV":
                    setUVConfiguration();
                    break;
            }
        } catch (Exception e) {
            //
        }
    }
}
