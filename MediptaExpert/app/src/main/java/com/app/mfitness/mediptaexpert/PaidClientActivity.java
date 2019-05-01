package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaidClientActivity extends AppCompatActivity{
    private RecyclerView mRecyclerPerson;
    private ArrayList<Person> mListPerson;
    private AdapterPaid mAdapterPerson;
    private String namePaid;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    private SwipeRefreshLayout swipeRefreshLayout;
    Activity context = this;
    SharedPrefManager sharedPrefManager;
    private SearchView searchView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_activity_paid);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Paid Client");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emptyView = findViewById(R.id.empty_view);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        sharedPrefManager = new SharedPrefManager();

        mRecyclerPerson =findViewById(R.id.recyclerPlaces);
        mRecyclerPerson.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListPerson = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshPaid);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RequestQueue requestQueue = Volley.newRequestQueue(PaidClientActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        RestAPI.dev_api + "api/method/phr.get_paid_fitness_users",
                        null,
                        new ResponseListener(),
                        new ErrorListener()
                );

                requestQueue.add(jsonObjectRequest);
            }
        });

        mAdapterPerson = new AdapterPaid(mListPerson);
        mRecyclerPerson.setAdapter(mAdapterPerson);
        mAdapterPerson.setOnPlaceClickListener(new PlaceClickListener());

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_paid_fitness_users",
                null,
                new ResponseListener(),
                new ErrorListener()
        );

        requestQueue.add(jsonObjectRequest);
    }

    class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag", "Fragment : " + jResponse);

            JSONArray jArrPerson = null;
            try {
                mListPerson.clear();
                jArrPerson = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrPerson.length(); i++) {

                    JSONObject jPerson = jArrPerson.getJSONObject(i);
                    Person person = new Person();

                    person.goalId = jPerson.getString("name");
                    System.out.println("name = " + person.goalId);

                    person.name = jPerson.getString("first_name");
                    person.contact = jPerson.getString("contact");
                    person.fitnessGoal = jPerson.getString("fitness_goal");
                    person.email = jPerson.getString("email");
                    person.gender = jPerson.getString("gender");
                    person.BMI = jPerson.getString("bmi");
                    person.age = jPerson.getInt("age");
                    person.profileId = jPerson.getString("profile_id");

                    mListPerson.add(person);

                }

                if (mListPerson.size() == 0){
                    mRecyclerPerson.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerPerson.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                mAdapterPerson.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            swipeRefreshLayout.setRefreshing(false);
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_paid,menu);

        MenuItem search = menu.findItem(R.id.action_search_paid);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapterPerson.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class PlaceClickListener implements AdapterPaid.OnPlaceClickListener {
        @Override
        public void onPlaceClick(Person person) {

            Intent i = new Intent(PaidClientActivity.this, PaidUserDetailsActivity.class);
            i.putExtra("firstName", person.name);
            i.putExtra("contact", person.contact);
            i.putExtra("fitnessGoal", person.fitnessGoal);
            i.putExtra("email", person.email);
            i.putExtra("gender", person.gender);
            i.putExtra("bmi", person.BMI);
            i.putExtra("age", person.age);
            i.putExtra("goalID", person.goalId);
            i.putExtra("profileId",person.profileId);

            sharedPrefManager.saveGoalId(context,person.goalId);
            sharedPrefManager.setUserId(context,person.profileId);

            startActivity(i);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
