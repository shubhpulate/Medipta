package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FitnessPackagesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerPackage;
    private ArrayList<FitnessPackage> mListPackage;
    private AdapterFitnessPackage mAdapterPackage;
    private ProgressDialog mProgressDialog;
    private String name;
    private SearchView searchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_packages);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Fitness Package");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mBtnCreatePackage = findViewById(R.id.btnCreatePackage);

        fab = (FloatingActionButton) findViewById(R.id.fabAddPackage);
        mRecyclerPackage =findViewById(R.id.recyclerPackage);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FitnessPackagesActivity.this,CreatePackageActivity.class);
                startActivity(i);
            }
        });

        mRecyclerPackage.addOnScrollListener(new RecyclerView.OnScrollListener(){
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

        mRecyclerPackage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListPackage = new ArrayList<>();
        mRecyclerPackage.setNestedScrollingEnabled(false);

        mAdapterPackage = new AdapterFitnessPackage(mListPackage);
        mRecyclerPackage.setAdapter(mAdapterPackage);
        mAdapterPackage.setOnPlaceClickListener(new PlaceClickListener());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Fitness Packages...");
        mProgressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_fitness_packages",
                null,
                new ResponseListener(),
                new ErrorListener()
        );

        requestQueue.add(jsonObjectRequest);

    }

    class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mProgressDialog.dismiss();
        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag", "Fragment : " + jResponse);

            mProgressDialog.dismiss();

            JSONArray jArrPerson = null;
            try {
                jArrPerson = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrPerson.length(); i++) {

                    JSONObject jPerson = jArrPerson.getJSONObject(i);
                    FitnessPackage fitnessPackage = new FitnessPackage();

                    name = jPerson.getString("name");
                    System.out.println("name = " + name);

                    fitnessPackage.packageName = jPerson.getString("package_name");
                    fitnessPackage.packageType = jPerson.getString("package_type");
                    fitnessPackage.duration = jPerson.getString("duration");
                    fitnessPackage.exercisePlans = jPerson.getInt("exercise_plans_count");
                    fitnessPackage.dietPlans = jPerson.getString("diet_plans_count");
                    fitnessPackage.calls = jPerson.getString("call_count");
                    fitnessPackage.price = jPerson.getString("amount");
                    fitnessPackage.imageUrl = jPerson.getString("image");

                    mListPackage.add(fitnessPackage);
                }

                mAdapterPackage.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_package, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapterPackage.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    class PlaceClickListener implements AdapterFitnessPackage.OnPlaceClickListener {
        @Override
        public void onPlaceClick(FitnessPackage fitnessPackage) {

            Intent i = new Intent(FitnessPackagesActivity.this, PaidUserDetailsActivity.class);
            i.putExtra("packageName", fitnessPackage.packageName);
            i.putExtra("packageType", fitnessPackage.packageType);
            i.putExtra("duration", fitnessPackage.duration);
            i.putExtra("exercisePlans", fitnessPackage.exercisePlans);
            i.putExtra("dietPlans", fitnessPackage.dietPlans);
            i.putExtra("calls", fitnessPackage.calls);
            i.putExtra("price", fitnessPackage.price);
            i.putExtra("name", name);
            startActivity(i);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }

        super.onBackPressed();
    }
}
