package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class ViewDietPlansActivity extends AppCompatActivity {
    private RecyclerView mRecyclerPlan;
    private ArrayList<DietPlan> mListPlan;
    private AdapterDietPlans mAdapterPlans;
    private ProgressDialog mProgressDialog;
    private String name,gen,planName,goalId;
    SharedPrefManager sharedPrefManager;
    Activity context = this;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diet_plans);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Diet Plans");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager = new SharedPrefManager();
        goalId = sharedPrefManager.getGoalId(context);

        mRecyclerPlan =findViewById(R.id.recyclerPlaces);
        mRecyclerPlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListPlan = new ArrayList<>();

        mAdapterPlans = new AdapterDietPlans(mListPlan,ViewDietPlansActivity.this);
        mRecyclerPlan.setAdapter(mAdapterPlans);
        mAdapterPlans.setOnPlaceClickListener(new PlaceClickListener());

        fab = findViewById(R.id.fabAddPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewDietPlansActivity.this,CreateDietPlanActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

        mRecyclerPlan.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) {
                    fab.hide();
                    return;
                }
                if (dy < 0) {
                    fab.show();
                }
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading Diet Plans...");
        mProgressDialog.show();
        sendPostRequest();

    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_diet_plans",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();

                        JSONArray jArrPerson = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jArrPerson = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jArrPerson.length(); i++) {

                                JSONObject jPerson = jArrPerson.getJSONObject(i);
                                DietPlan diet = new DietPlan();

                                diet.dietPlan = jPerson.getString("plan_name");
                                diet.name = jPerson.getString("name");
                                diet.startDate = jPerson.getString("start_date");
                                diet.endDate = jPerson.getString("end_date");
                                diet.userId = jPerson.getString("user_id");
                                diet.isActive = jPerson.getString("is_active");
                                System.out.println("Plan name : "+diet.isActive);
                                mListPlan.add(diet);
                            }

                            mAdapterPlans.notifyDataSetChanged();

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

        MySingleton.getInstance(ViewDietPlansActivity.this).addTorequestque(stringRequest);
    }

    class PlaceClickListener implements AdapterDietPlans.OnPlaceClickListener {

        @Override
        public void onPlaceClick(DietPlan diet) {

            Intent i = new Intent(ViewDietPlansActivity.this, DietPlanDetailsActivity.class);
            i.putExtra("dietPlan", diet.dietPlan);
            i.putExtra("name",diet.name);
            i.putExtra("userId",diet.userId);
            i.putExtra("startDate",diet.startDate);
            i.putExtra("endDate",diet.endDate);
            i.putExtra("isActive",diet.isActive);
            startActivity(i);

        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        //Refresh your stuff here
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
