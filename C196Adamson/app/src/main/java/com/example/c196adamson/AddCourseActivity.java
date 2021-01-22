package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.c196adamson.Database.Converters;
import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Course;
import com.example.c196adamson.Utilities.DatePickerFragment;
import com.example.c196adamson.Utilities.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddCourseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final int PERMISSIONS_REQUEST_SMS = 0;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    RoomDB DB;
    int termID;
    Intent intent;
    EditText addCourseNameText;
    Spinner addCourseStatus;
    TextView addSCourseTerm;
    TextView addECourseTerm;
    Switch addCourseAlert;
    EditText addCourseNotes;
    Button addCourseButton;
    Button acSendbutton;
    EditText acSendNumber;
    SimpleDateFormat formatter;
    String statusV;
    String phone;
    String message;
    String name;
    Date stDate;
    Date enDate;
    Date cuDate;
    int courseID;
    private TextView datePickerView;
    boolean courseAdded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        addCourseNameText = findViewById(R.id.addCourseNameText);
        addCourseStatus = findViewById(R.id.addCourseStatus);
        addSCourseTerm = findViewById(R.id.addSCourseTerm);
        addECourseTerm = findViewById(R.id.addECourseTerm);
        addCourseAlert = findViewById(R.id.addCourseAlert);
        addCourseNotes = findViewById(R.id.addCourseNotes);
        addCourseButton = findViewById(R.id.addCourseButton);
        acSendbutton = findViewById(R.id.acSendbutton);
        acSendNumber = findViewById(R.id.acSendNumber);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCourseStatus.setAdapter(adapter);

        addCourseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = addCourseStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (courseAdded == true) {
                    Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    startActivity(intent);
                }
            }
        });

        acSendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagePermission();
            }
        });
    }

    private void addCourse() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = addCourseNameText.getText().toString();
        String sDate = addSCourseTerm.getText().toString();
        String eDate = addECourseTerm.getText().toString();
        String notes = addCourseNotes.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = addCourseAlert.isChecked();
        stDate = formatter.parse(sDate);
        enDate = formatter.parse(eDate);
        cuDate = formatter.parse(cDate);


        if (name.trim().isEmpty()){
            Toast.makeText(this, "A course name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sDate.trim().isEmpty()){
            Toast.makeText(this, "A course start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eDate.trim().isEmpty()){
            Toast.makeText(this, "A course end date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (notes.trim().isEmpty()){
            notes = " ";
        }

        if (stDate.after(enDate)){
            Toast.makeText(this, "A course start date cant be after the end date", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course();
        course.setTerm_id_fk(termID);
        course.setCourse_name(name);
        course.setCourse_start(stDate);
        course.setCourse_end(enDate);
        course.setCourse_status(statusV);
        course.setCourse_notes(notes);
        course.setCourse_alert(alert);
        DB.courseDao().insertCourse(course);
        Toast.makeText(this, "Course has been added", Toast.LENGTH_SHORT).show();
        courseAdded = true;
        if (alert){
            AddCourseAlert();
        }
    }

    public void AddCourseAlert(){
        Course course = new Course();
        course = DB.courseDao().getCurrentCourse(termID);
        courseID = course.getCourse_id();
        String sText = "The Course Starts today!";
        String eText = "The Course Ends today!";

        setAlert(courseID, stDate, name, sText);
        setAlert(courseID, enDate, name, eText);
    }

    private void setAlert(int ID, Date date, String title, String text) {
        long alertTime = Converters.dateToTimeStamp(date);
        if(date.compareTo(cuDate) < 0) {
            return;
        }
        Notifications.setCourseAlert(getApplicationContext(), ID, alertTime, title, text);
        Toast.makeText(this, "Course alarm added", Toast.LENGTH_SHORT).show();
    }

    private void setupDatePicker(){

        addSCourseTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addSCourseTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        addECourseTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addECourseTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month = month +1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // String currentDateString = month + "/" + dayOfMonth + "/" + year;
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }

    protected void messagePermission() {
        phone = acSendNumber.getText().toString();
        String notes = addCourseNotes.getText().toString();
        String cName = addCourseNameText.getText().toString();
        message = "Course: " + cName + "  Notes: " + notes;

        if (phone.trim().isEmpty()) {
            Toast.makeText(this, "A Phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cName.trim().isEmpty()) {
            Toast.makeText(this, "A Course name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (notes.trim().isEmpty()) {
            Toast.makeText(this, "Notes are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSMSMessage();
            }
        }
    }

    protected void sendSMSMessage(){

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS message sent", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResult) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SMS: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS message sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS message failed, please try again", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}