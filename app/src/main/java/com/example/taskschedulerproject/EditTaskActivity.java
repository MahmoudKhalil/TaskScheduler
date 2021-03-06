package com.example.taskschedulerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {
    FloatingActionButton finish;
    EditText TaskTitle,TaskDiscription;
    TextView DatePicked, TimePicked;
    ImageButton calenderButton,timeButton;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    ArrayList<String> priorities;
    ArrayAdapter<String> adapter;
    Spinner priority;
    Calendar c,choosedCalender;
    int priorityId=0,MyDay=-1,MyMonth=-1,MyYear=-1,MyHour=-1,MyMin=-1;

    private DatabaseHelper dbh;

    private String intentTag = "";
    private String intentListName = "";

    private SimpleDateFormat stf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        choosedCalender = Calendar.getInstance();
        dbh = new DatabaseHelper(getApplicationContext());
        stf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        intentTag = getIntent().getStringExtra("MODE");
        intentListName = getIntent().getStringExtra("LIST");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#232931")));

        timeButton = findViewById(R.id.timeButton);
        priority = findViewById(R.id.PrioritySpinner);
        DatePicked = findViewById(R.id.dateEditText);
        TimePicked = findViewById(R.id.timeEdittext);
        TaskTitle = findViewById(R.id.NoteTitleEdittext);
        TaskDiscription = findViewById(R.id.NoteDiscriptionEdittext);
        calenderButton = findViewById(R.id.calenderButton);
        priority = findViewById(R.id.PrioritySpinner);
        priorities = new ArrayList<>(3);
        priorities.add("Low");
        priorities.add("Medium");
        priorities.add("High");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,priorities);
        finish = findViewById(R.id.NoteFAB);
        priority.setAdapter(adapter);

        priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                priorityId = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                priorityId = 0;
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int mHours = c.get(Calendar.HOUR_OF_DAY);
                int mMins = c.get(Calendar.MINUTE);
                MyHour = mHours;
                MyMin = mMins;

                tpd = new TimePickerDialog(EditTaskActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int mins) {
                        String hoursString = hours + "";
                        if(hours < 10) {
                            hoursString = "0" + hoursString;
                        }

                        String minsString = mins + "";
                        if(mins < 10) {
                            minsString = "0" + minsString;
                        }
                        TimePicked.setText(hoursString+":"+minsString);
                    }
                },mHours,mMins,true);
                tpd.show();

            }
        });
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                MyDay = day;
                MyMonth = month;
                MyYear = year;
                dpd = new DatePickerDialog(EditTaskActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        DatePicked.setText(i+"/"+(i1+1)+"/"+i2);
                        choosedCalender.set(i,i1,i2);
                    }
                },year,month,day);
                dpd.show();

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b1 = true, b2 = true, b3 = true, b4 = true, b5 = true;
                if (TaskTitle.getText().toString().isEmpty()) {
                    b1 = false;
                    Toast.makeText(getApplicationContext(), "No Title written, Please go set a title", Toast.LENGTH_SHORT).show();
                }
                else{
                    b1 = true;
                }
                if (TaskDiscription.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No Description written, Please go set a description", Toast.LENGTH_SHORT).show();
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (DatePicked.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No Date Picked, Please go set a date.", Toast.LENGTH_SHORT).show();
                    b3 = false;
                }
                else{
                    b3 = true;
                }
                if (TimePicked.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No Time Picked, Please go set a time.", Toast.LENGTH_SHORT).show();
                    b4 = false;
                }
                else{
                    b4 = true;
                }
                if (choosedCalender.before(c)) {
                    if (c.get(Calendar.YEAR) != choosedCalender.get(Calendar.YEAR) || c.get(Calendar.MONTH) != choosedCalender.get(Calendar.MONTH) || c.get(Calendar.DAY_OF_MONTH) != choosedCalender.get(Calendar.DAY_OF_MONTH)) {
                        Toast.makeText(getApplicationContext(), "The picked date is earlier than now, please pick valid date", Toast.LENGTH_SHORT).show();
                        b5 = false;
                    }
                }
                else{
                    b5 = true;
                }

                if (b1 && b2 && b3 && b4 && b5)
                    finished();
            }
        });

        if(intentTag.equalsIgnoreCase("edit")) {
            loadInfo();
        }

    }

    private void loadInfo() {
        try {
            String title = getIntent().getStringExtra("OLD");
            Cursor c = dbh.getTaskItem(title);
            c.moveToFirst();

            TaskTitle.setText(title);
            TaskDiscription.setText(c.getString(c.getColumnIndex("TaskDescription")));
            priority.setSelection(c.getInt(c.getColumnIndex("TaskPriority")) - 1);
            DatePicked.setText(c.getString(c.getColumnIndex("TaskDeadline")));
            TimePicked.setText(c.getString(c.getColumnIndex("TaskDuration")));
        } catch(SQLException ex) {
            Toast.makeText(getApplicationContext(), "Couldn't load selected item info", Toast.LENGTH_LONG).show();
        }
    }

    public void finished() {
        String taskTitle = TaskTitle.getText().toString().trim();
        String taskDescription = TaskDiscription.getText().toString().trim();
        String creationDate = stf.format(new Date());
        int priorityVal = priorityId + 1;
        String duration = TimePicked.getText().toString();
        String deadline = DatePicked.getText().toString();

        try {
            if (intentTag.equalsIgnoreCase("create")) {
                dbh.insertTaskItem(intentListName, taskTitle, taskDescription, creationDate, priorityVal, duration, deadline);
            } else {
                String oldTitle = getIntent().getStringExtra("OLD");
                dbh.updateCheckListItem(oldTitle, intentListName, taskTitle, taskDescription, creationDate, priorityVal, duration, deadline, 0);
            }

            setResult(RESULT_OK);
        } catch(SQLException ex) {
            Toast.makeText(getApplicationContext(), "Something went wrong, try entering different item name", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }

        finish();
    }

}
