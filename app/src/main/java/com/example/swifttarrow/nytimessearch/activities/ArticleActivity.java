package com.example.swifttarrow.nytimessearch.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.swifttarrow.nytimessearch.models.Article;
import com.example.swifttarrow.nytimessearch.R;
import com.example.swifttarrow.nytimessearch.utils.NetworkUtils;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Article article = (Article) getIntent().getSerializableExtra("article");

        if (!NetworkUtils.isNetworkAvailable(this) || !NetworkUtils.isOnline()){
            Toast.makeText(this, "You are not currently connected to the Internet. Please try again later.", Toast.LENGTH_SHORT);
            finish();
        }

        WebView webView = (WebView) findViewById(R.id.wvArticle);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        webView.loadUrl(article.getWebUrl());
    }
}
