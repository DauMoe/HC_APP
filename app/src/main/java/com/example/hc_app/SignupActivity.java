package com.example.hc_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hc_app.Models.Config.BMI;
import static com.example.hc_app.Models.Config.STEPRANGE;
import static com.example.hc_app.Models.Config.USERNAME;
import static com.example.hc_app.Models.Config.USER_ID;
import static com.example.hc_app.Models.Config.USER_TOKEN;

public class SignupActivity extends AppCompatActivity {

    AppCompatButton btnRegister;
    EditText username, password, repeatPassword;
    TextView login;
    ProgressDialog p;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        login           = findViewById(R.id.login);
        username        = findViewById(R.id.register_username);
        password        = findViewById(R.id.register_password);
        repeatPassword  = findViewById(R.id.repeat_password);
        btnRegister     = findViewById(R.id.register_btn);
        p               = new ProgressDialog(this);
        APIConfig x     = RetrofitConfig.JSONconfig().create(APIConfig.class);

        login.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            String usernameTxt  = username.getText().toString();
            String passwordTxt  = password.getText().toString();
            String repreatTxt   = repeatPassword.getText().toString();

            if (usernameTxt.isEmpty() || passwordTxt.isEmpty() || repreatTxt.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.e("EQUALS: ", String.valueOf(passwordTxt.equals(repreatTxt)));
            if (!passwordTxt.equals(repreatTxt)) {
                Toast.makeText(this, "Password is not match", Toast.LENGTH_SHORT).show();
                return;
            }
            p.setMessage("Progress");
            p.show();
            Map<String, Object> mReq = new ArrayMap<>();
            mReq.put("username", usernameTxt);
            mReq.put("password", passwordTxt);
            mReq.put("roles", 0);
            mReq.put("token", pref.getString(USER_TOKEN, ""));
            RequestBody body = RequestBody
                    .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
            Call<RespObj> g = x.CreateUser(body);
            g.enqueue(new Callback<RespObj>() {
                @Override
                public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                    p.hide();
                    //Login OK
                    if (response.body().getCode() == 200) {
                        Toast.makeText(SignupActivity.this, "Create user success!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    } else {
                        //Another err. Msg will be returned by server
                        Toast.makeText(getApplicationContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                        Log.i("LOGIN:", response.body().getMsg().toString());
                    }
                    //DEBUG AREA
                    Log.i("CODE:", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<RespObj> call, Throwable t) {
                    p.hide();
                    Toast.makeText(getApplicationContext(), String.valueOf(t), Toast.LENGTH_LONG).show();

                    //DEBUG AREA
                    Log.i("CODE:", String.valueOf(call));
                    Log.i("ERR:", String.valueOf(t));
                }
            });
        });
    }
}