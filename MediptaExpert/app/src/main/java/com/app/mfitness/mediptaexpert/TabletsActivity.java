package com.app.mfitness.mediptaexpert;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class TabletsActivity extends AppCompatActivity implements RecyclerItemTouchHelperTablet.RecyclerItemTouchHelperListener {
    private Button mBtnAddTablet;
    final Context context = this;
    private TextInputLayout mTxtInputTabletTime,mTxtInputTablet;
    private EditText mEdtTabletName,mEdtTabletTime;
    public boolean isValid = true;
    private String tabletName,tabletTime,name,itemName;
    private String itemType = "tablet";
    private RecyclerView mRecyclerTablet;
    private ArrayList<Tablet> mListTablet;
    private AdapterTablets mAdapterTablet;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablets);
        mBtnAddTablet = findViewById(R.id.btnAddTablet);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tablets");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coordinatorLayout = findViewById(R.id.coordinatorLayoutTablet);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Tablets..");
        mProgressDialog.show();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshTablet);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendPostRequest();
            }
        });

        mRecyclerTablet =findViewById(R.id.recyclerPackage);
        mRecyclerTablet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListTablet = new ArrayList<>();
        mRecyclerTablet.setNestedScrollingEnabled(false);
        mRecyclerTablet.setItemAnimator(new DefaultItemAnimator());
        mRecyclerTablet.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerTablet.setAdapter(mAdapterTablet);

        mAdapterTablet = new AdapterTablets(mListTablet);
        mAdapterTablet.setOnPlaceClickListener(new PlaceClickListener());
        mRecyclerTablet.setAdapter(mAdapterTablet);
        mAdapterTablet.setOnPlaceClickListener(new PlaceClickListener());

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperTablet(0, ItemTouchHelper.LEFT, TabletsActivity.this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerTablet);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        sendPostRequest();

        mBtnAddTablet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.lay_prompt_tablet, null);

                mEdtTabletName = promptsView.findViewById(R.id.edtTablet);
                mEdtTabletTime = promptsView.findViewById(R.id.edtTabletTime);
                mTxtInputTablet = promptsView.findViewById(R.id.txtInputTablet);
                mTxtInputTabletTime = promptsView.findViewById(R.id.txtInputTime);

                final AlertDialog d = new AlertDialog.Builder(context)
                        .setView(promptsView)
                        .setTitle("")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                if (mEdtTabletName.getText().toString().isEmpty()){

                                    mTxtInputTablet.setError("Please enter Tablet");
                                    isValid=false;

                                }else if (mEdtTabletTime.getText().toString().isEmpty()){

                                    mTxtInputTabletTime.setError("Please Enter Time");
                                    isValid = false;

                                }else{

                                    postTablet();
                                    mTxtInputTablet.setErrorEnabled(false);
                                    mTxtInputTabletTime.setErrorEnabled(false);
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterTablets.HolderTablets) {

             itemName = mListTablet.get(viewHolder.getAdapterPosition()).name;
            System.out.println("item is : "+itemName);
             tabletName = mListTablet.get(viewHolder.getAdapterPosition()).tabletName;

            final Tablet deletedItem = mListTablet.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            mAdapterTablet.removeItem(viewHolder.getAdapterPosition());
//            new DeleteRequest().execute();
//            Snackbar snackbar = Snackbar.make(coordinatorLayout,tabletName+" removed from tablets",Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mAdapterTablet.restoreItem(deletedItem, deletedIndex);
//                    new DeleteRequest().cancel(true);
//                }
//            });
//
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to remove this Tablet?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            new DeleteRequest().execute();
                            deleteRequest();
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAdapterTablet.restoreItem(deletedItem, deletedIndex);
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }

    public void postTablet(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.add_tablest",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            Tablet tablet = new Tablet();
                            tablet.tabletName = msg.getString("tablet_name");
                            mListTablet.add(tablet);

                            if (msg.getString("returncode").equals("200")){

                                Toast.makeText(getApplicationContext(),"Tablet Added Successfully..!!",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Please enter required fields", Toast.LENGTH_SHORT).show();
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
                tabletName = mEdtTabletName.getText().toString();
                tabletTime = mEdtTabletTime.getText().toString();
                userParams.put("tablet_name",tabletName);
                userParams.put("name",name);
                userParams.put("tablet_time",tabletTime);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(TabletsActivity.this).addTorequestque(stringRequest);
    }

    public void deleteRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.delete_item",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response : "+response);
                        mListTablet.clear();

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
                userParams.put("name",itemName);
                userParams.put("item_type",itemType);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(TabletsActivity.this).addTorequestque(stringRequest);
    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_diet_plan_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mProgressDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            mListTablet.clear();

                            JSONObject jsonObject= new JSONObject(response);
                            JSONObject message = jsonObject.getJSONObject("message");
                            JSONArray tablet = message.getJSONArray("tablets");

                            for (int i = 0; i < tablet.length(); i++){
                                Tablet tablet1 = new Tablet();
                                JSONObject jPre = tablet.getJSONObject(i);
                                tablet1.tabletName = jPre.getString("tablet_name");
                                tablet1.tabletTime = jPre.getString("tablet_time");
                                tablet1.name = jPre.getString("name");
                                mListTablet.add(tablet1);
                            }

                            mAdapterTablet.notifyDataSetChanged();

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

        MySingleton.getInstance(TabletsActivity.this).addTorequestque(stringRequest);
    }

    class PlaceClickListener implements AdapterTablets.OnPlaceClickListener {
        @Override
        public void onPlaceClick(final Tablet tablet) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to remove "+tablet.tabletName+" ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    RestAPI.dev_api + "api/method/phr.delete_item",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e("response","Response : "+response);
                                            mListTablet.clear();
                                            finish();
                                            startActivity(getIntent());

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
                                    userParams.put("name",tablet.name);
                                    userParams.put("item_type",itemType);

                                    JSONObject userJson = new JSONObject(userParams);
                                    params.put("data",userJson.toString());
                                    return params;
                                }
                            };

                            MySingleton.getInstance(TabletsActivity.this).addTorequestque(stringRequest);

                        }
                    });

            builder.setNegativeButton("No",
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
