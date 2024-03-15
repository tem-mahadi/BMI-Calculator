package com.temmahadi.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class startPage extends AppCompatActivity {
    Spinner spinner;
    AutoCompleteTextView autocm;
    TextView t;
    ArrayList<String> arr = new ArrayList<>();
    ArrayList<String> autoArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Intent next = new Intent(this, MainActivity.class);
        spinner= findViewById(R.id.spinner);
        autocm=findViewById(R.id.autocm);
        t=findViewById(R.id.text2);

        arr.add("?_?");
        arr.add("I Am Fine");
        arr.add("I Am Sad");
        autoArr.add("fat. I think i got so much weight!");
        autoArr.add("skinny. I think i need to gain weight!");
        autoArr.add("normal. I think i'm cool!");

        //adapter takes the elements from an array and put it to a view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoArr);
        spinner.setAdapter(adapter);
        autocm.setAdapter(adapter2); autocm.setThreshold(3);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    t.setText("Feeling fat or skinny, dear?");
                    Toast.makeText(startPage.this, "WHY Are You Sad?? Say I'm Happy!", Toast.LENGTH_LONG).show();
                } else if (i == 1) {
                    t.setText("Guess what you're(fat/skinny/normal)");
                    Toast.makeText(startPage.this, "It's Good To see You Happy!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // This method is required but can be left empty for your case
            }
        });


        autocm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(startPage.this,"Let's Calculate BMI", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() { //delay 3s before running run() method
                    @Override
                    public void run() {
                        startActivity(next);
                        finish();
                    }
                },1300);

            }
        });
    }
}