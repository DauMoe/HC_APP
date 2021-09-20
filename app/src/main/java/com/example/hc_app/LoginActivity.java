package com.example.hc_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.EditText;
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

import static com.example.hc_app.Models.Config.*;

public class LoginActivity extends AppCompatActivity {
    AppCompatButton login_btn;
    EditText username, password;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref        = getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);

        login_btn   = findViewById(R.id.login_btn);
        username    = findViewById(R.id.login_email);
        password    = findViewById(R.id.login_password);

        APIConfig x = RetrofitConfig.JSONconfig().create(APIConfig.class);

        login_btn.setOnClickListener(v -> {
            String userTxt  = username.getText().toString().trim();
            String passTxt  = password.getText().toString().trim();

            //Check empty
            if (userTxt.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fill username!", Toast.LENGTH_LONG).show();
                return;
            }
            if (passTxt.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fill password!", Toast.LENGTH_LONG).show();
                return;
            }

            //Create request body
            //Doc: https://stackoverflow.com/questions/21398598/how-to-post-raw-whole-json-in-the-body-of-a-retrofit-request
            Map<String, String> mReq = new ArrayMap<>();
            mReq.put("username", userTxt);
            mReq.put("password", passTxt);
            RequestBody body = RequestBody
                    .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());

            //Create call API
            DisableBtn(true);
            Call<RespObj> g = x.login(body);
            g.enqueue(new Callback<RespObj>() {
                @Override
                public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                    if (response.body().getCode() == 200) {
                        //Login OK
                        try {
                            //Convert response msg to JSON
                            JSONObject a = new JSONObject(String.valueOf(response.body().getMsg().get(0)));
                            if (
                                //Store token and username to SharedPreferences
                                    pref.edit().putInt(USER_ID, a.getInt("userID")).commit() &&
                                    pref.edit().putString(USER_TOKEN, a.getString("token")).commit() &&
                                    pref.edit().putString(USERNAME, a.getString("username")).commit()
                            ) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            //Convert failed exception
                            Toast.makeText(getApplicationContext(), "Can't convert response to JSONObject", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //Another err. Msg will be returned by server
                        DisableBtn(false);
                        Toast.makeText(getApplicationContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                    }
                    //DEBUG AREA
                    Log.i("LOGIN:", response.body().getMsg().toString());
                    Log.i("CODE:", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<RespObj> call, Throwable t) {
                    DisableBtn(false);
                    Toast.makeText(getApplicationContext(), "Call API failed!", Toast.LENGTH_LONG).show();

                    //DEBUG AREA
                    Log.i("CODE:", String.valueOf(call));
                    Log.i("ERR:", String.valueOf(t));
                }
            });
        });
    }

    private void DisableBtn(boolean state) {
        if (!state) {
            login_btn.setClickable(true);
            login_btn.setFocusable(true);
        } else {
            login_btn.setClickable(false);
            login_btn.setFocusable(false);
        }
    }
}