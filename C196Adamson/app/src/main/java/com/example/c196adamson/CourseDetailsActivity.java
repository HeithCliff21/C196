package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Course;
import com.example.c196adamson.Entity.CourseMentor;
import com.example.c196adamson.Entity.Assessment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CourseDetailsActivity extends AppCompatActivity {
    RoomDB DB;
    Intent intent;
    int courseID;
    int termID;
    ListView cdMentorList;
    ListView cdAssessmentList;
    List<CourseMentor> allMentors;
    List<Assessment> allAssessments;
    FloatingActionButton cdAddMentorButton;
    FloatingActionButton cdAddAssessmentButton;
    FloatingActionButton cdEditCourseButton;
    TextView cdName;
    TextView cdStatus;
    TextView cdAlert;
    TextView cdSdate;
    TextView cdEdate;
    TextView cdNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        cdMentorList = findViewById(R.id.cdMentorList);
        cdAssessmentList = findViewById(R.id.cdAssessmentList);
        cdAddMentorButton  = findViewById(R.id.cdAddMentorButton);
        cdAddAssessmentButton = findViewById(R.id.cdAddAssessmentButton);
        cdEditCourseButton = findViewById(R.id.cdEditCourseButton);
        cdName = findViewById(R.id.cdName);
        cdStatus = findViewById(R.id.cdStatus);
        cdAlert = findViewById(R.id.cdAlert);
        cdSdate = findViewById(R.id.cdSdate);
        cdEdate = findViewById(R.id.cdEdate);
        cdNotes = findViewById(R.id.cdNotes);


        setValues();
        updateLists();

        cdEditCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditCourseActivity.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                intent.putExtra("mentorList", allMentors.size());
                intent.putExtra("assessmentList", allAssessments.size());
                startActivity(intent);
            }
        });

        //Mentors
        cdAddMentorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMentorActivity.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
            }
        });

        cdMentorList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), MentorDetailsActivity.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", courseID);
            intent.putExtra("mentorID", allMentors.get(position).getMentor_id());
            startActivity(intent);
            Toast.makeText(this, "mentorID: " + allMentors.get(position).getMentor_id(), Toast.LENGTH_SHORT).show();
            System.out.println(id);
        });

        //Assessments
        cdAddAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAssessmentActivity.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
            }
        });

        cdAssessmentList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", courseID);
            intent.putExtra("assessmentID", allAssessments.get(position).getAssessment_id());
            startActivity(intent);
            Toast.makeText(this, "assessmentID: " + allAssessments.get(position).getAssessment_id(), Toast.LENGTH_SHORT).show();
            System.out.println(id);
        });

    }

    private void setValues(){
        Course course = new Course();
        course = DB.courseDao().getCourse(termID, courseID);
        String name = course.getCourse_name();
        String status = course.getCourse_status();
        boolean alert1 = course.getCourse_alert();
        String sDate = DateFormat.format("MM/dd/yyyy", course.getCourse_start()).toString();
        String eDate = DateFormat.format("MM/dd/yyyy", course.getCourse_end()).toString();
        String notes = course.getCourse_notes();
        String alert = "Off";

        if (alert1){
            alert = "On";
        }


        cdName.setText(name);
        cdStatus.setText(status);
        cdAlert.setText(alert);
        cdSdate.setText(sDate);
        cdEdate.setText(eDate);
        cdNotes.setText(notes);
    }

    private void updateLists(){
        List<CourseMentor> allMentors = DB.courseMentorDao().getMentorList(courseID);
        ArrayAdapter<CourseMentor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allMentors);
        cdMentorList.setAdapter(adapter);
        this.allMentors = allMentors;

        adapter.notifyDataSetChanged();

        List<Assessment> allAssessments = DB.assessmentDao().getAssessmentList(courseID);
        ArrayAdapter<Assessment> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allAssessments);
        cdAssessmentList.setAdapter(adapter2);
        this.allAssessments = allAssessments;

        adapter2.notifyDataSetChanged();
    }
}