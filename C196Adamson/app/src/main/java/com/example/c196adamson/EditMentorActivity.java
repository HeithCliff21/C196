package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.CourseMentor;

public class EditMentorActivity extends AppCompatActivity {
    RoomDB DB;
    int termID;
    int courseID;
    int mentorID;
    Intent intent;
    EditText editMentorName;
    EditText editMentorPhone;
    EditText editMentorEmailAddress;
    Button deleteMentorButton;
    Button updateMentorButton;
    boolean mentorUpdated;
    boolean mentorDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        mentorID = intent.getIntExtra("mentorID", -1);
        editMentorName = findViewById(R.id.editMentorName);
        editMentorPhone = findViewById(R.id.editMentorPhone);
        editMentorEmailAddress = findViewById(R.id.editMentorEmailAddress);
        deleteMentorButton = findViewById(R.id.deleteMentorButton);
        updateMentorButton = findViewById(R.id.updateMentorButton);

        setValues();

        updateMentorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMentor();

                if (mentorUpdated) {
                    Intent intent = new Intent(getApplicationContext(), MentorDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    intent.putExtra("mentorID", mentorID);
                    startActivity(intent);
                }
            }
        });

        deleteMentorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMentor();

                if (mentorDeleted){
                    Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setValues(){
        CourseMentor mentor = new CourseMentor();
        mentor = DB.courseMentorDao().getMentor(courseID, mentorID);
        String name = mentor.getMentor_name();
        String phone = mentor.getMentor_phone();
        String email = mentor.getMentor_email();

        editMentorName.setText(name);
        editMentorPhone.setText(phone);
        editMentorEmailAddress.setText(email);
    }

    private void updateMentor() {
        String name = editMentorName.getText().toString();
        String phone = editMentorPhone.getText().toString();
        String email = editMentorEmailAddress.getText().toString();

        if (name.trim().isEmpty()){
            Toast.makeText(this, "A mentor name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.trim().isEmpty()){
            Toast.makeText(this, "A mentor phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.trim().isEmpty()){
            Toast.makeText(this, "A mentor email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        CourseMentor mentor = new CourseMentor();
        mentor.setCourse_id_fk(courseID);
        mentor.setMentor_id(mentorID);
        mentor.setMentor_name(name);
        mentor.setMentor_phone(phone);
        mentor.setMentor_email(email);
        DB.courseMentorDao().updateMentor(mentor);
        Toast.makeText(this, "Mentor has been updated", Toast.LENGTH_SHORT).show();
        mentorUpdated = true;
    }

    private void deleteMentor() {
        CourseMentor mentor = new CourseMentor();
        mentor = DB.courseMentorDao().getMentor(courseID, mentorID);
        DB.courseMentorDao().deleteMentor(mentor);
        Toast.makeText(this, "Mentor has been deleted", Toast.LENGTH_SHORT).show();
        mentorDeleted = true;
    }
}