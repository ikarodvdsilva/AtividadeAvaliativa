package br.com.ikaro.atividadeavaliativa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import br.com.ikaro.atividadeavaliativa.models.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "EnviroCrimePrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_ADMIN = "is_admin";

    private final SharedPreferences preferences;
    private final Gson gson;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveAuthToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getAuthToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void saveUserDetails(User user) {
        String userJson = gson.toJson(user);
        preferences.edit()
            .putString(KEY_USER, userJson)
            .putInt(KEY_ID, user.getId())
            .putString(KEY_NAME, user.getName())
            .putString(KEY_EMAIL, user.getEmail())
            .putBoolean(KEY_IS_ADMIN, user.isAdmin())
            .apply();
    }

    public User getUserDetails() {
        String userJson = preferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void createLoginSession(int id, String name, String email, boolean isAdmin) {
        preferences.edit()
            .putInt(KEY_ID, id)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply();
    }

    public int getUserId() {
        return preferences.getInt(KEY_ID, 0);
    }

    public String getUserName() {
        return preferences.getString(KEY_NAME, "");
    }

    public String getUserEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public boolean isAdmin() {
        return preferences.getBoolean(KEY_IS_ADMIN, false);
    }

    public void logoutUser() {
        clearSession();
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
