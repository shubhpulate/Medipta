package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private TextInputLayout mInputFirstName,mInputLastName,mInputEmail,mInputMobile,mInputExpYear,mInputExpMonth,mInputQualification,
                            mInputLanguage,mInputCity,mInputState,mInputAddress;
    private EditText mEdtFirstName,mEdtLastName,mEdtEmail,mEdtMobile,mEdtExpYear,mEdtExpMonth,mEdtQualification,
                        mEdtLanguage,mEdtCity,mEdtState,mEdtAddress;
    private ImageView mImgProfile;
    private FloatingActionButton mFab;
    Activity context = this;
    SharedPrefManager sharedPrefManager;
    CoordinatorLayout coordinatorLayout;
    private ProgressDialog mProgressDialog;
    private Button mBtnUpdate;

    private String base64,picturePath,filename,extension;
//    private static final int SELECT_PICTURE = 100;
//    private static final String TAG = "MainActivity";

    private int REQUEST_CAMERA = 1, SELECT_FILE = 2;
    private String Camera = "Take Photo";
    private String Gallery = "Choose from Gallery";
    private String Cancel = "Cancel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Update Profile");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPrefManager = new SharedPrefManager();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        initView();
        getUserDetails();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();

            }
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(UpdateProfileActivity.this)){
                    sendPostRequest();
                }else {
                    Toast.makeText(UpdateProfileActivity.this, "Please check your internet connection..!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void initView(){
        mInputFirstName = findViewById(R.id.update_input_first_name);
        mInputLastName = findViewById(R.id.update_input_last_name);
        mInputEmail = findViewById(R.id.update_input_email);
        mInputMobile = findViewById(R.id.update_input_mobile);
        mInputExpYear = findViewById(R.id.update_input_exp_year);
        mInputExpMonth = findViewById(R.id.update_input_exp_month);
        mInputQualification = findViewById(R.id.update_input_qualification);
        mInputLanguage = findViewById(R.id.update_input_language);
        mInputCity = findViewById(R.id.update_input_city);
        mInputState = findViewById(R.id.update_input_state);
        mInputAddress = findViewById(R.id.update_input_address);

        mEdtFirstName = findViewById(R.id.edtUpdateFirstName);
        mEdtLastName = findViewById(R.id.edtUpdateLastName);
        mEdtEmail = findViewById(R.id.edtUpdateEmail);
        mEdtEmail.setEnabled(false);
        mEdtMobile = findViewById(R.id.edtUpdateMobile);
        mEdtMobile.setEnabled(false);
        mEdtExpYear = findViewById(R.id.edtUpdateExpYear);
        mEdtExpMonth = findViewById(R.id.edtUpdateExpMonth);
        mEdtQualification = findViewById(R.id.edtUpdateQualification);
        mEdtLanguage = findViewById(R.id.edtUpdateLanguage);
        mEdtCity = findViewById(R.id.edtUpdateCity);
        mEdtState = findViewById(R.id.edtUpdateState);
        mEdtAddress = findViewById(R.id.edtUpdateAddress);

        mImgProfile = findViewById(R.id.imgUpdateImage);

        mFab = findViewById(R.id.fab);
        coordinatorLayout = findViewById(R.id.container);
        mBtnUpdate = findViewById(R.id.btnUpdate);

    }

    public void getUserDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_fitness_expert_profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");
                            String address = msg.getString("address");
                            String qualification = msg.getString("qualification");
                            String phone = msg.getString("contact");
                            String email = msg.getString("email");
                            String profileImage = msg.getString("user_image");
                            String expYear = msg.getString("experience_in_year");
                            String expMonth = msg.getString("experience_in_month");
                            String fName = msg.getString("first_name");
                            String lName = msg.getString("last_name");
                            String language = msg.getString("language");
                            String city = msg.getString("city");
                            String state = msg.getString("state");

                            Picasso.with( UpdateProfileActivity.this )
                                    .load( RestAPI.dev_api + profileImage )
                                    .placeholder( R.mipmap.ic_launcher )
                                    .resize(500,0)
                                    .into( mImgProfile);

                            sharedPrefManager.setExpertImage(context,profileImage);

                            mEdtAddress.setText(address);
                            mEdtEmail.setText(email);
                            mEdtMobile.setText(phone);
                            mEdtExpMonth.setText(expMonth);
                            mEdtExpYear.setText( expYear );
                            mEdtQualification.setText(qualification);
                            mEdtFirstName.setText(fName);
                            mEdtLastName.setText(lName);
                            mEdtLanguage.setText(language);
                            mEdtState.setText(state);
                            mEdtCity.setText(city);

                            if (email.equals("null")){
                                mEdtEmail.setText("");
                            }
                            if (address.equals("null")){
                                mEdtAddress.setText("");
                            }
                            if (expYear.equals("null")){
                                mEdtExpYear.setText("");
                            }
                            if (expMonth.equals("null")){
                                mEdtExpMonth.setText("");
                            }
                            if (qualification.equals("null") || qualification.equals("")){
                                mEdtQualification.setText("");
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
                        mProgressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("expert_id",sharedPrefManager.getExpertId(context));

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(UpdateProfileActivity.this).addTorequestque(stringRequest);
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public void sendPostRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.update_expert_profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response = "+response);

                        try {

                            JSONObject aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")){
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Profile Updated Successfully..!!", Snackbar.LENGTH_SHORT);

                                snackbar.show();

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
                String fName = mEdtFirstName.getText().toString();
                String lName = mEdtLastName.getText().toString();
                String expYear = mEdtExpYear.getText().toString();
                String expMonth = mEdtExpMonth.getText().toString();
                String qualification = mEdtQualification.getText().toString();
                String language = mEdtLanguage.getText().toString();
                String city = mEdtCity.getText().toString();
                String state = mEdtState.getText().toString();
                String address = mEdtAddress.getText().toString();

                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("first-name",fName);
                userParams.put("last-name",lName);
                userParams.put("experience_in_year",expYear);
                userParams.put("experience_in_month",expMonth);
                userParams.put("qualification",qualification);
                userParams.put("language",language);
                userParams.put("city",city);
                userParams.put("state",state);
                userParams.put("address",address);
                userParams.put("send_sms","false");
                userParams.put("profile_id",sharedPrefManager.getExpertId(UpdateProfileActivity.this));

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());

                return params;
            }
        };

        MySingleton.getInstance(UpdateProfileActivity.this).addTorequestque(stringRequest);
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        selectImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        String message = "This app needs permission to use this feature. You can grant them in app settings.";

        builder.setTitle("Need Permissions");
        builder.setMessage(message);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void selectImage() {

        final CharSequence[] options = { Camera, Gallery,Cancel };

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(Camera)){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                            ex.printStackTrace();

                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(UpdateProfileActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }

                }

                else if (options[item].equals(Gallery)){
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);

                }

                else if (options[item].equals(Cancel)) {
                    dialog.dismiss();
                }

            }

        });

        builder.show();

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(picturePath);
        filename = f.getName();
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        extension = picturePath.substring(picturePath.lastIndexOf("."));
        Bitmap bm = BitmapFactory.decodeFile(picturePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        base64 = Base64.encodeToString(b, Base64.DEFAULT);
        Log.w("filename",filename);
        System.out.println("file name : "+extension);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int REQUEST_PHONE_CALL = 1;

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                if (ContextCompat.checkSelfPermission(UpdateProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_PHONE_CALL);
                }

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImgProfile.setImageBitmap(imageBitmap);
                galleryAddPic();
                setPic();
                updateImage();
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage("Uploading Image please wait...");
                mProgressDialog.show();

            } else if (requestCode == SELECT_FILE) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                picturePath = c.getString(columnIndex);

                c.close();

                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                Log.w("path of image : ", picturePath+"");
                File file = new File(picturePath);
                filename = file.getName();
                extension = picturePath.substring(picturePath.lastIndexOf("."));
                Bitmap bm = BitmapFactory.decodeFile(picturePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                base64 = Base64.encodeToString(b, Base64.DEFAULT);

                mImgProfile.setImageBitmap(thumbnail);
                updateImage();
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage("Uploading Image please wait...");
                mProgressDialog.show();

            }else {
                filename = "";
                extension = "";
                base64 = "";
            }

        }
    }


    private void setPic() {
        int targetW = mImgProfile.getWidth();
        int targetH = mImgProfile.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
        mImgProfile.setImageBitmap(bitmap);

    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        System.out.println("file name b : "+imageFileName);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        picturePath = image.getAbsolutePath();

        return image;
    }

    public void updateImage(){
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(UpdateProfileActivity.this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("file_name", filename);
            jsonBody.put("img_extension",extension);
            jsonBody.put("bin_img",base64);
            jsonBody.put("profile_id",sharedPrefManager.getExpertId(context));

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    RestAPI.dev_api + "api/method/phr.setProfileImage",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("tag","onResponse : " + response);
                            mProgressDialog.dismiss();

                            try {

                                JSONObject aq = new JSONObject(response);
                                JSONObject msg = aq.getJSONObject("message");

                                if (msg.getString("returncode").equals("200")){

                                    Toast.makeText(getApplicationContext(),"Profile Photo Updated Successfully..!!",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong..!!", Toast.LENGTH_SHORT).show();
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
                            mProgressDialog.dismiss();
                        }
                    }){

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

//                    @Override
//                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                        String responseString = "";
//                        if (response != null) {
//                            responseString = String.valueOf(response.statusCode);
//                        }

//                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                    }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
