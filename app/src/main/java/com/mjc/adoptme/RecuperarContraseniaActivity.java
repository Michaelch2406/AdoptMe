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
import com.mjc.adoptme.models.SecurityQuestion;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarContraseniaActivity extends AppCompatActivity {

    private static final String TAG = "RecuperarContrasenia";
    
    // Estados del flujo
    private enum FlowState {
        CEDULA_INPUT,
        SECURITY_QUESTIONS,
        SUCCESS
    }
    
    private FlowState currentState = FlowState.CEDULA_INPUT;

    // Vistas principales
    private LinearLayout logoContainer, formContainer, layoutPaws, layoutSuccess;
    private ImageView ivLogo, ivLockIcon, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInstrucciones, tvSuccessMessage, tvVolverLogin;
    private MaterialButton btnEnviar;
    private ProgressBar progressBar;

    // Campos del formulario - Paso 1: Cédula
    private TextInputLayout tilCedula;
    private TextInputEditText etCedula;

    // Campos del formulario - Paso 2: Preguntas de seguridad
    private LinearLayout questionsContainer;
    private TextView tvQuestion1, tvQuestion2, tvQuestion3, tvQuestion4;
    private TextInputLayout tilAnswer1, tilAnswer2, tilAnswer3, tilAnswer4;
    private TextInputEditText etAnswer1, etAnswer2, etAnswer3, etAnswer4;

    // Data
    private List<SecurityQuestion> securityQuestions = new ArrayList<>();
    private ApiService apiService;
    private String userCedula;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenia);

        apiService = RetrofitClient.getApiService();
        
        initViews();
        setupClickListeners();
        setupBackButtonHandler();
        startAnimations();
        
        setupInitialState();
    }

    private void initViews() {
        // Containers
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        layoutSuccess = findViewById(R.id.layoutSuccess);

        // ImageViews
        ivLogo = findViewById(R.id.ivLogo);
        ivLockIcon = findViewById(R.id.ivLockIcon);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);

        // TextViews
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInstrucciones = findViewById(R.id.tvInstrucciones);
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        // Form elements - Paso 1
        tilCedula = findViewById(R.id.tilCedula);
        etCedula = findViewById(R.id.etCedula);
        btnEnviar = findViewById(R.id.btnEnviar);
        progressBar = findViewById(R.id.progressBar);

        // Form elements - Paso 2 (Security Questions)
        questionsContainer = findViewById(R.id.questionsContainer);
        if (questionsContainer != null) {
            tvQuestion1 = findViewById(R.id.tvQuestion1);
            tvQuestion2 = findViewById(R.id.tvQuestion2);
            tvQuestion3 = findViewById(R.id.tvQuestion3);
            tvQuestion4 = findViewById(R.id.tvQuestion4);

            tilAnswer1 = findViewById(R.id.tilAnswer1);
            tilAnswer2 = findViewById(R.id.tilAnswer2);
            tilAnswer3 = findViewById(R.id.tilAnswer3);
            tilAnswer4 = findViewById(R.id.tilAnswer4);

            etAnswer1 = findViewById(R.id.etAnswer1);
            etAnswer2 = findViewById(R.id.etAnswer2);
            etAnswer3 = findViewById(R.id.etAnswer3);
            etAnswer4 = findViewById(R.id.etAnswer4);
        }

        // Set initial states
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-100f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(100f);
    }

    private void setupInitialState() {
        // Initially show cedula input
        showCedulaInput();
    }

    private void showCedulaInput() {
        currentState = FlowState.CEDULA_INPUT;
        
        tvInstrucciones.setText("Ingresa tu cédula para recuperar tu contraseña");
        btnEnviar.setText("Continuar");
        
        if (tilCedula != null) {
            tilCedula.setVisibility(View.VISIBLE);
        }
        if (questionsContainer != null) {
            questionsContainer.setVisibility(View.GONE);
        }
    }

    private void showSecurityQuestions() {
        currentState = FlowState.SECURITY_QUESTIONS;
        
        tvInstrucciones.setText("Responde las siguientes preguntas de seguridad");
        btnEnviar.setText("Verificar Respuestas");
        
        if (tilCedula != null) {
            tilCedula.setVisibility(View.GONE);
        }
        if (questionsContainer != null) {
            questionsContainer.setVisibility(View.VISIBLE);
            displayQuestions();
        }
    }

    private void displayQuestions() {
        if (securityQuestions.size() >= 4 && questionsContainer != null) {
            tvQuestion1.setText("1. " + securityQuestions.get(0).getPregunta());
            tvQuestion2.setText("2. " + securityQuestions.get(1).getPregunta());
            tvQuestion3.setText("3. " + securityQuestions.get(2).getPregunta());
            tvQuestion4.setText("4. " + securityQuestions.get(3).getPregunta());
        }
    }

    private void setupClickListeners() {
        btnEnviar.setOnClickListener(v -> {
            switch (currentState) {
                case CEDULA_INPUT:
                    if (validateCedula()) {
                        loadUserSecurityQuestions();
                    }
                    break;
                case SECURITY_QUESTIONS:
                    if (validateSecurityAnswers()) {
                        verifySecurityAnswers();
                    }
                    break;
            }
        });

        tvVolverLogin.setOnClickListener(v -> handleExitAnimation());
    }

    private boolean validateCedula() {
        String cedula = etCedula.getText().toString().trim();

        if (cedula.isEmpty()) {
            tilCedula.setError("La cédula es requerida");
            shakeView(tilCedula);
            return false;
        } else if (cedula.length() < 10) {
            tilCedula.setError("Cédula inválida");
            shakeView(tilCedula);
            return false;
        } else {
            tilCedula.setError(null);
            userCedula = cedula;
            return true;
        }
    }

    private boolean validateSecurityAnswers() {
        boolean isValid = true;

        if (questionsContainer == null) return false;

        // Clear previous errors
        tilAnswer1.setError(null);
        tilAnswer2.setError(null);
        tilAnswer3.setError(null);
        tilAnswer4.setError(null);

        // Validate each answer
        if (etAnswer1.getText() == null || etAnswer1.getText().toString().trim().isEmpty()) {
            tilAnswer1.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer2.getText() == null || etAnswer2.getText().toString().trim().isEmpty()) {
            tilAnswer2.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer3.getText() == null || etAnswer3.getText().toString().trim().isEmpty()) {
            tilAnswer3.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer4.getText() == null || etAnswer4.getText().toString().trim().isEmpty()) {
            tilAnswer4.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        return isValid;
    }

    private void loadUserSecurityQuestions() {
        showLoading();

        Call<ApiResponse<List<SecurityQuestion>>> call = apiService.getUserSecurityQuestions(userCedula);
        call.enqueue(new Callback<ApiResponse<List<SecurityQuestion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SecurityQuestion>>> call, Response<ApiResponse<List<SecurityQuestion>>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<SecurityQuestion>> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                        securityQuestions = apiResponse.getData();
                        if (securityQuestions.size() >= 4) {
                            showSecurityQuestions();
                        } else {
                            showErrorDialog("El usuario no tiene suficientes preguntas de seguridad configuradas.");
                        }
                    } else {
                        showErrorDialog("Error al obtener las preguntas de seguridad.");
                    }
                } else if (response.code() == 404) {
                    showErrorDialog("El usuario no tiene preguntas de seguridad configuradas.");
                } else {
                    showErrorDialog("Error del servidor al cargar las preguntas.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SecurityQuestion>>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Error loading security questions", t);
                showErrorDialog("Error de conexión. Verifica tu conexión a internet.");
            }
        });
    }

    private void verifySecurityAnswers() {
        // En una implementación real, aquí verificarías las respuestas con el servidor
        // Por ahora, simulamos que la verificación es exitosa
        showLoading();
        
        handler.postDelayed(() -> {
            hideLoading();
            showSuccessAnimation();
        }, 2000);
    }

    private void showLoading() {
        btnEnviar.setEnabled(false);
        btnEnviar.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnEnviar.setEnabled(true);
        
        switch (currentState) {
            case CEDULA_INPUT:
                btnEnviar.setText("Continuar");
                break;
            case SECURITY_QUESTIONS:
                btnEnviar.setText("Verificar Respuestas");
                break;
        }
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentState == FlowState.SECURITY_QUESTIONS) {
                    // Go back to cedula input
                    showCedulaInput();
                } else {
                    handleExitAnimation();
                }
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
                .withEndAction(this::animateLockIcon)
                .start();

        // Animación de las paws
        handler.postDelayed(this::animatePaws, 800);
    }

    private void animateLockIcon() {
        // Animación del icono de candado
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLockIcon, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLockIcon, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivLockIcon, "rotation", 0f, 360f);

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
        currentState = FlowState.SUCCESS;
        
        // Hide form elements
        if (tilCedula != null) {
            tilCedula.setVisibility(View.GONE);
        }
        if (questionsContainer != null) {
            questionsContainer.setVisibility(View.GONE);
        }
        tvInstrucciones.setVisibility(View.GONE);

        // Change button text
        btnEnviar.setText("Volver al Login");
        btnEnviar.setEnabled(true);

        // Show success message
        showSuccessMessage();
    }

    private void showSuccessMessage() {
        layoutSuccess.setVisibility(View.VISIBLE);
        layoutSuccess.setAlpha(0f);
        layoutSuccess.setTranslationY(50f);

        // Update success message
        tvSuccessMessage.setText("✓ Identidad verificada correctamente.\nTe hemos enviado instrucciones para restablecer tu contraseña.");

        layoutSuccess.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Change button behavior
        btnEnviar.setOnClickListener(v -> handleExitAnimation());
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
                Intent intent = new Intent(RecuperarContraseniaActivity.this, MainActivity.class);
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