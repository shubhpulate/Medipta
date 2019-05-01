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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class CreatePackageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextInputLayout mInputName,mInputDuration,mInputCall,mInputDietPlans,mInputExercise,mInputAmount,mInputDescription;
    private EditText mEdtPackageName,mEdtDuration,mEdtCall,mEdtDietPlans,mEdtExercise,mEdtAmount,mEdtDescription;
    private Button mBtnAddPackage;
    String[] packageType = { "Weight Gain","Weight Loss","Weight Maintain"  };
    private String item;
    public boolean isValid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Create Fitness Package");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spin = findViewById(R.id.spinnerPackageType);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,packageType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        mInputName = findViewById(R.id.input_layout_name);
        mInputDuration = findViewById(R.id.input_layout_duration);
        mInputCall = findViewById(R.id.input_layout_call);
        mInputDietPlans = findViewById(R.id.input_layout_diet_plan);
        mInputExercise = findViewById(R.id.input_layout_exercise);
        mInputAmount = findViewById(R.id.input_layout_amount);
        mInputDescription = findViewById(R.id.input_layout_description);

        mEdtPackageName = findViewById(R.id.edtPackageName);
        mEdtDuration = findViewById(R.id.edtPackageDuration);
        mEdtCall = findViewById(R.id.edtPackageCall);
        mEdtDietPlans = findViewById(R.id.edtPackageDiet);
        mEdtExercise = findViewById(R.id.edtPackageExercise);
        mEdtAmount = findViewById(R.id.edtPackageAmount);
        mEdtDescription = findViewById(R.id.edtPackageDescription);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mBtnAddPackage = findViewById(R.id.btnAddPackage);

        mBtnAddPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtPackageName.getText().toString().isEmpty()){
                    mInputName.setError("Please enter Package Name");
                    isValid = false;
                }else if (mEdtDuration.getText().toString().isEmpty()){
                    mInputDuration.setError("Please enter duration");
                    isValid = false;
                }else if (mEdtCall.getText().toString().isEmpty()){
                    mInputCall.setError("Please enter total calls");
                    isValid = false;
                }else if (mEdtDietPlans.getText().toString().isEmpty()){
                    mInputDietPlans.setError("Please enter Total diet plans");
                    isValid = false;
                }else if (mEdtExercise.getText().toString().isEmpty()){
                    mInputExercise.setError("Please enter total exercise");
                    isValid = false;

                }else if (mEdtAmount.getText().toString().isEmpty()){
                    mInputAmount.setError("Please enter Amount");
                    isValid = false;
                }
                else {
                    mInputName.setErrorEnabled(false);
                    mInputDuration.setErrorEnabled(false);
                    mInputCall.setErrorEnabled(false);
                    mInputDietPlans.setErrorEnabled(false);
                    mInputExercise.setErrorEnabled(false);
                    mInputAmount.setErrorEnabled(false);
                    postRequest();
                }

            }
        });

    }  public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.create_fitness_packages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {



                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")){
                                Toast.makeText(getApplicationContext(),"Fitness Package Added Successfully..!!",Toast.LENGTH_SHORT).show();
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
                final String packageName = mEdtPackageName.getText().toString();
                final String duration = mEdtDuration.getText().toString();
                final String amount = mEdtAmount.getText().toString();
                final String description = mEdtDescription.getText().toString();
                final String call = mEdtCall.getText().toString();
                final String diet = mEdtDietPlans.getText().toString();
                final String exercise = mEdtExercise.getText().toString();

                userParams.put("package_name",packageName);
                userParams.put("duration",duration);
                userParams.put("amount",amount);
                userParams.put("description",description);
                userParams.put("call_count",call);
                userParams.put("diet_plans_count",diet);
                userParams.put("exercise_plans_count",exercise);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(CreatePackageActivity.this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
