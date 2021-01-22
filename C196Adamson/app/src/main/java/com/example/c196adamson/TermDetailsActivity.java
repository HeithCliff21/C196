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
import com.example.c196adamson.Entity.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TermDetailsActivity extends AppCompatActivity {
    RoomDB DB;
    ListView tdClassList;
    Intent intent;
    int termID;
    FloatingActionButton tdAddClassButton;
    FloatingActionButton tdEditTermButton;
    List<Course> allCourses;
    TextView tdName;
    TextView tdStatus;
    TextView tdSdate;
    TextView tdEdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);
        intent = getIntent();
        tdClassList = findViewById(R.id.tdClassList);
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        tdName = findViewById(R.id.tdName);
        tdStatus = findViewById(R.id.tdStatus);
        tdName = findViewById(R.id.tdName);
        tdSdate = findViewById(R.id.tdSdate);
        tdEdate = findViewById((R.id.tdEdate));
        tdEditTermButton = findViewById(R.id.tdEditTermButton);
        tdAddClassButton = findViewById(R.id.tdAddClassButton);
        setValues();
        updateClassList();

        tdEditTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditTermActivity.class);
                intent.putExtra("termID",termID);
                intent.putExtra("courseList", allCourses.size());
                startActivity(intent);
            }
        });

        tdAddClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCourseActivity.class);
                intent.putExtra("termID", termID);
                startActivity(intent);
            }
        });

        tdClassList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", allCourses.get(position).getCourse_id());
            startActivity(intent);
            Toast.makeText(this, "courseID: " + allCourses.get(position).getCourse_id(), Toast.LENGTH_SHORT).show();
            System.out.println(id);
        });

    }

    private void setValues() {
        Term term = new Term();
        term = DB.termDao().getTerm(termID);
        String name = term.getTerm_name();
        String status = term.getTerm_status();
        String sDate = DateFormat.format("MM/dd/yyyy", term.getTerm_start()).toString();
        String eDate = DateFormat.format("MM/dd/yyyy", term.getTerm_end()).toString();

        tdName.setText(name);
        tdStatus.setText(status);
        tdSdate.setText(sDate);
        tdEdate.setText(eDate);
    }

    private void updateClassList(){
        List<Course> allCourses = DB.courseDao().getCourseList(termID);
        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allCourses);
        tdClassList.setAdapter(adapter);
        this.allCourses = allCourses;

        adapter.notifyDataSetChanged();
    }

}

