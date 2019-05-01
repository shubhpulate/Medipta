package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ExpertProfileActivity extends AppCompatActivity {
    private TextView mTxtAddress,mTxtPhone,mTxtEmail,mTxtQualification,mTxtName,mTxtQua,mTxtExperience;
    SharedPrefManager sharedPrefManager;
    Activity context = this;
    String expertId,name,experience;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImg = findViewById(R.id.img);

        mTxtAddress = findViewById(R.id.txtAddress);
        mTxtPhone = findViewById(R.id.txtPhone);
        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtQualification = findViewById(R.id.txtQualification);
        mTxtName = findViewById(R.id.txtExpertName);
        mTxtQua = findViewById(R.id.txtExpertQualification);
        mTxtExperience = findViewById(R.id.txtExpertExperience);

        sharedPrefManager = new SharedPrefManager();
        expertId = sharedPrefManager.getExpertId(context);
        System.out.println("expert id : "+expertId);
//        new SendPostRequest().execute();
        postRequest();

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_fitness_expert_profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            name = msg.getString("full_name");
                            String address = msg.getString("address");
                            String qualification = msg.getString("qualification");
                            String phone = msg.getString("contact");
                            String email = msg.getString("email");
                            String profileImage = msg.getString("user_image");
                            experience = msg.getString("experience_in_year");

                            Picasso.with( ExpertProfileActivity.this )
                                    .load( RestAPI.dev_api + profileImage )
                                    .placeholder( R.mipmap.ic_launcher )
                                    .into( mImg);

                            sharedPrefManager.setExpertImage(context,profileImage);

                            mTxtAddress.setText(address);
                            mTxtEmail.setText(email);
                            mTxtPhone.setText(phone);
                            mTxtName.setText(name);
                            //    mTxtQua.setText(qualification);
                            mTxtExperience.setText("Experience : "+ experience + " years");
                            mTxtQualification.setText(qualification);

                            if (email.equals("null")){
                                mTxtEmail.setText("--");
                            }
                            if (address.equals("null")){
                                mTxtAddress.setText("--");
                            }
                            if (experience.equals("null")){
                                mTxtExperience.setText("Experience : --");
                            }
                            if (qualification.equals("null") || qualification.equals("")){
                                mTxtQualification.setText("--");
                                //  mTxtQua.setText("--");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("response","Response = "+response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("expert_id",expertId);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(ExpertProfileActivity.this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
