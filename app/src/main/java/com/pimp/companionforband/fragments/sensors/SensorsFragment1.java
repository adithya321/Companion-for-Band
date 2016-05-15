/*package com.pimp.companionforband.fragments.sensors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.band.BandIOException;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.subscription.Band1SubscriptionTask;
import com.pimp.companionforband.utils.band.subscription.Band2SubscriptionTask;
import com.pimp.companionforband.utils.band.subscription.HeartRateSubscriptionTask;
import com.pimp.companionforband.utils.band.subscription.RRIntervalSubscriptionTask;

public class SensorsFragment extends Fragment {

    public static TextView band2TV, logTV, backlogTV, accelerometerTV, altimeterTV, ambientLightTV,
            barometerTV, caloriesTV, contactTV, distanceTV, gsrTV, gyroscopeTV, heartRateTV,
            pedometerTV, rrTV, skinTempTV, uvTV;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backlogTV = (TextView) view.findViewById(R.id.backlogStatus);
        band2TV = (TextView) view.findViewById(R.id.band2TxtStatus);

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

        int[] hzIds = {R.id.accelerometer_switch, R.id.gyroscope_switch, R.id.gsr_switch};
        String[] hzStrings = {"acc", "gyr", "gsr"};
        TextView[] hzTextViews = {accelerometerTV, gyroscopeTV, gsrTV};
        int[] hzRadioGroups = {R.id.accelerometer_radioGroup, R.id.gyroscope_radioGroup, R.id.gsr_radioGroup};
        for (int i = 0; i < hzIds.length; i++)
            setSwitch(view, hzIds[i], hzStrings[i], hzTextViews[i], hzRadioGroups[i]);
    }

    public static void appendToUI(final String string, final TextView textView) {
        try {
            MainActivity.sActivity.runOnUiThread(new Runnable() {
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
}*/