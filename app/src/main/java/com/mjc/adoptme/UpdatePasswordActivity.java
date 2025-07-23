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
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.UpdatePasswordRequest;
import com.mjc.adoptme.models.UpdatePasswordResponse;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import SecureX.Library;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    private static final String TAG = "UpdatePassword";

    // Vistas principales
    private LinearLayout logoContainer, formContainer, layoutPaws, layoutSuccess;
    private ImageView ivLogo, ivPasswordIcon, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInstrucciones, tvSuccessMessage, tvVolverLogin;
    private MaterialButton btnUpdatePassword;
    private ProgressBar progressBar;

    // Campos del formulario
    private TextInputLayout tilNewPassword, tilConfirmPassword;
    private TextInputEditText etNewPassword, etConfirmPassword;

    // Data
    private ApiService apiService;
    private String userCedula;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        apiService = RetrofitClient.getApiService();
        
        // Get cedula from intent
        try {
            userCedula = getIntent().getStringExtra("cedula");
            if (userCedula == null || userCedula.isEmpty()) {
                showErrorDialog("Error: No se pudo obtener la información del usuario");
                finish();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user cedula from intent", e);
            showErrorDialog("Error al iniciar la actividad. Inténtalo de nuevo.");
            finish();
            return;
        }
        
        initViews();
        setupClickListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void initViews() {
        // Containers
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        layoutSuccess = findViewById(R.id.layoutSuccess);

        // ImageViews
        ivLogo = findViewById(R.id.ivLogo);
        ivPasswordIcon = findViewById(R.id.ivPasswordIcon);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);

        // TextViews
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInstrucciones = findViewById(R.id.tvInstrucciones);
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        // Form elements
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        progressBar = findViewById(R.id.progressBar);

        // Set initial states
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-100f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(100f);
    }

    private void setupClickListeners() {
        btnUpdatePassword.setOnClickListener(v -> {
            if (validatePasswords()) {
                updatePassword();
            }
        });

        tvVolverLogin.setOnClickListener(v -> handleExitAnimation());
    }

    private boolean validatePasswords() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Clear previous errors
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean isValid = true;

        // Validate new password
        if (newPassword.isEmpty()) {
            tilNewPassword.setError("La nueva contraseña es requerida");
            shakeView(tilNewPassword);
            isValid = false;
        } else if (newPassword.length() < 8) {
            tilNewPassword.setError("La contraseña debe tener al menos 8 caracteres");
            shakeView(tilNewPassword);
            isValid = false;
        } else if (!newPassword.matches(".*[A-Za-z].*") || !newPassword.matches(".*\\d.*")) {
            tilNewPassword.setError("La contraseña debe contener letras y números");
            shakeView(tilNewPassword);
            isValid = false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Confirma tu nueva contraseña");
            shakeView(tilConfirmPassword);
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            shakeView(tilConfirmPassword);
            isValid = false;
        }

        return isValid;
    }

    private void updatePassword() {
        showLoading();

        String newPassword = etNewPassword.getText().toString().trim();
        String passwordHash = generatePasswordHash(newPassword);

        UpdatePasswordRequest request = new UpdatePasswordRequest(userCedula, passwordHash);

        try {
            Call<ApiResponse<UpdatePasswordResponse>> call = apiService.updatePassword(request);
            call.enqueue(new Callback<ApiResponse<UpdatePasswordResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UpdatePasswordResponse>> call, Response<ApiResponse<UpdatePasswordResponse>> response) {
                    hideLoading();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<UpdatePasswordResponse> apiResponse = response.body();
                            if (apiResponse.getStatus() == 200) {
                                showSuccessAnimation();
                            } else {
                                showErrorDialog("Error al actualizar la contraseña: " + apiResponse.getMessage());
                            }
                        } else if (response.code() == 400) {
                            showErrorDialog("Datos inválidos. Verifica la información e intenta nuevamente.");
                        } else if (response.code() == 404) {
                            showErrorDialog("Usuario no encontrado. Verifica tu información.");
                        } else if (response.code() == 500) {
                            showErrorDialog("Error interno del servidor. Intenta más tarde.");
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error updating password: " + errorBody);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            showErrorDialog("Error al actualizar la contraseña. Intenta nuevamente.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing update password response", e);
                        showErrorDialog("Error al procesar la respuesta del servidor.");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UpdatePasswordResponse>> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Error updating password", t);
                    showErrorDialog("Error de conexión. Verifica tu conexión a internet.");
                }
            });
        } catch (Exception e) {
            hideLoading();
            Log.e(TAG, "Error calling update password endpoint", e);
            showErrorDialog("Error al iniciar la solicitud. Inténtalo de nuevo.");
        }
    }

    private String generatePasswordHash(String password) {
        try {
            // Use exactly the same hash function as registration
            String hashedPassword = Library.Hash.hashBase64(password);
            
            Log.d(TAG, "Generated password hash length: " + hashedPassword.length());
            Log.d(TAG, "Generated password hash: " + hashedPassword);
            
            return hashedPassword;
        } catch (Exception e) {
            Log.e(TAG, "Error generating password hash", e);
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }

    private void showLoading() {
        btnUpdatePassword.setEnabled(false);
        btnUpdatePassword.setText("Actualizando...");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnUpdatePassword.setEnabled(true);
        btnUpdatePassword.setText("Actualizar Contraseña");
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
        // Animación del logo container
        logoContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación del logo
        ivLogo.animate()
                .rotation(360f)
                .setDuration(800)
                .setStartDelay(100)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación del formulario
        formContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(this::animatePasswordIcon)
                .start();

        // Animación de las paws
        handler.postDelayed(this::animatePaws, 800);
    }

    private void animatePasswordIcon() {
        // Animación del icono de contraseña
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivPasswordIcon, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivPasswordIcon, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivPasswordIcon, "rotation", 0f, 360f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotation);
        animatorSet.setDuration(600);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
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

    private void showSuccessAnimation() {
        // Hide form elements
        tilNewPassword.setVisibility(View.GONE);
        tilConfirmPassword.setVisibility(View.GONE);
        tvInstrucciones.setVisibility(View.GONE);

        // Change button text
        btnUpdatePassword.setText("Ir al Login");
        btnUpdatePassword.setEnabled(true);

        // Show success message
        showSuccessMessage();
    }

    private void showSuccessMessage() {
        layoutSuccess.setVisibility(View.VISIBLE);
        layoutSuccess.setAlpha(0f);
        layoutSuccess.setTranslationY(50f);

        // Update success message
        tvSuccessMessage.setText("✓ Contraseña actualizada correctamente.\\nYa puedes iniciar sesión con tu nueva contraseña.");

        layoutSuccess.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Change button behavior
        btnUpdatePassword.setOnClickListener(v -> handleExitAnimation());
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
                Intent intent = new Intent(UpdatePasswordActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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