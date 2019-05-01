package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public  class CreateDietPlanActivity extends AppCompatActivity{
    private TextInputLayout mTextInputLayoutFrom;
    private TextInputLayout mTxtInputLayoutTo,mInputLayoutPlan,mInputLayoutWater;
    private TextInputEditText mTxtInputTo;
    private TextInputEditText mTxtInputFrom,mEdtPlanName,mTxtInputWater;
    private Button mBtnCreatePlan;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;
    int cur = 0;
    public boolean isValid = true;
    Activity context = this;
    SharedPrefManager sharedPrefManager;
    private String goalId,expertId,userId;
    private CheckBox mChkBox;
    String isCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet_plan);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Diet Plan");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mChkBox = findViewById(R.id.chkBoxActive);

        sharedPrefManager = new SharedPrefManager();
        goalId = sharedPrefManager.getGoalId(context);
        expertId = sharedPrefManager.getExpertId(context);
        userId = sharedPrefManager.getUserId(context);

        mTextInputLayoutFrom = findViewById(R.id.textInputLayoutFrom);
        mInputLayoutWater = findViewById(R.id.textInputLayoutWater);
        mTxtInputWater = findViewById(R.id.textInputEditTextWater);
        mTxtInputFrom = findViewById(R.id.textInputEditTextFrom);
        mTxtInputLayoutTo = findViewById(R.id.textInputLayoutTo);
        mTxtInputTo = findViewById(R.id.textInputEditTextTo);
        mInputLayoutPlan = findViewById(R.id.inputLayoutPlan);
        mEdtPlanName = findViewById(R.id.edtPlanName);
        mBtnCreatePlan = findViewById(R.id.btnCreatePlan);

        setCurrentDateOnView();

        mBtnCreatePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtPlanName.getText().toString().isEmpty()){
                    mInputLayoutPlan.setError("Please enter Plan name");
                    isValid=false;
                }else {
                    mInputLayoutPlan.setErrorEnabled(false);
                    postRequest();
                    finish();

                }

            }
        });

        mTxtInputFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        mTxtInputTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID2);
            }
        });
    }

    public void itemClick(View view) {
        CheckBox checkBox = (CheckBox) view;

        if(checkBox.isChecked()){
            isCheck = "on";

        }

        else {
            isCheck = "off";

        }
    }

    public void setCurrentDateOnView() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        mTxtInputFrom.setText( new StringBuilder()
                .append(year).append("-").append(month+1).append("-")
                .append(day).append(" ") );

        mTxtInputTo.setText( new StringBuilder()
                .append(year).append("-").append(month+1).append("-")
                .append(day).append(" ") );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:
                System.out.println("onCreateDialog  : " + id);
                cur = DATE_DIALOG_ID;

                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

            case DATE_DIALOG_ID2:
                cur = DATE_DIALOG_ID2;
                System.out.println("onCreateDialog2  : " + id);

                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            if(cur == DATE_DIALOG_ID){

                mTxtInputFrom.setText( new StringBuilder().append(year)
                        .append("-").append(month+1).append("-").append(day)
                        .append(" "));
            }

            else{

                mTxtInputTo.setText( new StringBuilder().append(year)
                        .append("-").append(month+1).append("-").append(day)
                        .append(" "));

            }

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.create_diet_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")) {
                                Toast.makeText(getApplicationContext(), "Diet Plan Added Successfully..!!", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("response","Response = " + response);

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
                final String startDate = mTxtInputFrom.getText().toString();
                final String endDate = mTxtInputTo.getText().toString();
                final String planName = mEdtPlanName.getText().toString();
                final String water = mTxtInputWater.getText().toString();

                userParams.put("user_id",userId);
                userParams.put("expert_id",expertId);
                userParams.put("goal_id",goalId);
                userParams.put("plan_name",planName);
                userParams.put("start_date",startDate);
                userParams.put("end_date",endDate);
                userParams.put("is_active",isCheck);

                userParams.put("water_intake",water);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(CreateDietPlanActivity.this).addTorequestque(stringRequest);
    }


}
