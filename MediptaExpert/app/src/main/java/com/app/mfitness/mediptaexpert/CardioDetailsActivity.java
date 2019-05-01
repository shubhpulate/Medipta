package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CardioDetailsActivity extends AppCompatActivity{
    private TextView mTxtName,mTxtDescription;
    private ImageView mImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio_details);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Cardio Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTxtName = findViewById(R.id.txtTitle);
        mTxtDescription = findViewById(R.id.txtDescription);
        mImg = findViewById(R.id.imgCardio);

        Intent intent = getIntent();
        String img = intent.getStringExtra("image");
        mTxtName.setText(intent.getStringExtra("exercise"));
        mTxtDescription.setText(intent.getStringExtra("description"));

        Picasso.with(CardioDetailsActivity.this)
                .load(RestAPI.dev_api +img)
                .placeholder(R.drawable.cardio)
                .into(mImg);


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
