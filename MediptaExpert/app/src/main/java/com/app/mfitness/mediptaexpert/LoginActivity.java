package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import me.pushy.sdk.Pushy;

public class LoginActivity extends AppCompatActivity{
    private TextView mNewUser,mForgotPass;
    private Button mBtnLogin;
    private EditText mEdtLoginEmail,mEdtPassword;
    String server_url = RestAPI.dev_api + "api/method/phr.Login";
    ProgressDialog pd;
    private SharedPrefManager sharedPreferences;
    String uName,pass,deviceToken;
    public static final String PREFS_NAME = "MyPrefsFile";
    Activity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mNewUser = findViewById(R.id.txtNewUser);
        mForgotPass = findViewById(R.id.txtForgotPass);
        mBtnLogin = findViewById(R.id.btnLogin);
        mEdtLoginEmail = findViewById(R.id.edtLoginEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        sharedPreferences = new SharedPrefManager();
        new RegisterForPushNotificationsAsync().execute();

        final String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        pd = new ProgressDialog(LoginActivity.this);

        mEdtLoginEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = mEdtLoginEmail.getEditableText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (email.matches(emailPattern) && s.length() > 0)
                {
                    mEdtLoginEmail.requestFocus();
                }
                else
                {
                    mEdtLoginEmail.setError("Invalid Email");
                    mEdtLoginEmail.requestFocus();
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uName = mEdtLoginEmail.getText().toString();
                pass = mEdtPassword.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("tag","onResponse : " + response);

                                try {
                                    JSONObject obj = new JSONObject( response );

                                    if (obj.getString("message").equals("No App")) {
                                        sendPushy();
                                        JSONObject userJson = obj.getJSONObject("profile");
                                        String city = userJson.getString("city");
                                        String email = userJson.getString("email");
                                        String me = obj.getString("message");
                                        String firstName = userJson.getString("first_name");
                                        String phone = userJson.getString("contact");
                                        String lastName = userJson.getString("last_name");
                                        String profileId = userJson.getString("profile_id");

                                        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("profile_id",profileId);

                                        editor.putBoolean("hasLoggedIn", true);

                                        editor.commit();

                                        Intent i = new Intent(LoginActivity.this,ExpertDashboard.class);
//                                        i.putExtra("City",City);
//                                        i.putExtra("firstName",firstName);
                                        startActivity(i);
                                        finish();

                                        sharedPreferences.setExpertId(context, profileId);
                                        sharedPreferences.setExpertName(context,firstName);
                                        sharedPreferences.setExpertEmail(context,email);

                                        Bundle bundle = new Bundle();
                                        bundle.putString("firstName",firstName);
                                        bundle.putString("lastName",lastName);
                                        bundle.putString("email",email);
                                        bundle.putString("city",city);
                                        bundle.putString("age",firstName);

                                        FragmentExpertDashboard fragobj = new FragmentExpertDashboard();
                                        fragobj.setArguments(bundle);

                                    }else if (obj.getString("message").equals("User disabled or missing")){
                                        Toast.makeText(getApplicationContext(), "Wrong credentials!", Toast.LENGTH_SHORT).show();

                                    }else if (obj.getString("message").equals("Incorrect password")){
                                        Toast.makeText(getApplicationContext(), "Wrong credentials!", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(getApplicationContext(), "Please enter email and password..!!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("usr", uName);
                        params.put("pwd", pass);
                        return params;

                    }
                };

                MySingleton.getInstance(LoginActivity.this).addTorequestque(stringRequest);

            }
        });

        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);

            }
        });

        mNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
            }
        });


    }

    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                 deviceToken = Pushy.register(getApplicationContext());
                 sharedPreferences.setGCMId(context,deviceToken);

                Log.d("MyApp", "Pushy device token: " + deviceToken);

            }
            catch (Exception exc) {
                return exc;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Exception exc) {
            if (exc != null) {
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                System.out.println("error : "+exc);
                return;
            }

        }
    }

    public void sendPushy(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.setGCMId",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag","onResponse : " + response);
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
                final String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                sharedPreferences.setDeviceId(context, deviceId);

                String profileId = sharedPreferences.getExpertId(LoginActivity.this);
                System.out.println("profile id : "+profileId);
                userParams.put("profile_id",profileId);
                userParams.put("gcm_id",deviceToken);
                userParams.put("device_id",deviceId);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };
        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }
}
