package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SelectExerciseActivity extends AppCompatActivity{
    private TextView mTxtStrength,mTxtCardio;
    public String exeName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Select Exercise");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        exeName = intent.getStringExtra("name");
        System.out.println("exe name : "+exeName);

        mTxtStrength = findViewById(R.id.txtStrengthExercise);
        mTxtCardio = findViewById(R.id.txtCardioExercise);

        mTxtStrength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectExerciseActivity.this,StrengthActivity.class);
                i.putExtra("name",exeName);
                startActivity(i);
            }
        });

        mTxtCardio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectExerciseActivity.this,CardioActivity.class);
                i.putExtra("name",exeName);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
