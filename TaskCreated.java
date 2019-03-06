package com.example.gb.forcemultiplier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskCreated extends AppCompatActivity {

    private TextView fieldeng;
    private Button backButton;
    private Session supportSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_created);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        supportSession = new Session(getApplicationContext());
        fieldeng = (TextView)findViewById(R.id.engName);
        backButton = (Button)findViewById(R.id.done);

        String name = getIntent().getExtras().getString("eng_name");
        name = fieldeng.getText().toString() + name ;
        fieldeng.setText(name);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCreated.super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        TaskCreated.super.onBackPressed();
    }
}
