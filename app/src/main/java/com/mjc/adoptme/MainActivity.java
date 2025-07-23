package com.mjc.adoptme;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.LoginRequest;
import com.mjc.adoptme.models.LoginResponse;
import com.mjc.adoptme.models.UserData;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;
import SecureX.Library;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Vistas (del primer archivo para mantener el diseño)
    private ImageView ivLogo;
    private TextView tvAppName, tvSubtitle, tvRegistrar, tvForgotPassword;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmailLogin, etPasswordLogin;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización y configuración (del primer archivo)
        initViews();
        setupAnimations();
        setupListeners();
        setupTextWatchers();
    }

    private void initViews() {
        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvRegistrar = findViewById(R.id.tvRegistrar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    // --- MÉTODOS DE ANIMACIÓN (Sin cambios, copiados del primer archivo) ---

    private void setupAnimations() {
        animateLogo();
        animateTitle();
        animateForm();
        animateRegisterText();
    }

    private void animateLogo() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivLogo, "rotation", -180f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f);

        AnimatorSet logoAnimator = new AnimatorSet();
        logoAnimator.playTogether(scaleX, scaleY, rotation, alpha);
        logoAnimator.setDuration(1000);
        logoAnimator.setInterpolator(new DecelerateInterpolator());
        logoAnimator.setStartDelay(200);
        logoAnimator.start();
        startPulseAnimation();
    }

    private void startPulseAnimation() {
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 1f, 1.05f, 1f);
        // Aplicar repetición al animador individual
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator pulseY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 1f, 1.05f, 1f);
        // Aplicar repetición al animador individual
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setRepeatMode(ValueAnimator.RESTART);

        AnimatorSet pulseAnimator = new AnimatorSet();
        pulseAnimator.playTogether(pulseX, pulseY);
        pulseAnimator.setDuration(2000); // Duración total de una iteración
        pulseAnimator.setStartDelay(2000); // Retraso antes de que el conjunto comience
        pulseAnimator.start();
    }

    private void animateTitle() {
        ObjectAnimator translateY = ObjectAnimator.ofFloat(tvAppName, "translationY", -200f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvAppName, "alpha", 0f, 1f);
        AnimatorSet titleAnimator = new AnimatorSet();
        titleAnimator.playTogether(translateY, alpha);
        titleAnimator.setDuration(800);
        titleAnimator.setStartDelay(400);
        titleAnimator.start();

        ObjectAnimator subtitleAlpha = ObjectAnimator.ofFloat(tvSubtitle, "alpha", 0f, 1f);
        ObjectAnimator subtitleTranslate = ObjectAnimator.ofFloat(tvSubtitle, "translationY", 50f, 0f);
        AnimatorSet subtitleAnimator = new AnimatorSet();
        subtitleAnimator.playTogether(subtitleAlpha, subtitleTranslate);
        subtitleAnimator.setDuration(600);
        subtitleAnimator.setStartDelay(800);
        subtitleAnimator.start();
    }

    private void animateForm() {
        View formContainer = findViewById(R.id.formContainer);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(formContainer, "translationY", 300f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(formContainer, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(formContainer, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(formContainer, "scaleY", 0.9f, 1f);
        AnimatorSet formAnimator = new AnimatorSet();
        formAnimator.playTogether(translateY, alpha, scaleX, scaleY);
        formAnimator.setDuration(800);
        formAnimator.setStartDelay(600);
        formAnimator.setInterpolator(new DecelerateInterpolator());
        formAnimator.start();
    }

    private void animateRegisterText() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvRegistrar, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(tvRegistrar, "translationY", 50f, 0f);
        AnimatorSet registerAnimator = new AnimatorSet();
        registerAnimator.playTogether(alpha, translateY);
        registerAnimator.setDuration(600);
        registerAnimator.setStartDelay(1000);
        registerAnimator.start();
    }

    // --- LÓGICA ADAPTADA ---

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (!isAnimating) {
                animateButtonPress(v);
                performLogin();
            }
        });

        tvRegistrar.setOnClickListener(v -> {
            animateClickEffect(v);
            // Lógica del segundo archivo: navegar a RegistroActivity
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> {
            animateClickEffect(v);
            Toast.makeText(this, "Recuperar contraseña...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, RecuperarContraseniaActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    private void performLogin() {
        if (!validateForm()) {
            return;
        }

        startLoadingAnimation();

        try {
            String email = etEmailLogin.getText().toString().trim();
            String password = etPasswordLogin.getText().toString().trim();
            String hashedPassword = Library.Hash.hashBase64(password);

            LoginRequest loginRequest = new LoginRequest(email, hashedPassword);
            ApiService apiService = RetrofitClient.getApiService();

            // La llamada ahora espera un objeto ApiResponse que contiene UserData
            Call<ApiResponse<UserData>> call = apiService.iniciarSesion(loginRequest);
            call.enqueue(new Callback<ApiResponse<UserData>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserData>> call, Response<ApiResponse<UserData>> response) {
                    stopLoadingAnimation();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<UserData> apiResponse = response.body();

                            if (apiResponse.getData() != null) {
                                UserData userData = apiResponse.getData();
                                String userName = userData.getNameUser();
                                String cedula = userData.getCedula(); // <-- Obtenemos la cédula

                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                // Guardamos el nombre Y la cédula en la sesión
                                sessionManager.createLoginSession(userName, cedula, "");

                                showSuccessDialog("¡Bienvenido, " + userName + "!");

                                Intent intent = new Intent(MainActivity.this, PanelActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                showErrorDialog("Respuesta inesperada del servidor.");
                            }
                        } else {
                            showErrorDialog("Email o contraseña incorrectos.");
                            String errorMessage = "Error en el inicio de sesión";
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "Error en login. Código: " + response.code() + " | Cuerpo: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al parsear el cuerpo del error de login.", e);
                            }
                            showErrorDialog(errorMessage);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing login response", e);
                        showErrorDialog("Error al procesar la respuesta del servidor.");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserData>> call, Throwable t) {
                    stopLoadingAnimation();
                    Log.e(TAG, "Fallo en la conexión de login", t);
                    showErrorDialog("Error de conexión: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            stopLoadingAnimation();
            Log.e(TAG, "Error calling login endpoint", e);
            showErrorDialog("Error al iniciar la solicitud. Inténtalo de nuevo.");
        }
    }

    private boolean validateForm() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("El email es requerido");
            animateFieldError(tilEmail);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de email inválido");
            animateFieldError(tilEmail);
            return false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("La contraseña es requerida");
            animateFieldError(tilPassword);
            return false;
        }

        return true;
    }

    // En MainActivity.java

    private void saveAuthToken(String token) {
        // La forma moderna y recomendada de obtener SharedPreferences.
        // Le damos un nombre único a nuestro archivo de preferencias para evitar conflictos.
        SharedPreferences preferences = getSharedPreferences("com.mjc.adoptme.PREFERENCES", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AUTH_TOKEN", token);

        // apply() es asíncrono y preferido sobre commit() para no bloquear el hilo principal.
        editor.apply();

        Log.i(TAG, "Token de autenticación guardado de forma segura.");
    }



    // --- MÉTODOS DE VALIDACIÓN Y ANIMACIÓN DE FEEDBACK (Sin cambios) ---

    private void setupTextWatchers() {
        etEmailLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) tilEmail.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPasswordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) tilPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void animateFieldError(View field) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(field, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        shake.setDuration(600);
        shake.start();
    }

    private void animateButtonPress(View button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f, 1f);
        AnimatorSet pressAnimator = new AnimatorSet();
        pressAnimator.playTogether(scaleX, scaleY);
        pressAnimator.setDuration(150);
        pressAnimator.start();
    }

    private void animateClickEffect(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f);
        AnimatorSet clickAnimator = new AnimatorSet();
        clickAnimator.playTogether(scaleX, scaleY, alpha);
        clickAnimator.setDuration(200);
        clickAnimator.start();
    }

    private void startLoadingAnimation() {
        isAnimating = true;
        btnLogin.setText("Iniciando sesión...");
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator colorAnimator = ObjectAnimator.ofFloat(btnLogin, "alpha", 1f, 0.7f, 1f);
        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.start();
        etEmailLogin.setEnabled(false);
        etPasswordLogin.setEnabled(false);
        btnLogin.setEnabled(false);
    }

    private void stopLoadingAnimation() {
        isAnimating = false;
        btnLogin.setText("Iniciar Sesión");
        progressBar.setVisibility(View.INVISIBLE);
        btnLogin.clearAnimation();
        btnLogin.setAlpha(1f);
        etEmailLogin.setEnabled(true);
        etPasswordLogin.setEnabled(true);
        btnLogin.setEnabled(true);
    }

    // --- MÉTODOS DE CICLO DE VIDA (Sin cambios) ---

    @Override
    protected void onResume() {
        super.onResume();
        if (ivLogo.getScaleX() == 1f) {
            startPulseAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ivLogo.clearAnimation();
        btnLogin.clearAnimation();
    }

    private void showSuccessDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}