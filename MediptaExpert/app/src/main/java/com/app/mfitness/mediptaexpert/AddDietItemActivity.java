package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddDietItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> mListItem;
    String[] dietType = { "Wake Up Drink","Breakfast","Mid Meal (Morning)","Lunch","Midmeal (Evening)","Evening Snacks","Dinner","Preworkout","PostWorkout"  };
    String[] dietUnit = {"10 gm","15 gm","30 gm","50 gm","100 gm","150 gm","200 gm","250 gm","30 gm uncooked","30 ml","50 ml","100 ml","150 ml",
                        "200 ml","250 ml","tbsp","tsp","Glass","Scoop","Small Bowl","Medium Bowl","Big Bowl","Cup","Piece","Cooked","Uncooked",
                        "Chilla","Slice","Dosa","Serving","Roti","1 cup with milk 1 cup","Medium size"};
    Spinner spinType,spinUnit;
    private ArrayList<String> mListName;
    private String itemName,itemType,nameId,name,itemId,quantity,itemUnit;
    private EditText mEdtQuantity;
    private TextInputLayout mTxtInputQuantity;
    private Button mBtnAddItem;
    boolean isValid = true;
    AutoCompleteTextView auto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diet_item);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Diet Item");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnAddItem = findViewById(R.id.btnAddItem);
        mListItem = new ArrayList<>();
        mListName = new ArrayList<>();
        spinType = findViewById(R.id.spinnerItemType);

        spinUnit = findViewById(R.id.spinnerItemUnit);

        spinType.setOnItemSelectedListener(this);
        spinUnit.setOnItemSelectedListener(this);
        auto = findViewById(R.id.auto);

        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 itemName = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < mListItem.size(); i++) {
                    if (mListItem.get(i).equals(itemName)) {
                        pos = i;
                        itemId = String.valueOf(mListName.get(pos));
                        System.out.println("id : "+ itemId);
                        break;
                    }
                }

            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        mEdtQuantity = findViewById(R.id.edtQuantity);
        mTxtInputQuantity = findViewById(R.id.txtInputQuantity);

        mBtnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtQuantity.getText().toString().isEmpty()){
                    mTxtInputQuantity.setError("Please enter Item Quantity");
                    isValid=false;
                }else {
                    mTxtInputQuantity.setErrorEnabled(false);
                    new SendPostRequest().execute();

                }
            }
        });

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dietType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(aa);

        ArrayAdapter adapterUnit = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dietUnit);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinUnit.setAdapter(adapterUnit);

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
            error.printStackTrace();

        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag", "Fragment : " + jResponse);

            JSONArray jArrDiet = null;
            try {
                jArrDiet = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrDiet.length(); i++) {

                    JSONObject jDiet = jArrDiet.getJSONObject(i);

                    itemName = jDiet.getString("item_name");
                    nameId = jDiet.getString("name");

                    mListItem.add(itemName);
                    mListName.add(nameId);
                }

                auto.setAdapter(new ArrayAdapter<String>(AddDietItemActivity.this, android.R.layout.simple_spinner_dropdown_item, mListItem));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        itemName = parent.getItemAtPosition(position).toString();
//        itemType = parent.getItemAtPosition(3).toString();
//        System.out.println("at 0 : "+itemType);

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinnerItemName)
        {
             itemId = String.valueOf(mListName.get(position));

             itemName = parent.getItemAtPosition(position).toString();
        }

        else if(spinner.getId() == R.id.spinnerItemType)
        {

            switch (position){
                case 0:
                    itemType = parent.getItemAtPosition(0).toString();
                    itemType = "wakupdrink";
                    return;
                case 1:
                    itemType = parent.getItemAtPosition(1).toString();
                    itemType = "breakfast";
                    return;
                case 2:
                    itemType = parent.getItemAtPosition(2).toString();
                    itemType = "morningmidmeal";
                    return;
                case 3:
                    itemType = parent.getItemAtPosition(3).toString();
                    itemType = "lunch";
                    return;
                case 4:
                    itemType = parent.getItemAtPosition(4).toString();
                    itemType = "eveningmidmeal";
                    return;
                case 5:
                    itemType = parent.getItemAtPosition(5).toString();
                    itemType = "snacks";
                    return;
                case 6:
                    itemType = parent.getItemAtPosition(6).toString();
                    itemType = "dinner";
                    return;
                case 7:
                    itemType = parent.getItemAtPosition(7).toString();
                    itemType = "preworkout";
                    return;
                case 8:
                    itemType = parent.getItemAtPosition(8).toString();
                    itemType = "postworkout";
                    return;

            }
        }else if (spinner.getId() == R.id.spinnerItemUnit){
            itemUnit = parent.getItemAtPosition(position).toString();
            System.out.println("unit : "+itemUnit);
        }

    }

    public class SendPostRequest extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {

            try{

                quantity = mEdtQuantity.getText().toString();

                URL url = new URL(RestAPI.dev_api + "api/method/phr.add_diet_items");

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("quantity", quantity);
                jsonObject.put("name", name);
                jsonObject.put("item_type",itemType);
                jsonObject.put("item_id",itemId);
                jsonObject.put("item_unit",itemUnit);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("data",jsonObject);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String response) {
            try {

                JSONObject aq = new JSONObject(response);
                JSONObject msg = aq.getJSONObject("message");
             //   tablet.tabletName = msg.getString("tablet_name");
             //   mListTablet.add(tablet);

                if (msg.getString("returncode").equals("200")){
//                    Intent i = new Intent(AddDietItemActivity.this,DietPlanDetailsActivity.class);
//                    startActivity(i);
                    Toast.makeText(getApplicationContext(),"Diet Item Added Successfully..!!",Toast.LENGTH_SHORT).show();
                    finish();

                    startActivity(getIntent());
                }else {
                    String itemNam = auto.getText().toString();
                    auto.setText("");
                    Toast.makeText(getApplicationContext(), itemNam +" not available ", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("response","Response = "+response);
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;

            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return result.toString();
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
