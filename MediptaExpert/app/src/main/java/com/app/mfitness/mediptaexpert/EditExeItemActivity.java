package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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

public class EditExeItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextInputLayout mInputName,mInputDescription;
    private EditText mEdtExeName,mEdtDescription;
    private Button mBtnBrowsePic, mBtnUpdateExeItem;
    private ImageView mImgChosedExeItem;
    String[] itemType = { "Strength", "Cardio Vascular" };
    private String item,exerciseName,exerciseDescription,imageUrl,type,name, base64,picturePath,filename,extension;

    private int REQUEST_CAMERA = 1, SELECT_FILE = 2;
    private String Camera = "Take Photo";
    private String Gallery = "Choose from Gallery";
    private String Cancel = "Cancel";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edt_exe_item);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Edit Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        exerciseName = intent.getStringExtra("exercise");
        exerciseDescription = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("image");
        type = intent.getStringExtra("type");
        name = intent.getStringExtra("name");

        mInputName = findViewById(R.id.input_layout_exeName);
        mInputDescription = findViewById(R.id.input_layout_exeDescription);

        mEdtExeName = findViewById(R.id.edtExerciseName);
        mEdtDescription = findViewById(R.id.edtExerciseDescription);
        mBtnBrowsePic = findViewById(R.id.btnBrowseExe);
        mBtnUpdateExeItem = findViewById(R.id.btnUpdateExerciseItem);
        mImgChosedExeItem = findViewById(R.id.imgExeChosed);

        Spinner spin = (Spinner) findViewById(R.id.spinnerExerciseItemType);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,itemType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setSelection(getIndex(spin,type));

        mEdtExeName.setText(exerciseName);
        mEdtDescription.setText(exerciseDescription);

        exerciseName = mEdtExeName.getText().toString();
        exerciseDescription = mEdtDescription.getText().toString();

        Picasso.with( this )
                .load(RestAPI.dev_api + imageUrl )
                .resize(500,0)
                .placeholder( R.mipmap.ic_launcher )
                .into( mImgChosedExeItem );

        mBtnBrowsePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        mBtnUpdateExeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendPostRequest();

                sendPostRequest();
                mProgressDialog = new ProgressDialog(EditExeItemActivity.this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage("Updating Exercise Item...");
                mProgressDialog.show();

            }
        });

    }

    public void sendPostRequest(){
        String itemName = mEdtExeName.getText().toString().trim();
        String description = mEdtDescription.getText().toString().trim();
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(EditExeItemActivity.this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("description", description);
            jsonBody.put("file_name", filename);
            jsonBody.put("img_extension",extension);
            jsonBody.put("item_name",itemName);
            jsonBody.put("item_type",item);
            jsonBody.put("base64_image",base64);
            jsonBody.put("name",name);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    RestAPI.dev_api + "api/method/phr.update_exercise_item",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("tag","onResponse : " + response);

                            try {

                                JSONObject aq = new JSONObject(response);
                                JSONObject msg = aq.getJSONObject("message");

                                if (msg.getString("returncode").equals("200")){
                                    finish();
                                    Toast.makeText(getApplicationContext(),"Exercise Item Updated Successfully..!!",Toast.LENGTH_SHORT).show();
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

            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditExeItemActivity.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(EditExeItemActivity.this);
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
                            Uri photoURI = FileProvider.getUriForFile(EditExeItemActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }

                }

                else if (options[item].equals(Gallery)){
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
//                filename = picturePath.substring(picturePath.lastIndexOf("/")+1);
        Log.w("filename",filename);
        System.out.println("file name : "+extension);
//        System.out.println("base64 : "+base64);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int REQUEST_PHONE_CALL = 1;

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                if (ContextCompat.checkSelfPermission(EditExeItemActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditExeItemActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_PHONE_CALL);
                }

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImgChosedExeItem.setImageBitmap(imageBitmap);
                galleryAddPic();
                setPic();

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
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                base64 = Base64.encodeToString(b, Base64.DEFAULT);
                Log.w("filename",filename);
                System.out.println("file name : "+extension);

                mImgChosedExeItem.setImageBitmap(thumbnail);

            }else {
                filename = "";
                extension = "";
                base64 = "";
            }

        }
    }

    private void setPic() {
        int targetW = mImgChosedExeItem.getWidth();
        int targetH = mImgChosedExeItem.getHeight();

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
        mImgChosedExeItem.setImageBitmap(bitmap);
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

        System.out.println("photo path : "+picturePath);
        return image;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
