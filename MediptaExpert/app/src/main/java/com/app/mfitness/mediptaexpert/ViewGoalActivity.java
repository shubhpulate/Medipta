package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ViewGoalActivity extends AppCompatActivity {
    private TextView mTxtGoalValue,mTxtFoodValue,mTxtProgressValue,mTxtRoutineValue,mTxtActivityValue,mTxtAddiction,mTxtConsumeEggs,mTxtCurrentMedication,mTxtFoodAllergies,mTxtFoodDontLike,
                        mTxtMedHistory,mTxtPastPlan,mTxtSleepTime,mTxtWakeupTime,mTxtWorkingTime;
    private String name;
    SharedPrefManager sharedPrefManager;
    Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Goal");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
         name = intent.getStringExtra("name");
        System.out.println("View Goal name : "+name);

        mTxtGoalValue = findViewById(R.id.goalValue);
        mTxtFoodValue = findViewById(R.id.foodValue);
        mTxtProgressValue = findViewById(R.id.progressValue);
        mTxtRoutineValue = findViewById(R.id.routineValue);
        mTxtActivityValue = findViewById(R.id.activityValue);
        mTxtAddiction = findViewById(R.id.addiction);
        mTxtConsumeEggs = findViewById(R.id.consumeEggs);
        mTxtCurrentMedication = findViewById(R.id.currentMedication);
        mTxtFoodAllergies = findViewById(R.id.foodAllergies);
        mTxtFoodDontLike = findViewById(R.id.foodDontLike);
        mTxtMedHistory = findViewById(R.id.medicalHistory);
        mTxtPastPlan = findViewById(R.id.pastPlan);
        mTxtSleepTime = findViewById(R.id.sleepTime);
        mTxtWakeupTime = findViewById(R.id.wakeupTime);
        mTxtWorkingTime = findViewById(R.id.workingTime);

        sendPostRequest();

    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.fitness_goal_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            String goal = msg.getString("fitness_goal");
                            String foodPreference = msg.getString("food_preference");
                            String goalProgress = msg.getString("fitness_goal_progress");
                            String activity = msg.getString("physical_activity_level");
                            String daily_routine = msg.getString("daily_routin");
                            String addiction = msg.getString("addiction");
                            String consumeEgg = msg.getString("consume_egg");
                            String currentMedication = msg.getString("current_medication");
                            String foodAllergies = msg.getString("food_allergies");
                            String foodDontLike = msg.getString("food_dont_like");
                            String medHistory = msg.getString("past_medical_history");
                            String pastPlan = msg.getString("past_diet_plan_details");
                            String sleepTime = msg.getString("sleep_time");
                            String wakeUpTime = msg.getString("wake_up_time");
                            String workingTime = msg.getString("working_time");

                            mTxtMedHistory.setText(medHistory);
                            mTxtPastPlan.setText(pastPlan);
                            mTxtSleepTime.setText(sleepTime);
                            mTxtWakeupTime.setText(wakeUpTime);
                            mTxtWorkingTime.setText(workingTime);

                            if (goal.equals("")){
                                mTxtGoalValue.setText("");
                            }else {
                                mTxtGoalValue.setText(goal);
                            }

                            mTxtFoodValue.setText(foodPreference);

                            if (goalProgress.equals("")){
                                mTxtProgressValue.setText("--");
                            }else {
                                mTxtProgressValue.setText(goalProgress);
                            }

                            if (activity.equals("")){
                                mTxtActivityValue.setText("--");
                            }else {
                                mTxtActivityValue.setText(activity);
                            }
                            mTxtRoutineValue.setText(daily_routine);
                            mTxtAddiction.setText(addiction);

                            if (consumeEgg.equals("0")){
                                mTxtConsumeEggs.setText("No");
                            }else {
                                mTxtConsumeEggs.setText("Yes");
                            }
                            mTxtCurrentMedication.setText(currentMedication);
                            mTxtFoodAllergies.setText(foodAllergies);

                            if (foodDontLike.equals("null")){
                                mTxtFoodDontLike.setText("--");
                            }else {
                                mTxtFoodDontLike.setText(foodDontLike);
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
                userParams.put("name",name);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(ViewGoalActivity.this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
