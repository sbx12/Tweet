package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 100;
    @InjectView(R.id.rvTweets)
    RecyclerView rvTweets;
    @InjectView(R.id.SwipeContainer)
    SwipeRefreshLayout SwipeContainer;

    private TwitterClient client;
    private TweetsAdapter adapter;
    private List<Tweet> tweets;
    private SwipeRefreshLayout swipeContainer;

    private EndlessRecyclerViewScrollListener scrollListener;
    private long oldesttweet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);

        client = TwitterApp.getRestClient(this);

        swipeContainer = findViewById(R.id.SwipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweets = findViewById(R.id.rvTweets);

        //Infinite Pagination Liner
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        rvTweets.addItemDecoration(new DividerItemDecoration(
                rvTweets.getContext(), linearLayoutManager.getOrientation()
        ));

        populateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TwitterClient", "Content Refresh");
                populateHomeTimeline();
            }
        });

        //Infinite Pagination scrollListener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d("TwitterClient", "onLoadMore CALLED");
                loadMoreDate();
            }
        };
        //Infinite Pagination addOnScrollListener
        rvTweets.addOnScrollListener(scrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Pull info from intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Update recycler View
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Twiiter", response.toString());

                List<Tweet> tweetsToAdd = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonTweetObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonTweetObject);
                        tweetsToAdd.add(tweet);
                        checkOldesttweet(tweet.uid);
                        Log.d("Twiiter", String.valueOf(oldesttweet));
                        adapter.notifyItemInserted(tweets.size() - 1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Clear existing data
                adapter.clear();
                // Show the data we just recieved
                adapter.addTweets(tweetsToAdd);
                //Stop refresh signal
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twiiter", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twiiter", errorResponse.toString());
            }
        });
    }

    public void loadMoreDate() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Twiiter", response.toString());

                List<Tweet> tweetsToAdd = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonTweetObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonTweetObject);
                        tweetsToAdd.add(tweet);
                        checkOldesttweet(tweet.uid);
                        Log.d("Twiiter", String.valueOf(oldesttweet));
                        adapter.notifyItemInserted(tweets.size() - 1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Show the data we just recieved
                adapter.addTweets(tweetsToAdd);
                //Stop refresh signal
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twiiter", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twiiter", errorResponse.toString());
            }
        }, oldesttweet);
    }

    private void checkOldesttweet(long newtweet) {
        if (oldesttweet == 0)
            oldesttweet = newtweet - 1;
        else
            oldesttweet = oldesttweet > newtweet ? newtweet - 1 : oldesttweet - 1;
    }
}
