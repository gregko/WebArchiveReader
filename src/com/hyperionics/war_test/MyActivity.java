package com.hyperionics.war_test;

/**
 * Created by Greg Kochaniak, http://www.hyperionics.com
 * License: public domain.
 */
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.io.InputStream;

public class MyActivity extends Activity {

    // Sample WebViewClient in case it was needed...
    // See continueWhenLoaded() sample function for the best place to set it on our webView
    private class MyWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            Lt.d("Web page loaded: " + url);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WebView webView = (WebView) findViewById(R.id.webView);
        try {
            InputStream is = getAssets().open("TestHtmlArchive.xml");
            WebArchiveReader wr = new WebArchiveReader() {
                void onFinished(WebView v) {
                    // we are notified here when the page is fully loaded.
                    continueWhenLoaded(v);
                }
            };
            // To read from a file instead of an asset, use:
            // FileInputStream is = new FileInputStream(fileName);
            if (wr.readWebArchive(is)) {
                wr.loadToWebView(webView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void continueWhenLoaded(WebView webView) {
        Lt.d("Page from WebArchive fully loaded.");
        // If you need to set your own WebViewClient, do it here,
        // after the WebArchive was fully loaded:
        webView.setWebViewClient(new MyWebClient());
        // Any other code we need to execute after loading a page from a WebArchive...
    }
}
