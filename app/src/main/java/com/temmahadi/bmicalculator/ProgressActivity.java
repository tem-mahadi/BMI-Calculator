package com.temmahadi.bmicalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.temmahadi.bmicalculator.BdApps_Backend.MobileNumberActivity;
import com.temmahadi.bmicalculator.BdApps_Backend.SubscriptionManager;
import com.temmahadi.bmicalculator.data.AppDatabase;
import com.temmahadi.bmicalculator.data.BmiEntry;

import java.util.List;
import java.util.Locale;

public class ProgressActivity extends AppCompatActivity {

    private static final int REQUEST_POST_NOTIFICATIONS = 101;
    private static final String PREFS_NAME = "reminder_prefs";
    private static final String KEY_REMINDER_ENABLED = "daily_reminder_enabled";
    private static final String KEY_REMINDER_HOUR = "daily_reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "daily_reminder_minute";

    private TextView tvEntryCount;
    private TextView tvAvgBmi;
    private TextView tvTrend;
    private TextView tvEmpty;
    private TextView tvReminderStatus;
    private TextView tvReminderTime;
    private RecyclerView recyclerHistory;
    private SwitchCompat switchReminder;
    private Button btnChooseReminderTime;
    private Button btnLogout;
    private boolean ignoreSwitchCallback;
    private SharedPreferences reminderPrefs;
    private int reminderHour;
    private int reminderMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        tvEntryCount = findViewById(R.id.tvEntryCount);
        tvAvgBmi = findViewById(R.id.tvAvgBmi);
        tvTrend = findViewById(R.id.tvTrend);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        tvReminderTime = findViewById(R.id.tvReminderTime);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        switchReminder = findViewById(R.id.switchReminder);
        btnChooseReminderTime = findViewById(R.id.btnChooseReminderTime);
        btnLogout = findViewById(R.id.btnLogout);
        reminderPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        setupReminderToggle();
        setupTimePicker();
        setupLogout();

        loadProgress();
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            ReminderScheduler.cancelDaily(this);
            reminderPrefs.edit().clear().apply();
            SubscriptionManager.clearSubscriptionData(this);

            // Clear leftover state from previous login flow implementation.
            SharedPreferences legacyPrefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
            legacyPrefs.edit().clear().apply();

            Intent intent = new Intent(ProgressActivity.this, MobileNumberActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupReminderToggle() {
        boolean enabled = reminderPrefs.getBoolean(KEY_REMINDER_ENABLED, false);
        reminderHour = reminderPrefs.getInt(KEY_REMINDER_HOUR, 9);
        reminderMinute = reminderPrefs.getInt(KEY_REMINDER_MINUTE, 0);

        ignoreSwitchCallback = true;
        switchReminder.setChecked(enabled);
        ignoreSwitchCallback = false;
        updateReminderTimeLabel();
        updateReminderStatus(enabled);

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ignoreSwitchCallback) {
                return;
            }

            if (isChecked) {
                if (needsNotificationPermission() && !hasNotificationPermission()) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_POST_NOTIFICATIONS
                    );
                    return;
                }
                enableReminder();
            } else {
                disableReminder();
            }
        });
    }

    private boolean needsNotificationPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    private boolean hasNotificationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void enableReminder() {
        ReminderScheduler.scheduleDaily(this, reminderHour, reminderMinute);
        reminderPrefs.edit().putBoolean(KEY_REMINDER_ENABLED, true).apply();
        updateReminderStatus(true);
        Toast.makeText(this, "Daily reminder enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableReminder() {
        ReminderScheduler.cancelDaily(this);
        reminderPrefs.edit().putBoolean(KEY_REMINDER_ENABLED, false).apply();
        updateReminderStatus(false);
        Toast.makeText(this, "Daily reminder disabled", Toast.LENGTH_SHORT).show();
    }

    private void updateReminderStatus(boolean enabled) {
        if (enabled) {
            tvReminderStatus.setText(String.format(Locale.getDefault(), "Reminder is on and will notify daily at %s", formatTime(reminderHour, reminderMinute)));
        } else {
            tvReminderStatus.setText("Reminder is off");
        }
    }

    private void setupTimePicker() {
        btnChooseReminderTime.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        reminderHour = hourOfDay;
                        reminderMinute = minute;
                        reminderPrefs.edit()
                                .putInt(KEY_REMINDER_HOUR, reminderHour)
                                .putInt(KEY_REMINDER_MINUTE, reminderMinute)
                                .apply();
                        updateReminderTimeLabel();

                        if (switchReminder.isChecked()) {
                            ReminderScheduler.scheduleDaily(this, reminderHour, reminderMinute);
                            updateReminderStatus(true);
                            Toast.makeText(this, "Reminder time updated", Toast.LENGTH_SHORT).show();
                        }
                    },
                    reminderHour,
                    reminderMinute,
                    false
            );
            dialog.show();
        });
    }

    private void updateReminderTimeLabel() {
        tvReminderTime.setText(String.format(Locale.getDefault(), "Reminder time: %s", formatTime(reminderHour, reminderMinute)));
    }

    private String formatTime(int hourOfDay, int minute) {
        int displayHour = hourOfDay % 12;
        if (displayHour == 0) {
            displayHour = 12;
        }
        String amPm = hourOfDay >= 12 ? "PM" : "AM";
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableReminder();
                ignoreSwitchCallback = true;
                switchReminder.setChecked(true);
                ignoreSwitchCallback = false;
            } else {
                ignoreSwitchCallback = true;
                switchReminder.setChecked(false);
                ignoreSwitchCallback = false;
                updateReminderStatus(false);
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProgress() {
        AppDatabase db = AppDatabase.getInstance(this);
        List<BmiEntry> entries = db.bmiEntryDao().getAllEntries();
        int count = db.bmiEntryDao().getCount();
        Double avgBmi = db.bmiEntryDao().getAverageBmi();
        BmiEntry first = db.bmiEntryDao().getFirstEntry();
        BmiEntry latest = db.bmiEntryDao().getLatestEntry();

        tvEntryCount.setText(String.format(Locale.getDefault(), "Total check-ins: %d", count));
        tvAvgBmi.setText(String.format(Locale.getDefault(), "Average BMI: %s", avgBmi == null ? "--" : String.format(Locale.getDefault(), "%.1f", avgBmi)));

        if (first != null && latest != null && count > 1) {
            double delta = latest.bmi - first.bmi;
            String direction;
            if (delta > 0.15) {
                direction = String.format(Locale.getDefault(), "Trend: Up by %.1f", delta);
            } else if (delta < -0.15) {
                direction = String.format(Locale.getDefault(), "Trend: Down by %.1f", Math.abs(delta));
            } else {
                direction = "Trend: Stable";
            }
            tvTrend.setText(direction);
        } else {
            tvTrend.setText("Trend: Need at least 2 check-ins");
        }

        if (entries.isEmpty()) {
            tvEmpty.setText("No history yet. Calculate BMI to start your progress journey.");
            recyclerHistory.setAdapter(null);
        } else {
            tvEmpty.setText("");
            recyclerHistory.setAdapter(new BmiEntryAdapter(entries));
        }
    }
}
