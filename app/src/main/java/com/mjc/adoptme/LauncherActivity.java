package com.mjc.adoptme;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mjc.adoptme.data.SessionManager;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = null;
        try {
            sessionManager = new SessionManager(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SessionManager", e);
            Toast.makeText(this, "Error crítico al iniciar la aplicación.", Toast.LENGTH_LONG).show();
            // Fallback to MainActivity if SessionManager fails
            try {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            } catch (Exception ex) {
                Log.e(TAG, "Error starting MainActivity after SessionManager failure", ex);
            }
            finish();
            return;
        }

        try {
            // Decide a qué Activity ir
            if (sessionManager.isLoggedIn()) {
                // Si ya hay una sesión, ve directo al Panel
                Intent intent = new Intent(LauncherActivity.this, PanelActivity.class);
                startActivity(intent);
            } else {
                // Si no hay sesión, ve a la pantalla de Login
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting activity based on login status", e);
            Toast.makeText(this, "Error al cargar la pantalla principal.", Toast.LENGTH_LONG).show();
            // Fallback to MainActivity if any other error occurs during redirection
            try {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            } catch (Exception ex) {
                Log.e(TAG, "Error starting MainActivity after redirection failure", ex);
            }
        }

        // Cierra esta LauncherActivity para que no aparezca en el historial de navegación
        finish();
    }
}