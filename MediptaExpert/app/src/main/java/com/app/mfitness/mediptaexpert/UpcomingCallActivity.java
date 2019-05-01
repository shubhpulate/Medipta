package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class UpcomingCallActivity extends AppCompatActivity{
    private RecyclerView mRecyclerCall;
    private ArrayList<UpcomingCall> mListCall;
    private AdapterUpcomingCall mAdapterCall;
    private ProgressDialog mProgressDialog;
    private String namePaid;
    private ImageView mImgEmpty;
    SharedPrefManager sharedPrefManager;
    String expertId;
    Activity context = this;
    private TextView txtEmptyView;
//    SwipeRefreshLayout swipeRefreshLayout;
    private String server_URL = RestAPI.dev_api + "api/method/phr.get_upcoming_fitness_call";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_call);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Upcoming Call");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
//        swipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE,
//                Color.RED, Color.CYAN);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new SendPostRequest().execute();
//            }
//        });

        txtEmptyView = findViewById(R.id.empty_viewUpcoming);
        mImgEmpty = findViewById(R.id.imgEmptyUpcoming);

        sharedPrefManager = new SharedPrefManager();
        expertId = sharedPrefManager.getExpertId(context);

        mRecyclerCall =findViewById(R.id.recyclerPlaces);
        mRecyclerCall.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListCall = new ArrayList<>();

        mAdapterCall = new AdapterUpcomingCall(mListCall);
        mRecyclerCall.setAdapter(mAdapterCall);
        mAdapterCall.setOnPlaceClickListener(new PlaceClickListener());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Upcoming calls...");

        mProgressDialog.show();

        sendPostRequest();
        if (mListCall.size() == 0){
            mRecyclerCall.setVisibility(View.GONE);
            txtEmptyView.setVisibility(View.VISIBLE);
            mImgEmpty.setVisibility(View.VISIBLE);
        }else {
            mRecyclerCall.setVisibility(View.VISIBLE);
            txtEmptyView.setVisibility(View.GONE);
            mImgEmpty.setVisibility(View.GONE);
        }

    }

    class PlaceClickListener implements AdapterUpcomingCall.OnPlaceClickListener {

        @Override
        public void onPlaceClick(final UpcomingCall call) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpcomingCallActivity.this);

            alertDialog.setTitle("Call Client...");

            alertDialog.setMessage("Call client now ?");

            alertDialog.setIcon(R.drawable.medipta);

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    final int REQUEST_PHONE_CALL = 1;
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call.contact));

                    if (ContextCompat.checkSelfPermission(UpcomingCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UpcomingCallActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    }
                    else {
                        startActivity(callIntent);
                    }
                }
            });

            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                server_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();

                        JSONArray jArrPerson = null;
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            jArrPerson = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jArrPerson.length(); i++) {
                                System.out.println("json lenth : " + jArrPerson.length());

                                JSONObject jPerson = jArrPerson.getJSONObject(i);
                                UpcomingCall call = new UpcomingCall();

                                namePaid = jPerson.getString("name");

                                call.name = jPerson.getString("first_name");
                                call.contact = jPerson.getString("contact");
                                call.time = jPerson.getString("call_date_time");

                                mListCall.add(call);

                            }

                            mAdapterCall.notifyDataSetChanged();
                            Log.w("tag","caount : "+mListCall.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//            swipeRefreshLayout.setRefreshing(false);
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

        MySingleton.getInstance(UpcomingCallActivity.this).addTorequestque(stringRequest);
    }

    public class SendPostRequest extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {

            try{

                URL url = new URL(server_URL);

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("expert_id", expertId);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("data",jsonObject);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
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
                    String line = "";

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

            mProgressDialog.dismiss();

            JSONArray jArrPerson = null;
            try {

                JSONObject jsonObject = new JSONObject(response);
                   
                    jArrPerson = jsonObject.getJSONArray("message");
                    for (int i = 0; i < jArrPerson.length(); i++) {
                        System.out.println("json lenth : " + jArrPerson.length());

                        JSONObject jPerson = jArrPerson.getJSONObject(i);
                        UpcomingCall call = new UpcomingCall();

                        namePaid = jPerson.getString("name");

                        call.name = jPerson.getString("first_name");
                        call.contact = jPerson.getString("contact");
                        call.time = jPerson.getString("call_date_time");

                        mListCall.add(call);

                    }

                mAdapterCall.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();

            }

//            swipeRefreshLayout.setRefreshing(false);
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
