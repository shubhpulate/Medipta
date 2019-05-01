package com.app.mfitness.mediptaexpert;

import android.app.Activity;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscriptionExpiredActivity extends AppCompatActivity{
    private RecyclerView mRecyclerExpired;
    private ArrayList<Person> mListExpired;
    private AdapterSubscriptionExpired mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    Activity context = this;
    SharedPrefManager sharedPrefManager;
    private SearchView searchView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_expired);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Subscription Expired");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emptyView = findViewById(R.id.empty_view);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        mRecyclerExpired = findViewById(R.id.recyclerExpired);
        mRecyclerExpired.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshExpired);

        mListExpired = new ArrayList<>();
        mAdapter = new AdapterSubscriptionExpired(mListExpired);
        mRecyclerExpired.setAdapter(mAdapter);
        mAdapter.setOnPlaceClickListener(new PlaceClickListener());

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_expired_fitness_users",
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
            Log.e("tag", "Response : " + jResponse);

            JSONArray jArrPerson = null;
            try {
                mListExpired.clear();
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

                    mListExpired.add(person);
                }
                System.out.println("size : "+mListExpired.size());
                if (mListExpired.size() == 0){
                    mRecyclerExpired.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerExpired.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            mProgressDialog.dismiss();

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
        return super.onCreateOptionsMenu(menu);
    }

    private void search(SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public class PlaceClickListener implements AdapterSubscriptionExpired.OnPlaceClickListener{
        @Override
        public void onPlaceClick(Person person) {

        }
    }
}
