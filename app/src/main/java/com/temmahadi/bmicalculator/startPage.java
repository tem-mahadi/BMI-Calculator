package com.temmahadi.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class startPage extends AppCompatActivity {
    ListView l;
    ArrayList<String> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Intent next = new Intent(this, MainActivity.class);
        l = findViewById(R.id.listView);
        arr.add("I Am Fine");
        arr.add("I Am Sad");
        //adaptar takes the elements from an array and put it to a view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        l.setAdapter(adapter);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position==0){
                    startActivity(next);
                }
                if(position==1){
                    Toast.makeText(startPage.this,"WHY Are You Sad?? Say I'm Happy!", Toast.LENGTH_SHORT).show();
                }
            }

        });
//        startActivity(next);
    }
}