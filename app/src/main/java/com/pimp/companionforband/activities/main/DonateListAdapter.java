package com.pimp.companionforband.activities.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pimp.companionforband.R;

public class DonateListAdapter extends BaseAdapter {
    String[] title, amount;
    Context context;
    private LayoutInflater inflater;

    public DonateListAdapter(MainActivity mainActivity, String[] name, String[] price) {
        title = name;
        amount = price;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.donate_row, null);
        holder.tv2 = (TextView) rowView.findViewById(R.id.text11);
        holder.tv2.setText(amount[position]);
        holder.tv1 = (TextView) rowView.findViewById(R.id.text12);
        holder.tv1.setText(title[position]);
        return rowView;
    }

    public class Holder {
        TextView tv1, tv2;
    }
}
