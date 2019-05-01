package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class UnpaidClientActivity extends AppCompatActivity {
    private RecyclerView mRecyclerPerson;
    private ArrayList<Person> mListPerson;
    private AdapterUnpaid mAdapterUnpaid;
//    private ProgressDialog mProgressDialog;
    String name;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unpaid_clients);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Unpaid Client");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshUnpaid);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestQueue requestQueue = Volley.newRequestQueue( UnpaidClientActivity.this );

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        RestAPI.dev_api + "api/method/phr.get_unpaid_fitness_users",
                        null,
                        new ResponseListener(),
                        new ErrorListener()
                );

                requestQueue.add( jsonObjectRequest );
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestQueue requestQueue = Volley.newRequestQueue( UnpaidClientActivity.this );

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        RestAPI.dev_api + "api/method/phr.get_unpaid_fitness_users",
                        null,
                        new ResponseListener(),
                        new ErrorListener()
                );

                requestQueue.add( jsonObjectRequest );
            }
        });

        mRecyclerPerson = findViewById( R.id.recyclerPlaces );
        mRecyclerPerson.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false ));
        mListPerson = new ArrayList<>();

        mAdapterUnpaid = new AdapterUnpaid( mListPerson );
        mRecyclerPerson.setAdapter( mAdapterUnpaid );

        mAdapterUnpaid.setOnPlaceClickListener( new PlaceClickListener() );

//        mProgressDialog = new ProgressDialog( this );
//        mProgressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
//        mProgressDialog.setMessage("Fetching Unpaid Clients...");
//        mProgressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue( this );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_unpaid_fitness_users",
                null,
                new ResponseListener(),
                new ErrorListener()
        );

        requestQueue.add( jsonObjectRequest );
    }

    class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

//            mProgressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
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
                mAdapterUnpaid.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag","Fragment : "+jResponse);

//            mProgressDialog.dismiss();

            JSONArray jArrPerson = null;
            try {
                mListPerson.clear();
                jArrPerson = jResponse.getJSONArray("message");
                for( int i = 0; i < jArrPerson.length(); i++ ) {

                    JSONObject jPerson = jArrPerson.getJSONObject( i );
                    Person person = new Person();

                    name = jPerson.getString("name");
                    System.out.println("name = "+name);

                    person.name = jPerson.getString("first_name");
                    person.contact = jPerson.getString("contact");
                    person.fitnessGoal = jPerson.getString("fitness_goal");
                    person.email = jPerson.getString("email");
                    person.gender  = jPerson.getString("gender");
                    person.BMI = jPerson.getString("bmi");
                    person.age = jPerson.getInt("age");

                    mListPerson.add( person );
                }

                mAdapterUnpaid.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
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

    class PlaceClickListener implements AdapterUnpaid.OnPlaceClickListener {
        @Override
        public void onPlaceClick(Person person) {

            Intent i = new Intent(UnpaidClientActivity.this,UnpaidUserDetailsActivity.class);
            i.putExtra("firstName",person.name);
            i.putExtra("contact",person.contact);
            i.putExtra("fitnessGoal",person.fitnessGoal);
            i.putExtra("email",person.email);
            i.putExtra("gender",person.gender);
            i.putExtra("bmi",person.BMI);
            i.putExtra("age",person.age);
            i.putExtra("name",name);
            startActivity(i);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
