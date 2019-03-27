package com.example.gb.forcemultiplier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FieldDashBoard extends AppCompatActivity {

    private SwipeRefreshLayout mylayout;
    private String accessToken;
    private Session fieldSession;
    private String u_id;
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    private JSONArray taskqueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_dash_board);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        accessToken = getIntent().getExtras().getString("accessToken");
        u_id = getIntent().getExtras().getString("u_id");
        fieldSession = new Session(getApplicationContext());
        fieldSession.clearSharedPreferences();
        fieldSession.setuserToken(accessToken);


        mylayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh1);

        checkfornewtask();

        mylayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkfornewtask();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        checkfornewtask();
    }

    @Override
    public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to Logout?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            FieldDashBoard.super.onBackPressed();
                        }
                    }).create().show();
    }

    private void checkfornewtask() {
        //Toast.makeText(getApplicationContext(), "Screen Refresh", Toast.LENGTH_LONG).show();

        RequestParams rp = new RequestParams();
        ForceApiUtil.setHeader("Authorization",fieldSession.getuserToken());
        ForceApiUtil.get("getfieldengdata", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("rest","Calling restapi checkin");
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    taskqueue = (JSONArray)serverResp.getJSONObject("results").get("taskQueue");
                    if(taskqueue.length()==0){
                        Fragment notask = new NoTaskFragment();
                        if(!notask.isVisible()) {
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = manager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, notask);
                            //fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                    else{
                        Fragment btask = new busyFragment();
                        if(!btask.isVisible()) {
                            Bundle beng = new Bundle();
                            ArrayList<taskQueue> taskList = new ArrayList<taskQueue>();
                            for(int i=0; i<taskqueue.length(); i++){
                                try{
                                    JSONObject json_data = taskqueue.getJSONObject(i);
                                    String taskId = json_data.getString("_id");
                                    String cName = json_data.getString("custName");
                                    String issue = json_data.getString("description");
                                    String lat = json_data.getString("latitude");
                                    String lon = json_data.getString("longitude");
                                    String rtime = json_data.getString("reqTime");
                                    taskList.add(new taskQueue(cName,issue,lat,lon,rtime, taskId));
                                }
                                catch (Exception e) {
                                    //Toast.makeText(getApplicationContext(), "Object is Null", Toast.LENGTH_SHORT).show();
                                }
                            }
                            try {
                                taskQueue task = taskList.get(0);
                                String[] arr = {task.getCustomerName(),task.getIssue(),task.getLatitude(),task.getLongitude(),task.getReq_time(),task.getTid()};
                                beng.putStringArray("task",arr);
                                beng.putInt("tcount",taskqueue.length());
                                btask.setArguments(beng);
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, btask);
                                //fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            catch (Exception e){
                                Fragment notask = new NoTaskFragment();
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, notask);
                                //fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unable to Access Server Api", Toast.LENGTH_LONG).show();
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

        mylayout.setRefreshing(false);
    }
}




