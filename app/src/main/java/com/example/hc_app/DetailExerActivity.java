package com.example.hc_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailExerActivity extends AppCompatActivity {
    VideoView detail_video;
    APIConfig e;
    ProgressDialog p;
    static final int REQUEST_CODE = 100;
    int exerID;
    TextView title, desc;
    RatingBar rating;
    Button rating_btn;
    int stars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exer);
        Intent x        = getIntent();
        exerID          = x.getIntExtra("data", 0);

        e               = RetrofitConfig.JSONconfig().create(APIConfig.class);
        p               = new ProgressDialog(this);
        detail_video    = findViewById(R.id.detail_video);
        title           = findViewById(R.id.detail_title);
        desc            = findViewById(R.id.detail_desc);
        rating          = findViewById(R.id.rating_exercise);
        rating_btn      = findViewById(R.id.submit_rating);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                stars = Math.round(rating);
            }
        });

        rating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> mReq = new ArrayMap<>();
                mReq.put("exerID", exerID);
                mReq.put("star", stars);
                RequestBody body = RequestBody
                        .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());

                Call<RespObj> g = e.RatingExercise(body);

                g.enqueue(new Callback<RespObj>() {
                    @Override
                    public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                        if (response.body().getCode() == 200) {
                            Toast.makeText(DetailExerActivity.this, "Thanks for your rating!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("RATING:", response.body().getMsg().toString());
                            Toast.makeText(DetailExerActivity.this, response.body().getMsg().get(0).toString(), Toast.LENGTH_SHORT).show();
                        }
                        p.hide();
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
            }
        });


        detail_video.setOnPreparedListener(mp -> mp.setLooping(true));

        if (!(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Please allow this permission!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            GetDetailExercise();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean write_external = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (write_external) {
                        Log.e("PER", "ALLOWED2");
                        GetDetailExercise();
                    }
                }
        }
    }

    private void GetDetailExercise() {
        p.setMessage("Loading...");
        p.show();
        Map<String, Object> mReq = new ArrayMap<>();
        mReq.put("exerID", exerID);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());

        Call<RespObj> g = e.GetDetailExercise(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                if (response.body().getCode() == 200) {
                    try {
                        JSONObject n = new JSONObject(response.body().getMsg().get(0).toString());

                        //Write video to local
                        String path = Environment.getExternalStorageDirectory() + File.separator + "last_hc.mp4";
                        byte[] decodeBytes = Base64.decode(n.getString("videoBase64").getBytes(), Base64.DEFAULT);
                        FileOutputStream fos = new FileOutputStream(path);
                        fos.write(decodeBytes);
                        fos.close();
                        DisplayInfo(n, path);
                    } catch (JSONException | IOException e) {
                        Log.e("CONVERT ERR", e.getMessage());
                        Toast.makeText(DetailExerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("DETAIL_ACTIVITY:", response.body().getMsg().toString());
                    Toast.makeText(DetailExerActivity.this, response.body().getMsg().get(0).toString(), Toast.LENGTH_SHORT).show();
                }
                p.hide();
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
    }

    private void DisplayInfo(JSONObject n, String path) {
        detail_video.setVideoPath(path);
        detail_video.start();
        try {
            title.setText(n.getString("excer_name"));
            desc.setText(n.getString("description"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}