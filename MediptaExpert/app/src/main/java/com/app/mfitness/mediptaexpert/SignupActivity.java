package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class SignupActivity extends AppCompatActivity{
    private EditText mEdtFirstName,mEdtLastName,mEdtMobile,mEdtAddress,mEdtEmail,mEdtPassword;
    private Button mBtnSignup;
    private TextView mTxtAlreadyUser;
    private Spinner spinner;
    private CheckBox cbShowPwd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEdtFirstName = findViewById(R.id.edtFirstName);
        mEdtLastName = findViewById(R.id.edtLastName);
        mEdtMobile = findViewById(R.id.edtMobile);
        mEdtAddress = findViewById(R.id.edtAddress);
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mBtnSignup = findViewById(R.id.btnSignup);
        mTxtAlreadyUser = findViewById(R.id.txtAlreadyUser);
        spinner = findViewById(R.id.spinner);
        cbShowPwd = findViewById(R.id.cbShowPwd);


        String[] prefix = {"Dr.","Mr.","Miss.","Mrs."};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,R.layout.support_simple_spinner_dropdown_item,prefix);
        spinner.setAdapter(adapter);

        cbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {

                    mEdtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {

                    mEdtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }

        });

        mTxtAlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        mBtnSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String firstName = mEdtFirstName.getText().toString();
                final String lastName = mEdtLastName.getText().toString();
                final String mobile = mEdtMobile.getText().toString();
                final String address = mEdtAddress.getText().toString();
                final String email = mEdtEmail.getText().toString();
                final String password = mEdtPassword.getText().toString();

                if (TextUtils.isEmpty(firstName)) {
                    mEdtFirstName.setError("Please enter username");
                    mEdtFirstName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(lastName)){
                    mEdtLastName.setError("Please enter last name");
                    mEdtLastName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mobile)){
                    mEdtMobile.setError("Please enter Mobile No");
                    mEdtMobile.requestFocus();
                    return;
                }

                if (!isValidPassword(mEdtPassword.getText().toString().trim())) {
                    mEdtPassword.setError("Password should contain atleast 1 special character,1 number,1 alphabet");
                    mEdtPassword.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEdtEmail.setError("Enter a valid email");
                    mEdtEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mEdtPassword.setError("Enter a password");
                    mEdtPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(address)){
                    mEdtAddress.setError("Please enter address");
                    mEdtAddress.requestFocus();
                    return;
                }
                progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setMessage("Signing up..");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        RestAPI.dev_api  + "api/method/phr.create_fitness_expert_profile",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("tag","onResponse : " + response);
                                progressDialog.dismiss();

                                try {

                                    JSONObject aq = new JSONObject(response);
                                    JSONObject msg = aq.getJSONObject("message");
                                    String mm = msg.getString("doc");

                                     if (msg.getString("returncode").equals("200")){

                                        Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(getApplicationContext(),"Signup successful..!!",Toast.LENGTH_SHORT).show();
                                     }else if (msg.getString("returncode").equals("409")){
                                         Toast.makeText(getApplicationContext(),"User already exists..!!",Toast.LENGTH_SHORT).show();
                                     }else {
                                        Toast.makeText(getApplicationContext(),"Something went wrong..!!",Toast.LENGTH_SHORT).show();
                                     }
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();

                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        Map<String, String> userParams = new HashMap<String, String>();
                        final String firstName = mEdtFirstName.getText().toString();
                        final String lastName = mEdtLastName.getText().toString();
                        final String mobile = mEdtMobile.getText().toString();
                        final String address = mEdtAddress.getText().toString();
                        final String email = mEdtEmail.getText().toString();
                        final String password = mEdtPassword.getText().toString();
                        userParams.put("first_name",firstName);
                        userParams.put("email", email);
                        userParams.put("contact", mobile);
                        userParams.put("last_name", lastName);
                        userParams.put("new_password", password);
                        userParams.put("address", address);

                        JSONObject userJSON = new JSONObject(userParams);
                        params.put("data", userJSON.toString());

                        return params;

                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                MySingleton.getInstance(SignupActivity.this).addTorequestque(stringRequest);

            }
        });
    }



    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
}
