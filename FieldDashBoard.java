package com.example.gb.forcemultiplier;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FieldDashBoard extends AppCompatActivity {
    private TextView taskStat;
    private Button checkin;
    private String accessToken;
    private Session fieldSession;
    @Override
    protected void onStart() {
        if (isNetworkAvailable(getApplicationContext())) {
            super.onStart();
        } else {
            Intent noint = new Intent(getApplicationContext(),NoInternet.class);
            startActivity(noint);
        }

        accessToken = getIntent().getExtras().getString("accessToken");
        fieldSession = new Session(getApplicationContext());
        fieldSession.setuserToken(accessToken);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_dash_board);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        taskStat=(TextView)findViewById(R.id.taskStatus);
        checkin =(Button)findViewById(R.id.checkInButton);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getApplicationContext())) {
                    checkIn();
                } else {
                    Intent noint = new Intent(getApplicationContext(),NoInternet.class);
                    startActivity(noint);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to LogOut?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            FieldDashBoard.super.onBackPressed();
                        }
                    }).create().show();
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
            taskStat.setText(city);
            upDateLocation(stringLatitude,stringLongitude);
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    private void upDateLocation(String stringLatitude, String stringLongitude) {
        RequestParams rp = new RequestParams();
        rp.add("latitude", stringLatitude);
        rp.add("longitude", stringLongitude);
        rp.add("status","Idle");
        ForceApiUtil.setHeader("Authorization",fieldSession.getuserToken());
        ForceApiUtil.post("setfieldengdata", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("rest","Calling restapi checkin");
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    int result = serverResp.getInt("statusCode");
                    if(result==200){
                        Toast.makeText(getApplicationContext(),"Your Location has been recorded",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Unable to Set Location",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unable to write location", Toast.LENGTH_LONG).show();
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

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}




