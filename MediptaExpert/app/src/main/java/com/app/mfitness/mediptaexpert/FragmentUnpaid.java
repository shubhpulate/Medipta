package com.app.mfitness.mediptaexpert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FragmentUnpaid extends Fragment {
    private RecyclerView mRecyclerPerson;
    private ArrayList<Person> mListPerson;
    private AdapterUnpaid mAdapterUnpaid;
    private ProgressDialog mProgressDialog;
    String name;

    public static FragmentUnpaid newInstance() {
        FragmentUnpaid fragment1 = new FragmentUnpaid();
        return fragment1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lay_fragment_unpaid,container,false);
        mRecyclerPerson = view.findViewById( R.id.recyclerPlaces );
        mRecyclerPerson.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ));
        mListPerson = new ArrayList<>();

        mAdapterUnpaid = new AdapterUnpaid( mListPerson );
        mRecyclerPerson.setAdapter( mAdapterUnpaid );

        mAdapterUnpaid.setOnPlaceClickListener( new FragmentUnpaid.PlaceClickListener() );

        mProgressDialog = new ProgressDialog( getActivity() );
        mProgressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        mProgressDialog.setMessage("Fetching Unpaid Clients...");
        mProgressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue( getActivity() );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_unpaid_fitness_users",
                null,
                new FragmentUnpaid.ResponseListener(),
                new FragmentUnpaid.ErrorListener()
        );

        requestQueue.add( jsonObjectRequest );

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
            Log.e("tag","Fragment : "+jResponse);

            mProgressDialog.dismiss();

            JSONArray jArrPerson = null;
            try {
                jArrPerson = jResponse.getJSONArray("message");
                for( int i = 0; i < jArrPerson.length(); i++ ) {

                    JSONObject jPerson = jArrPerson.getJSONObject( i );
                    Person person = new Person();

                     name = jPerson.getString("name");
                    System.out.println("name = "+name);

                    person.name = jPerson.getString("first_name");
                    person.contact = jPerson.getString("contact");
                    person.fitnessGoal = jPerson.getString("fitness_goal");
                    person.email = jPerson.getString("email");
                    person.gender  = jPerson.getString("gender");
                    person.BMI = jPerson.getString("bmi");
                    person.age = jPerson.getInt("age");
                    person.profileId = jPerson.getString("profile_id");

                    System.out.println("age = "+person.profileId);

                    mListPerson.add( person );
                }

                mAdapterUnpaid.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class PlaceClickListener implements AdapterUnpaid.OnPlaceClickListener {
        @Override
        public void onPlaceClick(Person person) {

            Intent i = new Intent(getActivity(),UnpaidUserDetailsActivity.class);
            i.putExtra("firstName",person.name);
            i.putExtra("contact",person.contact);
            i.putExtra("fitnessGoal",person.fitnessGoal);
            i.putExtra("email",person.email);
            i.putExtra("gender",person.gender);
            i.putExtra("bmi",person.BMI);
            i.putExtra("age",person.age);
            i.putExtra("name",name);
            i.putExtra("profileID",person.profileId);
            startActivity(i);
        }
    }
}
