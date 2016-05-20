package com.pimp.companionforband.fragments.cloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pimp.companionforband.R;

import java.util.ArrayList;

public class SummariesFragment extends Fragment {

    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summaries, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(R.id.summaries_list);
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), R.layout.summaries_item_layout, items);
        listView.setAdapter(adapter);
    }
}
