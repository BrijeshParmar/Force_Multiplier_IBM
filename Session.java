package com.example.gb.forcemultiplier;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setuserToken(String userToken) {
        prefs.edit().putString("accessToken", userToken).apply();
    }

    public String getuserToken() {
        String userToken = prefs.getString("accessToken","");
        return userToken;
    }
}
