package com.example.c196adamson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.c196adamson.Database.RoomDB;
import com.example.c196adamson.Entity.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TermListActivity extends AppCompatActivity {
    RoomDB DB;
    ListView termList;
    FloatingActionButton addTermButton;
    List<Term> allTerms;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        termList = findViewById(R.id.tdTermList);
        DB = RoomDB.getInstance(getApplicationContext());
        addTermButton = findViewById(R.id.addTermButton);


        termList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);
            intent.putExtra("termID", allTerms.get(position).getTerm_id());
            startActivity(intent);
            Toast.makeText(this, "termID: " + allTerms.get(position).getTerm_id(), Toast.LENGTH_SHORT).show();
            System.out.println(id);
        });

        updateTermList();

        addTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTermActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateTermList() {
        List<Term> allTerms = DB.termDao().getTermList();
        ArrayAdapter<Term> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allTerms);
        termList.setAdapter(adapter);
        this.allTerms = allTerms;

        adapter.notifyDataSetChanged();
    }
}