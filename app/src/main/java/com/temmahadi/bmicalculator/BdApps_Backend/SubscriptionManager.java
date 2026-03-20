package com.temmahadi.bmicalculator.BdApps_Backend;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionManager {
    private static final String PREF_NAME = "SubscriptionPrefs";
    private static final String KEY_IS_SUBSCRIBED = "isSubscribed";
    private static final String KEY_USER_MOBILE = "userMobile";
    private static final String KEY_FIRST_TIME_USER = "firstTimeUser";

    /**
     * Check if user is subscribed
     */
    public static boolean isSubscribed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_SUBSCRIBED, false);
    }

    /**
     * Set user subscription status
     */
    public static void setSubscribed(Context context, boolean isSubscribed) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_SUBSCRIBED, isSubscribed);
        
        // If user is being subscribed, mark as not first time anymore
        if (isSubscribed) {
            editor.putBoolean(KEY_FIRST_TIME_USER, false);
        }
        
        editor.apply();
    }

    /**
     * Save user mobile number
     */
    public static void setUserMobile(Context context, String mobileNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_MOBILE, mobileNumber);
        editor.apply();
    }

    /**
     * Get saved user mobile number
     */
    public static String getUserMobile(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_MOBILE, null);
    }

    /**
     * Check if this is a first time user
     */
    public static boolean isFirstTimeUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_TIME_USER, true); // Default to true for new users
    }

    /**
     * Clear all subscription data (for logout)
     */
    public static void clearSubscriptionData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Complete user login process - save mobile and set subscribed
     */
    public static void completeUserLogin(Context context, String mobileNumber) {
        setUserMobile(context, mobileNumber);
        setSubscribed(context, true);
    }
}