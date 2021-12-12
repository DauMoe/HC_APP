package com.example.hc_app;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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

//import com.example.hc_app.Fragments.MapFragment;

public class StepCountActivity extends FragmentActivity {
    AppCompatButton step_stop;
    TextView step_counter, step_distance;
    Chronometer mChronometer;
    LinearLayout step_area;
    private boolean mIsBind;
    private SharedPreferences pref;
    private Long starttimestamp, endtimestamp;
    Float step_range;
    ProgressDialog p;
    int curStep;
    private boolean isResume;
    Handler handler;
    long tMilliSec,tStart, tBuff, tUpdate = 0L;
    int sec, min, milleSec;



//    MyLocationListener mylistener;
//    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Doc: https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
//        SupportMapFragment mapFrag = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFrag.getMapAsync(googleMap -> {
//            mMap = googleMap;
//            Log.e("MAP_STATE", "READY");
//            GetCurrentLocation();
//        });

        step_stop       = findViewById(R.id.step_stop);
        step_counter    = findViewById(R.id.step_counter);
        step_area       = findViewById(R.id.step_area);
        step_distance   = findViewById(R.id.step_distance);
        mChronometer       = findViewById(R.id.step_time);
        pref            = getApplicationContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        starttimestamp  = Calendar.getInstance().getTimeInMillis();
        endtimestamp    = Calendar.getInstance().getTimeInMillis();
        step_range      = pref.getFloat(STEPRANGE, 0f);
        p               = new ProgressDialog(this);
        curStep         = 0;

        handler = new Handler();


        step_counter.setText("0");
        step_distance.setText("0");
        mChronometer.setText("00:00:00");
        showStepCount(0, 0);
        setupService();
        step_stop.setOnClickListener(v -> UpdateSteps());

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    private class MyLocationListener implements LocationListener {
//        @Override
//        public void onLocationChanged(Location location) {
//            double longitude = location.getLongitude();
//            double latitude = location.getLatitude();
//            Log.e("GPS", location.getLatitude() + ", " + location.getLongitude());
//            LatLng here = new LatLng(latitude, longitude);
//            mMap.addMarker(new MarkerOptions().position(here).title("Current Location")).setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_people));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18.2f));
//        }
//    }

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
        if (currentCounts < totalStepNum) {
            currentCounts = totalStepNum;
        }
        step_counter.setText(currentCounts + "");
        step_distance.setText((int)(Math.round(currentCounts * step_range * 100))/100.0 + " m");
        tStart = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        mChronometer.start();

    }
    public Runnable runnable = new Runnable() {
    @Override
    public void run() {
        tMilliSec = SystemClock.uptimeMillis() - tStart;
        tUpdate = tBuff + tMilliSec;
        sec = (int) (tUpdate/1000);
        min = sec/60;
        sec = sec%60;
        milleSec = (int) (tUpdate%100);
        mChronometer.setText(String.format("%02d",min)+":"
        + String.format("%02d",sec) + ":" + String.format("%02d",milleSec));
        handler.postDelayed(this,60);

    }
};

    private void setupService() {
        Intent intent = new Intent(this, StepsService.class);
        mIsBind = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

//    private void GetCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("NO_GPS: ", "GPS IS NOT ALLOWED!");
//
//            //Request permissions
//            ActivityCompat.requestPermissions(StepCountActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
//        } else {
//            //Doc: https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android (11 Upvotes)
//            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//            Location location;
//            while(true) {
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                if (location != null) {
//                    break;
//                }
//            }
//            mylistener = new MyLocationListener();
//            mylistener.onLocationChanged(location);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mylistener);
////            locationManager.removeUpdates(mylistener);
//        }
//    }

    //@Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //Check permissions
//        switch (requestCode) {
//            case 200:
//                if (grantResults.length > 0) {
//                    boolean fine_location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean coarse_location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (fine_location && coarse_location) {
//                        GetCurrentLocation();
//                    }
//                }
//        }
//    }

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