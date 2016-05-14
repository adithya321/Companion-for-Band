package com.pimp.companionforband.fragments.sensors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimp.companionforband.R;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<String> list;
    private Context context;

    @Override
    public void onBindViewHolder(SensorViewHolder holder, int position) {
        String s = list.get(position);
        holder.vName.setText(s);
        holder.vDetails.setText("DETAILS");
        holder.vIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.band));
        setButtonEvents(holder);
    }

    public SensorAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setButtonEvents(SensorViewHolder sensorViewHolder) {
        final ImageView icon = sensorViewHolder.vIcon;
        final CardView cardView = sensorViewHolder.vCard;

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;

                Intent intent = new Intent(context, SensorActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String transitionName = context.getResources()
                            .getString(R.string.transition_sensor_icon);

                    ActivityOptions transitionActivityOptions = ActivityOptions
                            .makeSceneTransitionAnimation(activity, icon, transitionName);
                    context.startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
                }
            }
        });
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View sensorAdapterView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sensor_layout, viewGroup, false);
        return new SensorViewHolder(sensorAdapterView);
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vDetails;
        public ImageView vIcon;
        protected CardView vCard;

        public SensorViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txtName);
            vDetails = (TextView) v.findViewById(R.id.txtDetails);
            vIcon = (ImageView) v.findViewById(R.id.sensorIcon);
            vCard = (CardView) v.findViewById(R.id.sensor_card);
        }
    }
}