package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {
    private String goalID,neck,waist,thighs,chest,date;
    private TextView mTxtNeck,mTxtWaist,mTxtThighs,mTxtChest,mTxtDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Fitness Progress");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTxtNeck = findViewById(R.id.txtNeck);
        mTxtWaist = findViewById(R.id.txtWaist);
        mTxtThighs =findViewById(R.id.txtThighs);
        mTxtChest = findViewById(R.id.txtChest);
        mTxtDate = findViewById(R.id.txtDate);

        Intent intent = getIntent();
        goalID = intent.getStringExtra("goalID");

        sendRequest();
    }

    public void sendRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_user_fitness_progress",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag", "Fragment : " + response);

                        JSONArray jArrPerson = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jArrPerson = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jArrPerson.length(); i++) {

                                JSONObject jItem = jArrPerson.getJSONObject(i);
                                neck = jItem.getString("neck");
                                chest = jItem.getString("chest");
                                waist = jItem.getString("waist");
                                thighs = jItem.getString("thigs");
                                date = jItem.getString("created_date");
                                mTxtNeck.setText(neck);
                                mTxtChest.setText(chest);
                                mTxtWaist.setText(waist);
                                mTxtThighs.setText(thighs);
                                mTxtDate.setText(date);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();

                userParams.put("goal_id",goalID);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());

                return params;
            }
        };
        MySingleton.getInstance(ProgressActivity.this).addTorequestque(stringRequest);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
