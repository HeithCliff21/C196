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
import android.text.format.DateFormat;
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

public class EditCourseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final int PERMISSIONS_REQUEST_SMS = 0;
    RoomDB DB;
    Intent intent;
    int termID;
    int courseID;
    int mentorList;
    int assessmentList;
    EditText editCourseNameText;
    Spinner editCourseStatus;
    TextView editSCourseTerm;
    TextView editECourseTerm;
    Switch editCourseAlert;
    Button ecSendbutton;
    EditText ecSendNumber;
    EditText editCourseNotes;
    Button deleteCourseButton;
    Button updateCourseButton;
    String statusV;
    String phone;
    String message;
    SimpleDateFormat formatter;
    private TextView datePickerView;
    boolean courseUpdated;
    boolean courseDeleted;
    Date stDate;
    Date enDate;
    Date cuDate;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        mentorList = intent.getIntExtra("mentorList", -1);
        assessmentList = intent.getIntExtra("assessmentList", -1);
        editCourseNameText = findViewById(R.id.editCourseNameText);
        editCourseStatus = findViewById(R.id.editCourseStatus);
        editSCourseTerm = findViewById(R.id.editSCourseTerm);
        editECourseTerm = findViewById(R.id.editECourseTerm);
        editCourseAlert = findViewById(R.id.editCourseAlert);
        editCourseNotes = findViewById(R.id.editCourseNotes);
        deleteCourseButton = findViewById(R.id.deleteCourseButton);
        updateCourseButton = findViewById(R.id.updateCourseButton);
        ecSendbutton = findViewById(R.id.ecSendbutton);
        ecSendNumber = findViewById(R.id.ecSendNumber);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCourseStatus.setAdapter(adapter);

        editCourseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = editCourseStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setValues();

        updateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (courseUpdated == true) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });

        deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCourse();
                if(courseDeleted){
                    Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    startActivity(intent);
                }
            }
        });

        ecSendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagePermission();
            }
        });
    }

    private void setValues() {
        Course course = new Course();
        course = DB.courseDao().getCourse(termID, courseID);
        String name = course.getCourse_name();
        String status = course.getCourse_status();
        String sDate = DateFormat.format("MM/dd/yyyy", course.getCourse_start()).toString();
        String eDate = DateFormat.format("MM/dd/yyyy", course.getCourse_end()).toString();
        boolean alert1 = course.getCourse_alert();
        String notes = course.getCourse_notes();

        editCourseNameText.setText(name);
        editCourseStatus.setSelection(getIndex(editCourseStatus, status));
        editSCourseTerm.setText(sDate);
        editECourseTerm.setText(eDate);
        editCourseAlert.setChecked(alert1);
        editCourseNotes.setText(notes);
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private void setupDatePicker(){

        editSCourseTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editSCourseTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        editECourseTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editECourseTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    private void deleteCourse(){
        if (mentorList > 0){
            Toast.makeText(this, "Cannot delete a course with mentor associated to it", Toast.LENGTH_SHORT).show();
            return;
        }

        if (assessmentList > 0){
            Toast.makeText(this, "Cannot delete a course with assessment associated to it", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course();
        course = DB.courseDao().getCourse(termID, courseID);
        DB.courseDao().deleteCourse(course);
        Toast.makeText(this, "Course has been deleted", Toast.LENGTH_SHORT).show();
        courseDeleted = true;
    }

    private void updateCourse() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = editCourseNameText.getText().toString();
        String sDate = editSCourseTerm.getText().toString();
        String eDate = editECourseTerm.getText().toString();
        String notes = editCourseNotes.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = editCourseAlert.isChecked();
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
        course.setCourse_id(courseID);
        course.setCourse_name(name);
        course.setCourse_start(stDate);
        course.setCourse_end(enDate);
        course.setCourse_status(statusV);
        course.setCourse_notes(notes);
        course.setCourse_alert(alert);
        DB.courseDao().updateCourse(course);
        Toast.makeText(this, "Course has been added", Toast.LENGTH_SHORT).show();
        courseUpdated = true;
        if(alert){
            AddCourseAlert();
        }
    }

    public void AddCourseAlert(){
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month = month +1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }

    protected void messagePermission() {
        phone = ecSendNumber.getText().toString();
        String notes = editCourseNotes.getText().toString();
        String cName = editCourseNameText.getText().toString();
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSMSMessage();
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