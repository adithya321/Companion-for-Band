package com.pimp.companionforband.activities.cloud;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.main.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebviewActivity extends AppCompatActivity {

    String codeRequestUrl = "https://login.live.com/oauth20_authorize.srf?" +
            "client_id=$1" +
            "&scope=mshealth.ReadProfile mshealth.ReadActivityHistory mshealth.ReadDevices " +
            "mshealth.ReadActivityLocation offline_access" +
            "&response_type=code" +
            "&redirect_uri=https://login.live.com/oauth20_desktop.srf";

    String tokenRequestUrl = "https://login.live.com/oauth20_token.srf?" +
            "client_id=$1" +
            "&redirect_uri=https://login.live.com/oauth20_desktop.srf" +
            "&client_secret=$2" +
            "&grant_type=authorization_code";

    String code;
    String accessToken, refreshToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        codeRequestUrl = codeRequestUrl
                .replace("$1", getString(R.string.client_id));
        tokenRequestUrl = tokenRequestUrl
                .replace("$1", getString(R.string.client_id))
                .replace("$2", getString(R.string.client_secret));

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(codeRequestUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private String downloadUrl(String url) throws IOException {
        InputStream is = null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            return readIt(is, 9999);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).toString().contains(".srf?code=")) {
                code = url.substring(url.indexOf("code=") + 5, url.lastIndexOf("&"));

                String post = tokenRequestUrl + "&code=" + code;
                new DownloadWebpageTask().execute(post);
            }
            return false;
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            JsonAccessTokenExtractor jate = new JsonAccessTokenExtractor();
            accessToken = jate.extractAccessToken(result);
            refreshToken = jate.extractRefreshToken(result);
            if (accessToken != null) {
                Toast.makeText(getApplicationContext(), "Authentication Successful",
                        Toast.LENGTH_LONG).show();

                MainActivity.editor.putString("access_token", accessToken);
                MainActivity.editor.putString("refresh_token", refreshToken);
                MainActivity.editor.apply();

                startActivity(new Intent(getApplicationContext(), CloudActivity.class));
            } else
                Toast.makeText(getApplicationContext(), "Authentication Failure",
                        Toast.LENGTH_LONG).show();
        }
    }
}
