package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditDietPlanActivity extends AppCompatActivity{
    private TextInputEditText mTxtInputFrom,mEdtPlanName,mTxtInputTo,mEdtWater;
    TextInputLayout mInputFrom,mInputTo,mInputName,mInputWater;
    private Button mBtnUpdatePlan;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;
    int cur = 0;
    public boolean isValid = true;
    Activity context = this;
    SharedPrefManager sharedPrefManager;
    private String goalId,expertId,userId,name,startDate,endDate,planName,isCheck,isActive,water;
    private CheckBox mChkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edt_diet_plan);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Update Diet Plan");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager = new SharedPrefManager();
        goalId = sharedPrefManager.getGoalId(context);
        expertId = sharedPrefManager.getExpertId(context);
        userId = sharedPrefManager.getUserId(context);

        Intent intent= getIntent();
        startDate = intent.getStringExtra("startDate");
        endDate = intent.getStringExtra("endDate");
        planName = intent.getStringExtra("planName");
        userId = intent.getStringExtra("userId");
        name = intent.getStringExtra("name");
        isActive = intent.getStringExtra("isActive");
        water = intent.getStringExtra("water");

        mTxtInputFrom=findViewById(R.id.txtUpdateFrom);
        mTxtInputTo = findViewById(R.id.txtUpdateTo);
        mInputFrom = findViewById(R.id.inputLayoutFromUpdate);
        mInputTo = findViewById(R.id.inputLayoutToUpdate);
        mInputName = findViewById(R.id.inputLayoutPlan);
        mEdtPlanName = findViewById(R.id.edtUpdatePlanName);
        mInputWater = findViewById(R.id.inputLayoutWater);
        mEdtWater = findViewById(R.id.edtUpdateWater);
        mBtnUpdatePlan = findViewById(R.id.btnUpdatePlan);
        mChkBox = findViewById(R.id.chkBoxActive);
        mEdtPlanName.setText(planName);
        mTxtInputFrom.setText(startDate);
        mTxtInputTo.setText(endDate);
        mEdtWater.setText(water);
        setCurrentDateOnView();

        mBtnUpdatePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlan();

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

        if (isActive.equals("1")){
            mChkBox.setChecked(true);
            isCheck = "on";
        }else {
            isCheck = "off";
        }

    }

    public void updatePlan(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.update_diet_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response = "+response);
                        JSONObject aq = null;
                        try {

                            aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            if (msg.getString("returncode").equals("200")){

                                Toast.makeText(getApplicationContext(),"Diet Plan Updated Successfully..!!",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "Something went wrong..!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

                String planName = mEdtPlanName.getText().toString();
                String startDate = mTxtInputFrom.getText().toString();
                String endDate = mTxtInputTo.getText().toString();
                String water = mEdtWater.getText().toString();

                userParams.put("user_id",userId);
                userParams.put("name",name);
                userParams.put("plan_name",planName);
                userParams.put("start_date",startDate);
                userParams.put("end_date",endDate);
                userParams.put("water_intake",water);
                userParams.put("is_active",isCheck);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }

    public void setCurrentDateOnView(){
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

//        mTxtInputFrom.setText(new StringBuilder()
//                .append(year).append("-").append(month+1).append("-")
//                .append(day).append(" "));
//
//        mTxtInputTo.setText(new StringBuilder()
//                .append(year).append("-").append(month+1).append("-")
//                .append(day).append(" "));

//        mTxtInputTo.setText(endDate,TextView.BufferType.EDITABLE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:
                System.out.println("onCreateDialog  : " + id);
                cur = DATE_DIALOG_ID;

                return new DatePickerDialog(this, datePickerListener, year, month, day);

            case DATE_DIALOG_ID2:
                cur = DATE_DIALOG_ID2;
                System.out.println("onCreateDialog2  : " + id);

                return new DatePickerDialog(this, datePickerListener, year, month, day);

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

    public void itemClick(View view) {
        CheckBox checkBox = (CheckBox) view;

//        if (isActive.equals("1")){
//            checkBox.isChecked();
//        }

        if(checkBox.isChecked()){
            isCheck = "on";
            System.out.println("String : "+isCheck);
        }
        else {
            isCheck = "off";
            System.out.println("String : "+isCheck);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
