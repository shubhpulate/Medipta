package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UnpaidUserDetailsActivity extends AppCompatActivity {
    private TextView mTxtUserName , mTxtUserGoal, mTxtViewGoal,mTxtProfile;
    private String address,addiction,aadhar,bldGroup,profileId;
    private ImageView mImgProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unpaid_user_details);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Basic Information");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTxtUserName = findViewById(R.id.user_profile_name);
        mTxtUserGoal = findViewById(R.id.user_goal);
        mTxtViewGoal = findViewById(R.id.txtViewGoal);
        mTxtProfile = findViewById(R.id.txtProfile);
        mImgProfile = findViewById(R.id.user_profile_photo);

        Intent intent = getIntent();
        final String firstName = intent.getStringExtra("firstName");
        final String contact = intent.getStringExtra("contact");
        final String name= intent.getStringExtra("name");
        final Integer age = intent.getIntExtra("age",0);
        final String gender = intent.getStringExtra("gender");
        final String goal = intent.getStringExtra("fitnessGoal");
        final String email = intent.getStringExtra("email");
        final  String bmi = intent.getStringExtra("bmi");
        profileId = intent.getStringExtra("profileID");
        sendPostRequest();

        mTxtUserName.setText(firstName);

        mTxtUserGoal.setText(goal);

        mTxtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UnpaidUserDetailsActivity.this,UserProfileActivity.class);
                i.putExtra("firstName",firstName);
                i.putExtra("contact",contact);
                i.putExtra("email",email);
                i.putExtra("bmi",bmi);
                i.putExtra("age",age);
                i.putExtra("gender",gender);
                i.putExtra("fitnessGoal",goal);
                i.putExtra("address",address);
                startActivity(i);

            }
        });

        mTxtViewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UnpaidUserDetailsActivity.this,ViewGoalActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });
    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.getPatientProfile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jArrPerson = null;
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            JSONObject js = jsonObject.getJSONObject("message");
                            JSONObject ab = js.getJSONObject("profile");
                            String profileImage = ab.getString("profile_image");
                            address = ab.getString("address");
                            aadhar = ab.getString("aadhar_number");
                            bldGroup = ab.getString("blood_group");
                            addiction = ab.getString("addiction");
                            System.out.println("profile image : "+profileImage);

                            Picasso.with(UnpaidUserDetailsActivity.this)
                                    .load(RestAPI.dev_api+profileImage)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(mImgProfile);

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
                userParams.put("profile_id",profileId);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(UnpaidUserDetailsActivity.this).addTorequestque(stringRequest);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
