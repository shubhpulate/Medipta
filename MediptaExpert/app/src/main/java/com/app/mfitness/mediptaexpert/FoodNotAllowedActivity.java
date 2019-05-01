package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class FoodNotAllowedActivity extends AppCompatActivity{
    private Button mBtnFoodNotAllowed;
    final Context context = this;
    EditText mEdtFoodNotAllowed;
    String foodName,name;
    TextInputLayout mInput;
    public boolean isValid = true;
    private RecyclerView mRecyclerFood;
    private ArrayList<FoodNotAllowed> mListFood;
    private AdapterFoodNotAllowed mAdapterFood;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_not_allowed);

        mBtnFoodNotAllowed = findViewById(R.id.btnFoodNotAllowed);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Food Not Allowed");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshFoodNA);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getRequest();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Food Not Allowed..");
        mProgressDialog.show();

        mRecyclerFood =findViewById(R.id.recyclerFoodNA);
        mRecyclerFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListFood = new ArrayList<>();
        mRecyclerFood.setNestedScrollingEnabled(false);

        mAdapterFood = new AdapterFoodNotAllowed(mListFood);
        mRecyclerFood.setAdapter(mAdapterFood);
        mAdapterFood.setOnPlaceClickListener(new PlaceClickListener());

        getRequest();

        mBtnFoodNotAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.lay_prompt_food, null);
                mEdtFoodNotAllowed = (EditText) promptsView
                        .findViewById(R.id.edtFoodnAllowed);
                mInput = promptsView.findViewById(R.id.txtInputFoodNA);
                final AlertDialog d = new AlertDialog.Builder(context)
                        .setView(promptsView)
                        .setTitle("")
                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (mEdtFoodNotAllowed.getText().toString().isEmpty()){
                                    mInput.setError("Please enter food item");
                                    isValid=false;
                                }else {

                                    SendRequest();
                                    mInput.setErrorEnabled(false);
                                    d.dismiss();
                                    finish();
                                    startActivity(getIntent());

                                }
                            }
                        });
                    }
                });
                d.show();

            }
        });
    }

    public void SendRequest(){
        final ProgressDialog progressDialog = new ProgressDialog(FoodNotAllowedActivity.this);
        progressDialog.setMessage("Adding not allowed food..");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api  + "api/method/phr.add_not_allowed_food",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag","onResponse : " + response);
                        progressDialog.dismiss();

                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            String mm = msg.getString("doc");

                            if (msg.getString("returncode").equals("200")){

                                Toast.makeText(getApplicationContext(),"Food Item Added Successfully..!!",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Something went wrong..!!",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(FoodNotAllowedActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Map<String, String> userParams = new HashMap<String, String>();
                foodName = mEdtFoodNotAllowed.getText().toString();

                userParams.put("food_name",foodName);
                userParams.put("name", name);

                JSONObject userJSON = new JSONObject(userParams);
                params.put("data", userJSON.toString());

                return params;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(FoodNotAllowedActivity.this).addTorequestque(stringRequest);

    }

    public void getRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_diet_plan_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response = "+response);
                        try {
                            mProgressDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            mListFood.clear();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject message = jsonObject.getJSONObject("message");
                            JSONArray tablet = message.getJSONArray("food_not_allowed");
                            for (int i = 0; i < tablet.length(); i++){
                                FoodNotAllowed food = new FoodNotAllowed();
                                JSONObject jPre = tablet.getJSONObject(i);
                                food.foodNotAllowed = jPre.getString("food_name");
                                food.name = jPre.getString("name");
                                mListFood.add(food);
                            }
                            mAdapterFood.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<>();
                Map<String, String> userParams = new HashMap<String, String>();

                userParams.put("name", name);

                JSONObject userJSON = new JSONObject(userParams);
                params.put("data", userJSON.toString());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(FoodNotAllowedActivity.this).addTorequestque(stringRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class PlaceClickListener implements AdapterFoodNotAllowed.OnPlaceClickListener {
        @Override
        public void onPlaceClick(FoodNotAllowed food) {


        }
    }
}
