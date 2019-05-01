package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddStrengthExercise extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextInputLayout mInputSet,mInputRepetition,mInputWeight;
    private EditText mEdtSet,mEdtRepetition,mEdtWeight;
    private Spinner mSpinner;
    private ArrayList<String> mListItem;
    private ArrayList<String> mListName;
    private Button mBtnAdd;
    private String type,name,item,itemName,itemId,set,repetition,weight,planName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_strength_exercise);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Strength Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        planName = intent.getStringExtra("name");
        System.out.println("name : "+planName);

        mListItem = new ArrayList<>();
        mListName = new ArrayList<>();
        mInputSet = findViewById(R.id.txtInputStrengthSets);
        mInputRepetition = findViewById(R.id.txtInputStrengthRepetition);
        mInputWeight = findViewById(R.id.txtInputStrengthWeight);

        mEdtSet = findViewById(R.id.edtSets);
        mEdtRepetition = findViewById(R.id.edtRepetition);
        mEdtWeight = findViewById(R.id.edtWeight);

        mSpinner = findViewById(R.id.spinnerStrengthName);
        mSpinner.setOnItemSelectedListener(this);
        mBtnAdd = findViewById(R.id.btnAddStrength);

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStrengthRequest();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemName = parent.getItemAtPosition(position).toString();
        name = String.valueOf(mListName.get(position));
        System.out.println("item id : "+name);
    }

    public void postStrengthRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.add_exercise_to_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")){
//
                                Toast.makeText(getApplicationContext(),"Diet Item Added Successfully..!!",Toast.LENGTH_SHORT).show();
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

                set = mEdtSet.getText().toString();
                System.out.println("set : "+set);
                repetition = mEdtRepetition.getText().toString();
                weight = mEdtWeight.getText().toString();

                userParams.put("item_name",name);
                userParams.put("name",planName);
                userParams.put("item_type","Strength");
                userParams.put("repetition",repetition);
                userParams.put("sets",set);
                userParams.put("weight",weight);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(AddStrengthExercise.this).addTorequestque(stringRequest);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class ErrorListener implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag", "Fragment : " + jResponse);

            JSONArray jArrPerson = null;
            try {
                jArrPerson = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrPerson.length(); i++) {

                    JSONObject jItem = jArrPerson.getJSONObject(i);
                    type = jItem.getString("item_type");
                    if (type.equals("Strength")){
                        itemName = jItem.getString("item_name");
                        name = jItem.getString("name");
                        mListItem.add(itemName);
                        mListName.add(name);
                    }
//                    ExerciseItem item = new ExerciseItem();
//                    item.exercise = jItem.getString("item_name");
//                    item.type = jItem.getString("item_type");
//                    item.imageUrl = jItem.getString("item_image");


//                    mListName.add(name);
                }

                mSpinner.setAdapter(new ArrayAdapter<String>(AddStrengthExercise.this, android.R.layout.simple_spinner_dropdown_item, mListItem));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
