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

public class CardioActivity extends AppCompatActivity {
    private RecyclerView mRecyclerCardio;
    private ArrayList<Cardio> mListCardio;
    private AdapterCardio mAdapterCardio;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Cardio Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshCardio);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postRequest();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Cardio Exercises..");
        mProgressDialog.show();

        mRecyclerCardio =findViewById(R.id.recyclerCardio);
        mRecyclerCardio.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListCardio = new ArrayList<>();
        mRecyclerCardio.setNestedScrollingEnabled(false);

        mAdapterCardio = new AdapterCardio(mListCardio);
        mRecyclerCardio.setAdapter(mAdapterCardio);
        mAdapterCardio.setOnPlaceClickListener(new PlaceClickListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddCardio);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardioActivity.this, AddCardioExercise.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });

        postRequest();
    }

    public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_exercise_plan_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipeRefreshLayout.setRefreshing(false);

                        try {
                            mProgressDialog.dismiss();

                            mListCardio.clear();
                            JSONObject jsonObject= new JSONObject(response);
                            JSONObject message = jsonObject.getJSONObject("message");
                            JSONArray exercise = message.getJSONArray("cardio_items");
                            for (int i = 0; i < exercise.length(); i++){
                                Cardio cardio = new Cardio();
                                JSONObject jPre = exercise.getJSONObject(i);
                                cardio.cardioName = jPre.getString("item_name");
                                cardio.cardioType = jPre.getString("item_type");
                                cardio.cardioImage = jPre.getString("item_image");
                                cardio.description = jPre.getString("description");
                                mListCardio.add(cardio);
                            }
                            mAdapterCardio.notifyDataSetChanged();

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
                userParams.put("exercise_plan_name",name);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(CardioActivity.this).addTorequestque(stringRequest);

    }

    public class SendPostRequest extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {

            try{

                URL url = new URL(RestAPI.dev_api + "api/method/phr.get_exercise_plan_details");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("exercise_plan_name", name);

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
            swipeRefreshLayout.setRefreshing(false);

            try {
                mProgressDialog.dismiss();

                mListCardio.clear();
                JSONObject jsonObject= new JSONObject(response);
                JSONObject message = jsonObject.getJSONObject("message");
                JSONArray exercise = message.getJSONArray("cardio_items");
                for (int i = 0; i < exercise.length(); i++){
                    Cardio cardio = new Cardio();
                    JSONObject jPre = exercise.getJSONObject(i);
                    cardio.cardioName = jPre.getString("item_name");
                    cardio.cardioType = jPre.getString("item_type");
                    cardio.cardioImage = jPre.getString("item_image");
                    cardio.description = jPre.getString("description");
                    mListCardio.add(cardio);
                }
                mAdapterCardio.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("response","Response = "+response);
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

    }
    class PlaceClickListener implements AdapterCardio.OnPlaceClickListener {

        @Override
        public void onPlaceClick(Cardio cardio) {

            Intent i = new Intent(CardioActivity.this, CardioDetailsActivity.class);
            i.putExtra("exercise", cardio.cardioName);
            i.putExtra("description",cardio.description);
            i.putExtra("image",cardio.cardioImage);
            startActivity(i);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
