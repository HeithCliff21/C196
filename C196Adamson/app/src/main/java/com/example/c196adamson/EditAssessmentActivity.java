package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.c196adamson.Entity.Assessment;
import com.example.c196adamson.Utilities.DatePickerFragment;
import com.example.c196adamson.Utilities.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAssessmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    RoomDB DB;
    int termID;
    int courseID;
    int assessmentID;
    Intent intent;
    EditText editAssessmentNameText;
    Spinner editAssessmentType;
    Spinner editAssessmentStatus;
    TextView editAssessmentDueDate;
    Switch editaAlert;
    Button updateAssessmentButton;
    Button deleteAssessmentButton;
    SimpleDateFormat formatter;
    String status;
    String type;
    private TextView datePickerView;
    boolean assessmentUpdated;
    boolean assessmentDeleted;
    Date duDate;
    Date cuDate;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        assessmentID = intent.getIntExtra("assessmentID", -1);
        editAssessmentNameText = findViewById(R.id.editAssessmentNameText);
        editAssessmentType = findViewById(R.id.editAssessmentType);
        editAssessmentStatus = findViewById(R.id.editAssessmentStatus);
        editAssessmentDueDate = findViewById(R.id.editAssessmentDueDate);
        editaAlert = findViewById(R.id.editaAlert);
        updateAssessmentButton = findViewById(R.id.updateAssessmentButton);
        deleteAssessmentButton = findViewById(R.id.deleteAssessmentButton);

        setupDatePicker();

        setupSpinner();

        updateAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateAssessment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (assessmentUpdated) {
                    Intent intent = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    intent.putExtra("assessmentID", assessmentID);
                    startActivity(intent);
                }
            }
        });

        deleteAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAssessment();

                if (assessmentDeleted){
                    Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });

        setValues();
    }



    private void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentType.setAdapter(adapter);

        editAssessmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = editAssessmentType.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.assessment_status_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentStatus.setAdapter(adapter2);

        editAssessmentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = editAssessmentStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setValues() {
        Assessment assessment = new Assessment();
        assessment = DB.assessmentDao().getAssessment(courseID, assessmentID);
        String name = assessment.getAssessment_name();
        String type = assessment.getAssessment_type();
        String status = assessment.getAssessment_status();
        String dDate = DateFormat.format("MM/dd/yyyy", assessment.getAssessment_due_date()).toString();
        boolean alert1 = assessment.getAssessment_alert();

        editAssessmentNameText.setText(name);
        editAssessmentType.setSelection(getIndex(editAssessmentType, type));
        editAssessmentStatus.setSelection(getIndex(editAssessmentStatus, status));
        editAssessmentDueDate.setText(dDate);
        editaAlert.setChecked(alert1);
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private void updateAssessment() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = editAssessmentNameText.getText().toString();
        String dDate = editAssessmentDueDate.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = editaAlert.isChecked();
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
        assessment.setAssessment_id(assessmentID);
        assessment.setAssessment_name(name);
        assessment.setAssessment_type(type);
        assessment.setAssessment_status(status);
        assessment.setAssessment_due_date(duDate);
        assessment.setAssessment_alert(alert);
        DB.assessmentDao().updateAssessment(assessment);
        Toast.makeText(this, "Assessment has been Updated", Toast.LENGTH_SHORT).show();
        assessmentUpdated = true;
        if (alert){
            AddAssessmentAlert();
        }
    }

    public void AddAssessmentAlert(){
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

    private void deleteAssessment() {
        Assessment assessment = new Assessment();
        assessment = DB.assessmentDao().getAssessment(courseID, assessmentID);
        DB.assessmentDao().deleteAssessment(assessment);
        Toast.makeText(this, "Assessment has been deleted", Toast.LENGTH_SHORT).show();
        assessmentDeleted = true;
    }


    private void setupDatePicker(){

        editAssessmentDueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editAssessmentDueDate);
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
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }
}