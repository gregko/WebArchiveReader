package com.hyperionics.war_test;

/**
 * Created by Greg Kochaniak, http://www.hyperionics.com
 * License: public domain.
 */
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.*;

public class MyActivity extends Activity {

    WebView webView;

    // Sample WebViewClient in case it was needed...
    // See continueWhenLoaded() sample function for the best place to set it on our webView
    private class MyWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            Lt.d("Web page loaded: " + url);
            super.onPageFinished(view, url);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webView = (WebView) findViewById(R.id.webView);

        // tests...
        webView.setWebViewClient(new MyWebClient());
        webView.getSettings().setJavaScriptEnabled(true);


        //webView.loadUrl("http://www.sme.sk/");
        //webView.loadUrl("http://e-pao.org/");
        //webView.loadUrl("http://www.androidauthority.com/who-will-be-the-next-samsung-147873/");
        //webView.loadUrl("http://www.chicagotribune.com/news/local/breaking/chi-chief-keef-jailed-20130115,0,6580088.story");
        //webView.loadUrl("https://twitter.com/kennykerr/status/290981527293132800");

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArchive("SavedArchive.xml");
            }
        });

        try {
            InputStream is = getAssets().open("TestHtmlArchive.xml");
            //InputStream is = getAssets().open("1373985294472.xml");
            //FileInputStream is = new FileInputStream("/sdcard/Hyperionics/SavedArchive.xml");
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

    public void saveArchive(String fileName){
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/Hyperionics/");
            if(!dir.exists()){
                dir.mkdirs();
            }
            webView.saveWebArchive(dir.toString() + File.separator + fileName);
            //mWebView.saveWebArchive(filename);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
