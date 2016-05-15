package com.pimp.companionforband.fragments.sensors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.SampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.listeners.AccelerometerEventListener;
import com.pimp.companionforband.utils.band.listeners.GsrEventListener;
import com.pimp.companionforband.utils.band.listeners.GyroscopeEventListener;

public class SensorActivity extends AppCompatActivity {

    String sensorName;
    TextView nameTV, dataTV, detailsTV;
    CardView optionsCV;
    RadioButton option1, option2, option3;
    public static GraphView graphView;
    public static LineGraphSeries<DataPoint> series1, series2, series3;
    public static double graphLastValueX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();

        try {
            switch (sensorName) {
                case "Accelerometer":
                    setAccelerometerConfiguration();
                    break;
                case "Gyroscope":
                    setGyroscopeConfiguration();
                    break;
                case "GSR":
                    setGSRConfiguration();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
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
        graphView = (GraphView) findViewById(R.id.sensor_graph);
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

        series1 = new LineGraphSeries<>();
        series1.setColor(getResources().getColor(R.color.accent));
        series2 = new LineGraphSeries<>();
        series2.setColor(getResources().getColor(R.color.primary_dark));
        series3 = new LineGraphSeries<>();
        series3.setColor(getResources().getColor(R.color.primary_light));
        graphView.addSeries(series1);
        graphView.addSeries(series2);
        graphView.addSeries(series3);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

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

    void setAccelerometerConfiguration() throws Exception {
        ((AccelerometerEventListener) SensorsFragment.bandAccelerometerEventListener)
                .setViews(dataTV, true);
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
        detailsTV.setText(getString(R.string.accelerometer_details));
    }

    void setGyroscopeConfiguration() throws Exception {
        ((GyroscopeEventListener) SensorsFragment.bandGyroscopeEventListener)
                .setViews(dataTV, true);
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
        detailsTV.setText(getString(R.string.gyroscope_details));
    }

    void setGSRConfiguration() throws Exception {
        ((GsrEventListener) SensorsFragment.bandGsrEventListener)
                .setViews(dataTV, true);
        if (MainActivity.sharedPreferences.getInt("gsr_hz", 31) == 31)
            MainActivity.client.getSensorManager().registerGsrEventListener(
                    SensorsFragment.bandGsrEventListener, GsrSampleRate.MS200);
        else
            MainActivity.client.getSensorManager().registerGsrEventListener(
                    SensorsFragment.bandGsrEventListener, GsrSampleRate.MS5000);
        detailsTV.setText(getString(R.string.gsr_details));
    }
}
