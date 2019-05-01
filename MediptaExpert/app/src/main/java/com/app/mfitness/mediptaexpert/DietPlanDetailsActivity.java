package com.app.mfitness.mediptaexpert;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DietPlanDetailsActivity extends AppCompatActivity{
    AdapterExpandableList listAdapter;
    ExpandableListView expListView;
    List<Group> listGroup;
    List<Child> lunchList;
    List<Child> dinnerList;
    List<Child> snackList;
    List<Child> eveMidMealList;
    List<Child> breakFastList;
    List<Child> preWorkList;
    List<Child> postWorkList;
    List<Child> mrngMidMealList;
    List<Child> wakeUpList;
    Child wakeupChild,bfChild,preChild,postChild,midMealChild,lunchChild,eveMidChild,snackChild,dinnerChild;
    Group wakeUpGroup,bfGrp,preGrp,postGrp,midMealGrp,lunchGrp,eveMidGrp,snackGrp,dinnerGrp;
    String planName,name;
    HashMap<Group, List<Child>> listDataChild;
    private TextView mTxtTablets,mTxtFoodNotAllowed,mTxtWaterIntake;
    SharedPrefManager sharedPrefManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    Activity context = this;
    private LinearLayout layoutFabDiet;
    private LinearLayout layoutFabWater;
    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings,fabDiet,fabWater;
    private TextInputLayout mInputWater;
    private EditText mEdtWater;
    boolean isValid = true;
    private String waterQuantity,PLAN_NAME,startDate,endDate,userId,isActive,water;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan_details);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Diet Plan Details");
        setSupportActionBar(tb);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wakeUpGroup = new Group();
        bfGrp = new Group();
        preGrp = new Group();
        postGrp = new Group();
        midMealGrp = new Group();
        lunchGrp = new Group();
        eveMidGrp = new Group();
        snackGrp = new Group();
        dinnerGrp = new Group();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshDiet);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postRequest();
            }
        });

        fabSettings = this.findViewById(R.id.fabSetting);
        fabDiet = this.findViewById(R.id.fabDiet);
        fabWater = this.findViewById(R.id.fabWater);

        layoutFabDiet = this.findViewById(R.id.layoutFabDiet);
        layoutFabWater = this.findViewById(R.id.layoutFabWater);

        mTxtTablets = findViewById(R.id.txtTablets);
        mTxtFoodNotAllowed = findViewById(R.id.txtFoodNotAllowed);
        mTxtWaterIntake = findViewById(R.id.txtWaterIntake);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        PLAN_NAME = intent.getStringExtra("dietPlan");
        startDate = intent.getStringExtra("startDate");
        endDate = intent.getStringExtra("endDate");
        userId = intent.getStringExtra("userId");
        isActive = intent.getStringExtra("isActive");

        System.out.println("diet name : " + name);

        fabDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DietPlanDetailsActivity.this,AddDietItemActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

        fabWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.lay_prompt_water, null);

                mEdtWater = promptsView.findViewById(R.id.edtWater);
                mInputWater = promptsView.findViewById(R.id.txtInputWater);

                final AlertDialog d = new AlertDialog.Builder(context)
                        .setView(promptsView)
                        .setTitle("")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                if (mEdtWater.getText().toString().isEmpty()){

                                    mInputWater.setError("Please enter Water in Litres");
                                    isValid=false;

                                }else{

                                    postWaterRequest();
                                    mInputWater.setErrorEnabled(false);
                                    finish();
                                    startActivity(getIntent());

                                    d.dismiss();

                                }
                            }
                        });
                    }
                });

                d.show();
            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        closeSubMenusFab();

        expListView = findViewById(R.id.expandableListView);

        prepareListData();

        listAdapter = new AdapterExpandableList(this, listGroup, listDataChild);
        listAdapter.setOnPlaceClickListener(new PlaceClickListener());
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                 String selected = (String) listAdapter.getChild(
//                        groupPosition, childPosition);
//
//                System.out.println("selected : "+selected);
                return true;
            }
        });

        mTxtFoodNotAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DietPlanDetailsActivity.this,FoodNotAllowedActivity.class);
                i.putExtra("name",name);
                startActivity(i);

            }
        });

        mTxtTablets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DietPlanDetailsActivity.this,TabletsActivity.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

        postRequest();
    }

    private void closeSubMenusFab(){
        layoutFabWater.setVisibility(View.INVISIBLE);
        layoutFabDiet.setVisibility(View.INVISIBLE);

        fabSettings.setImageResource(R.drawable.ic_settings);
        fabExpanded = false;
    }

    private void openSubMenusFab(){
        layoutFabWater.setVisibility(View.VISIBLE);
        layoutFabDiet.setVisibility(View.VISIBLE);

        fabSettings.setImageResource(R.drawable.ic_close);
        fabExpanded = true;
    }

    private void prepareListData() {
        listGroup = new ArrayList<>();
        listDataChild = new HashMap<>();

        preGrp.diet = "Pre Workout";
        preGrp.Calories = "0";
        listGroup.add(preGrp);

        postGrp.diet = "Post Workourt";
        postGrp.Calories = "0";
        listGroup.add(postGrp);

        wakeUpGroup.diet = "Wakeup Drink";
        wakeUpGroup.Calories = "0";
        listGroup.add(wakeUpGroup);

        bfGrp.diet = "Breakfast";
        bfGrp.Calories = "0";
        listGroup.add(bfGrp);

        midMealGrp.diet = "Mid Meal (Morning)";
        midMealGrp.Calories = "0";
        listGroup.add(midMealGrp);

        lunchGrp.diet = "Lunch";
        lunchGrp.Calories = "0";
        listGroup.add(lunchGrp);

        eveMidGrp.diet = "Mid Meal (Evening)";
        eveMidGrp.Calories = "0";
        listGroup.add(eveMidGrp);

        snackGrp.diet = "Snack";
        snackGrp.Calories = "0";
        listGroup.add(snackGrp);

        dinnerGrp.diet = "Dinner";
        dinnerGrp.Calories = "0";
        listGroup.add(dinnerGrp);

        preWorkList = new ArrayList<>();
        preChild = new Child();
        preChild.food = "--";
        preChild.quantity = "--";
        preWorkList.add(preChild);
        listDataChild.put(listGroup.get(0),preWorkList);

        postWorkList = new ArrayList<>();
        postChild= new Child();
        postChild.food = "--";
        postChild.quantity = "--";
        postWorkList.add(postChild);
        listDataChild.put(listGroup.get(1),postWorkList);

        wakeUpList = new ArrayList<>();
        wakeupChild = new Child();
        wakeupChild.food = "--";
        wakeupChild.quantity = "--";
        wakeUpList.add(wakeupChild);
        listDataChild.put(listGroup.get(2),wakeUpList);

        breakFastList = new ArrayList<>();
        bfChild = new Child();
        bfChild.food = "--";
        bfChild.quantity = "--";
        breakFastList.add(bfChild);
        listDataChild.put(listGroup.get(3),breakFastList);

        mrngMidMealList = new ArrayList<>();
        midMealChild = new Child();
        midMealChild.food = "--";
        midMealChild.quantity = "--";
        mrngMidMealList.add(midMealChild);
        listDataChild.put(listGroup.get(4),mrngMidMealList);

        lunchList = new ArrayList<>();
        lunchChild = new Child();
        lunchChild.food = "--";
        lunchChild.quantity = "--";
        lunchList.add(lunchChild);
        listDataChild.put(listGroup.get(5),lunchList);

        eveMidMealList = new ArrayList<>();
        eveMidChild = new Child();
        eveMidChild.food = "--";
        eveMidChild.quantity = "--";
        eveMidMealList.add(eveMidChild);
        listDataChild.put(listGroup.get(6),eveMidMealList);

        snackList = new ArrayList<>();
        snackChild = new Child();
        snackChild.food = "--";
        snackChild.quantity = "--";
        snackList.add(snackChild);
        listDataChild.put(listGroup.get(7),snackList);

        dinnerList = new ArrayList<>();
        dinnerChild = new Child();
        dinnerChild.quantity = "--";
        dinnerChild.food = "--";
        dinnerList.add(dinnerChild);
        listDataChild.put(listGroup.get(8),dinnerList);

    }

    public void postWaterRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.add_water_intake",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response = "+response);

                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject message = jsonObject.getJSONObject("message");
                            String water = message.getString("water_intake");
                            if (water.equals("null")) {
                                mTxtWaterIntake.setText("--");
                            } if (water.equals("")){
                                mTxtWaterIntake.setText("--");
                            }
                             if (water.equals("1")){
                                mTxtWaterIntake.setText(water + " Litre");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                waterQuantity = mEdtWater.getText().toString();
                userParams.put("water_intake",waterQuantity);
                userParams.put("name",name);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(DietPlanDetailsActivity.this).addTorequestque(stringRequest);
    }

    public void postRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.get_diet_plan_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jArrPerson = null;

                        try {

                            JSONObject jsonObject= new JSONObject(response);
                            Iterator<?> keys = jsonObject.keys();
                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jsonObject.get(key) instanceof JSONObject ) {
                                    System.out.println("key is : "+key);
                                }
                            }
                            JSONObject message = jsonObject.getJSONObject("message");

                            JSONArray dinner = message.getJSONArray("dinner_item");
                            JSONArray lunch = message.getJSONArray("lunch_item");
                            JSONArray snack = message.getJSONArray("snack_item");
                            JSONArray wakeUp = message.getJSONArray("wakeupdrink_item");
                            JSONArray preWorkOut = message.getJSONArray("preworkout_item");
                            JSONArray postWorkout = message.getJSONArray("postworkout_item");
                            JSONArray mrngMidMeal = message.getJSONArray("morningmidneal_item");
                            JSONArray eveMidMeal = message.getJSONArray("eveningmidmeal_item");
                            JSONArray tablet = message.getJSONArray("tablets");
                            JSONArray breakfast = message.getJSONArray("breakfast_item");
                            water = message.getString("water_intake");
                            planName = message.getString("name");

                            if (water.equals("null")) {
                                mTxtWaterIntake.setText("--");
                            }else if (water.equals("1")){
                                mTxtWaterIntake.setText(water + " Litre");
                            }else if (water.equals("")){
                                mTxtWaterIntake.setText("--");
                            }else {
                                mTxtWaterIntake.setText(water+ " Litres");
                            }

                            preWorkList = new ArrayList<>();
                            preGrp.Calories = message.getString("preworkout_calories");
                            if (preGrp.Calories.equals("null")){
                                preGrp.Calories = "0";
                            }
                            for (int i = 0; i < preWorkOut.length(); i++){
                                preChild = new Child();
                                JSONObject jPre = preWorkOut.getJSONObject(i);
                                preChild.food = jPre.getString("item_name");
                                preChild.quantity = jPre.getString("quantity");
                                preChild.name = jPre.getString("name");
                                preChild.itemType = "preworkout";
                                preWorkList.add(preChild);
                                listDataChild.put(listGroup.get(0),preWorkList);
                            }

                            postWorkList = new ArrayList<>();
                            postGrp.Calories = message.getString("postworkout_calories");
                            if (postGrp.Calories.equals("null")){
                                postGrp.Calories = "0";
                            }

                            for (int i = 0; i < postWorkout.length(); i++){
                                postChild = new Child();
                                JSONObject jPost = postWorkout.getJSONObject(i);
                                postChild.food = jPost.getString("item_name");
                                postChild.name = jPost.getString("name");
                                System.out.println("Diet item : " + postChild.food);
                                postChild.quantity = jPost.getString("quantity");
                                postChild.itemType = "postworkout";
                                postWorkList.add(postChild);
                                listDataChild.put(listGroup.get(1),postWorkList);

                            }

                            wakeUpList = new ArrayList<>();
                            wakeUpGroup.Calories = message.getString("wakeupdrink_calories");
                            if (wakeUpGroup.Calories.equals("null")){
                                wakeUpGroup.Calories = "0";
                            }
                            for (int i = 0; i < wakeUp.length(); i++){
                                wakeupChild = new Child();
                                JSONObject jWake = wakeUp.getJSONObject(i);
                                wakeupChild.food = jWake.getString("item_name");
                                wakeupChild.quantity = jWake.getString("quantity");
                                wakeupChild.name = jWake.getString("name");
                                wakeupChild.itemType = "wakupdrink";
                                wakeUpList.add(wakeupChild);
                                listDataChild.put(listGroup.get(2),wakeUpList);
                            }

                            breakFastList = new ArrayList<>();
                            bfGrp.Calories = message.getString("breakfast_calories");
                            if (bfGrp.Calories.equals("null")){
                                bfGrp.Calories = "0";
                            }

                            for (int m = 0; m < breakfast.length(); m++) {
                                bfChild = new Child();
                                JSONObject jBreakfast = breakfast.getJSONObject(m);
                                bfChild.food = jBreakfast.getString("item_name");
                                bfChild.quantity = jBreakfast.getString("quantity");
                                bfChild.name = jBreakfast.getString("name");
                                bfChild.itemType = "breakfast";
                                breakFastList.add(bfChild);
                                listDataChild.put(listGroup.get(3), breakFastList);
                            }

                            mrngMidMealList = new ArrayList<>();
                            midMealGrp.Calories = message.getString("morningmidneal_calories");
                            if (midMealGrp.Calories.equals("null")){
                                midMealGrp.Calories = "0";
                            }
                            for (int i = 0; i < mrngMidMeal.length(); i++){
                                midMealChild = new Child();
                                JSONObject jMrngMeal = mrngMidMeal.getJSONObject(i);
                                midMealChild.food = jMrngMeal.getString("item_name");
                                midMealChild.quantity = jMrngMeal.getString("quantity");
                                midMealChild.name = jMrngMeal.getString("name");
                                midMealChild.itemType = "morningmidmeal";
                                mrngMidMealList.add(midMealChild);
                                listDataChild.put(listGroup.get(4),mrngMidMealList);
                            }

                            lunchList = new ArrayList<>();
                            lunchGrp.Calories = message.getString("lunch_calories");
                            if (lunchGrp.Calories.equals("null")){
                                lunchGrp.Calories = "0";
                            }
                            for (int j = 0; j < lunch.length(); j++) {
                                lunchChild = new Child();
                                JSONObject jLunch = lunch.getJSONObject(j);
                                lunchChild.food = jLunch.getString("item_name");
                                lunchChild.quantity = jLunch.getString("quantity");
                                lunchChild.name = jLunch.getString("name");
                                lunchChild.itemType = "lunch";
                                lunchList.add(lunchChild);
                                listDataChild.put(listGroup.get(5), lunchList);
                            }

                            eveMidMealList = new ArrayList<>();
                            eveMidGrp.Calories = message.getString("eveningmidmeal_calories");
                            if (eveMidGrp.Calories.equals("null")){
                                eveMidGrp.Calories = "0";
                            }
                            for (int l = 0; l < eveMidMeal.length(); l++){
                                eveMidChild = new Child();
                                JSONObject jEveMidMeal = eveMidMeal.getJSONObject(l);
                                eveMidChild.food = jEveMidMeal.getString("item_name");
                                eveMidChild.quantity = jEveMidMeal.getString("quantity");
                                eveMidChild.name = jEveMidMeal.getString("name");
                                eveMidChild.itemType = "eveningmidmeal";
                                eveMidMealList.add(eveMidChild);
                                listDataChild.put(listGroup.get(6),eveMidMealList);
                            }

                            snackList = new ArrayList<>();
                            snackGrp.Calories = message.getString("snack_calories");
                            if (snackGrp.Calories.equals("null")){
                                snackGrp.Calories = "0";
                            }
                            for (int k = 0; k < snack.length(); k++) {
                                snackChild = new Child();
                                JSONObject jSnack = snack.getJSONObject(k);
                                snackChild.food = jSnack.getString("item_name");
                                snackChild.quantity = jSnack.getString("quantity");
                                snackChild.name = jSnack.getString("name");
                                snackChild.itemType = "snacks";
                                snackList.add(snackChild);
                                listDataChild.put(listGroup.get(7), snackList);
                            }

                            dinnerList = new ArrayList<>();
                            dinnerGrp.Calories = message.getString("dinner_calories");
                            if (dinnerGrp.Calories.equals("null")){
                                dinnerGrp.Calories = "0";
                            }
                            for (int i = 0; i < dinner.length(); i++) {
                                dinnerChild = new Child();
                                JSONObject jPerson = dinner.getJSONObject(i);
                                dinnerChild.food = jPerson.getString("item_name");
                                dinnerChild.quantity = jPerson.getString("quantity");
                                dinnerChild.name = jPerson.getString("name");
                                dinnerChild.itemType = "dinner";

                                System.out.println("Dinner Food : "+dinnerChild.food);
                                dinnerList.add(dinnerChild);
                            }

                            listDataChild.put(listGroup.get(8), dinnerList);

                            listAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e("response","Response = "+response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("name",name);

                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(DietPlanDetailsActivity.this).addTorequestque(stringRequest);
    }

    public void deletePlan(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                RestAPI.dev_api + "api/method/phr.delete_diet_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","Response = "+response);
                        JSONObject aq = null;
                        try {
                            aq = new JSONObject(response);
                            JSONObject msg = aq.getJSONObject("message");

                            if (msg.getString("returncode").equals("200")){

                                Toast.makeText(getApplicationContext(),"Diet Plan Deleted Successfully..!!",Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getApplicationContext(), "Something went wrong..!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userParams = new HashMap<>();
                userParams.put("name",name);
                JSONObject userJson = new JSONObject(userParams);
                params.put("data",userJson.toString());
                return params;
            }
        };

        MySingleton.getInstance(this).addTorequestque(stringRequest);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        postRequest();
    }

