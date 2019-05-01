package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param btn1
     *            the btn1
     * @param btn2
     *            the btn2
     * @param listener1
     *            the listener1
     * @param listener2
     *            the listener2
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg, String btn1,
                                         String btn2, DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        // builder.setTitle(R.string.app_name);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null && listener2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param btn1
     *            the btn1
     * @param btn2
     *            the btn2
     * @param listener1
     *            the listener1
     * @param listener2
     *            the listener2
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg, int btn1,
                                         int btn2, DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2)
    {

        return showDialog(ctx, ctx.getString(msg), ctx.getString(btn1),
                ctx.getString(btn2), listener1, listener2);

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param btn1
     *            the btn1
     * @param btn2
     *            the btn2
     * @param listener
     *            the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg, String btn1,
                                         String btn2, DialogInterface.OnClickListener listener)
    {

        return showDialog(ctx, msg, btn1, btn2, listener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        dialog.dismiss();
                    }
                });

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param btn1
     *            the btn1
     * @param btn2
     *            the btn2
     * @param listener
     *            the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg, int btn1,
                                         int btn2, DialogInterface.OnClickListener listener)
    {

        return showDialog(ctx, ctx.getString(msg), ctx.getString(btn1),
                ctx.getString(btn2), listener);

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param listener
     *            the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg,
                                         DialogInterface.OnClickListener listener)
    {

        return showDialog(ctx, msg, ctx.getString(android.R.string.ok), null,
                listener, null);
    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @param listener
     *            the listener
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg,
                                         DialogInterface.OnClickListener listener)
    {

        return showDialog(ctx, ctx.getString(msg),
                ctx.getString(android.R.string.ok), null, listener, null);
    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, String msg)
    {

        return showDialog(ctx, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

                dialog.dismiss();
            }
        });

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param msg
     *            the msg
     * @return the alert dialog
     */
    public static AlertDialog showDialog(Context ctx, int msg)
    {

        return showDialog(ctx, ctx.getString(msg));

    }

    /**
     * Show dialog.
     *
     * @param ctx
     *            the ctx
     * @param title
     *            the title
     * @param msg
     *            the msg
     * @param listener
     *            the listener
     */
    public static void showDialog(Context ctx, int title, int msg,
                                  DialogInterface.OnClickListener listener)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener);
        builder.setTitle(title);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Hide keyboard.
     *
     * @param ctx
     *            the ctx
     */
    public static final void hideKeyboard(Activity ctx)
    {

        if (ctx.getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    /**
     * Hide keyboard.
     *
     * @param ctx
     *            the ctx
     * @param v
     *            the v
     */
    public static final void hideKeyboard(Activity ctx, View v)
    {

        try
        {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static final void makeToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static final String convertDateToFormat(Date date, String format){

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return 	sdf.format(date);
    }

    public static final String convert24hoursTo12hoursTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

    public static String compressImage(String imageUri, Context context) {

        String filePath = getPath(context, Uri.parse(imageUri));
//        String filePath = getRealPathFromURI(imageUri, context);
        File file = new File(filePath);
        Log.d("Medipta", "Actual Image Size: " + file.length()/1000 + "Kb");

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory.
//      Just the bounds are loaded. If you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        Log.d("Medipta", "ActualHeight: " + actualHeight + " --- ActualWidth: " + actualWidth);

//      max Height and width values of the compressed image is taken as 1224x918

        float maxHeight = 1224.0f;
        float maxWidth = 918.0f;
        Log.d("Medipta", "MaxHeight: " + maxHeight + " --- MaxWidth: " + maxWidth);

        float imgRatio = actualHeight>0 ? actualWidth / actualHeight : 0;
        float maxRatio = maxHeight>0 ? maxWidth / maxHeight :0;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        String filename="";
        if(scaledBitmap!=null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                    middleY - bmp.getHeight() / 2,
                    new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("Medipta EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("Medipta EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("Medipta EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("Medipta EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(context);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return filename;
    }


    // The method getFilename():: It creates a folder in the SDCard used to store the images.
    public static String getFilename(Context context) {
		/*File file = new File(Environment.getExternalStorageDirectory().getPath(),
                "Medipta/Images");*/
        File photoDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(photoDir, context.getString(R.string.app_name));

        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    //  method getRealPathFromURI(imageUri):: Gives the actual filepath of the image from its contentUri::
    private static String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor =  context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    // The method calculateInSampleSize:: calculates a proper value for inSampleSize based on the actual and required dimensions:
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(Context context, Uri uri) {

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

    //Convert 12 Hrs to 24 Hrs
    public static String convertTo24Hour(String time)
    {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }

    //Convert 24 Hrs to 12 Hrs
    public static String convertTo12Hour(String time)
    {
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }
}
