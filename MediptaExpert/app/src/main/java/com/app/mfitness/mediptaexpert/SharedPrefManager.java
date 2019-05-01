package com.app.mfitness.mediptaexpert;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String PREFS_NAME = "MED_PREFS";
    public static final String PREFS_EXPERT_ID = "MED_PREFS_String";
    public static final String PREFS_GOAL_ID = "MED_PREFS_GOAL";
    public static final String PREFS_EXPERT_NAME = "MED_EXPERT_NAME";
    public static final String PREFS_EXPERT_EMAIL = "MED_EXPERT_EMAIL";
    public static final String PREFS_EXPERT_IMAGE = "MED_EXPERT_IMAGE";
    public static final String PREFS_USER_PROFILE_ID = "MED_USER_PROFILE_ID";
    public static final String PREFS_GCM_ID = "MED_GCM_ID";
    public static final String PREFS_DEVICE_ID = "MED_DEVICE_ID";

    public SharedPrefManager() {
        super();
    }

    public void setGCMId(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_GCM_ID,text);
        editor.commit();
    }

    public String getGCMId(Context context) {
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_GCM_ID, null);
        return text;
    }

    public void setDeviceId(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_DEVICE_ID,text);
        editor.commit();
    }

    public String getDeviceId(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        text = settings.getString(PREFS_DEVICE_ID,null);
        return text;
    }

    public void setUserId(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_USER_PROFILE_ID,text);
        editor.commit();
    }

    public String getUserId(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        text = settings.getString(PREFS_USER_PROFILE_ID,null);
        return text;
    }

    public void setExpertName(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_EXPERT_NAME,text);

        editor.commit();
    }

    public String getExpertName(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        text = settings.getString(PREFS_EXPERT_NAME,null);
        return text;
    }

    public void setExpertEmail(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_EXPERT_EMAIL,text);

        editor.commit();
    }

    public String getExpertEmail(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        text = settings.getString(PREFS_EXPERT_EMAIL,null);
        return text;
    }

    public void setExpertImage(Context context,String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_EXPERT_IMAGE,text);

        editor.commit();
    }

    public String getExpertImage(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        text = settings.getString(PREFS_EXPERT_IMAGE,null);
        return text;
    }


    public void setExpertId(Context context, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(PREFS_EXPERT_ID, text);

        editor.commit();
    }

    public String getExpertId(Context context) {
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_EXPERT_ID, null);
        return text;
    }

    public void saveGoalId(Context context, String text){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(PREFS_GOAL_ID, text);
        editor.commit();
    }

    public String getGoalId(Context context){
        SharedPreferences settings;
        String text;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_GOAL_ID,null);
        return text;
    }


    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(PREFS_EXPERT_ID);
        editor.commit();

    }
}
