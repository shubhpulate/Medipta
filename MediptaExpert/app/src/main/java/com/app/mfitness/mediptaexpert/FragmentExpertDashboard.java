package com.app.mfitness.mediptaexpert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentExpertDashboard extends Fragment {
//  private TextView mTxtPaidClient,mTxtUnpaidClient,mTxtDietItems,mTxtFitnessPackage;
    private LinearLayout llPaid,llUnpaid,llPackage,llDiet,llTodatCall,llUpcomingCall;

    public static FragmentExpertDashboard newInstance() {
        FragmentExpertDashboard fragment1 = new FragmentExpertDashboard();
        return fragment1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lay_fragment_expert_dashboard,container,false);
        llPaid = view.findViewById(R.id.llPaid);
        llUnpaid = view.findViewById(R.id.llUnpaid);
        llPackage = view.findViewById(R.id.llPackage);
        llDiet = view.findViewById(R.id.llDietItem);
        llTodatCall = view.findViewById(R.id.llTodayCall);
        llUpcomingCall = view.findViewById(R.id.llUpcoming);

        llPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),PaidClientActivity.class);
                startActivity(i);
            }
        });

//
        llTodatCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(getActivity(),TodayCallActivity.class);
              startActivity(i);
            }
        });

        llUpcomingCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),UpcomingCallActivity.class);
                startActivity(i);
            }
        });

        llUnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),UnpaidClientActivity.class);
                startActivity(i);
            }
        });

        llDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),DietItemsActivity.class);
                startActivity(i);

            }
        });

        llPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),FitnessPackagesActivity.class);
                startActivity(i);

            }
        });

        return view;
    }

}
