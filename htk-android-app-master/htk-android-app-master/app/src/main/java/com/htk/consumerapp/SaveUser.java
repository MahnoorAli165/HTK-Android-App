package com.htk.consumerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveUser{
    static final String PREF_LOGGED_IN_WITH = "none";
    static final String PREF_USER_ID= "userid";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userID);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    public static void setLoginType(Context ctx, String type) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGED_IN_WITH, type);
        editor.commit();
    }

    public static String getLoginType(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGGED_IN_WITH, "");
    }

}