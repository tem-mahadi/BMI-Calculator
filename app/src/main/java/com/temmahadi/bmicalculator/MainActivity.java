package com.temmahadi.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtResult;
        EditText editWeight, editHeight, editHeightInch;
        Button btnCalc;
        LinearLayout lnMain = findViewById(R.id.lnMain);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        editHeightInch = findViewById(R.id.editHeightInch);
        btnCalc = findViewById(R.id.btnCalc);
        txtResult = findViewById(R.id.txtResult);

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                int wt = Integer.parseInt(editWeight.getText().toString());
                int ht = Integer.parseInt(editHeight.getText().toString());
                int htInch = Integer.parseInt(editHeightInch.getText().toString());

                double height = 0.0254 * (ht*12 + htInch);
                double BMI = wt/(height*height);

                if (BMI < 18.5) {
                    txtResult.setText("You're Underweighted");
                    lnMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.uw));
                }
                else if (BMI < 25) {
                    txtResult.setText("You're Healthy");
                    lnMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.h));
                }
                else if (BMI < 30) {
                    txtResult.setText("You're Overweighted");
                    lnMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.ow));
                }
                else {
                    txtResult.setText("You're Obesed");
                    lnMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.o));
                }
            }
        });


    }
}