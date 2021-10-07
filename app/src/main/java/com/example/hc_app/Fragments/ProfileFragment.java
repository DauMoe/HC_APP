package com.example.hc_app.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hc_app.Models.RespObj;
import com.example.hc_app.R;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;
import com.google.android.material.internal.TextWatcherAdapter;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    EditText profile_steppermeter, age, height, weight;
    TextView profile_bmi;
    AppCompatButton profile_save;
    ProgressDialog p;
    APIConfig x;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v                  = inflater.inflate(R.layout.fragment_profile, container, false);
        profile_steppermeter    = v.findViewById(R.id.steppermeter);
        age                     = v.findViewById(R.id.profile_age);
        height                  = v.findViewById(R.id.profile_height);
        weight                  = v.findViewById(R.id.profile_weight);
        profile_bmi             = v.findViewById(R.id.profile_bmi);
        profile_save            = v.findViewById(R.id.profile_save);
        p                       = new ProgressDialog(getContext());
        x                       = RetrofitConfig.JSONconfig().create(APIConfig.class);
        pref                    = getContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);

        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sHeight = height.getText().toString();
                String sWeight = weight.getText().toString();
                if (sHeight.isEmpty() || sWeight.isEmpty()) return;
                if (Float.parseFloat(sHeight) == 0f || Float.parseFloat(sWeight) == 0f) {
                    profile_bmi.setText("0");
                    return;
                }
                profile_bmi.setText(100*Float.parseFloat(sWeight) / (2*Float.parseFloat(sHeight)) + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sHeight = height.getText().toString();
                String sWeight = weight.getText().toString();
                if (sHeight.isEmpty() || sWeight.isEmpty()) return;
                if (Float.parseFloat(sHeight) == 0f || Float.parseFloat(sWeight) == 0f) {
                    profile_bmi.setText("0");
                    return;
                }
                profile_bmi.setText(100*Float.parseFloat(sWeight) / (2*Float.parseFloat(sHeight)) + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        profile_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stepPmeter   = profile_steppermeter.getText().toString();
                String ageTxt       = age.getText().toString();
                String heightTxt    = height.getText().toString();
                String weightTxt    = weight.getText().toString();
                if (stepPmeter.isEmpty() || ageTxt.isEmpty() || heightTxt.isEmpty() || weightTxt.isEmpty()) {
                    Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                p.setMessage("Saving...");
                p.show();
                Map<String, Object> mReq = new ArrayMap<>();
                mReq.put("userID", pref.getInt(USER_ID, 0));
                mReq.put("tall", Float.parseFloat(heightTxt)/100);
                mReq.put("weight", Float.parseFloat(weightTxt));
                mReq.put("age", Integer.parseInt(ageTxt));
                mReq.put("ava", "");
                mReq.put("token", "");
                mReq.put("stepsOneMeter", Integer.parseInt(stepPmeter));
                RequestBody body = RequestBody
                        .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
                Call<RespObj> g= x.UpdateUserInfo(body);

                g.enqueue(new Callback<RespObj>() {
                    @Override
                    public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                        p.hide();
                        if (response.body().getCode() == 200) {
                            Toast.makeText(getContext(), "Update user info successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Another err. Msg will be returned by server
                            Toast.makeText(getContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_LONG).show();
                            Log.i("LOGIN:", response.body().getMsg().toString());
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
        });
        return v;
    }
}