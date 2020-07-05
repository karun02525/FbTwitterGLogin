package com.social.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FbActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ImageView img;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Facebook");

//  The following things are deprecated  with the newer facebook sdk
        /*The Facebook SDK is now auto initialized on Application start.
        If you are using the Facebook SDK in the main process and don't need a callback on SDK initialization completion you can now remove calls to FacebookSDK.sdkInitialize.
        If you do need a callback, you should manually invoke the callback in your code.

        Refer to: https://developers.facebook.com/docs/android/upgrading-4x
        */

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);


        //this method is using to get KeyHash which will be used in facebook developer account creation
        // once you register this method is no use further
//        getKeyHash();

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        img = (ImageView) findViewById(R.id.img);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("mmm loginResult", "" + loginResult.getAccessToken());
                getUserInfo(loginResult);
                // App code
            }

            @Override
            public void onCancel() {
                Toast.makeText(FbActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                Log.i("mmm onCancel", "onCancel");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("mmm onError", "onError===" + exception.toString());
            }
        });
    }

    protected void getUserInfo(LoginResult loginResult) {

        txtName = (TextView) findViewById(R.id.txtName);
        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json_object, GraphResponse response) {
                try {
                    JSONObject json = response.getJSONObject();
                    Log.i("mmm json", "" + json.toString());
                    txtName.setText(json.get("name").toString() + "email" + json.get("email").toString());
                    JSONObject profile_pic_data = new JSONObject(json.get("picture").toString());
                    JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                    Log.i("mmm profile_pic ", "" + profile_pic_url.getString("url"));
                    if (!profile_pic_url.getString("url").equalsIgnoreCase("")) {
                        Picasso.with(FbActivity.this).load(profile_pic_url.getString("url")).into(img);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.colan.fblogintest", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
    }
}