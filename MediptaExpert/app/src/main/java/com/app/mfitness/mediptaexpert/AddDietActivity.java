package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddDietActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] dietType = { "Veg","Non-Veg","Vegan" };
    String[] itemUnit = {"5 gm","10 gm","15 gm","30 gm","50 gm","100 gm"};
    private String item,itemType, food,unit;
    private TextInputLayout mEdtInputFood,mEdtInputCalories;
    private EditText mEdtItemName,mEdtCalories;
    public boolean isValid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diet);
        Button mBtnAddDiet = findViewById(R.id.btnAddDiet);
        mEdtItemName = findViewById(R.id.edtItemName);
        mEdtInputFood = findViewById(R.id.input_layout_food);
        mEdtInputCalories = findViewById(R.id.input_layout_calories);
        mEdtCalories = findViewById(R.id.edtItemCalories);

        food = mEdtItemName.getText().toString();

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Diet");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spin = findViewById(R.id.spinnerItemType);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dietType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        Spinner unit = findViewById(R.id.spinnerItemUnit);
        unit.setOnItemSelectedListener(this);

        ArrayAdapter mAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,itemUnit);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit.setAdapter(mAdapter);

        mBtnAddDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mEdtItemName.getText().toString().isEmpty()){
                mEdtInputFood.setError("Please enter food item");
                isValid=false;
            }else {
                mEdtInputFood.setErrorEnabled(false);
                postDiet();
            }

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinnerItemType)
        {

            itemType = parent.getItemAtPosition(position).toString();
         //   Toast.makeText(parent.getContext(), "Selected: " + itemType, Toast.LENGTH_LONG).show();

        }
        else if(spinner.getId() == R.id.spinnerItemUnit)
        {
            unit = parent.getItemAtPosition(position).toString();
         //     Toast.makeText(parent.getContext(), "Selected: " + unit, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void postDiet(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api+ "api/method/phr.add_diet_item",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")){
                                Intent i = new Intent(AddDietActivity.this,DietItemsActivity.class);
                                startActivity(i);
                                finish();
                                Toast.makeText(getApplicationContext(),"Food Item Added Successfully..!!",Toast.LENGTH_SHORT).show();
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
                final String itemName = mEdtItemName.getText().toString();
                final String calories = mEdtCalories.getText().toString();

                userParams.put("item_name",itemName);
                userParams.put("item_type",item);
                userParams.put("item_calories",calories);
                userParams.put("item_unit",unit);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        MySingleton.getInstance(AddDietActivity.this).addTorequestque(stringRequest);
    }

//    public class SendPostRequest extends AsyncTask<String,Void,String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected String doInBackground(String... arg0) {
//
//            try{
////                final String itemName = mEdtItemName.getText().toString();
//
//                URL url = new URL(RestAPI.dev_api + "api/method/phr.add_diet_item");
//
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.put("item_name", itemName);
//                jsonObject.put("item_type", item);
//
//                JSONObject postDataParams = new JSONObject();
//                postDataParams.put("data",jsonObject);
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postDataParams));
//
//                writer.flush();
//                writer.close();
//                os.close();
//
//                int responseCode=conn.getResponseCode();
//
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//
//                    BufferedReader in=new BufferedReader(
//                            new InputStreamReader(
//                                    conn.getInputStream()));
//                    StringBuffer sb = new StringBuffer("");
//                    String line="";
//
//                    while((line = in.readLine()) != null) {
//
//                        sb.append(line);
//                        break;
//                    }
//
//                    in.close();
//                    return sb.toString();
//
//                }
//                else {
//                    return new String("false : "+responseCode);
//                }
//            }
//            catch(Exception e){
//                return new String("Exception: " + e.getMessage());
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//
//            try {
//
//                JSONObject aq = new JSONObject(response);
//                JSONObject msg = aq.getJSONObject("message");
//
//                if (msg.getString("returncode").equals("200")){
//                    Intent i = new Intent(AddDietActivity.this,DietItemsActivity.class);
//                    startActivity(i);
//                    finish();
//                    Toast.makeText(getApplicationContext(),"Food Item Added Successfully..!!",Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.e("response","Response = "+response);
//        }
//    }
//
//    public String getPostDataString(JSONObject params) throws Exception {
//
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        Iterator<String> itr = params.keys();
//
//        while(itr.hasNext()){
//
//            String key = itr.next();
//            Object value = params.get(key);
//
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(key, "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
//        }
//
//        return result.toString();
//    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
