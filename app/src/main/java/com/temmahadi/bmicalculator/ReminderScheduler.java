package com.temmahadi.bmicalculator;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    public static final String REMINDER_WORK_NAME = "daily_bmi_reminder";

    private ReminderScheduler() {
    }

    public static void scheduleDaily(Context context, int hourOfDay, int minute) {
        long initialDelayMinutes = calculateDelayUntilNext(hourOfDay, minute);

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(DailyReminderWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(REMINDER_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request);
    }

    public static void cancelDaily(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME);
    }

    private static long calculateDelayUntilNext(int hourOfDay, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hourOfDay);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);

        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }

        long millisDiff = next.getTimeInMillis() - now.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toMinutes(millisDiff);
    }
}
