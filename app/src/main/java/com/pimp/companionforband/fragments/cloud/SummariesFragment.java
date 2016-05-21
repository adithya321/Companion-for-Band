package com.pimp.companionforband.fragments.cloud;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.cloud.CloudConstants;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.utils.UIUtils;
import com.pimp.companionforband.utils.jsontocsv.parser.JsonFlattener;
import com.pimp.companionforband.utils.jsontocsv.writer.CSVWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummariesFragment extends Fragment {

    TextView statusTV;
    ListView summariesLV;
    ArrayAdapter<String> stringArrayAdapter;
    ArrayList<String> stringArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summaries, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        summariesLV = (ListView) view.findViewById(R.id.summaries_listview);
        statusTV = (TextView) view.findViewById(R.id.status_textview);
        stringArrayList = new ArrayList<>();
        stringArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activities_list_item,
                R.id.list_item_textView, stringArrayList);
        summariesLV.setAdapter(stringArrayAdapter);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET,
                CloudConstants.BASE_URL + CloudConstants.Summaries_URL
                        + "Daily?startTime=2015-01-01T16%3A04%3A49.8578590-07%3A00", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        statusTV.setText("CSV file can be found in CompanionForBand/Summaries\n");

                        Iterator<String> stringIterator = response.keys();
                        while (stringIterator.hasNext()) {
                            try {
                                String key = stringIterator.next();
                                JSONArray jsonArray = response.getJSONArray(key);

                                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + File.separator + "CompanionForBand"
                                        + File.separator + "Summaries";
                                File file = new File(path);
                                file.mkdirs();

                                JsonFlattener parser = new JsonFlattener();
                                CSVWriter writer = new CSVWriter();
                                try {
                                    List<LinkedHashMap<String, String>> flatJson = parser.parseJson(jsonArray.toString());
                                    writer.writeAsCSV(flatJson, path + File.separator + key + ".csv");
                                } catch (Exception e) {
                                    Log.e("SummariesParseJson", e.toString());
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject activity = jsonArray.getJSONObject(i);
                                    Iterator<String> iterator = activity.keys();
                                    String str = "";
                                    while (iterator.hasNext()) {
                                        key = iterator.next();
                                        str = str + UIUtils.splitCamelCase(key) + " : "
                                                + activity.get(key).toString() + "\n";
                                    }
                                    stringArrayAdapter.add(str);
                                }
                            } catch (Exception e) {
                                Log.e("Summaries", e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + MainActivity.sharedPreferences
                        .getString("access_token", "hi"));

                return headers;
            }
        };

        queue.add(profileRequest);
    }
}
