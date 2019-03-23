
package com.nasageek.utexasutilities.activities;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//consider showing this as a dialog instead

public class NutritionInfoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getExtras().getString("url");
        String title = getIntent().getExtras().getString("title");
        String formData = getIntent().getExtras().getString("formData");
        String summary = getIntent().getExtras().getString("summary");
        setupActionBar();
        actionBar.setTitle(title);
        final WebView wv = new WebView(this);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        if(formData == null) {
            wv.loadUrl(url);
        } else {
            Log.e("ERROR",formData);
            wv.getSettings().setUseWideViewPort(false);
            wv.postUrl(summary,formData.getBytes());
        }
        setContentView(wv);
    }
}
