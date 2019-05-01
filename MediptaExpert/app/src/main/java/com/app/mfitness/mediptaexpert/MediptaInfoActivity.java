package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import pl.droidsonroids.gif.GifTextView;

public class MediptaInfoActivity extends AppCompatActivity {
    private GifTextView mGif;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_gif);

        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);

        final boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
//            Intent intent = new Intent();
//            intent.setClass(MediptaInfoActivity.this, ExpertDashboard.class);
//            startActivity(intent);
//            MediptaInfoActivity.this.finish();
        }

        mGif = findViewById(R.id.gif);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hasLoggedIn)
                {
                    Intent intent = new Intent();
                    intent.setClass(MediptaInfoActivity.this, ExpertDashboard.class);
                    startActivity(intent);
                    MediptaInfoActivity.this.finish();
                }
                else {
                    Intent i = new Intent(MediptaInfoActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }
}
