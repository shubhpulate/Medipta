package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {
    private RecyclerView mRecyclerItem;
    private ArrayList<Article> mListItem;
    private AdapterArticle mAdapterArticle;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPrefManager sharedPrefManager;
    private SearchView searchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Articles");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager = new SharedPrefManager();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshArticle);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookRequest();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Articles..");
        mProgressDialog.show();

        mRecyclerItem = findViewById(R.id.recyclerArticle);
        mRecyclerItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListItem = new ArrayList<>();
        mRecyclerItem.setNestedScrollingEnabled(false);

        mAdapterArticle = new AdapterArticle(mListItem);
        mRecyclerItem.setAdapter(mAdapterArticle);
        mAdapterArticle.setOnPlaceClickListener(new PlaceClickListener());


        fab = findViewById(R.id.fabAddArticle);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleActivity.this, AddArticleActivity.class);
                startActivity(intent);

            }
        });
        mRecyclerItem.addOnScrollListener(new RecyclerView.OnScrollListener(){
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

        getBookRequest();
    }

    public void getBookRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_dbook_list",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag", "Fragment : " + response);
                        mProgressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);

                        JSONArray jArrMessage = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            jArrMessage = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jArrMessage.length(); i++){
                                JSONObject jItem = jArrMessage.getJSONObject(i);
                                Article article = new Article();
                                article.image = jItem.getString("dbook_images");
                                article.author = jItem.getString("dr_name");
                                article.description = jItem.getString("description");
                                article.title = jItem.getString("article_title");
                                article.name = jItem.getString("name");
                                article.drID = jItem.getString("dr_id");

                                mListItem.add(article);

                            }
                            mAdapterArticle.notifyDataSetChanged();
                            Log.w("tag","count : "+mListItem.isEmpty());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mProgressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                String expert = "Fitness Expert";

                userParams.put("profile_id" ,sharedPrefManager.getExpertId(ArticleActivity.this));
                userParams.put("limit_start","0");
                userParams.put("limit_page_length","0");
                userParams.put("user_type",expert);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;

            }
        };

        MySingleton.getInstance(this).addTorequestque(stringRequest);
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
                mAdapterArticle.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class PlaceClickListener implements AdapterArticle.OnPlaceClickListener {

        @Override
        public void onPlaceClick(Article article) {
            Intent i = new Intent(ArticleActivity.this,ArticleDetailsActivity.class);
            i.putExtra("title",article.title);
            i.putExtra("description",article.description);
            i.putExtra("image",article.image);
            startActivity(i);

        }

        @Override
        public void onDeleteClick(final Article article) {

            if (sharedPrefManager.getExpertId(ArticleActivity.this).equals(article.drID)){
                AlertDialog.Builder builder = new AlertDialog.Builder(ArticleActivity.this);
                builder.setMessage("Do you want to delete " + article.title + " ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes, Delete it!!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        RestAPI.dev_api + "api/method/phr.delete_article",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("tag","onResponse : " + response);
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
                                        userParams.put("name",article.name);

                                        JSONObject userJson = new JSONObject(userParams);
                                        params.put("data",userJson.toString());
                                        return params;
                                    }
                                };
                                MySingleton.getInstance(ArticleActivity.this).addTorequestque(stringRequest);

                            }
                        });

                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ArticleActivity.this);
                alert.setMessage("You can't delete Articles of others..!!");
                alert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
