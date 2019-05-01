package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class StrengthActivity extends AppCompatActivity {
    private RecyclerView mRecyclerStrength;
    private ArrayList<Strength> mListStrength;
    private AdapterStrength mAdapterStrength;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String name;
    private String SERVER_URL = RestAPI.dev_api + "api/method/phr.get_exercise_plan_details";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strength);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Strength Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshStrength);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                new SendPostRequest().execute();
                sendPostRequest();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Strength Exercise..");
        mProgressDialog.show();

        mRecyclerStrength =findViewById(R.id.recyclerStrength);
        mRecyclerStrength.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListStrength = new ArrayList<>();
        mRecyclerStrength.setNestedScrollingEnabled(false);
        mRecyclerStrength.setHasFixedSize(true);
        mRecyclerStrength.setItemViewCacheSize(20);
        mRecyclerStrength.setDrawingCacheEnabled(true);
        mRecyclerStrength.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mAdapterStrength = new AdapterStrength(mListStrength);
        mRecyclerStrength.setAdapter(mAdapterStrength);
        mAdapterStrength.setOnPlaceClickListener(new PlaceClickListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddStrength);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrengthActivity.this, AddStrengthExercise.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });

//        new SendPostRequest().execute();
        sendPostRequest();
    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response : "+response);
                        mProgressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        mListStrength.clear();
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            JSONArray exercise = msg.getJSONArray("exercise_items");

                            for (int i = 0; i < exercise.length(); i++){
                                Strength strength = new Strength();
                                JSONObject jPre = exercise.getJSONObject(i);
                                strength.exercise = jPre.getString("item_name");
                                strength.type = jPre.getString("item_type");
                                strength.image = jPre.getString("item_image");
                                strength.description = jPre.getString("description");
                                mListStrength.add(strength);
                            }
                            mAdapterStrength.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                userParams.put("exercise_plan_name",name);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());


//                Map<String,String> params = new HashMap<>();
//                Map<String,String> userParams = new HashMap<>();
//                userParams.put("exercise_plan_name",name);
//
//                JSONObject userJson = new JSONObject(userParams);
//                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(StrengthActivity.this).addTorequestque(stringRequest);
    }


    class PlaceClickListener implements AdapterStrength.OnPlaceClickListener {

        @Override
        public void onPlaceClick(Strength strength) {

            Intent i = new Intent(StrengthActivity.this, StrengthDetailsActivity.class);
            i.putExtra("exercise", strength.exercise);
            i.putExtra("image",strength.image);
            i.putExtra("type",strength.type);
            i.putExtra("description",strength.description);
            startActivity(i);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