//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        finish();
//        startActivity(getIntent());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_diet_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to delete "+PLAN_NAME + " ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deletePlan();
                            finish();
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;

        }else if (id == R.id.action_edit){
            Intent i = new Intent(DietPlanDetailsActivity.this,EditDietPlanActivity.class);
            i.putExtra("startDate",startDate);
            i.putExtra("endDate",endDate);
            i.putExtra("planName",PLAN_NAME);
            i.putExtra("userId",userId);
            i.putExtra("name",name);
            i.putExtra("isActive",isActive);
            i.putExtra("water",water);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class PlaceClickListener implements AdapterExpandableList.OnPlaceClickListener{

        @Override
        public void onDeleteClick(Child child) {

        }

        @Override
        public void onEditClick(final Child child) {
            System.out.println("child name : "+child.name);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove "+child.food+" ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                Toast.makeText(DietPlanDetailsActivity.this,child.food,Toast.LENGTH_SHORT).show();
                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        RestAPI.dev_api + "api/method/phr.delete_item",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("response","Response = "+response);
                                                listAdapter.notifyDataSetChanged();
                                                finish();
                                                startActivity(getIntent());

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();

                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        Map<String,String> userParams = new HashMap<>();
                                        userParams.put("name",child.name);
                                        userParams.put("item_type",child.itemType);
                                        JSONObject userJson = new JSONObject(userParams);
                                        params.put("data",userJson.toString());
                                        return params;
                                    }

                                };

                                MySingleton.getInstance(DietPlanDetailsActivity.this).addTorequestque(stringRequest);
                                onRestart();
                            }
                        });

                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

        }
    }
}
