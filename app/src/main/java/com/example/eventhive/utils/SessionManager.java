package com.example.eventhive.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "EventHivePref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_UID = "userUid"; // Firebase UID
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates login session with Firebase UID
     * 
     * @param uid   Firebase user ID (String)
     * @param name  User's first name
     * @param email User's email
     * @param role  User's role
     * @param phone User's phone number
     */
    public void createLoginSession(String uid, String name, String email, String role, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_UID, uid);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");
        editor.apply();
    }

    /**
     * Legacy method for backward compatibility with SQLite-based features
     * 
     * @deprecated Use createLoginSession(String uid, ...) for Firebase
     */
    @Deprecated
    public void createLoginSession(int id, String name, String email, String role, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Gets Firebase user UID
     * 
     * @return Firebase UID or empty string if not set
     */
    public String getUserUid() {
        return pref.getString(KEY_USER_UID, "");
    }

    /**
     * Legacy method for backward compatibility
     * 
     * @deprecated Use getUserUid() for Firebase
     */
    @Deprecated
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "User");
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public void updateSession(String name, String email, String phone) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
