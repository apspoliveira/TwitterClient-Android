package com.example.twitterclient;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener {

    private Twitter tweetTwitter;

    public final static String TWIT_KEY = "vMtlFXNct963dznJpbZMBIx15";
    public final static String TWIT_SECRET = "a60uvUjxeYp0zhNsRDuV6lYRySYyzqPOzEbjLQzcYE0uxXMzVh";
    public final static String ACCESS_TOKEN = "517635432-65YUd1CmHUPhgyja1J0WzqCA7Bfu6L4PjclTDVrZ";
    public final static String ACCESS_SECRET = "JWmJ1LjoEW3YiDfnGjGlnBXLmeI6fQG63qQmAkrqx9Dgl";

    private long tweetID = 0;
    private String tweetName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nice_tweet);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupTweet();
    }

    private void setupTweet() {

        Configuration twitConf = new ConfigurationBuilder()
                .setOAuthConsumerKey(TWIT_KEY)
                .setOAuthConsumerSecret(TWIT_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_SECRET)
                .build();

        tweetTwitter = new TwitterFactory(twitConf).getInstance();

        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            tweetID = extras.getLong("tweetID");
            tweetName = extras.getString("tweetUser");

            EditText theReply = (EditText)findViewById(R.id.tweettext);
            theReply.setText("@"+tweetName+" ");
            theReply.setSelection(theReply.getText().length());
        }

        else
        {
            EditText theReply = (EditText)findViewById(R.id.tweettext);
            theReply.setText("");
        }

        LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.homebtn);
        tweetClicker.setOnClickListener(this);

        Button tweetButton = (Button)findViewById(R.id.dotweet);
        tweetButton.setOnClickListener(this);
    }

    public void onClick(final View v) {

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    shareTweet(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void shareTweet(View v){

        final EditText tweetTxt = (EditText) findViewById(R.id.tweettext);

        switch (v.getId()) {
            case R.id.dotweet:
                String toTweet = tweetTxt.getText().toString();
                try {
                    if (tweetName != null && tweetName.length() > 0)
                        tweetTwitter.updateStatus(new StatusUpdate(toTweet).inReplyToStatusId(tweetID));

                    else
                        tweetTwitter.updateStatus(toTweet);

                    tweetTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            tweetTxt.setText("Hello!");
                        }
                    });

                } catch (TwitterException te) {
                    Log.e("", te.getMessage());
                }

                break;
            case R.id.homebtn:
                tweetTxt.post(new Runnable() {
                    @Override
                    public void run() {
                        tweetTxt.setText("Hello!");
                    }
                });
                break;
            default:
                break;
        }
    }
}