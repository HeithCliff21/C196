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
import android.widget.TextView;
import android.widget.Toast;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Term;
import com.example.c196adamson.Utilities.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTermActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    RoomDB DB;
    Intent intent;
    int termID;
    int courseList;
    EditText editTermName;
    Spinner editTermStatus;
    TextView editSDateTerm;
    TextView editEDateTerm;
    Button deleteTermButton;
    Button updateTermButton;
    String statusV;
    SimpleDateFormat formatter;
    private TextView datePickerView;
    boolean termUpdated;
    boolean termDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        intent = getIntent();
        DB = RoomDB.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseList = intent.getIntExtra("courseList", -1);
        editTermName = findViewById(R.id.editTermName);
        editTermStatus = findViewById(R.id.editTermStatus);
        editSDateTerm = findViewById(R.id.editSDateTerm);
        editEDateTerm = findViewById(R.id.editEDateTerm);
        deleteTermButton = findViewById(R.id.deleteTermButton);
        updateTermButton = findViewById(R.id.updateTermButton);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTermStatus.setAdapter(adapter);

        editTermStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = editTermStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setValues();

        deleteTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTerm();
                if(termDeleted){
                    Intent intent = new Intent(getApplicationContext(), TermListActivity.class);
                    startActivity(intent);
                }
            }
        });

        updateTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (termUpdated) {
                    Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);
                    intent.putExtra("termID", termID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setValues() {
        Term term = new Term();
        term = DB.termDao().getTerm(termID);
        String name = term.getTerm_name();
        String status = term.getTerm_status();
        String sDate = DateFormat.format("MM/dd/yyyy", term.getTerm_start()).toString();
        String eDate = DateFormat.format("MM/dd/yyyy", term.getTerm_end()).toString();

        editTermName.setText(name);
        editTermStatus.setSelection(getIndex(editTermStatus, status));
        editSDateTerm.setText(sDate);
        editEDateTerm.setText(eDate);
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private void deleteTerm(){
        if (courseList > 0){
            Toast.makeText(this, "Cannot delete a term with courses associated to it", Toast.LENGTH_SHORT).show();
            return;
        }

        Term term = new Term();
        term = DB.termDao().getTerm(termID);
        DB.termDao().deleteTerm(term);
        Toast.makeText(this, "Term has been deleted", Toast.LENGTH_SHORT).show();
        termDeleted = true;
    }

    private void updateTerm() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        String name = editTermName.getText().toString();
        String sDate = editSDateTerm.getText().toString();
        String eDate = editEDateTerm.getText().toString();
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
        term.setTerm_id(termID);
        term.setTerm_name(name);
        term.setTerm_status(statusV);
        term.setTerm_start(stDate);
        term.setTerm_end(enDate);
        DB.termDao().updateTerm(term);
        Toast.makeText(this, "Term has been added", Toast.LENGTH_SHORT).show();
        termUpdated = true;
    }

    private void setupDatePicker(){

        editSDateTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editSDateTerm);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        editEDateTerm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editEDateTerm);
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