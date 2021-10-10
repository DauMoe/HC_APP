package com.example.hc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.security.KeyStore;

import javax.crypto.Cipher;

public class LoadingActivity extends AppCompatActivity {
    AppCompatButton bnLogin, bnsignup;
    ImageView fingerPrint;
    private KeyStore keyStore;
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        bnLogin = findViewById(R.id.loginLoading);
        bnsignup = findViewById(R.id.signupLoading);
        fingerPrint = findViewById(R.id.fingerLoading);
        textView = findViewById(R.id.errorText);

        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                finish();
            }
        });

        bnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoadingActivity.this, SignupActivity.class));
                finish();
            }
        });


    }

}
