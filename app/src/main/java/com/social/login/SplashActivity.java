package com.social.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFB, btnTwitter,btnGoogle;
    private Intent intent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btnFB = (Button) findViewById(R.id.btnFB);
        btnTwitter = (Button) findViewById(R.id.btnTwitter);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);

        btnFB.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnFB: {
                intent = new Intent(this, FbActivity.class);
            }
            break;
            case R.id.btnTwitter: {
                intent = new Intent(this, TwitterActivity.class);
            }
            break;
            case R.id.btnGoogle: {
                intent = new Intent(this, GooglePlusActivity.class);
            }
            break;
        }
        startActivity(intent);
    }
}
