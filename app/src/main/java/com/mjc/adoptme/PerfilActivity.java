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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.mjc.adoptme.data.SessionManager; // <-- ¡IMPORTANTE!

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "PerfilActivity";

    // Vistas principales
    private LinearLayout logoContainer, cardsContainer, layoutPaws;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo, tvUserName;
    private MaterialButton btnRegresar;

    // Cards de configuración
    private CardView cardDatosPersonales, cardDomicilio, cardReferencias;

    // Gestor de sesión
    private SessionManager sessionManager;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // --- LÓGICA DE SESIÓN ---
        sessionManager = null;
        try {
            sessionManager = new SessionManager(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SessionManager", e);
            Toast.makeText(this, "Error crítico al iniciar el perfil.", Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            } catch (Exception ex) {
                Log.e(TAG, "Error starting MainActivity after SessionManager failure", ex);
            }
            finish();
            return;
        }

        // Protección: Si no hay sesión, no debería estar aquí. Lo mandamos al login.
        if (!sessionManager.isLoggedIn()) {
            try {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error redirecting to MainActivity", e);
            }
            finish();
            return; // Detenemos la ejecución
        }

        // Obtener el nombre directamente de la sesión guardada
        String userName = sessionManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "Usuario"; // Valor por defecto
        }
        // --- FIN DE LÓGICA DE SESIÓN ---

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupUserInfo(userName); // Pasamos el nombre obtenido
        setupClickListeners();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        cardsContainer = findViewById(R.id.cardsContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvUserName = findViewById(R.id.tvUserName);
        btnRegresar = findViewById(R.id.btnRegresar);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);
        cardDatosPersonales = findViewById(R.id.cardDatosPersonales);
        cardDomicilio = findViewById(R.id.cardDomicilio);
        cardReferencias = findViewById(R.id.cardReferencias);

        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        cardsContainer.setAlpha(0f);
        cardsContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
    }

    private void setupUserInfo(String userName) {
        // Establece el texto de bienvenida con el nombre real del usuario.
        tvUserName.setText("Hola, " + userName);
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> {
            animateCardClick(v);
            handleExitAnimation();
        });

        // --- NAVEGACIÓN EN MODO ACTUALIZACIÓN ---
        cardDatosPersonales.setOnClickListener(v -> {
            animateCardClick(v);
            try {
                Intent intent = new Intent(PerfilActivity.this, DatosPersonalesActivity.class);
                // ¡La clave! Le decimos a la activity que se abra en modo de actualización.
                intent.putExtra("IS_UPDATE_MODE", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } catch (Exception e) {
                Log.e(TAG, "Error starting DatosPersonalesActivity", e);
                Toast.makeText(PerfilActivity.this, "Error al abrir Datos Personales.", Toast.LENGTH_SHORT).show();
            }
        });

        cardDomicilio.setOnClickListener(v -> {
            animateCardClick(v);
            try {
                Intent intent = new Intent(PerfilActivity.this, DomicilioActivity.class);
                intent.putExtra("IS_UPDATE_MODE", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } catch (Exception e) {
                Log.e(TAG, "Error starting DomicilioActivity", e);
                Toast.makeText(PerfilActivity.this, "Error al abrir Domicilio.", Toast.LENGTH_SHORT).show();
            }
        });

        cardReferencias.setOnClickListener(v -> {
            animateCardClick(v);
            try {
                Intent intent = new Intent(PerfilActivity.this, ReferenciasPersonalesActivity.class);
                intent.putExtra("IS_UPDATE_MODE", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } catch (Exception e) {
                Log.e(TAG, "Error starting ReferenciasPersonalesActivity", e);
                Toast.makeText(PerfilActivity.this, "Error al abrir Referencias Personales.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleExitAnimation() {
        btnRegresar.setEnabled(false);
        AnimatorSet exitSet = new AnimatorSet();
        // ... (el resto de tu animación de salida es correcta)
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 0f);
        ObjectAnimator logoTranslation = ObjectAnimator.ofFloat(logoContainer, "translationY", -50f);
        ObjectAnimator cardsAlpha = ObjectAnimator.ofFloat(cardsContainer, "alpha", 0f);
        ObjectAnimator cardsTranslation = ObjectAnimator.ofFloat(cardsContainer, "translationY", 50f);
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(btnRegresar, "alpha", 0f);

        exitSet.playTogether(logoAlpha, logoTranslation, cardsAlpha, cardsTranslation, buttonAlpha);
        exitSet.setDuration(400);
        exitSet.setInterpolator(new AccelerateDecelerateInterpolator());
        exitSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // finish() es más apropiado aquí que crear un nuevo Intent,
                // ya que PanelActivity ya está en la pila de actividades.
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        exitSet.start();
    }

    // ===================================================================
    // MÉTODOS DE ANIMACIÓN (SIN CAMBIOS)
    // ===================================================================

    private void animateCardClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);
        AnimatorSet clickAnimator = new AnimatorSet();
        clickAnimator.playTogether(scaleX, scaleY);
        clickAnimator.setDuration(300);
        clickAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        clickAnimator.start();
    }

    private void startAnimations() {
        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ivLogo.animate().rotation(360f).setDuration(800).setStartDelay(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        cardsContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        tvInfo.animate().alpha(1f).setDuration(600).setStartDelay(500).start();
        animateCards();
        handler.postDelayed(this::animatePaws, 700);
    }

    private void animateCards() {
        CardView[] cards = {cardDatosPersonales, cardDomicilio, cardReferencias};
        for (int i = 0; i < cards.length; i++) {
            CardView card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(100f);
            card.setScaleX(0.9f);
            card.setScaleY(0.9f);
            card.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(500).setStartDelay(400 + (long)(i * 150)).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
    }

    private void animatePaws() {
        animatePaw(paw1, 0, () -> animatePaw(paw2, 100, () -> animatePaw(paw3, 100, this::startPawGlowAnimation)));
    }

    private void animatePaw(final ImageView paw, long delay, final Runnable onComplete) {
        handler.postDelayed(() -> {
            paw.setVisibility(View.VISIBLE);
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(paw, "scaleX", 0f, 1.2f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(paw, "scaleY", 0f, 1.2f, 1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(paw, "alpha", 0f, 0.6f);
            set.playTogether(scaleX, scaleY, alpha);
            set.setDuration(400);
            set.setInterpolator(new BounceInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (onComplete != null) onComplete.run();
                }
            });
            set.start();
        }, delay);
    }

    private void startPawGlowAnimation() {
        ValueAnimator glowAnimator = ValueAnimator.ofFloat(0.3f, 0.7f, 0.3f);
        glowAnimator.setDuration(2000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        glowAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            paw1.setAlpha(value);
            paw2.setAlpha(value * 0.8f);
            paw3.setAlpha(value * 0.6f);
        });
        glowAnimator.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        if (ivLogo != null) ivLogo.clearAnimation();
        if (logoContainer != null) logoContainer.clearAnimation();
        if (cardsContainer != null) cardsContainer.clearAnimation();
        if (layoutPaws != null) layoutPaws.clearAnimation();
    }
}