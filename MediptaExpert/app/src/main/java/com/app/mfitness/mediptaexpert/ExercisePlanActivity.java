package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ExercisePlanActivity extends AppCompatActivity {
    private RecyclerView mRecyclerPlan;
    private ArrayList<ExercisePlan> mListPlan;
    private AdapterExercisePlan mAdapterPlan;
    private ProgressDialog mProgressDialog;
    private String abc,name,gen,planName,goalId;
    SharedPrefManager sharedPrefManager;
    Activity context = this;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Exercise Plans");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshExePlan);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postRequest();
            }
        });

        sharedPrefManager = new SharedPrefManager();

        Intent intent = getIntent();
        name = intent.getStringExtra("userId");
        goalId = intent.getStringExtra("goalID");
        System.out.println("goal h : "+goalId);

        mRecyclerPlan =findViewById(R.id.recyclerExePlan);
        mRecyclerPlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListPlan = new ArrayList<>();

        mAdapterPlan = new AdapterExercisePlan(mListPlan,this);
        mRecyclerPlan.setAdapter(mAdapterPlan);
        mAdapterPlan.setOnPlaceClickListener(new PlaceClickListener());

        FloatingActionButton fab = findViewById(R.id.fabAddExePlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ExercisePlanActivity.this,CreateExercisePlanActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading Diet Plans...");
        mProgressDialog.show();
        postRequest();

    }

    public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_exercise_plans",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        mListPlan.clear();

                        JSONArray jArrPerson = null;
                        try {
                            JSONObject jsonObject= new JSONObject(response);

                            jArrPerson = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jArrPerson.length(); i++) {

                                JSONObject jPerson = jArrPerson.getJSONObject(i);
                                ExercisePlan exe = new ExercisePlan();

                                exe.exercisePlan = jPerson.getString("plan_name");
                                exe.name = jPerson.getString("name");
                                exe.endDate = jPerson.getString("end_date");
                                exe.startDate = jPerson.getString("start_date");
                                exe.isActive = jPerson.getString("is_active");
                                System.out.println("Plan name : " + exe.exercisePlan);
                                mListPlan.add(exe);
                            }

                            mAdapterPlan.notifyDataSetChanged();

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

                userParams.put("goal_id",goalId);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());

                return params;
            }
        };
        MySingleton.getInstance(ExercisePlanActivity.this).addTorequestque(stringRequest);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

//    @Override
//    public void onResume()
//    {  // After a pause OR at startup
//        super.onResume();
//
//
//        //Refresh your stuff here
//    }


    class PlaceClickListener implements AdapterExercisePlan.OnPlaceClickListener {

        @Override
        public void onPlaceClick(ExercisePlan exercisePlan) {

            Intent i = new Intent(ExercisePlanActivity.this, SelectExerciseActivity.class);
            i.putExtra("dietPlan", exercisePlan.exercisePlan);
            i.putExtra("name",exercisePlan.name);
            startActivity(i);

        }

        @Override
        public void onEditClick(ExercisePlan exercisePlan) {
            Intent i = new Intent(ExercisePlanActivity.this,EditExePlanActivity.class);
            i.putExtra("exercisePlan",exercisePlan.exercisePlan);
            i.putExtra("name",exercisePlan.name);
            i.putExtra("startDate",exercisePlan.startDate);
            i.putExtra("endDate",exercisePlan.endDate);
            i.putExtra("userId",name);
            i.putExtra("isActive",exercisePlan.isActive);

            startActivity(i);
        }

        @Override
        public void onDeleteClick(final ExercisePlan exercisePlan) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExercisePlanActivity.this);
            builder.setMessage("Do you want to delete " + exercisePlan.exercisePlan + " ?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes, Delete it!!",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    RestAPI.dev_api + "api/method/phr.delete_exercise_plan",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e("response","Response = "+response);
                                            mAdapterPlan.notifyDataSetChanged();
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
                                    userParams.put("name",exercisePlan.name);

                                    JSONObject userJson = new JSONObject(userParams);
                                    params.put("data",userJson.toString());
                                    return params;
                                }
                            };

                            MySingleton.getInstance(ExercisePlanActivity.this).addTorequestque(stringRequest);
                            finish();
                            startActivity(getIntent());

                        }
                    });

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
