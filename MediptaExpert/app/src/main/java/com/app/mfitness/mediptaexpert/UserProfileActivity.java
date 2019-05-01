package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity{
    private TextView mTxtFirstName,mTxtContact,mTxtEmail,mTxtBMI,mTxtAge,mTxtGender,mTxtGoal,mTxtName,mTxtAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mTxtFirstName = findViewById(R.id.txtPersonName);
        mTxtContact = findViewById(R.id.txtUserContact);
        mTxtEmail = findViewById(R.id.txtUserEmail);
        mTxtBMI = findViewById(R.id.txtUserBMI);
        mTxtAge = findViewById(R.id.txtUserAge);
        mTxtGender = findViewById(R.id.txtUserGender);
        mTxtGoal = findViewById(R.id.txtFitnessGoal);
        mTxtAddress = findViewById(R.id.txtPersonAddress);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("User Profile");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String firstName = intent.getStringExtra("firstName");
        final String contact = intent.getStringExtra("contact");
        final String email = intent.getStringExtra("email");
        final String bmi = intent.getStringExtra("bmi");
        final Integer age = intent.getIntExtra("age",0);
        final String gender = intent.getStringExtra("gender");
        final String goal = intent.getStringExtra("fitnessGoal");
        final String name = intent.getStringExtra("name");
        final String address = intent.getStringExtra("address");
        System.out.println("addres: "+address);

//        if (address.equals("null")){
//            mTxtAddress.setText("--");
//        }else {
//            mTxtAddress.setText(address);
//        }

        mTxtFirstName.setText(firstName);
        mTxtContact.setText(contact);
        mTxtEmail.setText(email);
        mTxtBMI.setText(bmi);
        mTxtAge.setText(age+"");
        mTxtGender.setText(gender);
        mTxtGoal.setText(goal);
//        mTxtAddress.setText(address);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
