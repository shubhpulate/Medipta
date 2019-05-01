package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExerciseItemActivity extends AppCompatActivity{
    private RecyclerView mRecyclerItem;
    private ArrayList<ExerciseItem> mListItem;
    private AdapterExerciseItem mAdapterItem;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_item);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Exercise Items");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Intent intent = getIntent();
//        name = intent.getStringExtra("name");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshExeItem);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestQueue requestQueue = Volley.newRequestQueue(ExerciseItemActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        RestAPI.dev_api + "api/method/phr.get_exercise_items",
                        null,
                        new ResponseListener(),
                        new ErrorListener()
                );

                requestQueue.add(jsonObjectRequest);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Exercise Items..");
        mProgressDialog.show();

        mRecyclerItem =findViewById(R.id.recyclerExeItem);
        mRecyclerItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListItem = new ArrayList<>();
        mRecyclerItem.setNestedScrollingEnabled(false);

        mAdapterItem = new AdapterExerciseItem(mListItem);
        mRecyclerItem.setAdapter(mAdapterItem);
        mAdapterItem.setOnPlaceClickListener(new PlaceClickListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddExeItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseItemActivity.this, AddExerciseItem.class);
                startActivity(intent);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_exercise_items",
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
            swipeRefreshLayout.setRefreshing(false);
            mListItem.clear();

            JSONArray jArrPerson = null;
            try {
                jArrPerson = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrPerson.length(); i++) {

                    JSONObject jItem = jArrPerson.getJSONObject(i);
                    ExerciseItem item = new ExerciseItem();
                    item.exercise = jItem.getString("item_name");
                    item.type = jItem.getString("item_type");
                    item.imageUrl = jItem.getString("item_image");
                    item.description = jItem.getString("description");
                    item.name = jItem.getString("name");

                    mListItem.add(item);
                }

                mAdapterItem.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_exercise_item,menu);

        MenuItem search = menu.findItem(R.id.action_search_exe);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapterItem.getFilter().filter(newText);
                return true;
            }
        });
    }

    class PlaceClickListener implements AdapterExerciseItem.OnPlaceClickListener {

        @Override
        public void onPlaceClick(ExerciseItem item) {


        }

        @Override
        public void onEditClick(ExerciseItem item) {
            Intent i  = new Intent(ExerciseItemActivity.this,EditExeItemActivity.class);
            i.putExtra("exercise",item.exercise);
            i.putExtra("type",item.type);
            i.putExtra("image",item.imageUrl);
            i.putExtra("description",item.description);
            i.putExtra("name",item.name);
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
