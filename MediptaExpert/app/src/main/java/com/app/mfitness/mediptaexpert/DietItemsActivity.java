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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

public class DietItemsActivity extends AppCompatActivity{
    private RecyclerView mRecyclerDiet;
    private ArrayList<Diet> mListDiet;
    private AdapterDietItems mAdapterDiet;
    private ProgressDialog mProgressDialog;
    private SearchView searchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_items);

        mRecyclerDiet = findViewById(R.id.recyclerPlaces);
        mRecyclerDiet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListDiet = new ArrayList<>();

        mAdapterDiet = new AdapterDietItems(mListDiet);
        mAdapterDiet.setOnPlaceClickListener(new PlaceClickListener());
        mRecyclerDiet.setAdapter(mAdapterDiet);
        fab = findViewById(R.id.fab);

        mRecyclerDiet.addOnScrollListener(new RecyclerView.OnScrollListener(){
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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Diet Items...");
        mProgressDialog.show();

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Diet Items");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietItemsActivity.this, AddDietActivity.class);
                startActivity(intent);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_diet_items",
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
            error.printStackTrace();
        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {

            Log.e("tag", "Fragment : " + jResponse);

            mProgressDialog.dismiss();

            JSONArray jArrDiet = null;
            try {

                jArrDiet = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrDiet.length(); i++) {

                    JSONObject jDiet = jArrDiet.getJSONObject(i);
                    Diet diet = new Diet();

                    diet.itemName = jDiet.getString("item_name");
                    diet.itemType = jDiet.getString("item_type");
                    diet.unit = jDiet.getString("item_unit");
                    diet.calories = jDiet.getString("item_calories");
                    diet.nameId = jDiet.getString("name");

                    mListDiet.add(diet);

                }

                mAdapterDiet.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_diet_item, menu);

        MenuItem search = menu.findItem(R.id.action_search_item);
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

                mAdapterDiet.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    class PlaceClickListener implements AdapterDietItems.OnPlaceClickListener {

        @Override
        public void onDeleteClick(Diet person) {
//            Intent i = new Intent(DietItemsActivity.this,PaidClientActivity.class);
//            startActivity(i);
        }

        @Override
        public void onEditClick(Diet diet) {
            Intent i = new Intent(DietItemsActivity.this,UpdateDietItemActivity.class);
            i.putExtra("name",diet.nameId);
            i.putExtra("itemName",diet.itemName);
            i.putExtra("itemType",diet.itemType);
            i.putExtra("itemUnit",diet.unit);
            i.putExtra("itemCalories",diet.calories);
            startActivity(i);
        }
    }
}
