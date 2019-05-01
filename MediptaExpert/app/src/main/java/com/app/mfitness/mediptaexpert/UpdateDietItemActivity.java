package com.app.mfitness.mediptaexpert;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateDietItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextInputLayout mUpdateInputFood,mUpdateInputCalories;
    private EditText mEdtUpdateItemName,mEdtUpdateCalories;
    private Button mBtnUpdate;
    String[] dietType = { "Veg","Non Veg","Vegan" };
    String[] itemUnit = {"5 gm","10 gm","15 gm","30 gm","50 gm","100 gm"};
    public boolean isValid = true;
    private String name,itemName,itemType,calories,unit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_diet_item);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Update Diet Item");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        itemName = intent.getStringExtra("itemName");
        itemType = intent.getStringExtra("itemType");
        calories = intent.getStringExtra("itemCalories");
        unit = intent.getStringExtra("itemUnit");
        System.out.println("itemType : "+itemType);
        System.out.println("unit : "+unit);

        mUpdateInputFood = findViewById(R.id.update_input_layout_food);
        mUpdateInputCalories = findViewById(R.id.update_input_layout_calories);
        mBtnUpdate = findViewById(R.id.btnUpdateDiet);
        mEdtUpdateItemName = findViewById(R.id.edtUpdateItemName);

        mEdtUpdateCalories = findViewById(R.id.edtUpdateItemCalories);

        Spinner spin = findViewById(R.id.SpinnerUpdateItemType);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dietType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(aa);
        spin.setSelection(getIndex(spin,itemType));

        Spinner spinner = findViewById(R.id.spinnerUpdateItemUnit);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter mAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,itemUnit);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        spinner.setSelection(getIndex(spinner,unit));

        mEdtUpdateItemName.setText(itemName);
        mEdtUpdateCalories.setText(calories);

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDietItem();
            }
        });

    }
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }

        return index;
    }

    public void updateDietItem(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.update_diet_item",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag", "Fragment : " + response);
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")) {
                                Toast.makeText(getApplicationContext(), "Diet Plan Added Successfully..!!", Toast.LENGTH_SHORT).show();
                                finish();
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
                final String itemName = mEdtUpdateItemName.getText().toString();
                final String itemCalories = mEdtUpdateCalories.getText().toString();

                userParams.put("name",name);
                userParams.put("item_name",itemName);
                userParams.put("item_type",itemType);
                userParams.put("item_calories",itemCalories);
                userParams.put("item_unit",unit);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        if(spinner.getId() == R.id.SpinnerUpdateItemType)
        {

            itemType = parent.getItemAtPosition(position).toString();
            //   Toast.makeText(parent.getContext(), "Selected: " + itemType, Toast.LENGTH_LONG).show();

        }
        else if(spinner.getId() == R.id.spinnerUpdateItemUnit)
        {
            unit = parent.getItemAtPosition(position).toString();
            //     Toast.makeText(parent.getContext(), "Selected: " + unit, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
