package com.pimp.companionforband.fragments.cloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProfileFragment extends Fragment {

    TextView profileTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileTV = (TextView) view.findViewById(R.id.profile_textview);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET,
                CloudConstants.BASE_URL + CloudConstants.Profile_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> stringIterator = response.keys();
                        while (stringIterator.hasNext()) {
                            try {
                                String key = stringIterator.next();
                                profileTV.append(splitCamelCase(key) + " : ");
                                profileTV.append(response.get(key).toString() + "\n\n");
                            } catch (Exception e) {
                                profileTV.append(e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                profileTV.setText(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + MainActivity.sharedPreferences
                        .getString("token", "hi"));

                return headers;
            }
        };

        JsonObjectRequest devicesRequest = new JsonObjectRequest(Request.Method.GET,
                CloudConstants.BASE_URL + CloudConstants.Devices_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> stringIterator = response.keys();
                        while (stringIterator.hasNext()) {
                            try {
                                String key = stringIterator.next();
                                if (key.equals("deviceProfiles")) {
                                    JSONArray jsonArray = response.getJSONArray("deviceProfiles");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject device = jsonArray.getJSONObject(i);
                                        Iterator<String> iterator = device.keys();
                                        while (iterator.hasNext()) {
                                            key = iterator.next();
                                            profileTV.append(splitCamelCase(key) + " : ");
                                            profileTV.append(device.get(key).toString() + "\n");
                                        }
                                        profileTV.append("\n\n");
                                    }
                                } else {
                                    profileTV.append(splitCamelCase(key) + " : ");
                                    profileTV.append(response.get(key).toString() + "\n\n");
                                }
                            } catch (Exception e) {
                                profileTV.append(e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                profileTV.setText(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + MainActivity.sharedPreferences
                        .getString("token", "hi"));

                return headers;
            }
        };

        queue.add(profileRequest);
        queue.add(devicesRequest);
    }

    String splitCamelCase(String s) {
        String str = s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ), " ");
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
