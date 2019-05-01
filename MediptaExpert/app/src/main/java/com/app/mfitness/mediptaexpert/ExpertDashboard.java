package com.app.mfitness.mediptaexpert;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.pushy.sdk.Pushy;

public class ExpertDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences.Editor editor;
    boolean doubleBackToExitPressedOnce = false;
    CoordinatorLayout coordinatorLayout;
    private TextView mTxtName,mTxtEmail;
    private ImageView mImgProfile;
    SharedPrefManager sharedPrefManager;
    Activity context = this;
    String name,email,image,currentVersion;

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment selectedFragment=null;
//
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    selectedFragment = FragmentExpertDashboard.newInstance();
//                   break;
//                case R.id.navigation_paid:
//                    selectedFragment = FragmentPaid.newInstance();
//                  break;
//                case R.id.navigation_unpaid:
//                    selectedFragment = FragmentUnpaid.newInstance();
//                    break;
//            }
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.frame_container, selectedFragment);
//            transaction.commit();
//            return true;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard");
        setSupportActionBar(toolbar);
        coordinatorLayout = findViewById(R.id.container);
        sharedPrefManager = new SharedPrefManager();

        try {
             currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.d("tag","Current version : "+currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Pushy.listen(this);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
//        boolean isRegistered = Pushy.isRegistered( ctx);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, FragmentExpertDashboard.newInstance());
        transaction.commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        mTxtName = header.findViewById(R.id.txtUserName);
        mTxtEmail = header.findViewById(R.id.txtUserEmail);
        mImgProfile = header.findViewById(R.id.imageView);

        name = sharedPrefManager.getExpertName(context);

        email = sharedPrefManager.getExpertEmail(context);
        image = sharedPrefManager.getExpertImage(context);

        mTxtName.setText(name);
        mTxtEmail.setText(email);

        Picasso.with( ExpertDashboard.this )
                .load( RestAPI.dev_api + image )
                .placeholder( R.drawable.ic_person )
                .into( mImgProfile);

         new GetVersionCode().execute();

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.app.medipta&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();

                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElements = ele.siblingElements();
                            for (Element sibElement : sibElements) {
                                newVersion = sibElement.text();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExpertDashboard.this);
                    builder.setMessage("New Update is available. Update now ??");
                    builder.setCancelable(false);
                    builder.setIcon(R.mipmap.m_logo);
                    builder.setTitle("Update Available!");
                    builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.app.medipta" )));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.app.medipta")));
                            }
                        }
                    });

                    builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            Log.d("update", "Current version : " + currentVersion + " playstore version : " + onlineVersion);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START) ) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else  if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please click BACK again to exit..", Snackbar.LENGTH_LONG);

            snackbar.show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }

            }, 2000);
        }

        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.expert_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_notification){
            Intent i = new Intent(ExpertDashboard.this,NotificationActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            dashboard();

        }else if (id == R.id.nav_profile ){
            Intent i = new Intent(ExpertDashboard.this,ExpertProfileActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_update_profile){
            Intent i = new Intent(ExpertDashboard.this,UpdateProfileActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.nav_fitness_call) {
            Intent i = new Intent(ExpertDashboard.this,UpcomingCallActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            logoutUser();

        }else if (id == R.id.nav_diet){
            Intent i = new Intent(ExpertDashboard.this,DietItemsActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_fitness_packages){
            Intent i = new Intent(ExpertDashboard.this,FitnessPackagesActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_today_call){
            Intent i = new Intent(ExpertDashboard.this,TodayCallActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_exercise_item){
            Intent i = new Intent(ExpertDashboard.this,ExerciseItemActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_article){
            Intent i = new Intent(ExpertDashboard.this,ArticleActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_sub_expired){
            Intent i = new Intent(ExpertDashboard.this,SubscriptionExpiredActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void shareApp(){

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "Download Medipta App now";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Install Medipta App");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));

    }

    public void paid(){

        Fragment selectedFragment = FragmentPaid.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, selectedFragment);
        transaction.commit();

    }

    public void unPaid(){

        Fragment selectedFragment = FragmentUnpaid.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, selectedFragment);
        transaction.commit();

    }
    public void dashboard(){

        Fragment selectedFragment = FragmentExpertDashboard.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, selectedFragment);
        transaction.commit();

    }

    public void logoutUser(){
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        editor = settings.edit();
        editor.clear();
        editor.commit();

        Intent i = new Intent(ExpertDashboard.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


}
