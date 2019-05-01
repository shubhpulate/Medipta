package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ArticleDetailsActivity extends AppCompatActivity {
    private TextView mTxtTitle,mTxtDescription;
    private ImageView mImgArticle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Articles");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtDescription = findViewById(R.id.txtDescription);
        mImgArticle = findViewById(R.id.imgArticle);

        Intent intent = getIntent();
        String img = intent.getStringExtra("image");
        mTxtTitle.setText(intent.getStringExtra("title"));
        mTxtDescription.setText(intent.getStringExtra("description"));

        Picasso.with(ArticleDetailsActivity.this)
                .load(RestAPI.dev_api + img)
                .placeholder(R.mipmap.ic_launcher)
                .into(mImgArticle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
