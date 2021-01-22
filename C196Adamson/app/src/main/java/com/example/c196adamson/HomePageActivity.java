package com.example.c196adamson;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Assessment;
import com.example.c196adamson.Entity.Course;
import com.example.c196adamson.Entity.Term;

import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    RoomDB DB;
    TextView termData;
    TextView termCompleteData;
    TextView termProgressData;
    TextView termBarData;
    TextView courseData;
    TextView courseCompletedData;
    TextView courseProgressData;
    TextView courseBarData;
    TextView assessmentData;
    TextView assessmentPassedData;
    TextView assessmentProgressData;
    TextView assessmentBarData;
    ProgressBar termProgressBar;
    ProgressBar courseProgressBar;
    ProgressBar assessmentProgressBar;
    Button hTermListbutton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = RoomDB.getInstance(getApplicationContext());
        // TextView Data
        termData = findViewById(R.id.termData);
        termCompleteData = findViewById(R.id.termCompleteData);
        termProgressData = findViewById(R.id.termProgressData);
        termBarData = findViewById(R.id.termBarData);
        courseData = findViewById(R.id.courseData);
        courseCompletedData = findViewById(R.id.courseCompletedData);
        courseProgressData = findViewById(R.id.courseProgressData);
        courseBarData = findViewById(R.id.courseBarData);
        assessmentData = findViewById(R.id.assessmentData);
        assessmentPassedData = findViewById(R.id.assessmentPassedData);
        assessmentProgressData = findViewById(R.id.assessmentProgressData);
        assessmentBarData = findViewById(R.id.assessmentBarData);
        // Progress Bar
        termProgressBar = findViewById(R.id.termProgressBar);
        courseProgressBar = findViewById(R.id.courseProgressBar);
        assessmentProgressBar = findViewById(R.id.assessmentProgressBar);
        hTermListbutton = findViewById(R.id.hTermListbutton);
        updateViews();
        hTermListbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                startActivity(intent);
            }
        });
    }


    private void updateViews(){
        int term = 0;
        int termComplete = 0;
        int termProgress = 0;
        int termBar = 0;
        int course = 0;
        int courseCompleted = 0;
        int courseProgress = 0;
        int courseBar = 0;
        int assessment = 0;
        int assessmentPassed = 0;
        int assessmentProgress = 0;
        int assessmentBar = 0;

        try {
            List<Term> termList = DB.termDao().getAllTerms();
            List<Course> courseList = DB.courseDao().getAllCourses();
            List<Assessment> assessmentList = DB.assessmentDao().getAllAssessments();

            try{
                for(int i = 0; i < termList.size(); i++){
                    term = termList.size();
                    if(termList.get(i).getTerm_status().contains("Completed")) termComplete++;
                    if(termList.get(i).getTerm_status().contains("In Progress")) termProgress++;
                }
                termBar = (int) ((double)termComplete / term * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try{
                for(int i = 0; i < courseList.size(); i++){
                    course = courseList.size();
                    if(courseList.get(i).getCourse_status().contains("Completed")) courseCompleted++;
                    if(courseList.get(i).getCourse_status().contains("In Progress")) courseProgress++;
                    if(courseList.get(i).getCourse_status().contains("Dropped")) courseProgress++;
                    if(courseList.get(i).getCourse_status().contains("Plan to Take")) courseProgress++;
                }
                courseBar = (int) ((double)courseCompleted / course * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try{
                for(int i = 0; i < assessmentList.size(); i++){
                    assessment = assessmentList.size();
                    if(assessmentList.get(i).getAssessment_status().contains("Passed")) assessmentPassed++;
                    if(assessmentList.get(i).getAssessment_status().contains("In Progress")) assessmentProgress++;
                    if(assessmentList.get(i).getAssessment_status().contains("Failed")) assessmentProgress++;
                }
                assessmentBar = (int) ((double)assessmentPassed / assessment * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        termData.setText(String.valueOf(term));
        termCompleteData.setText(String.valueOf(termComplete));
        termProgressData.setText(String.valueOf(termProgress));
        termBarData.setText(String.valueOf(termBar) + "%");
        termProgressBar.setProgress(termBar);
        courseData.setText(String.valueOf(course));
        courseCompletedData.setText(String.valueOf(courseCompleted));
        courseProgressData.setText(String.valueOf(courseProgress));
        courseBarData.setText(String.valueOf(courseBar) + "%");
        courseProgressBar.setProgress(courseBar);
        assessmentData.setText(String.valueOf(assessment));
        assessmentPassedData.setText(String.valueOf(assessmentPassed));
        assessmentProgressData.setText(String.valueOf(assessmentProgress));
        assessmentBarData.setText(String.valueOf(assessmentBar) + "%");
        assessmentProgressBar.setProgress(assessmentBar);
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateViews();
    }
}