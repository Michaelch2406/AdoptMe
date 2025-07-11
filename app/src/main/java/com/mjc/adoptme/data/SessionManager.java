package com.mjc.adoptme.data;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mjc.adoptme.MainActivity;

public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    // Nombre del archivo de preferencias
    private static final String PREF_NAME = "AdoptMeSession";
    // Clave para saber si está logueado
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    // Clave para el nombre del usuario
    private static final String KEY_USER_NAME = "userName";
    // Clave para el token (¡listo para el futuro!)
    private static final String KEY_AUTH_TOKEN = "authToken";


    public SessionManager(Context context) {
        this.context = context;
        // MODE_PRIVATE significa que solo esta app puede acceder a estas preferencias
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Crea la sesión de login guardando los datos del usuario.
     */
    public void createLoginSession(String name, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_AUTH_TOKEN, token); // Aunque tu API actual no lo devuelva, es bueno tenerlo listo.

        // apply() guarda los cambios de forma asíncrona
        editor.apply();
    }

    /**
     * Comprueba el estado del login.
     * Redirigirá a la pantalla de login si el usuario no está logueado.
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            // El usuario no está logueado, lo mandamos a MainActivity
            Intent i = new Intent(context, MainActivity.class);
            // Cierra todas las otras Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Añade una nueva flag para empezar una nueva task
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    /**
     * Obtiene los datos del usuario guardados.
     * Podrías devolver un objeto User si tuvieras más datos.
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null); // Devuelve null si no se encuentra
    }

    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Borra los detalles de la sesión y redirige al login.
     */
    public void logoutUser() {
        // Borra todos los datos de SharedPreferences
        editor.clear();
        editor.apply();

        // Después de cerrar sesión, redirige al usuario a la MainActivity
        Intent i = new Intent(context, MainActivity.class);
        // Cierra todas las Activities y empieza una nueva.
        // Esto evita que el usuario pueda volver al PanelActivity con el botón de "atrás".
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(i);
    }

    /**
     * Método rápido para comprobar si está logueado.
     * @return true si está logueado, false si no.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}