package com.example.hc_app.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.hc_app.ChangePassword;
import com.example.hc_app.Models.RespObj;
import com.example.hc_app.R;
import com.example.hc_app.Services.APIConfig;
import com.example.hc_app.Services.RetrofitConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.hc_app.Models.Config.LOGIN_DATA;
import static com.example.hc_app.Models.Config.USERNAME;
import static com.example.hc_app.Models.Config.USER_ID;
import static com.example.hc_app.Models.Config.USER_TOKEN;

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
    TextView profile_bmi, choose_img, changePass;
    ImageView mAvatar;
    LinearLayout uploadImage;
    static final int RESULT_LOAD_CODE = 100;
    static final int READ_EXTERNAL_CODE = 200;
    AppCompatButton profile_save;
    String base64Thum = "";

    ProgressDialog p;
    APIConfig x;
    SharedPreferences pref;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IMAGE_CODE", String.valueOf(requestCode));
        if (requestCode == RESULT_LOAD_CODE){
            Uri uri = data.getData();
            if (uri != null){
                try {
                    InputStream is = getContext().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mAvatar.setImageBitmap(bitmap);
                    mAvatar.setVisibility(View.VISIBLE);
                    base64Thum = toBase64(bitmap);
                    Log.e("BASE64", base64Thum);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,30,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

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
        choose_img              = v.findViewById(R.id.choose_image);
        uploadImage             = v.findViewById(R.id.layoutAddImage);
        mAvatar                 = v.findViewById(R.id.avatar);
        changePass              = v.findViewById(R.id.change_pin);
        profile_save            = v.findViewById(R.id.profile_save);
        p                       = new ProgressDialog(getContext());
        x                       = RetrofitConfig.JSONconfig().create(APIConfig.class);
        pref                    = getContext().getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);

        GetUserInfo();
        uploadImage.setOnClickListener(v1 -> CheckPermission());
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(getContext(), ChangePassword.class);
                getContext().startActivity(x);
            }
        });

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
                if (base64Thum.isEmpty()){
                    Toast.makeText(getContext(),"Choose image", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    Integer.parseInt(stepPmeter);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Num of step mush be a number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                p.setMessage("Saving...");
                p.show();
                Map<String, Object> mReq = new ArrayMap<>();
                mReq.put("userID", pref.getInt(USER_ID, 0));
                mReq.put("tall", Float.parseFloat(heightTxt)/100);
                mReq.put("weight", Float.parseFloat(weightTxt));
                mReq.put("age", Integer.parseInt(ageTxt));
                mReq.put("ava", base64Thum);
                mReq.put("token", pref.getString(USER_TOKEN, ""));
                mReq.put("stepsOneMeter", stepPmeter);
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
                        Log.i("ProfileFragmentCODE:", String.valueOf(call));
                        Log.i("ProfileFragment:", String.valueOf(t));
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_EXTERNAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ReadImagesFromDevice();
                }
        }
    }

    private void CheckPermission(){
        if (!(ActivityCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, READ_EXTERNAL_CODE);
        } else {
            ReadImagesFromDevice();
        }
    }

    private void ReadImagesFromDevice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_CODE);
    }

    private void GetUserInfo() {
        p.setMessage("Loading...");
        p.show();
        Map<String, Object> mReq = new ArrayMap<>();
        mReq.put("username", pref.getString(USERNAME, null));
        mReq.put("token", pref.getString(USER_TOKEN, ""));
        RequestBody body = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(mReq)).toString());
        Call<RespObj> g= x.GetUserInfo(body);

        g.enqueue(new Callback<RespObj>() {
            @Override
            public void onResponse(Call<RespObj> call, Response<RespObj> response) {
                if (response.body().getCode() == 200) {
                    try {
                        JSONObject x = new JSONObject(response.body().getMsg().get(0).toString());
                        height.setText(String.valueOf(x.getDouble("height") * 100));
                        profile_steppermeter.setText(String.valueOf(Math.round(1/x.getDouble("step_range"))));
                        weight.setText(String.valueOf(x.getDouble("weight")));
                        profile_bmi.setText(String.valueOf(x.getDouble("bmi")));
                        age.setText(String.valueOf((int)x.getDouble("age")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), response.body().getMsg().get(0).toString(), Toast.LENGTH_SHORT).show();
                }
                Log.e("GetUserInfo", response.body().getMsg().get(0).toString());
                p.hide();
            }

            @Override
            public void onFailure(Call<RespObj> call, Throwable t) {
                p.hide();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GetUserInfoFAIL", t.getMessage());
            }
        });
    }
}