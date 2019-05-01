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
import com.android.volley.DefaultRetryPolicy;
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

public class AddCardioExercise extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner mSpinner;
    private TextInputLayout mInputCardio;
    private EditText mEdtDuration;
    private Button mBtnAddExercise;
    ArrayList<String> mListName;
    ArrayList<String> mListItem;
    private String name,type,itemName,duration,planName;
    boolean isValid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cardio_exercise);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Cardio Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        planName = intent.getStringExtra("name");

        mListName = new ArrayList<>();
        mListItem = new ArrayList<>();

        mSpinner = findViewById(R.id.spinnerCardioName);
        mInputCardio = findViewById(R.id.txtInputCardioDuration);
        mEdtDuration = findViewById(R.id.edtDuration);
        mBtnAddExercise = findViewById(R.id.btnAddCardio);
        mSpinner.setOnItemSelectedListener(this);

        mBtnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtDuration.getText().toString().isEmpty()){
                    mInputCardio.setError("Please enter Duration");
                    isValid = false;

                }else {
                    mInputCardio.setErrorEnabled(false);
                    sendRequest();
                }
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
        System.out.println("name : "+name);
    }

    public void sendRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.add_exercise_to_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");


                            if (msg.getString("returncode").equals("200")){

                                Toast.makeText(getApplicationContext(),"Cardio Exercise Added Successfully..!!",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("item_name",name);
                userParams.put("name",planName);
                userParams.put("item_type","Cardio Vascular");
                userParams.put("exercise_time",duration);

                JSONObject userJSON = new JSONObject(userParams);
                params.put("data",userJSON.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(AddCardioExercise.this).addTorequestque(stringRequest);

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
//
//                duration = mEdtDuration.getText().toString();
//
//                URL url = new URL(RestAPI.dev_api + "api/method/phr.add_exercise_to_plan");
//
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("item_name", name);
//                jsonObject.put("name", planName);
//                jsonObject.put("item_type","Cardio Vascular");
//                jsonObject.put("exercise_time",duration);
//
//                JSONObject postDataParams = new JSONObject();
//                postDataParams.put("data",jsonObject);
//
//                System.out.println("json : "+postDataParams);
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
//            try {
//
//                JSONObject aq = new JSONObject(response);
//                JSONObject msg = aq.getJSONObject("message");
//                //   tablet.tabletName = msg.getString("tablet_name");
//                //   mListTablet.add(tablet);
//
//                if (msg.getString("returncode").equals("200")){
////                    Intent i = new Intent(AddDietItemActivity.this,DietPlanDetailsActivity.class);
////                    startActivity(i);
//                    Toast.makeText(getApplicationContext(),"Cardio Exercise Added Successfully..!!",Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
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
//
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



    class ErrorListener implements Response.ErrorListener {
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
                    if (type.equals("Cardio Vascular")){
                        name = jItem.getString("name");
                        itemName = jItem.getString("item_name");
                        mListItem.add(itemName);
                        mListName.add(name);
                    }
//                    ExerciseItem item = new ExerciseItem();
//                    item.exercise = jItem.getString("item_name");
//                    item.type = jItem.getString("item_type");
//                    item.imageUrl = jItem.getString("item_image");
//

//                    mListName.add(name);
                }

                mSpinner.setAdapter(new ArrayAdapter<String>(AddCardioExercise.this, android.R.layout.simple_spinner_dropdown_item, mListItem));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
