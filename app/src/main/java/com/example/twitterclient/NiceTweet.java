package com.example.twitterclient;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NiceTweet extends Activity implements OnClickListener {

    /**shared preferences for user twitter details*/
    private SharedPreferences tweetPrefs;
    /**twitter object**/
    private Twitter tweetTwitter;

    /**twitter key*/
    public final static String TWIT_KEY = "vMtlFXNct963dznJpbZMBIx15";
    /**twitter secret*/
    public final static String TWIT_SECRET = "a60uvUjxeYp0zhNsRDuV6lYRySYyzqPOzEbjLQzcYE0uxXMzVh";

    public final static String ACCESS_TOKEN = "517635432-65YUd1CmHUPhgyja1J0WzqCA7Bfu6L4PjclTDVrZ";

    public final static String ACCESS_SECRET = "JWmJ1LjoEW3YiDfnGjGlnBXLmeI6fQG63qQmAkrqx9Dgl";

    /**the update ID for this tweet if it is a reply*/
    private long tweetID = 0;
    /**the username for the tweet if it is a reply*/
    private String tweetName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nice_tweet);
    }

    /*
* Call setup method when this activity starts
*/
    @Override
    public void onResume() {
        super.onResume();
        //call helper method
        setupTweet();
    }

    /** Method called whenever this Activity starts
     * - get ready to tweet
     * Sets up twitter and onClick listeners
     * - also sets up for replies
     */
    private void setupTweet() {
        // prepare to tweet

        //get preferences for user twitter details
        tweetPrefs = getSharedPreferences("TwitNicePrefs", 0);

        //get user token and secret for authentication
        String userToken = tweetPrefs.getString("user_token", null);
        String userSecret = tweetPrefs.getString("user_secret", null);

        //create a new twitter configuration using user details
        Configuration twitConf = new ConfigurationBuilder()
                .setOAuthConsumerKey(TWIT_KEY)
                .setOAuthConsumerSecret(TWIT_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_SECRET)
                .build();

        //create a twitter instance
        tweetTwitter = new TwitterFactory(twitConf).getInstance();

        //get any data passed to this intent for a reply
        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            //if there are extras, they represent the tweet to reply to

            //get the ID of the tweet we are replying to
            tweetID = extras.getLong("tweetID");
            //get the user screen name for the tweet we are replying to
            tweetName = extras.getString("tweetUser");

            //get a reference to the text field for tweeting
            EditText theReply = (EditText)findViewById(R.id.tweettext);
            //start the tweet text for the reply @username
            theReply.setText("@"+tweetName+" ");
            //set the cursor to the end of the text for entry
            theReply.setSelection(theReply.getText().length());
        }

        else
        {
            EditText theReply = (EditText)findViewById(R.id.tweettext);
            theReply.setText("");
        }

        //set up listener for choosing home button to go to timeline
        LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.homebtn);
        tweetClicker.setOnClickListener(this);

        //set up listener for send tweet button
        Button tweetButton = (Button)findViewById(R.id.dotweet);
        tweetButton.setOnClickListener(this);
    }

    /**
     * Listener method for button clicks
     * - for home button and send tweet button
     */

    public void onClick(final View v) {
        // handle home and send button clicks

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //Your code goes here
                    shareTweet(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void shareTweet(View v){

        EditText tweetTxt = (EditText) findViewById(R.id.tweettext);
        //find out which view has been clicked
        switch (v.getId()) {
            case R.id.dotweet:
                // send tweet
                String toTweet = tweetTxt.getText().toString();
                try {
                    //is a reply
                    if (tweetName != null && tweetName.length() > 0) {
                        tweetTwitter.updateStatus(new StatusUpdate(toTweet).inReplyToStatusId(tweetID));
                    }
                    //is a normal tweet
                    else {
                        tweetTwitter.updateStatus(toTweet);
                    }
                    //reset the edit text
                    tweetTxt.setText("");
                } catch (TwitterException te) {
                    Log.e("NiceTweet", te.getMessage());
                }

                break;
            case R.id.homebtn:
                // go to the home timeline
                tweetTxt.setText("");
                break;
            default:
                break;
        }
        //finish to go back to home
        finish();
    }
}
