package com.pimp.companionforband.fragments.sensors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandAmbientLightEventListener;
import com.microsoft.band.sensors.BandBarometerEventListener;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEventListener;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.band.listeners.AccelerometerEventListener;
import com.pimp.companionforband.utils.band.listeners.CaloriesEventListener;
import com.pimp.companionforband.utils.band.listeners.ContactEventListener;
import com.pimp.companionforband.utils.band.listeners.DistanceEventListener;
import com.pimp.companionforband.utils.band.listeners.GyroscopeEventListener;
import com.pimp.companionforband.utils.band.listeners.HeartRateEventListener;
import com.pimp.companionforband.utils.band.listeners.PedometerEventListener;
import com.pimp.companionforband.utils.band.listeners.SkinTemperatureEventListener;
import com.pimp.companionforband.utils.band.listeners.UVEventListener;
import com.pimp.companionforband.utils.band.subscription.Band1SubscriptionTask;

import java.lang.ref.WeakReference;

public class SensorsFragment extends Fragment {

    public static Spinner chart_spinner;
    public static WeakReference<Activity> reference;
    public static TextView band2TV, logTV, backlogTV, accelerometerTV, altimeterTV, ambientLightTV,
            barometerTV, caloriesTV, contactTV, distanceTV, gsrTV, gyroscopeTV, heartRateTV,
            pedometerTV, rrTV, skinTempTV, uvTV;
    public static LineGraphSeries<DataPoint> series1, series2, series3;
    public static double graphLastValueX = 0;

    public static CardView accelerometerCard, caloriesCard, contactCard, distanceCard, gyroscopeCard,
            heartRateCard, pedometerCard, skinTemperatureCard, uvCard;

    public static BandAccelerometerEventListener bandAccelerometerEventListener;
    public static BandAltimeterEventListener bandAltimeterEventListener;
    public static BandAmbientLightEventListener bandAmbientLightEventListener;
    public static BandBarometerEventListener bandBarometerEventListener;
    public static BandCaloriesEventListener bandCaloriesEventListener;
    public static BandContactEventListener bandContactEventListener;
    public static BandDistanceEventListener bandDistanceEventListener;
    public static BandGsrEventListener bandGsrEventListener;
    public static BandGyroscopeEventListener bandGyroscopeEventListener;
    public static BandHeartRateEventListener bandHeartRateEventListener;
    public static BandPedometerEventListener bandPedometerEventListener;
    public static BandRRIntervalEventListener bandRRIntervalEventListener;
    public static BandSkinTemperatureEventListener bandSkinTemperatureEventListener;
    public static BandUVEventListener bandUVEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accelerometerCard = (CardView) view.findViewById(R.id.accelerometer_card);
        caloriesCard = (CardView) view.findViewById(R.id.calories_card);
        contactCard = (CardView) view.findViewById(R.id.contact_card);
        distanceCard = (CardView) view.findViewById(R.id.distance_card);
        gyroscopeCard = (CardView) view.findViewById(R.id.gyroscope_card);
        heartRateCard = (CardView) view.findViewById(R.id.heartRate_card);
        pedometerCard = (CardView) view.findViewById(R.id.pedometer_card);
        skinTemperatureCard = (CardView) view.findViewById(R.id.skinTemperature_card);
        uvCard = (CardView) view.findViewById(R.id.uv_card);

        CardView[] cardViews = {accelerometerCard, caloriesCard, contactCard, distanceCard,
                gyroscopeCard, heartRateCard, pedometerCard, skinTemperatureCard, uvCard};
        String[] sensors = {"Accelerometer", "Calories", "Contact", "Distance", "Gyroscope",
                "Heart Rate", "Pedometer", "SkinTemperature", "UV"};

        bandAccelerometerEventListener = new AccelerometerEventListener((TextView) caloriesCard.findViewById(R.id.txtDetails));
        bandCaloriesEventListener = new CaloriesEventListener((TextView) caloriesCard.findViewById(R.id.txtDetails));
        bandContactEventListener = new ContactEventListener((TextView) contactCard.findViewById(R.id.txtDetails));
        bandDistanceEventListener = new DistanceEventListener((TextView) distanceCard.findViewById(R.id.txtDetails));
        bandGyroscopeEventListener = new GyroscopeEventListener((TextView) gyroscopeCard.findViewById(R.id.txtDetails));
        bandHeartRateEventListener = new HeartRateEventListener((TextView) heartRateCard.findViewById(R.id.txtDetails));
        bandPedometerEventListener = new PedometerEventListener((TextView) pedometerCard.findViewById(R.id.txtDetails));
        bandSkinTemperatureEventListener = new SkinTemperatureEventListener((TextView) skinTemperatureCard.findViewById(R.id.txtDetails));
        bandUVEventListener = new UVEventListener((TextView) uvCard.findViewById(R.id.txtDetails));

        for (int i = 0; i < cardViews.length; i++) {
            ((TextView) cardViews[i].findViewById(R.id.txtName)).setText(sensors[i]);
            cardViews[i].setOnClickListener(cardViewOnClickListener);
        }
    }

    View.OnClickListener cardViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView sensorName = (TextView) v.findViewById(R.id.txtName);
            Intent intent = new Intent(getContext(), SensorActivity.class);
            intent.putExtra("sensor_name", sensorName.getText().toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = getResources().getString(R.string.transition_sensor_name);

                ActivityOptions transitionActivityOptions = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(), sensorName, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            } else {
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
            }
        }
    };

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
        try {
            MainActivity.client.getSensorManager().unregisterAllListeners();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AccelerometerEventListener) bandAccelerometerEventListener).setTextView((TextView) accelerometerCard.findViewById(R.id.txtDetails));
        ((CaloriesEventListener) bandCaloriesEventListener).setTextView((TextView) caloriesCard.findViewById(R.id.txtDetails));
        ((ContactEventListener) bandContactEventListener).setTextView((TextView) contactCard.findViewById(R.id.txtDetails));
        ((DistanceEventListener) bandDistanceEventListener).setTextView((TextView) distanceCard.findViewById(R.id.txtDetails));
        ((GyroscopeEventListener) bandGyroscopeEventListener).setTextView((TextView) gyroscopeCard.findViewById(R.id.txtDetails));
        ((HeartRateEventListener) bandHeartRateEventListener).setTextView((TextView) heartRateCard.findViewById(R.id.txtDetails));
        ((PedometerEventListener) bandPedometerEventListener).setTextView((TextView) pedometerCard.findViewById(R.id.txtDetails));
        ((SkinTemperatureEventListener) bandSkinTemperatureEventListener).setTextView((TextView) skinTemperatureCard.findViewById(R.id.txtDetails));
        ((UVEventListener) bandUVEventListener).setTextView((TextView) uvCard.findViewById(R.id.txtDetails));

        new Band1SubscriptionTask().execute();
    }
}