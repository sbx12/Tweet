package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 140;
    @InjectView(R.id.etCompose)
    EditText etCompose;
    @InjectView(R.id.tvCounter)
    TextView tvCounter;
    @InjectView(R.id.btnTweet)
    Button btnTweet;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.inject(this);

        client = TwitterApp.getRestClient(this);

        //Set Click Listner for API CALL

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCounter.setText(String.valueOf(140 - s.toString().length()));
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    return;
                }

                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    return;
                }
                client.composeTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            Tweet tweet = Tweet.fromJson(response);
                            Intent data = new Intent();
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, data);
                            Log.d("Twitter", "OYEAHH");
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Twitter", "ONNNNOOOOOO");
                    }
                });
            }
        });
    }
}
