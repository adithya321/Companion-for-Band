package com.pimp.companionforband.activities.cloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CloudActivity extends AppCompatActivity {

    String refreshTokenRequestUrl = "https://login.live.com/oauth20_token.srf?" +
            "client_id=$1" +
            "&redirect_uri=https://login.live.com/oauth20_desktop.srf" +
            "&client_secret=$2" +
            "&refresh_token=$3" +
            "&grant_type=refresh_token";

    String accessToken, refreshToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloud);

        CloudPagerAdapter cloudPagerAdapter;
        ViewPager mViewPager;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null)
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        cloudPagerAdapter = new CloudPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(cloudPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        refreshTokenRequestUrl = refreshTokenRequestUrl
                .replace("$1", getString(R.string.client_id))
                .replace("$2", getString(R.string.client_secret))
                .replace("$3", MainActivity.sharedPreferences.getString("refresh_token", "hi"));

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest tokenRequest = new JsonObjectRequest(Request.Method.GET,
                refreshTokenRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JsonAccessTokenExtractor jate = new JsonAccessTokenExtractor();
                        accessToken = jate.extractAccessToken(response.toString());
                        refreshToken = jate.extractRefreshToken(response.toString());
                        MainActivity.editor.putString("access_token", accessToken);
                        MainActivity.editor.putString("refresh_token", refreshToken);
                        MainActivity.editor.apply();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CloudActivity.this, error.toString(),
                        Toast.LENGTH_LONG).show();
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

        queue.add(tokenRequest);
    }
}