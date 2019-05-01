package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddArticleActivity extends AppCompatActivity{
    private TextInputLayout mInputName,mInputDescription;
    private EditText mEdtName,mEdtDescription;
    private Button mBtnAddArticle;
    private LinearLayout linLayTests;
    private LinearLayout allImagesLay,addMoreImgs;
    private ImageView crossView;
    private Uri mHighQualityImageUri = null;
    private static final int PICK_FROM_CAMERA = 1888, SELECT_DOCTOR = 25;
    private static final int SELECT_PICTURE = 1;
    private long imageLength=0, imgMaxLimit=1000000*5 , imgUnCompressLimit=1000000*2;
    private Dialog dialogPop = null;
    private ArrayList<JSONObject> Njarray= new ArrayList<>();
    private ArrayList<ImageUploadObj> imgUploadCombinList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        mInputName = findViewById(R.id.input_layout_artName);
        mInputDescription = findViewById(R.id.input_layout_artDescription);
        mEdtName = findViewById(R.id.edtArticleName);
        mEdtDescription = findViewById(R.id.edtArticleDescription);
        mBtnAddArticle = findViewById(R.id.btnAddArticle);
        allImagesLay= (LinearLayout) findViewById(R.id.allImagesLay);
        addMoreImgs= (LinearLayout) findViewById(R.id.addMoreImgs);
        linLayTests = (LinearLayout) findViewById(R.id.linLayTests);
        dialogPop = new Dialog(AddArticleActivity.this);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Add Article");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addMoreImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
//                requestGalleryReadPermission();
//                requestGalleryWritePermission();
               /* if(allImagesLay.getChildCount()<6) {
                    selectImage();
                }
                else
                {
                    new android.support.v7.app.AlertDialog.Builder(AddMyHealthRecord.this)
                            .setCancelable(false)
                            .setTitle("Limit Exceeded")
                            .setIcon(R.drawable.ic_launcher)
                            .setMessage("You have exceeded your limit to add images")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }*/
            }
        });



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

