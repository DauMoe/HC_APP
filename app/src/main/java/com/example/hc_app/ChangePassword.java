package com.example.hc_app;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.USERNAME;

public class ChangePassword extends AppCompatActivity {
    EditText oldpass, newPass, repeatPass;
    AppCompatButton changePIN;
    APIConfig x;
    ProgressDialog p;
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        oldpass = findViewById(R.id.oldpass);
        newPass = findViewById(R.id.new_pass);
        repeatPass = findViewById(R.id.repeat_pass);
        changePIN = findViewById(R.id.changePin_btn);
        pref = getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        x = RetrofitConfig.JSONconfig().create(APIConfig.class);
        p = new ProgressDialog(this);

        changePIN.setOnClickListener(v -> {
            String oldpassTxt = oldpass.getText().toString();
            String newpassTxt = newPass.getText().toString();
            String repreatTxt = repeatPass.getText().toString();
            if (oldpassTxt.isEmpty() || newpassTxt.isEmpty() || repreatTxt.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newpassTxt.equals(repreatTxt)) {
                Toast.makeText(this, "Password is not match", Toast.LENGTH_SHORT).show();
                return;
            }
            p.setMessage("Update password");
            p.show();
            Map<String, Object> mReq = new ArrayMap<>();
            mReq.put("username", pref.getString(USERNAME, null));
            mReq.put("password", oldpassTxt);
            mReq.put("newpass", newpassTxt);
            RequestBody body = RequestBody
                    .create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(mReq)).toString());
            Log.e("REQ", mReq.toString());
            Call<RespObj> g = x.ChangePass(body);
            g.enqueue(new Callback<RespObj>() {
                @Override
                public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                    p.hide();
                    if (response.body().getCode() == 200) {
                        Toast.makeText(ChangePassword.this, "Change password successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                    }
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


