package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class TodayCallActivity extends AppCompatActivity {
    private RecyclerView mRecyclerCall;
    private ArrayList<UpcomingCall> mListCall;
    private AdapterTodayCall mAdapterCall;
    private ProgressDialog mProgressDialog;
    private String namePaid,contact,expertId;
    private TextView mTxtEmptyView;
    private ImageView mImgEmpty;
    SharedPrefManager sharedPrefManager;
    Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_call);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Today's Call");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager = new SharedPrefManager();
        expertId = sharedPrefManager.getExpertId(context);

        mTxtEmptyView = findViewById(R.id.empty_view);
        mImgEmpty = findViewById(R.id.imgEmpty);

        mRecyclerCall =findViewById(R.id.recyclerPlaces);
        mRecyclerCall.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListCall = new ArrayList<>();

        mAdapterCall = new AdapterTodayCall(mListCall);
        mRecyclerCall.setAdapter(mAdapterCall);
        mAdapterCall.setOnPlaceClickListener(new PlaceClickListener());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Today's calls...");

        mProgressDialog.show();
        sendPostRequest();

        if (mListCall.isEmpty()){
            mRecyclerCall.setVisibility(View.GONE);
            mTxtEmptyView.setVisibility(View.VISIBLE);
            mImgEmpty.setVisibility(View.VISIBLE);
        }else {
            mRecyclerCall.setVisibility(View.VISIBLE);
            mTxtEmptyView.setVisibility(View.GONE);
            mImgEmpty.setVisibility(View.GONE);
        }

    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_today_fitness_call",
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
                                    UpcomingCall call = new UpcomingCall();

                                    namePaid = jPerson.getString("name");
                                    System.out.println("name = " + namePaid);
                                    contact = jPerson.getString("contact");

                                    call.name = jPerson.getString("first_name");
                                    call.contact = jPerson.getString("contact");
                                    call.time = jPerson.getString("call_date_time");

                                    mListCall.add(call);
                                }

                            mAdapterCall.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("response","Response : "+response);
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
        MySingleton.getInstance(TodayCallActivity.this).addTorequestque(stringRequest);
    }

    class PlaceClickListener implements AdapterTodayCall.OnPlaceClickListener {
        @Override
        public void onPlaceClick(UpcomingCall call) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodayCallActivity.this);

            alertDialog.setTitle("Call Client...");

            alertDialog.setMessage("Are you sure you want call client ?");

            alertDialog.setIcon(R.drawable.medipta);

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    final int REQUEST_PHONE_CALL = 1;
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact));

                    if (ContextCompat.checkSelfPermission(TodayCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TodayCallActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    }

                    else
                    {
                        startActivity(callIntent);
                    }

                }
            });

            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            alertDialog.show();

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
