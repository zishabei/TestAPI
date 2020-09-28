
package com.example.testapi.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class NetworkLibPreferenceManager {
    private static final String TAG = NetworkLibPreferenceManager.class.getSimpleName();

    private static final boolean SHOULD_ENCRYPT_SENSITIVE_INFO = true;
    private static final String AUTH_URL_SEED_KEY = "0123456789012345";

    private SharedPreferences mSharedPref;

    public NetworkLibPreferenceManager(Context ctx) {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public boolean contains(String key) {
        return mSharedPref.contains(key);
    }

    public void putString(String key, String value){
        try{
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(key, value);
            editor.apply();
        }catch(Exception e){
            Log.e(TAG, "putString " + e.getMessage());
        }
    }
    public String getString(String key, String defValue){
        return mSharedPref.getString(key, defValue);
    }

    public void remove(String key){
        try{
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.remove(key);
            editor.apply();
        }catch(Exception e){
            Log.e(TAG, "remove " + e.getMessage());
        }
    }

    public void putBoolean(String key, boolean value){
        try{
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }catch(Exception e){
            Log.e(TAG, "putBoolean " + e.getMessage());
        }
    }
    public boolean getBoolean(String key, boolean value){
        return mSharedPref.getBoolean(key, value);
    }

    public void putLong(String key, long value){
        try{
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putLong(key, value);
            editor.apply();
        }catch(Exception e){
            Log.e(TAG, "putLong " + e.getMessage());
        }
    }
    public long getLong(String key, long value){
        return mSharedPref.getLong(key, value);
    }

    public void putInt(String key, int value){
        try{
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(key, value);
            editor.apply();
        }catch(Exception e){
            Log.e(TAG, "putInt " + e.getMessage());
        }
    }
    public int getInt(String key, int value){
        return mSharedPref.getInt(key, value);
    }
}
