package com.example.swifttarrow.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.swifttarrow.nytimessearch.R;
import com.example.swifttarrow.nytimessearch.adapters.ArticlesAdapter;
import com.example.swifttarrow.nytimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.example.swifttarrow.nytimessearch.models.Article;
import com.example.swifttarrow.nytimessearch.utils.NetworkUtils;
import com.example.swifttarrow.nytimessearch.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    RecyclerView rvResults;

    List<Article> articles;
    ArticlesAdapter adapter;

    String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    RequestParams params;

    String beginDate;
    String sortOrder;
    boolean isArts;
    boolean isFashionAndStyle;
    boolean isSports;

    AsyncHttpClient client = new AsyncHttpClient();

    private static final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!NetworkUtils.isNetworkAvailable(getApplicationContext()) || !NetworkUtils.isOnline()){
                    Toast.makeText(getApplicationContext(), "You are not currently connected to the Internet. Please try again later.", Toast.LENGTH_SHORT);
                }

                adapter.clear();

                params = new RequestParams();
                params.put("api-key", "be478aa47a3944c8b8f6637fd6b79f2e");
                params.put("page", 0);
                params.put("q", query);

                if (beginDate != null){
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        params.put("begin_date", DateFormat.format("yyyyMMdd", format.parse(beginDate)));
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                }
                if (sortOrder != null){
                    params.put("sort", sortOrder);
                }
                if (isArts || isFashionAndStyle || isSports){
                    String paramVal = "news_desk:(";
                    List<String> filterOptions = new ArrayList<>();
                    if (isArts) filterOptions.add(StringUtils.surroundWithQuotes(getString(R.string.arts)));
                    if (isFashionAndStyle) filterOptions.add(StringUtils.surroundWithQuotes(getString(R.string.fashion_style)));
                    if (isSports) filterOptions.add(StringUtils.surroundWithQuotes(getString(R.string.sports)));
                    params.put("fq", paramVal + TextUtils.join(" ", filterOptions) + ")");
                }

                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        JSONArray articleJsonResults = null;

                        try {
                            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                            adapter.addAll(Article.fromJSONArray(articleJsonResults));
                            Log.d("DEBUG", articles.toString());
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", throwable.toString());
                        client.get(url, params, this);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; //TODO: Should I add some different behavior here?
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_filter:
                Intent i = new Intent(SearchActivity.this, SearchFilterActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
            case R.id.action_search:


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupViews() {
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticlesAdapter(this, articles);
        rvResults.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(layoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        Log.d("loadNextDataFromApi", "BEING CALLED");
        params.put("page", offset);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", throwable.toString());
                client.get(url, params, this);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            beginDate = data.getExtras().getString("beginDate");
            sortOrder = data.getExtras().getString("sortOrder");
            isArts = data.getExtras().getBoolean("isArts");
            isFashionAndStyle = data.getExtras().getBoolean("isFashionAndStyle");
            isSports = data.getExtras().getBoolean("isSports");
        }
    }

}
