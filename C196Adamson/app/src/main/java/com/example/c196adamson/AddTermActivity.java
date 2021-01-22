package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Term;
import com.example.c196adamson.Utilities.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTermActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    RoomDB DB;
    Spinner status;
    Button saveTermButton;
    EditText termName;
    TextView startDate;
    TextView endDate;
    SimpleDateFormat formatter;
    String statusV;
    private TextView datePickerView;
    boolean termAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        DB = RoomDB.getInstance(getApplicationContext());
        saveTermButton = findViewById(R.id.saveTermButton);
        status = findViewById(R.id.addTermStatus);
        termName = findViewById(R.id.addTermName);
        termAdded = false;
        startDate = findViewById(R.id.addSDateTerm);
        endDate = findViewById(R.id.addEDateTerm);

        setupDatePicker();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = status.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        saveTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (termAdded == true) {
                    Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void addTerm() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        String name = termName.getText().toString();
        String sDate = startDate.getText().toString();
        String eDate = endDate.getText().toString();
        Date stDate = formatter.parse(sDate);
        Date enDate = formatter.parse(eDate);

        if (name.trim().isEmpty()){
            Toast.makeText(this, "A term name is required", Toast.LENGTH_SHORT).show();
             return;
        }
        if (sDate.trim().isEmpty()){
            Toast.makeText(this, "A term start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eDate.trim().isEmpty()){
            Toast.makeText(this, "A term end date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stDate.after(enDate)){
            Toast.makeText(this, "A term start date cant be after the end date", Toast.LENGTH_SHORT).show();
            return;
        }

        Term term = new Term();
        term.setTerm_name(name);
        term.setTerm_status(statusV);
        term.setTerm_start(stDate);
        term.setTerm_end(enDate);
        DB.termDao().insertTerm(term);
        Toast.makeText(this, "Term has been added", Toast.LENGTH_SHORT).show();
        termAdded = true;
    }

    private void setupDatePicker(){
//        startDate = findViewById(R.id.addSDateTerm);
//        endDate = findViewById(R.id.addEDateTerm);

        startDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addSDateTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addEDateTerm);
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