package com.example.zpi.data_handling;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zpi.models.User;
import com.google.gson.Gson;

public class SharedPreferencesHandler {

    public static void saveLoggedInUser(Context context, User user){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        Log.i("put", json);
        prefsEditor.putString("user", json);
        prefsEditor.apply();
    }

    public static User getLoggedInUser(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        Log.i("get", json);
        return gson.fromJson(json, User.class);
    }

}