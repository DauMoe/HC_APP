package com.example.hc_app;
//NEW
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class StepsService extends android.app.Service implements SensorEventListener {
    //Doc: https://viblo.asia/p/the-walking-step-dem-buoc-chan-di-chuyen-OeVKBYgM5kW#_21-callback-update-ui-6

    private static final String TAG = "TAG: " + StepsService.class.getSimpleName();
    private static int mStepSensorType = -1;
    private UpdateUICallback mCallback;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private BroadcastReceiver mBroadcastReceiver;
    private StepBinder mStepBinder = new StepBinder();
    private SensorManager mSensorManager;
    private int mCurrentStep;
    private int mNotifyIdStep = 100;
    private int mHasStepCount = 0;
    private int mPreviousStepCount = 0;
    private boolean mHasRecord;

    @Override
    public void onCreate() {
        super.onCreate();
        initTodayData();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStepBinder;
    }

    public void registerCallback(UpdateUICallback paramICallback) {
        mCallback = paramICallback;
    }

    private void startStepDetector() {
        if (mSensorManager != null) {
            mSensorManager = null;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        } else {
            addBasePedometerListener();
        }
    }

    private void addCountStepListener() {
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            mStepSensorType = Sensor.TYPE_STEP_COUNTER;
            android.util.Log.v(TAG, "Sensor.TYPE_STEP_COUNTER");
            mSensorManager
                    .registerListener(StepsService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            mStepSensorType = Sensor.TYPE_STEP_DETECTOR;
            android.util.Log.v(TAG, "Sensor.TYPE_STEP_DETECTOR");
            mSensorManager.registerListener(StepsService.this, detectorSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            android.util.Log.v(TAG, "Count sensor not available!");
            addBasePedometerListener();
        }
    }

    private void addBasePedometerListener() {
        // TODO: 23/08/2017
    }

    public int getStepCount() {
        return mCurrentStep;
    }

    public PendingIntent getDefaultIntent(int flags) {
        return PendingIntent.getActivity(this, 1, new Intent(), flags);
    }

    private void initTodayData() {
        mCurrentStep = 0;
        updateStepCount();
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case Intent.ACTION_SCREEN_ON:
                        android.util.Log.i(TAG, "screen_on");
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        android.util.Log.i(TAG, "screen_off");
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        android.util.Log.i(TAG, "screen unlock");
                        break;
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        android.util.Log.i(TAG, "receive ACTION_CLOSE_SYSTEM_DIALOGS");
                        saveData();
                        break;
                    case Intent.ACTION_SHUTDOWN:
                        android.util.Log.i(TAG, "receive ACTION_SHUTDOWN");
                        saveData();
                        break;
                    case Intent.ACTION_DATE_CHANGED:
                        android.util.Log.i(TAG, "receive ACTION_DATE_CHANGED");
                        saveData();
                        break;
                    case Intent.ACTION_TIME_CHANGED:
                        android.util.Log.i(TAG, "receive ACTION_TIME_CHANGED");
                        saveData();
                        break;
                    case Intent.ACTION_TIME_TICK:
                        android.util.Log.i(TAG, "receive ACTION_TIME_TICK");
                        saveData();
                        break;
                }
            }
        };
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void updateStepCount() {
        if (mCallback != null) {
            mCallback.updateUi(mCurrentStep);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (mStepSensorType) {
            case Sensor.TYPE_STEP_COUNTER:
                int tempStep = (int) sensorEvent.values[0];
                if (!mHasRecord) {
                    mHasRecord = true;
                    mHasStepCount = tempStep;
                } else {
                    int thisStepCount = tempStep - mHasStepCount;
                    int thisStep = thisStepCount - mPreviousStepCount;
                    mCurrentStep += thisStep;
                    mPreviousStepCount = thisStepCount;
                }
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                if (sensorEvent.values[0] == 1.0) {
                    mCurrentStep++;
                }
                break;
        }
        updateStepCount();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void saveData() {
        android.util.Log.e("SAVE: ", "Save data");
    }

    public class StepBinder extends Binder {
        public StepsService getService() {
            return StepsService.this;
        }
    }
}