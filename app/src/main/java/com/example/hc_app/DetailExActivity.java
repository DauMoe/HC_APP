package com.example.hc_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.Adapter.ListExerAdapter;
import com.example.hc_app.Models.Exercise;
import com.example.hc_app.Models.RespObj;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hc_app.Models.Config.USER_ID;
import static com.example.hc_app.Models.Config.USER_TOKEN;

public class DetailExActivity extends AppCompatActivity {
    APIConfig x;
    ListExerAdapter adapter;
    RecyclerView detail_ex;
    int grID;
    ProgressDialog p;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ex);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        Intent f        = getIntent();
        grID            = f.getIntExtra("data", 0);
        x               = RetrofitConfig.JSONconfig().create(APIConfig.class);
        p               = new ProgressDialog(this);
        empty           = findViewById(R.id.empty_ex);
        adapter         = new ListExerAdapter(this);
        detail_ex       = findViewById(R.id.detail_gr_exercise);

        detail_ex.setLayoutManager(manager);
        detail_ex.setAdapter(adapter);
        GetListExBygrID();
    }

    private void GetListExBygrID() {
        p.setMessage("Loading...");
        p.show();
        Map<String, Object> mReq  = new ArrayMap<>();
        Log.e("grID", String.valueOf(grID));
        mReq.put("grID", grID);
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        Call<RespObj> g = x.GetListExByID(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                p.hide();
                if (response.body().getCode() == 200) {
                    ArrayList<Exercise> data = new ArrayList<>();
                    for (Object i: response.body().getMsg()) {
                        Exercise f = new Gson().fromJson(i.toString(), Exercise.class);
                        data.add(f);
                    }
                    if (data.size() == 0) {
                        empty.setVisibility(View.VISIBLE);
                        detail_ex.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.GONE);
                        detail_ex.setVisibility(View.VISIBLE);
                    }
                    Log.e("SIZE", String.valueOf(response.body().getMsg().size()));
                    adapter.setData(data, false);
                } else {
                    //Another err. Msg will be returned by server
                    Toast.makeText(getApplicationContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
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
    }
}