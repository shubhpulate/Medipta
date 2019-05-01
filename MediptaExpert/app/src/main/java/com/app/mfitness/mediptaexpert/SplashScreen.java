package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import pl.droidsonroids.gif.GifTextView;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=3000;
    ImageView img;
    GifTextView mGif;
    SharedPreferences sp;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        img = findViewById(R.id.imgMedipta);
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);

        final boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(hasLoggedIn)
                {
                    Intent intent = new Intent();
                    intent.setClass(SplashScreen.this, ExpertDashboard.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
                else {
                    Intent i = new Intent(SplashScreen.this, MediptaInfoActivity.class);
                    startActivity(i);

                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }

}
