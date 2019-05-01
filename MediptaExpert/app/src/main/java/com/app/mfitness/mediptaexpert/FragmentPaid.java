package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentPaid extends Fragment {
    private RecyclerView mRecyclerPerson;
    private ArrayList<Person> mListPerson;
    private AdapterPaid mAdapterPerson;
    private ProgressDialog mProgressDialog;
    private String abc;
    SharedPrefManager sharedPrefManager ;

    public static FragmentPaid newInstance() {
        FragmentPaid fragment = new FragmentPaid();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lay_fragment_paid, container, false);
        mRecyclerPerson = view.findViewById(R.id.recyclerPlaces);
        mRecyclerPerson.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mListPerson = new ArrayList<>();

        mAdapterPerson = new AdapterPaid(mListPerson);
        mRecyclerPerson.setAdapter(mAdapterPerson);
        sharedPrefManager = new SharedPrefManager();

        mAdapterPerson.setOnPlaceClickListener(new PlaceClickListener());

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Fetching Paid Clients...");
        mProgressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_paid_fitness_users",
                null,
                new ResponseListener(),
                new ErrorListener()
        );

        requestQueue.add(jsonObjectRequest);

        return view;
    }

    class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mProgressDialog.dismiss();
        }
    }

    class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jResponse) {
            Log.e("tag", "Fragment : " + jResponse);

            mProgressDialog.dismiss();

            JSONArray jArrPerson = null;
            try {
                jArrPerson = jResponse.getJSONArray("message");
                for (int i = 0; i < jArrPerson.length(); i++) {

                    JSONObject jPerson = jArrPerson.getJSONObject(i);
                    Person person = new Person();

                    person.goalId = jPerson.getString("name");
                    System.out.println("name = " + person.goalId);

                    person.name = jPerson.getString("first_name");
                    person.contact = jPerson.getString("contact");
                    person.fitnessGoal = jPerson.getString("fitness_goal");
                    person.email = jPerson.getString("email");
                    person.gender = jPerson.getString("gender");
                    person.BMI = jPerson.getString("bmi");
                    person.age = jPerson.getInt("age");
                    person.profileId = jPerson.getString("profile_id");

                    System.out.println("profile id : "+person.profileId);

                    mListPerson.add(person);
                }

                mAdapterPerson.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class PlaceClickListener implements AdapterPaid.OnPlaceClickListener {
        @Override
        public void onPlaceClick(Person person) {

            Intent i = new Intent(getActivity(), PaidUserDetailsActivity.class);
            i.putExtra("firstName", person.name);
            i.putExtra("contact", person.contact);
            i.putExtra("fitnessGoal", person.fitnessGoal);
            i.putExtra("email", person.email);
            i.putExtra("gender", person.gender);
            i.putExtra("bmi", person.BMI);
            i.putExtra("age", person.age);
            i.putExtra("name",person.goalId);
            i.putExtra("profileId",person.profileId);
            i.putExtra("goalID",person.goalId);

            sharedPrefManager.saveGoalId(getActivity(),person.goalId);

            startActivity(i);
        }
    }



}
