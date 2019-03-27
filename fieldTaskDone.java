package com.example.gb.forcemultiplier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class fieldTaskDone extends AppCompatActivity {

    private Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_task_done);

        home = (Button)findViewById(R.id.homeB);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldTaskDone.super.onBackPressed();
            }
        });
    }


    public void onBackPressed() {
        fieldTaskDone.super.onBackPressed();
    }
}
