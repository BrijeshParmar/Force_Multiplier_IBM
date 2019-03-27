package com.example.gb.forcemultiplier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class busyFragment extends Fragment {

    private Button checkin;
    private String accessToken;
    private Session fieldSession;
    private String u_id;
    private TextView custName;
    public String[] taskInfo;
    private TextView issuedesc;
    private TextView rtime;
    private Button gotomap;
    private Button tDone;
    private String taskId;
    public double latitude_location;
    public double longitude_location;

    @Override
    public void onStart() {
        if (isNetworkAvailable(getActivity())) {
            super.onStart();
        } else {
            Intent noint = new Intent(getActivity(),NoInternet.class);
            startActivity(noint);
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        taskInfo = getArguments().getStringArray("task");
        return inflater.inflate(R.layout.fragment_buzy_eng, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        custName = (TextView)getView().findViewById(R.id.cust_name_b);
        issuedesc =(TextView)getView().findViewById(R.id.issue_desc);
        rtime =(TextView)getView().findViewById(R.id.r_time);
        gotomap = (Button)getView().findViewById(R.id.go_maps);
        tDone = (Button)getView().findViewById(R.id.tdone);
        accessToken = getActivity().getIntent().getExtras().getString("accessToken");
        u_id = getActivity().getIntent().getExtras().getString("u_id");
        fieldSession = new Session(getActivity());

        custName.setText(taskInfo[0]);
        issuedesc.setText(taskInfo[1]);
        rtime.setText(taskInfo[4]);
        taskId = taskInfo[5];
        checkin = (Button)getView().findViewById(R.id.checkInButton_b);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getActivity())) {
                    checkIn();
                } else {
                    Intent noint = new Intent(getActivity(),NoInternet.class);
                    startActivity(noint);
                }
            }
        });

        gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + taskInfo[3] + "," + taskInfo[2] + " (" + taskInfo[0] + ")";
                Intent gointent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(geoUri));
                startActivity(gointent);
            }
        });

        tDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTask(taskId);
            }
        });

    }

    private void removeTask(String taskId) {
        ForceApiUtil.setHeader("Authorization",fieldSession.getuserToken());
        ForceApiUtil.get("check-out/"+taskId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("rest","Calling restapi task check out");
                Intent gotodone = new Intent(getContext(),fieldTaskDone.class);
                startActivity(gotodone);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        });

    }

    public void checkIn(){
        GPSTracker gpsTracker = new GPSTracker(getContext());

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            String country = gpsTracker.getCountryName(getContext());
            String city = gpsTracker.getLocality(getContext());
            String postalCode = gpsTracker.getPostalCode(getContext());
            String addressLine = gpsTracker.getAddressLine(getContext());
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
                        Toast.makeText(getActivity(),"Your Location has been recorded",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(),"Unable to Set Location",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Unable to write location", Toast.LENGTH_LONG).show();
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    int result = errorResponse.getInt("statusCode");
                    if(result==400){
                        Toast.makeText(getActivity(), errorResponse.getString("error"), Toast.LENGTH_LONG).show();
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