//    private void requestGalleryReadPermission(){
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        selectImage();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        if (response.isPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }
//    private void requestGalleryWritePermission(){
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        selectImage();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        if (response.isPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddArticleActivity.this);
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

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddArticleActivity.this);
        builder.setTitle("Add Photo");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    mHighQualityImageUri = generateTimeStampPhotoFileUri();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mHighQualityImageUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);//ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }
            }
        });

        builder.show();

    }


    private void deleteImage()
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+getString(R.string.app_name));
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public String getStringImage(Bitmap bmp, int quality){
        String encodedImage ="";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        catch (OutOfMemoryError e)
        {
            Utils.makeToast(AddArticleActivity.this, "An error occured while processing your request, please try again.");
        }
        return encodedImage;
    }

    private Uri generateTimeStampPhotoFileUri() {

        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {
            File photoFile = new File(outputDir, System.currentTimeMillis()
                    + ".jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageStagte = Environment.getExternalStorageState();
        //if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
        File photoDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        outputDir = new File(photoDir, getString(R.string.app_name));
        if (!outputDir.exists())
            if (!outputDir.mkdirs()) {
                Toast.makeText(
                        this,
                        "Failed to create directory "
                                + outputDir.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                outputDir = null;
            }
        //}
        return outputDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final boolean isKitKatVersion = Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20 ;
        Bitmap src=null;
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_CAMERA) {
                //final Bitmap src = (Bitmap) data.getExtras().get("data");
                //Uri uri=getImageUri(getApplicationContext(), src);
                Log.d("Medipta", "Compressing Image");
                String selectedImagePath = Utils.compressImage(mHighQualityImageUri.toString(),
                        AddArticleActivity.this);
                //mHighQualityImageUri = Uri.parse(imagePath);
                Log.d("Medipta", "Image Path: " + selectedImagePath);
//                String selectedImagePath = getPath(getApplicationContext(), mHighQualityImageUri);
                /*BitmapFactory.Options options = new BitmapFactory.Options();

                if(isKitKatVersion) {
                    options.inSampleSize = 8;
                }
                else
                {
                    options.inSampleSize = 2;
                }*/
               /* src = BitmapFactory.decodeFile(selectedImagePath, options);
                int convertedHeight = options.outHeight;
                int convertedWidth = options.outWidth;
                Log.d("Medipta", "convertedHeight: " + convertedHeight + " --- convertedWidth: "
                        + convertedWidth);*/
                src = BitmapFactory.decodeFile(selectedImagePath);
                if(selectedImagePath!=null) {
                    File file = new File(selectedImagePath);
                    Log.d("Medipta", "Image Size: " + file.length()/1000 + "Kb");
                    imageLength = file.length();
                }
                final ImageView currentImg;
                dialogPop.setCancelable(false);
                dialogPop.setCanceledOnTouchOutside(false);
                dialogPop.setContentView(R.layout.custom_popup_progress);
                dialogPop.setTitle("Select Tag");
                currentImg = (ImageView) dialogPop.findViewById(R.id.currentImg);
                Button resetImage = (Button) dialogPop.findViewById(R.id.resetImage);
                Button addNewImage = (Button) dialogPop.findViewById(R.id.addNewImage);
//                final Spinner tagSpinner = (Spinner) dialogPop.findViewById(R.id.tagSpinner);
                final EditText edt_desc = (EditText) dialogPop.findViewById(R.id.edt_desc);
//                if (SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab")) {
//                    tagSpinner.setSelection(1);
//                } else {
//                    tagSpinner.setSelection(0);
//                }
                currentImg.setImageBitmap(src);
                //currentImg.setImageURI(mHighQualityImageUri);
                deleteImage();
                final Bitmap finalSrc = src;
                addNewImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(finalSrc!=null) {
                            if (imageLength > imgMaxLimit) {
                                Toast.makeText(getApplicationContext(), "Image size should not exceed 5MB", Toast.LENGTH_SHORT).show();
                            } else {
                                if (imageLength > imgUnCompressLimit) {
//                                    tagSpinner.getSelectedItem();

                                    ArrayList<String> base64 = new ArrayList<String>();
                                    base64.add(getStringImage(finalSrc, 80));
                                    ArrayList<String> tags = new ArrayList<String>();
//                                    tags.add(tagSpinner.getSelectedItem().toString());
                                    String description=edt_desc.getText().toString();
//                                    if (SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab")) {
//                                        createImageView(base64, true, tags, description);
//                                    } else {
//                                        createImageView(base64, false, tags, description);
//                                    }
                                    createImageView(base64, false, tags, description);

                                    dialogPop.dismiss();
                                } else {
//                                    tagSpinner.getSelectedItem();

                                    ArrayList<String> base64 = new ArrayList<String>();
                                    base64.add(getStringImage(finalSrc, 80));
                                    ArrayList<String> tags = new ArrayList<String>();
//                                    tags.add(tagSpinner.getSelectedItem().toString());
                                    String description=edt_desc.getText().toString();
//                                    if (SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab")) {
//                                        createImageView(base64, true, tags, description);
//                                    } else {
//                                        createImageView(base64, false, tags, description);
//                                    }
                                    createImageView(base64, false, tags, description);


                                    dialogPop.dismiss();
                                }
                            }
                        }
                        else
                        {
                            Utils.makeToast(getApplicationContext(), "Invalid image");
                        }
                    }
                });
                resetImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPop.dismiss();
                    }
                });

                dialogPop.show();
            }
            else  if (requestCode == SELECT_PICTURE) {
                final Uri selectedImageUri = data.getData();
                String selectedImagePath = Utils.compressImage(selectedImageUri.toString(),
                        AddArticleActivity.this);
                Log.e("tag","image: "+selectedImagePath);
//                String selectedImagePath = getPath(getApplicationContext(), selectedImageUri);
                Bitmap bmp=null;
                File file = new File(selectedImagePath);
                imageLength = file.length();

                //BitmapFactory.Options options = new BitmapFactory.Options();

                //options.inSampleSize = 2;

                bmp = BitmapFactory.decodeFile(selectedImagePath);

                // Dlog Pop
                final ImageView currentImg;
                dialogPop.setCancelable(false);
                dialogPop.setCanceledOnTouchOutside(false);
                dialogPop.setContentView(R.layout.custom_popup_progress);
                dialogPop.setTitle("Select Tag");
                currentImg = (ImageView) dialogPop.findViewById(R.id.currentImg);
                Button resetImage = (Button) dialogPop.findViewById(R.id.resetImage);
                Button addNewImage = (Button) dialogPop.findViewById(R.id.addNewImage);
//                final Spinner tagSpinner = (Spinner) dialogPop.findViewById(R.id.tagSpinner);
                final EditText edt_desc = (EditText) dialogPop.findViewById(R.id.edt_desc);
//                if(SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab"))
//                {
//                    tagSpinner.setSelection(1);
//                }
//                else {
//                    tagSpinner.setSelection(0);
//                }

                currentImg.setImageBitmap(bmp);
                //currentImg.setImageURI(selectedImageUri);
                final Bitmap finalBmp = bmp;
                addNewImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(finalBmp!=null) {
                            if(imageLength>imgMaxLimit)
                            {
                                Toast.makeText(getApplicationContext(), "Image size should not exceed 5MB", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (imageLength > imgUnCompressLimit) {
//                                    tagSpinner.getSelectedItem();
                                    //addReportImages(getStringImage(bmp, 80), tagSpinner.getSelectedItem().toString());

                                    ArrayList<String> base64 = new ArrayList<String>();
                                    base64.add(getStringImage(finalBmp, 80));
                                    ArrayList<String> tags = new ArrayList<String>();
                                    tags.add("assd");
                                    String description=edt_desc.getText().toString();
//                                    if (SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab")) {
//                                        createImageView(base64, true, tags, description);
//                                    } else {
//                                        createImageView(base64, false, tags, description);
//                                    }
                                    createImageView(base64, false, tags, description);

                                    dialogPop.dismiss();
                                } else {
//                                    tagSpinner.getSelectedItem();
                                    //addReportImages(getStringImage(bmp, 100), tagSpinner.getSelectedItem().toString());

                                    ArrayList<String> base64 = new ArrayList<String>();
                                    base64.add(getStringImage(finalBmp, 80));
                                    ArrayList<String> tags = new ArrayList<String>();
//                                    tags.add(tagSpinner.getSelectedItem().toString());
                                    String description=edt_desc.getText().toString();
//                                    if (SaveSharedPreference.getAccessRole(getApplicationContext()).equals("Lab")) {
//                                        createImageView(base64, true, tags, description);
//                                    } else {
//                                        createImageView(base64, false, tags, description);
//                                    }
                                    createImageView(base64, false, tags, description);
                                    dialogPop.dismiss();
                                }
                            }
                        }
                        else
                        {
                            Utils.makeToast(getApplicationContext(), "Invalid image");
                            dialogPop.dismiss();
                        }
                    }
                });
                resetImage.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        dialogPop.dismiss();
                    }
                });

                dialogPop.show();
            }
        }
       /* if (requestCode == SELECT_DOCTOR) {
            Log.d("Medipta: ", "SELECT_DOCTOR requestCode = "+requestCode);

            if(data != null){
                String doctorId = data.getStringExtra("doctor_id");
                String doctorName = data.getStringExtra("doctor_name");

                if(doctorId.equals("")){
                    CreateReferralRequest();
                } else {
                    ShareReferralRequest(doctorId, doctorName);
                }
            } else {
                CreateReferralRequest();
            }
        }*/
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void createImageView(final ArrayList<String> base64StrList, Boolean isLab, final
    ArrayList<String> tagArr, String description) {
        tagArr.add("tag");
        Log.e("tag","array1: " + tagArr.size());
        Log.e("tag","array: " + base64StrList.size());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);

        for (int i = 0; i < base64StrList.size(); i++) {

            if (base64StrList.size() > i) {
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = vi.inflate(R.layout.custom_image_view_with_cross_button, null);
               /*  layoutParams.setMargins(0, 0, 10, 0);
                view.setLayoutParams(layoutParams);*/http://localhost/api/method/phr.add_fitness_progress

                crossView = view.findViewById(R.id.cancelView);

                crossView.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {

                        int position = allImagesLay.indexOfChild(view);
                        allImagesLay.removeViewAt(position);
                        imgUploadCombinList.remove(position);
                        Njarray.remove(position);
                    }
                });

                ImageView imageView = (ImageView) view.findViewById(R.id.imageToSet);
                byte[] imageAsBytes = Base64.decode(base64StrList.get(i).getBytes(),
                        Base64
                                .DEFAULT);
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 4;

                imageView.setImageBitmap(
                        BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
                );

                ImageUploadObj imageUploadObj = new ImageUploadObj();
                imageUploadObj.setFile_name(".jpg");
                imageUploadObj.setReferred_image(base64StrList.get(i));
                imageUploadObj.setTag(tagArr.get(i));

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("base64_image", base64StrList.get(i));
                    jsonObject.put("img_extension", ".jpg");
                    jsonObject.put("image_label", tagArr.get(i));
                    //jsonObject.put("image_label", description);
                    Log.e("tag","json: "+jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                imgUploadCombinList.add(imageUploadObj);
                int pos=allImagesLay.getChildCount()-1;
                allImagesLay.addView(view,pos);
                //crossView.setTag(pos);
                Njarray.add(jsonObject);
            }
        }

    }


    public void sendRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();

                return params;
            }
        };
        MySingleton.getInstance(AddArticleActivity.this).addTorequestque(stringRequest);

    }
}
