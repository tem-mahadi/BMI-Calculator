package com.temmahadi.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class startPage extends AppCompatActivity {
    private Spinner spinner;
    private TextView t;
    private Button btnContinue;
    private final ArrayList<String> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        spinner = findViewById(R.id.spinner);
        t = findViewById(R.id.text2);
        btnContinue = findViewById(R.id.btnContinue);

        arr.add("Select your goal");
        arr.add("Lose weight safely");
        arr.add("Maintain fitness");
        arr.add("Gain healthy weight");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        spinner.setAdapter(adapter);
        btnContinue.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                btnContinue.setEnabled(i != 0);
                if (i == 1) {
                    t.setText("We'll help you track a gentle calorie deficit and progress trend.");
                } else if (i == 2) {
                    t.setText("Great. Let's keep your BMI in a healthy range with consistency.");
                } else if (i == 3) {
                    t.setText("We'll focus on healthy gain using balanced meals and routine checks.");
                } else {
                    t.setText("Choose your goal to personalize your BMI journey.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                btnContinue.setEnabled(false);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(startPage.this, MainActivity.class));
                finish();
            }
        });
    }
}