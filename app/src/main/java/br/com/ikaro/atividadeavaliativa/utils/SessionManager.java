package br.com.ikaro.atividadeavaliativa.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "EnviroCrimeApp";

    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_ADMIN = "isAdmin";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int id, String name, String email, boolean isAdmin) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_ID, 0);
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
