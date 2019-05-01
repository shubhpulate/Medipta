package com.app.mfitness.mediptaexpert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity{
    private RecyclerView mRecyclerNotification;
    private ArrayList<Notification> mListNotification;
    private AdapterNotification mAdapterNotification;
    private ProgressDialog mProgressDialog;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Notification");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager = new SharedPrefManager();

        mRecyclerNotification = findViewById(R.id.recyclerNotification);
        mRecyclerNotification.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mListNotification = new ArrayList<>();

        mAdapterNotification = new AdapterNotification(mListNotification,this);
        mRecyclerNotification.setAdapter(mAdapterNotification);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading Notification ..");
        mProgressDialog.show();

        getNotification();
    }

    public void  getNotification(){

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_all_unseen_gcms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("tag", "Response : " + response);
                        mProgressDialog.dismiss();
                        JSONArray jsonArray = null;
                        try {
                            JSONObject jObject  = new JSONObject(response);
                            JSONObject message = jObject.getJSONObject("message");
                            jsonArray = message.getJSONArray("notification_list");
                            for (int i = 0; i< jsonArray.length(); i++){
                                JSONObject jPerson = jsonArray.getJSONObject(i);

                                Notification notification = new Notification();
                                notification.count = message.getString("count");
                                notification.message = jPerson.getString("message");
                                System.out.println("message : "+notification.message);

                                mListNotification.add(notification);
                            }

                            mAdapterNotification.notifyDataSetChanged();

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

                userParams.put("profile_id",sharedPrefManager.getExpertId(NotificationActivity.this));
                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(NotificationActivity.this);
            dialog.setMessage("Are you sure you want to delete all messages ?");
            dialog.setCancelable(true);
            dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteMsg();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteMsg(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.set_all_gcm_as_seen_profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(NotificationActivity.this,"Messages Deleted Successfully",Toast.LENGTH_SHORT).show();
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
                userParams.put("profile_id",sharedPrefManager.getExpertId(NotificationActivity.this));
                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
