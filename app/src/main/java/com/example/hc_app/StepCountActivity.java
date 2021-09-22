package com.example.hc_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StepCountActivity extends AppCompatActivity {
    AppCompatButton start_step, step_stop;
    TextView step_counter;
    LinearLayout step_area;
    private boolean mIsBind;

    private ServiceConnection mServiceConnection = new
            ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder service) {
                    StepsService stepService = ((StepsService.StepBinder) service).getService();
                    showStepCount(0, stepService.getStepCount());
                    stepService.registerCallback(new UpdateUICallback() {
                        @Override
                        public void updateUi(int stepCount) {
                            showStepCount(0, stepCount);
                        }
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                }
            };

    public void showStepCount(int totalStepNum, int currentCounts) {
//        Log.i("UPDATE: ", String.valueOf(currentCounts));
        if (currentCounts < totalStepNum) {
            currentCounts = totalStepNum;
        }
        step_counter.setText(currentCounts + " steps");
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

        start_step      = findViewById(R.id.start_step);
        step_stop       = findViewById(R.id.step_stop);
        step_counter    = findViewById(R.id.step_counter);
        step_area       = findViewById(R.id.step_area);

        showStepCount(0, 0);
        setupService();
//        start_step.setVisibility(View.VISIBLE);
//        step_area.setVisibility(View.GONE);
        step_area.setVisibility(View.VISIBLE);
        start_step.setVisibility(View.GONE);

        start_step.setOnClickListener(v -> {
            step_area.setVisibility(View.VISIBLE);
            start_step.setVisibility(View.GONE);
            step_counter.setText("0 step");
            setupService();
        });

        step_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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