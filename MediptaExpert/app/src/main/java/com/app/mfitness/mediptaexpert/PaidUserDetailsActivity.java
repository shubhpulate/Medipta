package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PaidUserDetailsActivity extends AppCompatActivity {
    private TextView mTxtUserName, mTxtUserGoal,mTxtProgress,mTxtViewGoal,mTxtUserProfile,mTxtSetDiet,mTxtViewExercise;
    private ImageView mImgCallClient,mImgProfile;
    String name,profileId,address,aadhar,bldGroup,addiction,goalID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_user_details);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Basic Information");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        new SendPostRequest().execute();
        sendPostRequest();

        mTxtUserName = findViewById(R.id.user_profile_name);
        mTxtUserGoal = findViewById(R.id.user_goal);
        mImgCallClient = findViewById(R.id.call_client);
        mTxtProgress = findViewById(R.id.txtProgress);
        mTxtViewGoal = findViewById(R.id.txtViewGoal);
        mTxtUserProfile = findViewById(R.id.txtUserProfile);
        mTxtSetDiet = findViewById(R.id.txtSetDiet);
        mTxtViewExercise = findViewById(R.id.txtViewExercise);
        mImgProfile = findViewById(R.id.user_profile_photo);

        Intent intent = getIntent();
        final String firstName = intent.getStringExtra("firstName");
        final String contact = intent.getStringExtra("contact");
        final String email = intent.getStringExtra("email");
        final String bmi = intent.getStringExtra("bmi");
        final Integer age = intent.getIntExtra("age",0);
        final String gender = intent.getStringExtra("gender");
        final String goal = intent.getStringExtra("fitnessGoal");
        goalID = intent.getStringExtra("goalID");
        profileId = intent.getStringExtra("profileId");
        name = intent.getStringExtra("name");
        System.out.println("goal : "+goalID);


        mTxtUserName.setText(firstName);
        mTxtUserGoal.setText(goal);

        mTxtViewExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaidUserDetailsActivity.this,ExercisePlanActivity.class);
                i.putExtra("userId",profileId);
                i.putExtra("goalID",goalID);
                startActivity(i);
            }
        });

        mTxtSetDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaidUserDetailsActivity.this,ViewDietPlansActivity.class);
                i.putExtra("name1",name);
                startActivity(i);
            }
        });

        mTxtUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaidUserDetailsActivity.this,UserProfileActivity.class);
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
                Intent i = new Intent(PaidUserDetailsActivity.this,ViewGoalActivity.class);
                i.putExtra("name",name);
                startActivity(i);

            }
        });

        mTxtProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PaidUserDetailsActivity.this,ProgressActivity.class);
                i.putExtra("goalID",goalID);
                startActivity(i);
            }
        });

        mImgCallClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaidUserDetailsActivity.this);
                builder.setMessage("Do you want to call " +firstName + " ?");
                builder.setCancelable(true);
                builder.setPositiveButton("CALL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final int REQUEST_PHONE_CALL = 1;
                                Intent callIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + contact));

                                if (ContextCompat.checkSelfPermission(PaidUserDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PaidUserDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                                }

                                else
                                {
                                    startActivity(callIntent);
                                }
                            }
                        });

                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

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

                            Picasso.with(PaidUserDetailsActivity.this)
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
        MySingleton.getInstance(PaidUserDetailsActivity.this).addTorequestque(stringRequest);
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {
            JSONArray jArrPerson = null;
            try{

                URL url = new URL(RestAPI.dev_api + "api/method/phr.getPatientProfile");

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("profile_id", profileId);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("data",jsonObject);


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String response) {
//            Toast.makeText(getApplicationContext(), response,
//                    Toast.LENGTH_LONG).show();

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

                Picasso.with(PaidUserDetailsActivity.this)
                        .load(RestAPI.dev_api+profileImage)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(mImgProfile);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("response","Response = "+response);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
