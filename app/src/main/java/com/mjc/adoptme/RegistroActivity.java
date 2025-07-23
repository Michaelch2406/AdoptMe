package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.RegistroCompleto;
// IMPORTA LA CLASE DE TU LIBRERÍA SECUREX
import SecureX.Library;

// YA NO NECESITAS ESTOS IMPORTS
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";

    // Vistas y Contenedores
    private LinearLayout logoContainer, formContainer, layoutPaws;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvLogin;
    private MaterialButton btnRegistrar;
    private ProgressBar progressBar;

    // TextInputLayouts
    private TextInputLayout tilNombres, tilApellidos, tilEmail, tilPassword, tilConfirmarPassword;

    // TextInputEditTexts
    private TextInputEditText etNombres, etApellidos, etEmail, etPassword, etConfirmarPassword;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        
        // Ensure views are visible when returning
        if (logoContainer != null && formContainer != null) {
            Log.d(TAG, "Restoring view visibility");
            logoContainer.setVisibility(View.VISIBLE);
            logoContainer.setAlpha(1f);
            logoContainer.setTranslationY(0f);
            
            formContainer.setVisibility(View.VISIBLE);
            formContainer.setAlpha(1f);
            formContainer.setTranslationY(0f);
            
            // Restore data from repository when returning from DatosPersonalesActivity
            // Only restore if views are initialized
            if (etNombres != null && etApellidos != null && etEmail != null) {
                Log.d(TAG, "Restoring data from repository");
                restoreDataFromRepository();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_registro);
            Log.d(TAG, "Layout cargado correctamente");

            initViews();
            setupClickListeners();
            setupBackButtonHandler();
            startAnimations();
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            showErrorDialog("Error al iniciar: " + e.getMessage());
        }
    }

    private void initViews() {
        try {
            logoContainer = findViewById(R.id.logoContainer);
            formContainer = findViewById(R.id.formContainer);
            layoutPaws = findViewById(R.id.layoutPaws);
            ivLogo = findViewById(R.id.ivLogo);
            paw1 = findViewById(R.id.paw1);
            paw2 = findViewById(R.id.paw2);
            paw3 = findViewById(R.id.paw3);
            tvAppName = findViewById(R.id.tvAppName);
            tvSubtitle = findViewById(R.id.tvSubtitle);
            tvLogin = findViewById(R.id.tvLogin);
            progressBar = findViewById(R.id.progressBar);
            btnRegistrar = findViewById(R.id.btnRegistrar);
            tilNombres = findViewById(R.id.tilNombres);
            tilApellidos = findViewById(R.id.tilApellidos);
            tilEmail = findViewById(R.id.tilEmail);
            tilPassword = findViewById(R.id.tilPassword);
            tilConfirmarPassword = findViewById(R.id.tilConfirmarPassword);
            etNombres = findViewById(R.id.etNombres);
            etApellidos = findViewById(R.id.etApellidos);
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            etConfirmarPassword = findViewById(R.id.etConfirmarPassword);

            // Configurar estados iniciales
            logoContainer.setAlpha(0f);
            logoContainer.setTranslationY(-100f);
            formContainer.setAlpha(0f);
            formContainer.setTranslationY(100f);

            Log.d(TAG, "Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas", e);
            throw e;
        }
    }

    private void setupClickListeners() {
        btnRegistrar.setOnClickListener(v -> {
            Log.d(TAG, "Botón registrar presionado");
            if (validateForm()) {
                performRegistration();
            }
        });

        tvLogin.setOnClickListener(v -> {
            Log.d(TAG, "TextView login presionado");
            handleExitAnimation();
        });
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    private void startAnimations() {
        logoContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        ivLogo.animate()
                .rotation(360f)
                .setDuration(800)
                .setStartDelay(100)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        formContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(this::animatePaws)
                .start();
    }

    private void animatePaws() {
        animatePaw(paw1, 0, () -> animatePaw(paw2, 150, () -> animatePaw(paw3, 150, this::startPawGlowAnimation)));
    }

    private void animatePaw(final ImageView paw, long delay, final Runnable onComplete) {
        handler.postDelayed(() -> {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(paw, "scaleX", 0f, 1.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(paw, "scaleY", 0f, 1.3f, 1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(paw, "alpha", 0f, 1f);
            set.playTogether(scaleX, scaleY, alpha);
            set.setDuration(400);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            set.start();
        }, delay);
    }

    private void startPawGlowAnimation() {
        ValueAnimator glowAnimator = ValueAnimator.ofFloat(0.3f, 1f, 0.3f);
        glowAnimator.setDuration(2000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            paw1.setAlpha(value);
            paw2.setAlpha(value * 0.8f);
            paw3.setAlpha(value * 0.6f);
        });
        glowAnimator.start();
    }

    private void shakeView(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
                .setDuration(500)
                .start();
    }

    private void performRegistration() {
        Log.d(TAG, "Iniciando proceso de registro");
        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        saveDataToRepository();

        handler.postDelayed(() -> {
            try {
                Log.d(TAG, "Datos guardados, iniciando transición a Datos Personales.");
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                animateSuccessTransition();
            } catch (Exception e) {
                Log.e(TAG, "Error en performRegistration", e);
                progressBar.setVisibility(View.GONE);
                btnRegistrar.setEnabled(true);
                btnRegistrar.setText("Registrar");
                showErrorDialog("Error: " + e.getMessage());
            }
        }, 2000);
    }

    // En RegistroActivity.java
    private void saveDataToRepository() {
        try {
            RegistroRepository repository = RegistroRepository.getInstance();
            // Obtenemos el objeto principal
            RegistroCompleto data = repository.getRegistroData();

            data.setNombres(etNombres.getText().toString().trim());
            data.setApellidos(etApellidos.getText().toString().trim());
            data.setEmail(etEmail.getText().toString().trim());

            String password = etPassword.getText().toString();
            // Tu JSON de ejemplo usa Base64. ¡Esta es la forma correcta!
            String hashedPassword = Library.Hash.hashBase64(password);
            data.setPasswordHash(hashedPassword);

            Log.i(TAG, "Datos iniciales guardados en el repositorio con contraseña hasheada.");
        } catch (Exception e) {
            Log.e(TAG, "Error saving data to repository", e);
            showErrorDialog("Error al guardar los datos. Inténtalo de nuevo.");
        }
    }

    private void restoreDataFromRepository() {
        RegistroRepository repository = RegistroRepository.getInstance();
        RegistroCompleto data = repository.getRegistroData();
        
        Log.d(TAG, "restoreDataFromRepository() - data is null: " + (data == null));
        
        if (data != null) {
            Log.d(TAG, "Datos disponibles - Nombres: " + data.getNombres() + ", Apellidos: " + data.getApellidos() + ", Email: " + data.getEmail());
            
            // Always restore data when returning from DatosPersonalesActivity
            if (data.getNombres() != null) {
                etNombres.setText(data.getNombres());
                Log.d(TAG, "Nombres restaurados: " + data.getNombres());
            }
            if (data.getApellidos() != null) {
                etApellidos.setText(data.getApellidos());
                Log.d(TAG, "Apellidos restaurados: " + data.getApellidos());
            }
            if (data.getEmail() != null) {
                etEmail.setText(data.getEmail());
                Log.d(TAG, "Email restaurado: " + data.getEmail());
            }
            // Don't restore password fields for security
            Log.d(TAG, "Datos restaurados desde el repositorio completamente");
        } else {
            Log.w(TAG, "No hay datos en el repositorio para restaurar");
        }
    }


    private boolean validateForm() {
        boolean isValid = true;

        if (etNombres.getText().toString().trim().isEmpty()) {
            tilNombres.setError("Este campo es requerido");
            shakeView(tilNombres);
            isValid = false;
        } else {
            tilNombres.setError(null);
        }

        if (etApellidos.getText().toString().trim().isEmpty()) {
            tilApellidos.setError("Este campo es requerido");
            shakeView(tilApellidos);
            isValid = false;
        } else {
            tilApellidos.setError(null);
        }

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Este campo es requerido");
            shakeView(tilEmail);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de correo inválido");
            shakeView(tilEmail);
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (password.isEmpty()) {
            tilPassword.setError("Este campo es requerido");
            shakeView(tilPassword);
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            shakeView(tilPassword);
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!password.equals(etConfirmarPassword.getText().toString())) {
            tilConfirmarPassword.setError("Las contraseñas no coinciden");
            shakeView(tilConfirmarPassword);
            isValid = false;
        } else {
            tilConfirmarPassword.setError(null);
        }

        return isValid;
    }

    private void animateSuccessTransition() {
        Log.d(TAG, "Iniciando animación de éxito");

        AnimatorSet successSet = new AnimatorSet();
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formScale = ObjectAnimator.ofFloat(formContainer, "scaleX", 1f, 0.9f);
        ObjectAnimator formScaleY = ObjectAnimator.ofFloat(formContainer, "scaleY", 1f, 0.9f);
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);

        successSet.playTogether(formAlpha, formScale, formScaleY, logoAlpha);
        successSet.setDuration(400);
        successSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    Log.d(TAG, "Animación completada, iniciando DatosPersonalesActivity");

                    Intent intent = new Intent(RegistroActivity.this, DatosPersonalesActivity.class);
                    startActivity(intent);

                    // Usar la animación fade que prefieres
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    // Don't finish() in registration flow so user can return with preserved data
                    // finish();
                    Log.d(TAG, "Transición completada exitosamente");

                } catch (Exception e) {
                    Log.e(TAG, "Error al iniciar DatosPersonalesActivity", e);
                    e.printStackTrace();

                    // Mostrar mensaje de error detallado
                    String errorMessage = "Error al navegar: " + e.getMessage();
                    if (e.getCause() != null) {
                        errorMessage += "\nCausa: " + e.getCause().getMessage();
                    }
                    showErrorDialog(errorMessage);

                    // Rehabilitar el botón
                    progressBar.setVisibility(View.GONE);
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setText("Registrar");
                }
            }
        });
        successSet.start();
    }

    private void handleExitAnimation() {
        AnimatorSet exitSet = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator logoTranslation = ObjectAnimator.ofFloat(logoContainer, "translationY", 0f, -100f);
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formTranslation = ObjectAnimator.ofFloat(formContainer, "translationY", 0f, 100f);

        exitSet.playTogether(logoAlpha, logoTranslation, formAlpha, formTranslation);
        exitSet.setDuration(300);
        exitSet.setInterpolator(new AccelerateDecelerateInterpolator());
        exitSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    Log.e(TAG, "Error al volver a MainActivity", e);
                }
            }
        });
        exitSet.start();
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