package com.pimp.companionforband.activities.cloud;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;
import com.pimp.companionforband.activities.main.ZoomOutPageTransformer;
import com.pimp.companionforband.fragments.cloud.SummariesFragment;
import com.pimp.companionforband.fragments.cloud.WebviewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CloudActivity extends AppCompatActivity {

    static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloud);
        activity = this;

        CloudPagerAdapter cloudPagerAdapter;
        ViewPager mViewPager;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        cloudPagerAdapter = new CloudPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(cloudPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void testClick(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                CloudConstants.BASE_URL + CloudConstants.Profile_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                SummariesFragment.items.add(response.getString("modelName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        SummariesFragment.adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WebviewFragment.mTextView.setText(error.getMessage());
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

        queue.add(jsonObjectRequest);
    }
}