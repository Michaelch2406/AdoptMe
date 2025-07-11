package com.mjc.adoptme;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mjc.adoptme.data.SessionManager;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

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

        // Cierra esta LauncherActivity para que no aparezca en el historial de navegación
        finish();
    }
}