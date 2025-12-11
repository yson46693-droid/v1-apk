
package com.example.webviewapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    WebView web;
    private static final String TAG = "MainActivity";
    // Change this to your server endpoint where you will register the token (optional)
    private static final String TOKEN_POST_URL = "https://almostafa.free.nf/v1/register_token.php";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        setContentView(web);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        web.setWebViewClient(new WebViewClient());
        // Add JavaScript interface so the web page can call Android functions if needed
        web.addJavascriptInterface(new WebAppInterface(), "Android");

        web.loadUrl("https://almostafa.free.nf/v1/");

        // Get FCM token and send it to your server so your website can trigger notifications
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    // Optionally send token to your website server so it can use it to send notifications via FCM
                    sendTokenToServer(token);
                    // Also inject token into the web page (so site JS can read it if needed)
                    runOnUiThread(() -> web.evaluateJavascript("window.__fcm_token = '" + token + "';", null));
                });
    }

    private void sendTokenToServer(String token) {
        // This runs on background thread to avoid network on main thread - keep simple using new Thread
        new Thread(() -> {
            try {
                URL url = new URL(TOKEN_POST_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(8000);
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String body = "token=" + token;
                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.close();
                int resp = conn.getResponseCode();
                Log.d(TAG, "Token POST response: " + resp);
                conn.disconnect();
            } catch (Exception e) {
                Log.w(TAG, "Failed to post token", e);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // JS interface example (if you want site to call Android.showToast('hi'))
    private class WebAppInterface {
        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
