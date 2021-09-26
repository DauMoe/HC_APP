package com.example.hc_app.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hc_app.Adapter.ListExerAdapter;
import com.example.hc_app.Models.Exercise;
import com.example.hc_app.Models.RespObj;
import com.example.hc_app.R;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseFragment newInstance(String param1, String param2) {
        ExerciseFragment fragment = new ExerciseFragment();
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

    ProgressDialog p;
    ListExerAdapter adapter;
    RecyclerView exer_rcv;
    ChipGroup filter_exercise;
    Chip single, group, recom;
    APIConfig x;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exercise, container, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        p               = new ProgressDialog(getContext());
        exer_rcv        = v.findViewById(R.id.exer_rcv);
        adapter         = new ListExerAdapter(getContext());
        filter_exercise = v.findViewById(R.id.filter_exercise);
        x               = RetrofitConfig.JSONconfig().create(APIConfig.class);

        exer_rcv.setLayoutManager(manager);
        exer_rcv.setAdapter(adapter);
//        single          = v.findViewById(R.id.single);
//        group           = v.findViewById(R.id.group);
//        recom           = v.findViewById(R.id.recom);

        filter_exercise.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.single:
                    GetSingleExercises();
                    break;
                case R.id.group:
                    GetGroupExercise();
                    break;
                case R.id.recom:
                    GetRecommendExercise();
                    break;
            }
        });

        GetSingleExercises();
        return v;
    }

    private void GetSingleExercises() {
        p.setMessage("Waiting...");
        p.show();
        Call<RespObj> g = x.GetListExercise();
        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                p.hide();
                if (response.body().getCode() == 200) {
                    ArrayList<Exercise> data = new ArrayList<>();
                    for (Object i: response.body().getMsg()) {
                        Exercise f = new Gson().fromJson(i.toString(), Exercise.class);
                        data.add(f);
                        Log.e("TEST", i.toString());
                    }
                    adapter.setData(data);
                } else {
                    //Another err. Msg will be returned by server
                    Log.i("EXERCISE_FRAGMENT:", response.body().getMsg().toString());
                    Toast.makeText(getContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                }
                //DEBUG AREA
                Log.i("CODE:", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<RespObj> call, Throwable t) {
                p.hide();
                Toast.makeText(getContext(), String.valueOf(t), Toast.LENGTH_LONG).show();

                //DEBUG AREA
                Log.i("CODE:", String.valueOf(call));
                Log.i("ERR:", String.valueOf(t));
            }
        });
    }

    private void GetGroupExercise() {
        p.setMessage("Waiting...");
        p.show();
    }

    private void GetRecommendExercise() {
        p.setMessage("Waiting...");
        p.show();
    }
}