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

public class AddMentorActivity extends AppCompatActivity {
    RoomDB DB;
    int termID;
    int courseID;
    Intent intent;
    EditText addMentorName;
    EditText addMentorPhone;
    EditText addMentorEmailAddress;
    Button addMentorButton;
    boolean mentorAdded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        addMentorName = findViewById(R.id.addMentorName);
        addMentorPhone = findViewById(R.id.addMentorPhone);
        addMentorEmailAddress = findViewById(R.id.addMentorEmailAddress);
        addMentorButton = findViewById(R.id.addMentorButton);

        addMentorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor();

                if (mentorAdded == true) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });

    }

    private void addMentor() {
        String name = addMentorName.getText().toString();
        String phone = addMentorPhone.getText().toString();
        String email = addMentorEmailAddress.getText().toString();

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
        mentor.setMentor_name(name);
        mentor.setMentor_phone(phone);
        mentor.setMentor_email(email);
        DB.courseMentorDao().insertMentor(mentor);
        Toast.makeText(this, "Course has been added", Toast.LENGTH_SHORT).show();
        mentorAdded = true;
    }
}