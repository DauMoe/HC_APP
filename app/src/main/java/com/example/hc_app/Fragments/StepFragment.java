package com.example.hc_app.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.LoginActivity;
import com.example.hc_app.Models.ChartData;
import com.example.hc_app.Models.ExerHistory;
import com.example.hc_app.Models.RespObj;
import com.example.hc_app.R;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.example.hc_app.StepCountActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.USER_ID;
import static com.example.hc_app.Models.Config.USER_TOKEN;
import static java.util.Calendar.MONTH;

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
    ImageView step_filter;
    APIConfig x;
    List<Entry> StepsData= new ArrayList<>();
    List<String> TimeStamp = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineDataSet set1;
    LineChart step_chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v                  = inflater.inflate(R.layout.fragment_home, container, false);
        pref                    = getContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        last_records            = v.findViewById(R.id.last_records);
        step_filter             = v.findViewById(R.id.step_filter);
        step_chart              = v.findViewById(R.id.step_chart);
        x                       = RetrofitConfig.JSONconfig().create(APIConfig.class);

        last_records.setOnClickListener(v1 -> startActivity(new Intent(getContext(), StepCountActivity.class)));
        step_filter.setOnClickListener(v12 -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            Calendar now = Calendar.getInstance();
            builder.setSelection(new Pair<> (now.getTimeInMillis(), now.getTimeInMillis()));
            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
            picker.show(getActivity().getSupportFragmentManager(), picker.toString());
            picker.addOnNegativeButtonClickListener(v1 -> picker.dismiss());
            picker.addOnPositiveButtonClickListener(selection -> {
            Log.i("RANGE: ", selection.first + " to "+selection.second);
                DrawStepsHistory(selection.first, selection.second);
            });
        });

        //init timestamp
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
//        Long starttime = c.getTimeInMillis();
//        Long endtime = starttime + 86400000L; //86,400,000 milliseconds = a day
//        System.out.println("START: " + starttime);
//        System.out.println("END: " + endtime);
        GetStepsToday();

        //Get step history one month
        Calendar h                  = Calendar.getInstance();
        Long starttime              = h.getTimeInMillis();
        h.add(MONTH, -1);
        Long endtime                = h.getTime().getTime();

        YAxis rightYAxis = step_chart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = step_chart.getAxisLeft();
        leftYAxis.setEnabled(false);
        XAxis topXAxis = step_chart.getXAxis();
        topXAxis.setEnabled(false);
        XAxis xAxis = step_chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        //String setter in x-Axis
        step_chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(TimeStamp));
        step_chart.animateX(2000);
        step_chart.invalidate();
        step_chart.getLegend().setEnabled(true);
        step_chart.getDescription().setText("Date");
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
        starttime = 1632070800000L;
        endtime = 1632675600000L;
        Log.e("START-END: ", starttime + ": " + endtime);
        Map<String, Object> mReq  = new ArrayMap<>();
        mReq.put("userID", pref.getInt(USER_ID, 0));
        mReq.put("token", pref.getString(USER_TOKEN, ""));
        mReq.put("starttime", starttime);
        mReq.put("endtime", endtime);
        RequestBody body        = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        Call<RespObj> g = x.GetChartData(body);
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                if (response.body().getCode() == 200) {
                    //Start draw chart
                    if (response.body().getMsg().size() == 0) {
                        StepsData.clear();
                        TimeStamp.clear();
                        Toast.makeText(getContext(), "No step today!", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i=0; i<response.body().getMsg().size(); i++) {
                            ChartData v = new Gson().fromJson(response.body().getMsg().get(i).toString(), ChartData.class);
                            StepsData.add(new Entry(i, v.getTotal_step()));
                            TimeStamp.add(v.getStarttime());
                        }
                    }
                    dataSets.clear();
                    set1 = new LineDataSet(StepsData, "Steps");
                    set1.setColor(Color.rgb(31, 236, 180));
                    set1.setValueTextColor(Color.rgb(7, 169, 125));
                    set1.setValueTextSize(10f);
                    set1.setMode(LineDataSet.Mode.LINEAR);
                    set1.setLineWidth(2f);
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    step_chart.setData(data);

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