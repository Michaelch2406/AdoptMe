package com.mjc.adoptme.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;

import com.mjc.adoptme.MainActivity;

public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private static final String PREF_NAME = "AdoptMeSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_CEDULA = "userCedula"; // <-- NUEVA CLAVE
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String TAG = "SessionManager";


    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Crea la sesión de login guardando los datos del usuario, incluyendo la cédula.
     */
    public void createLoginSession(String name, String cedula, String token) {
        try {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USER_NAME, name);
            editor.putString(KEY_USER_CEDULA, cedula); // <-- GUARDAMOS LA CÉDULA
            editor.putString(KEY_AUTH_TOKEN, token);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error creating login session", e);
        }
    }

    public String getUserName() {
        try {
            return prefs.getString(KEY_USER_NAME, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user name", e);
            return null;
        }
    }

    /**
     * Obtiene la cédula del usuario guardada en la sesión.
     * @return La cédula del usuario o null si no se encuentra.
     */
    public String getCedula() {
        try {
            return prefs.getString(KEY_USER_CEDULA, null); // <-- NUEVO MÉTODO
        } catch (Exception e) {
            Log.e(TAG, "Error getting cedula", e);
            return null;
        }
    }

    public String getAuthToken() {
        try {
            return prefs.getString(KEY_AUTH_TOKEN, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting auth token", e);
            return null;
        }
    }

    public void logoutUser() {
        try {
            editor.clear();
            editor.apply();

            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
        }
    }

    public boolean isLoggedIn() {
        try {
            return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status", e);
            return false;
        }
    }
}