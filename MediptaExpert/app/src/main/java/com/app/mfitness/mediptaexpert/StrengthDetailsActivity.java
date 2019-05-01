package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class StrengthDetailsActivity extends AppCompatActivity{
    private TextView mTxtTitle,mTxtDescription;
    private ImageView mImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strength_details);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Strength Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtDescription = findViewById(R.id.txtDescription);
        mImg = findViewById(R.id.imgStrength);

        Intent intent = getIntent();
        String img = intent.getStringExtra("image");
        Picasso.with(StrengthDetailsActivity.this)
                .load(RestAPI.dev_api + img)
                .placeholder(R.drawable.strength)
                .resize(500,0)
                .into(mImg);

        mTxtTitle.setText(intent.getStringExtra("exercise"));
        mTxtDescription.setText(intent.getStringExtra("description"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
