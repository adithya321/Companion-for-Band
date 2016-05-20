package com.pimp.companionforband.fragments.cloud;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ActivitiesFragment extends Fragment {

    TextView activitiesTV, statusTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activitiesTV = (TextView) view.findViewById(R.id.activities_textview);
        statusTV = (TextView) view.findViewById(R.id.status_textview);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest activitiesRequest = new JsonObjectRequest(Request.Method.GET,
                CloudConstants.BASE_URL + CloudConstants.Activities_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        statusTV.setText("CSV file can be found in CompanionForBand/Activities\n");

                        Iterator<String> stringIterator = response.keys();
                        while (stringIterator.hasNext()) {
                            try {
                                String key = stringIterator.next();
                                JSONArray jsonArray = response.getJSONArray(key);

                                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + File.separator + "CompanionForBand"
                                        + File.separator + "Activities";
                                File file = new File(path);
                                file.mkdirs();

                                JsonFlattener parser = new JsonFlattener();
                                CSVWriter writer = new CSVWriter();
                                try {
                                    List<Map<String, String>> flatJson = parser.parseJson(jsonArray.toString());
                                    writer.writeAsCSV(flatJson, path + File.separator + key + ".csv");
                                } catch (Exception e) {
                                    Log.e("ActivitiesParseJson", e.toString());
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject activity = jsonArray.getJSONObject(i);
                                    Iterator<String> iterator = activity.keys();
                                    while (iterator.hasNext()) {
                                        key = iterator.next();
                                        activitiesTV.append(UIUtils.splitCamelCase(key) + " : ");
                                        activitiesTV.append(activity.get(key).toString() + "\n");
                                    }
                                    activitiesTV.append("\n\n");
                                }
                            } catch (Exception e) {
                                activitiesTV.append(e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activitiesTV.setText(error.getMessage());
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

        queue.add(activitiesRequest);
    }
}
