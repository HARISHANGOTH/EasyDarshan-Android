package com.easydarshan.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.easydarshan.data.model.User;

public class SessionManager {

    private static final String PREF_NAME = "EasyDarshanPref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    
    private static SessionManager instance;
    private User currentUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // For backward compatibility
    public static synchronized SessionManager getInstance() {
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }
    
    public String getToken() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_TOKEN, null) : null;
    }
    
    public void setRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }
    
    public String getRefreshToken() {
        return sharedPreferences != null ? sharedPreferences.getString(KEY_REFRESH_TOKEN, null) : null;
    }

    public void clearSession() {
        currentUser = null;
        if (editor != null) {
            editor.remove(KEY_TOKEN);
            editor.remove(KEY_REFRESH_TOKEN);
            editor.apply();
        }
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
