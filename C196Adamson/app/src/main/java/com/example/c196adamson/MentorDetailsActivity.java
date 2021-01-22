package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.CourseMentor;

public class MentorDetailsActivity extends AppCompatActivity {
    RoomDB DB;
    int termID;
    int courseID;
    int mentorID;
    Intent intent;
    TextView mdName;
    TextView mdPhone;
    TextView mdEmail;
    Button mdEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        mentorID = intent.getIntExtra("mentorID", -1);
        mdName = findViewById(R.id.mdName);
        mdPhone = findViewById(R.id.mdPhone);
        mdEmail = findViewById(R.id.mdEmail);
        mdEditButton = findViewById(R.id.mdEditButton);

        setValues();

        mdEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditMentorActivity.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                intent.putExtra("mentorID", mentorID);
                startActivity(intent);
            }
        });

    }

    private void setValues(){
        CourseMentor mentor = new CourseMentor();
        mentor = DB.courseMentorDao().getMentor(courseID, mentorID);
        String name = mentor.getMentor_name();
        String phone = mentor.getMentor_phone();
        String email = mentor.getMentor_email();

        mdName.setText(name);
        mdPhone.setText(phone);
        mdEmail.setText(email);
    }
}