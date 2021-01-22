package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.c196adamson.Entity.Assessment;
import com.example.c196adamson.Entity.Course;
import com.example.c196adamson.Utilities.DatePickerFragment;
import com.example.c196adamson.Utilities.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAssessmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    RoomDB DB;
    int termID;
    int courseID;
    int assessmentID;
    Intent intent;
    EditText addAssessmentName;
    Spinner addAssessmentType;
    Spinner addAssessmentStatus;
    TextView addAssessmentDueDate;
    Switch aAlert;
    Button addAssessmentButton;
    SimpleDateFormat formatter;
    private TextView datePickerView;
    boolean assessmentAdded;
    String status;
    String type;
    Date duDate;
    Date cuDate;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        addAssessmentName = findViewById(R.id.addAssessmentName);
        addAssessmentType = findViewById(R.id.addAssessmentType);
        addAssessmentStatus = findViewById(R.id.addAssessmentStatus);
        addAssessmentDueDate = findViewById(R.id.addAssessmentDueDate);
        aAlert = findViewById(R.id.aAlert);
        addAssessmentButton = findViewById(R.id.addAssessmentButton);

        setupDatePicker();

        setupSpinner();

        addAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addAssessment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (assessmentAdded == true) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addAssessmentType.setAdapter(adapter);

        addAssessmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = addAssessmentType.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.assessment_status_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addAssessmentStatus.setAdapter(adapter2);

        addAssessmentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = addAssessmentStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void addAssessment() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = addAssessmentName.getText().toString();
        String dDate = addAssessmentDueDate.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = aAlert.isChecked();
        duDate = formatter.parse(dDate);
        cuDate = formatter.parse(cDate);


        if (name.trim().isEmpty()){
            Toast.makeText(this, "A assessment name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dDate.trim().isEmpty()){
            Toast.makeText(this, "A assessment due date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment assessment = new Assessment();
        assessment.setCourse_id_fk(courseID);
        assessment.setAssessment_name(name);
        assessment.setAssessment_type(type);
        assessment.setAssessment_status(status);
        assessment.setAssessment_due_date(duDate);
        assessment.setAssessment_alert(alert);
        DB.assessmentDao().insertAssessment(assessment);
        Toast.makeText(this, "Assessment has been added", Toast.LENGTH_SHORT).show();
        assessmentAdded = true;
        if (alert){
            AddAssessmentAlert();
        }
    }

    public void AddAssessmentAlert(){
        Assessment assessment = new Assessment();
        assessment = DB.assessmentDao().getCurrentAssessment(courseID);
        assessmentID = assessment.getAssessment_id();
        String sText = "The Assessment Due Date is Today!";
        setAlert(assessmentID, duDate, name, sText);
    }

    private void setAlert(int ID, Date date, String title, String text) {
        long alertTime = Converters.dateToTimeStamp(date);
        if(duDate.compareTo(cuDate) < 0) {
            return;
        }
        Notifications.setAssessmentAlert(getApplicationContext(), ID, alertTime, title, text);
        Toast.makeText(this, "Assessment due date alarm added", Toast.LENGTH_SHORT).show();
    }

    private void setupDatePicker(){

        addAssessmentDueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addAssessmentDueDate);
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

}