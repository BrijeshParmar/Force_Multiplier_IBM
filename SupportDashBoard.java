package com.example.gb.forcemultiplier;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class SupportDashBoard extends AppCompatActivity implements
        View.OnClickListener {

    Button btnTimePicker;
    EditText txtTime;
    private int mHour, mMinute;
    private String accessToken;
    private Session supportSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_dash_board);

        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtTime=(EditText)findViewById(R.id.in_time);

        btnTimePicker.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }
}