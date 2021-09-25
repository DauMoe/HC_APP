package com.example.hc_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.STEPRANGE;
import static com.example.hc_app.Models.Config.USER_ID;

public class StepCountActivity extends AppCompatActivity {
    AppCompatButton step_stop;
    TextView step_counter, step_distance;
    LinearLayout step_area;
    private boolean mIsBind;
    private SharedPreferences pref;
    private Long starttimestamp, endtimestamp;
    Float step_range;
    ProgressDialog p;
    int curStep;

    private ServiceConnection mServiceConnection = new
            ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder service) {
                    StepsService stepService = ((StepsService.StepBinder) service).getService();
                    showStepCount(0, stepService.getStepCount());
                    stepService.registerCallback(stepCount -> {
                        curStep = stepCount;
                        showStepCount(0, stepCount);
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {}
            };

    public void showStepCount(int totalStepNum, int currentCounts) {
//        Log.i("UPDATE: ", String.valueOf(currentCounts));
        if (currentCounts < totalStepNum) {
            currentCounts = totalStepNum;
        }
        step_counter.setText(currentCounts + " steps");
        step_distance.setText(String.format("%2f m", (float) (currentCounts * step_range)));
    }

    private void setupService() {
        Intent intent = new Intent(this, StepsService.class);
        mIsBind = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        step_stop       = findViewById(R.id.step_stop);
        step_counter    = findViewById(R.id.step_counter);
        step_area       = findViewById(R.id.step_area);
        step_distance   = findViewById(R.id.step_distance);
        pref            = getApplicationContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        starttimestamp  = Calendar.getInstance().getTimeInMillis();
        step_range      = pref.getFloat(STEPRANGE, 0f);
        p               = new ProgressDialog(this);
        curStep         = 0;

        step_counter.setText("0 step");
        step_distance.setText("0m");
        showStepCount(0, 0);
        setupService();
        step_stop.setOnClickListener(v -> UpdateSteps());
    }

    private void UpdateSteps() {
        p.setMessage("Progress...");
        p.show();
        Log.e("STEPSSSSS:", String.valueOf(curStep));
        endtimestamp = Calendar.getInstance().getTimeInMillis();
        Map<String, Object> mReq = new ArrayMap<>();
        mReq.put("userID", pref.getInt(USER_ID, -1));
        mReq.put("steps", curStep);
        mReq.put("starttime", starttimestamp);
        mReq.put("endtime", endtimestamp);
        mReq.put("calo", 0);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        APIConfig x = RetrofitConfig.JSONconfig().create(APIConfig.class);
        Call<RespObj> g = x.UpdateStep(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                p.hide();
                if (response.body().getCode() == 200) {
                    startActivity(new Intent(StepCountActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                }
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBind) {
            unbindService(mServiceConnection);
        }
    }
}