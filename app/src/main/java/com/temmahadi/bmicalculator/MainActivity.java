package com.temmahadi.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.temmahadi.bmicalculator.data.AppDatabase;
import com.temmahadi.bmicalculator.data.BmiEntry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final double MIN_BMI = 18.5;
    private static final double MAX_BMI = 24.9;

    private TextView txtResult;
    private TextView txtBmiValue;
    private TextView txtCategory;
    private TextView txtAdvice;
    private TextView txtHealthyRange;
    private EditText editWeight;
    private EditText editHeight;
    private EditText editHeightInch;
    private Button btnCalc;
    private Button btnViewProgress;
    private ImageView imageView;
    private RadioGroup unitGroup;
    private RadioButton radioMetric;
    private MaterialCardView resultCard;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout lnMain = findViewById(R.id.lnMain);

        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        editHeightInch = findViewById(R.id.editHeightInch);
        btnCalc = findViewById(R.id.btnCalc);
        btnViewProgress = findViewById(R.id.btnViewProgress);
        txtResult = findViewById(R.id.txtResult);
        txtBmiValue = findViewById(R.id.txtBmiValue);
        txtCategory = findViewById(R.id.txtCategory);
        txtAdvice = findViewById(R.id.txtAdvice);
        txtHealthyRange = findViewById(R.id.txtHealthyRange);
        imageView = findViewById(R.id.imageView);
        unitGroup = findViewById(R.id.unitGroup);
        radioMetric = findViewById(R.id.radioMetric);
        resultCard = findViewById(R.id.resultCard);
        appDatabase = AppDatabase.getInstance(this);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotatescale);
        updateInputMode(true);

        unitGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isMetric = checkedId == R.id.radioMetric;
            updateInputMode(isMetric);
        });

        btnViewProgress.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProgressActivity.class)));

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(anim);
                if (!validateInputs()) {
                    return;
                }

                boolean isMetric = radioMetric.isChecked();
                double weight = parseDouble(editWeight);
                double mainHeight = parseDouble(editHeight);
                double inchPart = isMetric ? 0 : parseDouble(editHeightInch);
                double bmi = calculateBmi(weight, mainHeight, isMetric);

                if (bmi <= 0) {
                    Toast.makeText(MainActivity.this, "Please provide valid values.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (bmi < MIN_BMI) {
                    saveBmiEntry(weight, mainHeight, inchPart, bmi, isMetric, "Underweight");
                    applyResult(
                            bmi,
                            "Underweight",
                            "Small calorie surplus and strength training can help healthy weight gain.",
                            R.color.uw,
                            lnMain
                    );
                } else if (bmi < 25) {
                    saveBmiEntry(weight, mainHeight, inchPart, bmi, isMetric, "Healthy");
                    applyResult(
                            bmi,
                            "Healthy",
                            "Great zone. Focus on consistency: sleep, hydration, and balanced meals.",
                            R.color.h,
                            lnMain
                    );
                } else if (bmi < 30) {
                    saveBmiEntry(weight, mainHeight, inchPart, bmi, isMetric, "Overweight");
                    applyResult(
                            bmi,
                            "Overweight",
                            "A daily walk and a modest calorie deficit can improve your trend safely.",
                            R.color.ow,
                            lnMain
                    );
                } else {
                    saveBmiEntry(weight, mainHeight, inchPart, bmi, isMetric, "Obese");
                    applyResult(
                            bmi,
                            "Obese",
                            "Start with low-impact activity and portion control; seek professional guidance if possible.",
                            R.color.o,
                            lnMain
                    );
                }

                txtHealthyRange.setText("Healthy BMI range: 18.5 - 24.9");
            }
        });
    }

    private void updateInputMode(boolean isMetric) {
        editWeight.setText("");
        editHeight.setText("");
        editHeightInch.setText("");

        if (isMetric) {
            editWeight.setHint(getString(R.string.hint_weight_kg));
            editHeight.setHint(getString(R.string.hint_height_cm));
            editHeightInch.setVisibility(View.GONE);
        } else {
            editWeight.setHint(getString(R.string.hint_weight_lb));
            editHeight.setHint(getString(R.string.hint_height_ft));
            editHeightInch.setHint(getString(R.string.hint_height_in));
            editHeightInch.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(editWeight.getText().toString().trim())) {
            editWeight.setError("Weight is required");
            editWeight.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(editHeight.getText().toString().trim())) {
            editHeight.setError("Height is required");
            editHeight.requestFocus();
            return false;
        }

        if (!radioMetric.isChecked() && TextUtils.isEmpty(editHeightInch.getText().toString().trim())) {
            editHeightInch.setError("Inches are required");
            editHeightInch.requestFocus();
            return false;
        }
        return true;
    }

    private double parseDouble(EditText editText) {
        try {
            return Double.parseDouble(editText.getText().toString().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private double calculateBmi(double weight, double mainHeight, boolean isMetric) {
        if (weight <= 0 || mainHeight <= 0) {
            return -1;
        }

        if (isMetric) {
            double heightMeter = mainHeight / 100.0;
            if (heightMeter <= 0) {
                return -1;
            }

            return weight / (heightMeter * heightMeter);
        }

        double inchPart = parseDouble(editHeightInch);
        if (inchPart < 0 || inchPart >= 12) {
            editHeightInch.setError("Inches should be between 0 and 11.99");
            return -1;
        }

        double totalInches = (mainHeight * 12.0) + inchPart;
        if (totalInches <= 0) {
            return -1;
        }
        return 703.0 * weight / (totalInches * totalInches);
    }

    private void saveBmiEntry(double weight, double mainHeight, double heightInch, double bmi, boolean isMetric, String category) {
        BmiEntry entry = new BmiEntry(
                System.currentTimeMillis(),
                weight,
                mainHeight,
                heightInch,
                bmi,
                isMetric ? "Metric" : "Imperial",
                category
        );
        appDatabase.bmiEntryDao().insert(entry);
    }

    private void applyResult(double bmi, String category, String advice, int colorRes, LinearLayout lnMain) {
        txtResult.setText("Your BMI Analysis");
        txtBmiValue.setText(String.format(Locale.getDefault(), "BMI: %.1f", bmi));
        txtCategory.setText(String.format(Locale.getDefault(), "Category: %s", category));
        txtAdvice.setText(advice);

        int color = ContextCompat.getColor(getBaseContext(), colorRes);
        resultCard.setCardBackgroundColor(color);
        lnMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.surface_bg));
        resultCard.animate().alpha(1f).setDuration(250).start();
    }
}