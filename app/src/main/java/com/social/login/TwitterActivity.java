package com.social.login;

import android.content.Intent;
import android.os.Bundle;import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;

public class TwitterActivity extends AppCompatActivity {
    private String TAG = "mmm " + TwitterActivity.class.getSimpleName();
    TwitterLoginButton loginButton;
    TwitterAuthClient twitterAuthClient;
    ImageView img;
    TextView txtName, txtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_twitter);


        setTitle("Twitter");

        img = (ImageView) findViewById(R.id.img);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        initTwitterUI();
    }

    private void initTwitterUI() {

        twitterAuthClient = new TwitterAuthClient();

        loginButton = findViewById(R.id.btnTwitter);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                user.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        String name = userResult.data.name;
                        String email = userResult.data.email;

                        txtName.setText("UserName: " + name);
                        txtEmail.setText("" + email);
                        // _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)
                        String photoUrlNormalSize = userResult.data.profileImageUrl;
                        String photoUrlBiggerSize = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                        String photoUrlMiniSize = userResult.data.profileImageUrl.replace("_normal", "_mini");
                        String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");
                        if (photoUrlOriginalSize != null && photoUrlOriginalSize.length() != 0) {
                            Picasso.with(TwitterActivity.this).load(photoUrlOriginalSize).into(img);
                        }
                    }

                    @Override
                    public void failure(TwitterException exc) {
                        Log.d("TwitterKit", "Verify Credentials Failure", exc);
                    }
                });
                String strUserName = session.getUserName();
                getUserProfilePic(strUserName);

            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(TwitterActivity.this, "TwitterException", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUserProfilePic(String strUserName) {

        String url = "https://avatars.io/twitter/@" + strUserName;
        Log.i("mmm url", "" + url);
        if (!url.equalsIgnoreCase("")) {
            Picasso.with(TwitterActivity.this).load(url).into(img);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}