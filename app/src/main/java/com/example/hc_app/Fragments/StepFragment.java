package com.example.hc_app.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.LoginActivity;
import com.example.hc_app.Models.ExerHistory;
import com.example.hc_app.Models.RespObj;
import com.example.hc_app.R;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.example.hc_app.StepCountActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.USER_ID;
import static com.example.hc_app.Models.Config.USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepFragment newInstance(String param1, String param2) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private SharedPreferences pref;
    TextView last_records;
    APIConfig x;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v                  = inflater.inflate(R.layout.fragment_home, container, false);
        pref                    = getContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        last_records            = v.findViewById(R.id.last_records);
        x                       = RetrofitConfig.JSONconfig().create(APIConfig.class);

        last_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StepCountActivity.class));
            }
        });

        //init timestamp
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long starttime = c.getTimeInMillis();
        Long endtime = starttime + 86400000L; //86,400,000 milliseconds = a day
        System.out.println("START: " + starttime);
        System.out.println("END: " + endtime);
        GetStepsToday();
        DrawStepsHistory(starttime, endtime);
        return v;
    }

    private void GetStepsToday() {
        Map<String, Object> mReq  = new ArrayMap<>();
        mReq.put("userID", pref.getInt(USER_ID, 0));
        mReq.put("token", pref.getString(USER_TOKEN, ""));
        RequestBody body        = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        Call<RespObj> g = x.GetRecords(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                if (response.body().getCode() == 200) {
                    Log.e("RSEP: ", response.body().getMsg().toString());
                    if (response.body().getMsg().size() == 0) {
                        last_records.setText("0 step");
                    } else {
                        try {
//                        Log.i("RESPONSE: ", String.valueOf(response.body().getMsg().get(0)));
                            JSONObject x = new JSONObject(response.body().getMsg().get(0).toString());
                            last_records.setText(x.getInt("stepofday") + " steps");
                        } catch (JSONException e) {
                            //Convert failed exception
                            Toast.makeText(getContext(), "Can't convert response to JSONObject", Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (response.body().getCode() == 205) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    Toast.makeText(getContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespObj> call, Throwable t) {
                Toast.makeText(getContext(), "Call API failed!", Toast.LENGTH_LONG).show();

                //DEBUG AREA
                Log.i("CODE:", String.valueOf(call));
                Log.i("ERR:", String.valueOf(t));
            }
        });
    }

    private void DrawStepsHistory(Long starttime, Long endtime) {
        Map<String, Object> mReq  = new ArrayMap<>();
        mReq.put("userID", pref.getInt(USER_ID, 0));
        mReq.put("token", pref.getString(USER_TOKEN, ""));
        mReq.put("starttime", starttime);
        mReq.put("endtime", endtime);
        RequestBody body        = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        Call<RespObj> g = x.GetSteps(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                if (response.body().getCode() == 200) {
                    for (Object i: response.body().getMsg()) {
                        ExerHistory v = new Gson().fromJson(i.toString(), ExerHistory.class);
                        Log.e("DISTANCE:", String.valueOf(v.getDistanceofday()));
                    }
                } else if (response.body().getCode() == 205) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    Toast.makeText(getContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespObj> call, Throwable t) {
                Toast.makeText(getContext(), "Call API failed!", Toast.LENGTH_LONG).show();

                //DEBUG AREA
                Log.i("CODE:", String.valueOf(call));
                Log.i("ERR:", String.valueOf(t));
            }
        });
    }
}