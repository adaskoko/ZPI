/*package com.example.zpi.data_handling;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHandler {

    public static void saveObject(String key, Object value){
        SharedPreferences  mPrefs = this.getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }

}
*/