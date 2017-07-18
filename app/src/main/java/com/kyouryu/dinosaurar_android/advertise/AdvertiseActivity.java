package com.kyouryu.dinosaurar_android.advertise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.kyouryu.dinosaurar_android.R;
import com.kyouryu.dinosaurar_android.Session;
import com.kyouryu.dinosaurar_android.face_tracker.FaceTrackerActivity;
import com.kyouryu.dinosaurar_android.marker.ARMarkerActivity;

/**
 * Created by ty on 2017/07/13.
 */

public class AdvertiseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session.getInstance().setActivity(this);
        Session.getInstance().setContext(this);

        setContentView(R.layout.advertise);

        // webViewの設定
        WebView advertiseWebView = (WebView)findViewById(R.id.ad_web_view);
        advertiseWebView.getSettings().setJavaScriptEnabled(true);
        advertiseWebView.setWebViewClient(new WebViewClient());
        advertiseWebView.loadUrl(getString(R.string.ad_url));

        // ARマーカー画面へ遷移ボタンの設定
        Button toMarkerButton = (Button)findViewById(R.id.to_marker_button);
        toMarkerButton.setOnClickListener(new OnMarkerButtonClickListener());

        // 閉じるボタンの設定
        Button closeButton = (Button)findViewById(R.id.ad_close_button);
        closeButton.setOnClickListener(new OnCloseButtonClickListener());

    }

    private class OnMarkerButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(AdvertiseActivity.this, ARMarkerActivity.class);
            startActivity(intent);
        }
    }

    private class OnCloseButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            closeActivity();
        }
    }

    private void closeActivity() {
        // 最初の画面へ戻る
        Intent intent = new Intent(AdvertiseActivity.this, FaceTrackerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
