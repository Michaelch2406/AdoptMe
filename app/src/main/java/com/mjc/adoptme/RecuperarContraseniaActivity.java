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

public class RecuperarContraseniaActivity extends AppCompatActivity {

    private static final String TAG = "RecuperarContrasenia";

    // Vistas principales
    private LinearLayout logoContainer, formContainer, layoutPaws, layoutSuccess;
    private ImageView ivLogo, ivLockIcon, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInstrucciones, tvSuccessMessage, tvVolverLogin;
    private MaterialButton btnEnviar;
    private ProgressBar progressBar;

    // Campos del formulario
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean emailSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenia);

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

        // Form elements
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnEnviar = findViewById(R.id.btnEnviar);
        progressBar = findViewById(R.id.progressBar);

        // Set initial states
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-100f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(100f);
    }

    private void setupClickListeners() {
        btnEnviar.setOnClickListener(v -> {
            if (validateForm()) {
                sendResetEmail();
            }
        });

        tvVolverLogin.setOnClickListener(v -> handleExitAnimation());
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

    private boolean validateForm() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("El correo electrónico es requerido");
            shakeView(tilEmail);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de correo inválido");
            shakeView(tilEmail);
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }

    private void shakeView(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
                .setDuration(500)
                .start();
    }

    private void sendResetEmail() {
        // Deshabilitar el botón y mostrar progress
        btnEnviar.setEnabled(false);
        btnEnviar.setText("");
        progressBar.setVisibility(View.VISIBLE);

        String email = etEmail.getText().toString().trim();
        Log.d(TAG, "Enviando instrucciones a: " + email);

        // Simular envío de email (aquí iría la lógica real de envío)
        handler.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            emailSent = true;
            showSuccessAnimation();
        }, 2000);
    }

    private void showSuccessAnimation() {
        // Animar la desaparición del formulario de entrada
        AnimatorSet hideFormSet = new AnimatorSet();

        ObjectAnimator fadeOutEmail = ObjectAnimator.ofFloat(tilEmail, "alpha", 1f, 0f);
        ObjectAnimator fadeOutInstructions = ObjectAnimator.ofFloat(tvInstrucciones, "alpha", 0.8f, 0f);
        ObjectAnimator translateEmail = ObjectAnimator.ofFloat(tilEmail, "translationY", 0f, -50f);

        hideFormSet.playTogether(fadeOutEmail, fadeOutInstructions, translateEmail);
        hideFormSet.setDuration(300);
        hideFormSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tilEmail.setVisibility(View.GONE);
                tvInstrucciones.setVisibility(View.GONE);

                // Cambiar el texto del botón
                btnEnviar.setText("Volver al Login");
                btnEnviar.setEnabled(true);

                // Mostrar mensaje de éxito
                showSuccessMessage();
            }
        });

        hideFormSet.start();
    }

    private void showSuccessMessage() {
        layoutSuccess.setVisibility(View.VISIBLE);
        layoutSuccess.setAlpha(0f);
        layoutSuccess.setTranslationY(50f);

        layoutSuccess.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Cambiar el comportamiento del botón
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
}