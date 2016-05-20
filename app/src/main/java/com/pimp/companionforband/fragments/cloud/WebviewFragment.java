package com.pimp.companionforband.fragments.cloud;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.pimp.companionforband.R;
import com.pimp.companionforband.activities.cloud.JsonAccessTokenExtractor;
import com.pimp.companionforband.activities.main.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebviewFragment extends Fragment {

    public static TextView mTextView;
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
    String accessToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        codeRequestUrl = codeRequestUrl
                .replace("$1", getString(R.string.client_id));
        tokenRequestUrl = tokenRequestUrl
                .replace("$1", getString(R.string.client_id))
                .replace("$2", getString(R.string.client_secret));

        WebView myWebView = (WebView) view.findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(codeRequestUrl);

        mTextView = (TextView) view.findViewById(R.id.textView);
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
            accessToken = jate.extract(result);
            mTextView.setText(accessToken);

            MainActivity.editor.putString("token", accessToken);
            MainActivity.editor.apply();
        }
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
}
