package com.pimp.companionforband.fragments.sensors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.sensors.SampleRate;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.listeners.AccelerometerEventListener;

public class SensorActivity extends AppCompatActivity {
    String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();

        TextView dataTV = (TextView) findViewById(R.id.sensor_data);

        ((AccelerometerEventListener) SensorsFragment.bandAccelerometerEventListener).setTextView(dataTV);
        try {
            switch (sensorName) {
                case "Accelerometer":
                    MainActivity.client.getSensorManager()
                            .registerAccelerometerEventListener(SensorsFragment.bandAccelerometerEventListener, SampleRate.MS128);
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
        TextView sensorNameTV = (TextView) findViewById(R.id.sensor_name);
        sensorNameTV.setText(sensorName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }
}
