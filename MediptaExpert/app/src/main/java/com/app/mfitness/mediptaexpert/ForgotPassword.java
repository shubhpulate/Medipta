package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPassword extends AppCompatActivity {
    private Button mBtnRequest;
    private EditText mEdtForgotPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mBtnRequest = findViewById(R.id.btnRequest);
        mEdtForgotPass = findViewById(R.id.edtForgotPass);

        mEdtForgotPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = mEdtForgotPass.getEditableText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (email.matches(emailPattern) && s.length() > 0) {
                    mEdtForgotPass.requestFocus();
                } else {
                    mEdtForgotPass.setError("Invalid Email");
                    mEdtForgotPass.requestFocus();
                }

            }
        });

        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendPostRequest();

            }
        });
    }

    public void sendPostRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.forgotPassword",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            String mm = msg.getString("msg_display");

                            if (msg.getString("returncode").equals("200")) {
                                Intent i = new Intent(ForgotPassword.this, LoginActivity.class);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(), "Verification Link send successfully", Toast.LENGTH_SHORT).show();
                            } else if (msg.getString("returncode").equals("401")) {
                                Toast.makeText(getApplicationContext(), "Invalid Email id", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("response", "Response = " + response);
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
                Map<String, String> params = new HashMap<>();
                Map<String, String> userParams = new HashMap<>();
                final String password = mEdtForgotPass.getText().toString();
                userParams.put("email", password);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data", userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(ForgotPassword.this).addTorequestque(stringRequest);
    }

}
