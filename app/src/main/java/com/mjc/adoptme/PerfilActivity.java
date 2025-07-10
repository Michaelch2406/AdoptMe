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

public class PerfilActivity extends AppCompatActivity {

    // Vistas principales
    private LinearLayout logoContainer, cardsContainer, layoutPaws;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo, tvUserName;
    private MaterialButton btnRegresar;

    // Cards de configuración
    private CardView cardDatosPersonales, cardDomicilio, cardReferencias;

    private String nombreUsuario;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Ocultar la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Obtener nombre del usuario del Intent
        nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            nombreUsuario = "Usuario"; // Valor por defecto
        }

        initViews();
        setupUserInfo();
        setupClickListeners();
        startAnimations();
    }

    private void initViews() {
        // Contenedores y vistas principales
        logoContainer = findViewById(R.id.logoContainer);
        cardsContainer = findViewById(R.id.cardsContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvUserName = findViewById(R.id.tvUserName);
        btnRegresar = findViewById(R.id.btnRegresar);

        // Huellas para animación
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);

        // Cards
        cardDatosPersonales = findViewById(R.id.cardDatosPersonales);
        cardDomicilio = findViewById(R.id.cardDomicilio);
        cardReferencias = findViewById(R.id.cardReferencias);

        // NOTA: Los TextViews dentro de las cards no tienen ID en el XML,
        // por lo que no se inicializan aquí. La interacción es a nivel de la CardView.

        // Configurar estados iniciales para animaciones
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        cardsContainer.setAlpha(0f);
        cardsContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
    }

    private void setupUserInfo() {
        // Establece un texto de bienvenida con el nombre del usuario.
        tvUserName.setText("Hola, " + nombreUsuario);
    }

    private void setupClickListeners() {
        // Listener para el botón de regresar
        btnRegresar.setOnClickListener(v -> {
            animateCardClick(v);
            handleExitAnimation();
        });

        // Listener para la tarjeta de datos personales
        cardDatosPersonales.setOnClickListener(v -> {
            animateCardClick(v);
            Toast.makeText(this, "Abriendo datos personales...", Toast.LENGTH_SHORT).show();
            // Aquí iría la lógica para navegar a la actividad de Datos Personales
            // Intent intent = new Intent(PerfilActivity.this, DatosPersonalesActivity.class);
            // intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
            // startActivity(intent);
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Listener para la tarjeta de domicilio
        cardDomicilio.setOnClickListener(v -> {
            animateCardClick(v);
            Toast.makeText(this, "Abriendo datos de domicilio...", Toast.LENGTH_SHORT).show();
            // Aquí iría la lógica para navegar a la actividad de Domicilio
            // Intent intent = new Intent(PerfilActivity.this, DomicilioActivity.class);
            // intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
            // startActivity(intent);
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Listener para la tarjeta de referencias
        cardReferencias.setOnClickListener(v -> {
            animateCardClick(v);
            Toast.makeText(this, "Abriendo referencias personales...", Toast.LENGTH_SHORT).show();
            // Aquí iría la lógica para navegar a la actividad de Referencias
            // Intent intent = new Intent(PerfilActivity.this, ReferenciasActivity.class);
            // intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
            // startActivity(intent);
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void animateCardClick(View view) {
        // Animación de pulsación para cualquier vista clickeable
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);

        AnimatorSet clickAnimator = new AnimatorSet();
        clickAnimator.playTogether(scaleX, scaleY);
        clickAnimator.setDuration(300);
        clickAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        clickAnimator.start();
    }

    private void startAnimations() {
        // Animación de entrada del logo y título
        logoContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación de rotación del logo
        ivLogo.animate()
                .rotation(360f)
                .setDuration(800)
                .setStartDelay(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación de entrada del contenedor de tarjetas
        cardsContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación de aparición del texto informativo
        tvInfo.animate()
                .alpha(1f) // Cambiado a 1f para total visibilidad
                .setDuration(600)
                .setStartDelay(500)
                .start();

        // Animar tarjetas individuales con efecto de cascada
        animateCards();

        // Animar las huellas de mascota después de las animaciones principales
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

            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setStartDelay(400 + (long)(i * 150))
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void animatePaws() {
        // Anima las huellas en secuencia
        animatePaw(paw1, 0, () ->
                animatePaw(paw2, 100, () ->
                        animatePaw(paw3, 100, this::startPawGlowAnimation)));
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
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            set.start();
        }, delay);
    }

    private void startPawGlowAnimation() {
        // Animación de brillo intermitente para las huellas
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

    private void handleExitAnimation() {
        btnRegresar.setEnabled(false); // Deshabilitar botón para evitar doble click

        AnimatorSet exitSet = new AnimatorSet();
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
                // Navegar de regreso a PanelActivity
                Intent intent = new Intent(PerfilActivity.this, PanelActivity.class);
                intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Finaliza esta actividad
                // Transición de salida personalizada
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        exitSet.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Limpiar animaciones para evitar fugas de memoria si la actividad se pausa
        handler.removeCallbacksAndMessages(null); // Limpia cualquier tarea pendiente en el handler
        ivLogo.clearAnimation();
        logoContainer.clearAnimation();
        cardsContainer.clearAnimation();
        layoutPaws.clearAnimation();
    }
}