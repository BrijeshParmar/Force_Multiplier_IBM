package com.example.gb.forcemultiplier;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.net.ConnectivityManager;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class SetTask extends AppCompatActivity  {

    private NumberPicker np;
    private NumberPicker mp;
    private EditText custname;
    private EditText problem;
    private int p_hours;
    private int p_mins;
    private Button checkin, addTask;

    private String accessToken;
    private Session supportSession;
    private String cust_long;
    private String cust_lat;

    @Override
    protected void onStart() {
        if (isNetworkAvailable(getApplicationContext())) {
            super.onStart();
        } else {
            Intent noint = new Intent(getApplicationContext(),NoInternet.class);
            startActivity(noint);
        }

        accessToken = getIntent().getExtras().getString("accessToken");
        supportSession = new Session(getApplicationContext());
        supportSession.setuserToken(accessToken);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        custname.getText().clear();
        problem.getText().clear();
        np.setValue(0);
        mp.setValue(0);
        addTask.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        p_hours = 0;
        p_mins = 0;

        cust_long = "notset";
        cust_lat = "notset";
        addTask = (Button)findViewById(R.id.addTask);
        custname = (EditText)findViewById(R.id.custName);
        problem = (EditText)findViewById(R.id.problem);
        checkin =(Button)findViewById(R.id.checkin);
        addTask.setEnabled(false);
        np = (NumberPicker)findViewById(R.id.hour_time);
        np.setMinValue(0);// restricted number to minimum value i.e 1
        np.setMaxValue(24);// restricked number to maximum value i.e. 31
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                p_hours = newVal;
            }
        });
        mp = (NumberPicker)findViewById(R.id.min_time);
        mp.setMinValue(0);// restricted number to minimum value i.e 1
        mp.setMaxValue(60);// restricked number to maximum value i.e. 31
        mp.setWrapSelectorWheel(true);

        mp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                p_mins = newVal;
            }
        });


        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTask.setEnabled(true);
                if (isNetworkAvailable(getApplicationContext())) {
                    checkIn();
                } else {
                    Intent noint = new Intent(getApplicationContext(),NoInternet.class);
                    startActivity(noint);
                }
            }
        });


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String custName = custname.getText().toString();
                String description = problem.getText().toString();
                int r_time =  p_hours*60+p_mins;
                createNewTask(custName,description,r_time,cust_lat,cust_long);
            }
        });

    }


    public void checkIn(){
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            String country = gpsTracker.getCountryName(this);
            String city = gpsTracker.getLocality(this);
            String postalCode = gpsTracker.getPostalCode(this);
            String addressLine = gpsTracker.getAddressLine(this);

            cust_lat = stringLatitude;
            cust_long = stringLongitude;
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to LogOut?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SetTask.super.onBackPressed();
                    }
                }).create().show();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }



    private void createNewTask(String customer_name,String problem_desc,int req_time,String stringLatitude, String stringLongitude) {
        RequestParams rp = new RequestParams();
        rp.add("latitude", stringLatitude);
        rp.add("longitude", stringLongitude);
        rp.add("reqTime",String.valueOf(req_time));
        rp.add("custName",customer_name);
        rp.add("description",problem_desc);
        ForceApiUtil.setHeader("Authorization",supportSession.getuserToken());

        ForceApiUtil.post("set-task", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("rest","Calling restapi create Task");
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    final JSONObject res_code = serverResp.getJSONObject("task");
                    String result = res_code.getString("_id");
                    if(result!=null){
                        ForceApiUtil.setHeader("Authorization",supportSession.getuserToken());
                        ForceApiUtil.get("assign-eng/"+result, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    final JSONObject task = response.getJSONObject("results");
                                    String field_eng = task.getString("fieldEngineer");
                                    Intent redirect = new Intent(getApplicationContext(),TaskCreated.class);
                                    redirect.putExtra("eng_name",field_eng);
                                    startActivity(redirect);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Unable to Create Task",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    int result = errorResponse.getInt("statusCode");
                    if(result==400){
                        Toast.makeText(getApplicationContext(), errorResponse.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        });
    }

}



