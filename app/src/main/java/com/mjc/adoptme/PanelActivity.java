package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PanelActivity extends AppCompatActivity {

    // Vistas principales
    private ImageView ivLogoPet;
    private TextView tvWelcomeMessage, tvUserName, tvSubtitle;
    private LinearLayout logoContainer, cardsContainer;
    private CardView cardPerfil, cardAdopciones, cardFundaciones, cardPortafolio;
    private LinearLayout pawsDecoration;
    private TextView tvLogout;

    private String nombreUsuario;
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        // Ocultar la barra de acción para mantener consistencia
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Obtener el nombre del usuario desde Intent
        nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        if (nombreUsuario == null) {
            nombreUsuario = "Usuario";
        }

        initViews();
        setupWelcomeMessage();
        setupAnimations();
        setupCardClickListeners();
        setupBackButtonHandler();
    }

    private void initViews() {
        ivLogoPet = findViewById(R.id.ivLogoPet);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        tvUserName = findViewById(R.id.tvUserName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        logoContainer = findViewById(R.id.logoContainer);
        cardsContainer = findViewById(R.id.cardsContainer);
        cardPerfil = findViewById(R.id.cardPerfil);
        cardAdopciones = findViewById(R.id.cardAdopciones);
        cardFundaciones = findViewById(R.id.cardFundaciones);
        cardPortafolio = findViewById(R.id.cardPortafolio);
        pawsDecoration = findViewById(R.id.pawsDecoration);
        tvLogout = findViewById(R.id.tvLogout);
    }

    private void setupBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Llama a tu lógica de animación de salida personalizada
                animateExit();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setupWelcomeMessage() {
        String welcomeMessage = "¡Hola, " + nombreUsuario + "!";
        tvUserName.setText(welcomeMessage);
    }

    private void setupAnimations() {
        animateLogo();
        animateWelcomeText();
        animateCards();
        animatePawsDecoration();
    }

    private void animateLogo() {
        // Animación similar al MainActivity - logo con escala, rotación y alpha
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLogoPet, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLogoPet, "scaleY", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivLogoPet, "rotation", -180f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivLogoPet, "alpha", 0f, 1f);

        AnimatorSet logoAnimator = new AnimatorSet();
        logoAnimator.playTogether(scaleX, scaleY, rotation, alpha);
        logoAnimator.setDuration(1000);
        logoAnimator.setInterpolator(new DecelerateInterpolator());
        logoAnimator.setStartDelay(200);
        logoAnimator.start();

        // Iniciar animación de pulso después del logo
        startPulseAnimation();
    }

    private void startPulseAnimation() {
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(ivLogoPet, "scaleX", 1f, 1.05f, 1f);
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator pulseY = ObjectAnimator.ofFloat(ivLogoPet, "scaleY", 1f, 1.05f, 1f);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setRepeatMode(ValueAnimator.RESTART);

        AnimatorSet pulseAnimator = new AnimatorSet();
        pulseAnimator.playTogether(pulseX, pulseY);
        pulseAnimator.setDuration(2000);
        pulseAnimator.setStartDelay(2000);
        pulseAnimator.start();
    }

    private void animateWelcomeText() {
        // Animación del mensaje de bienvenida similar al título en MainActivity
        ObjectAnimator translateY = ObjectAnimator.ofFloat(tvUserName, "translationY", -200f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvUserName, "alpha", 0f, 1f);
        AnimatorSet welcomeAnimator = new AnimatorSet();
        welcomeAnimator.playTogether(translateY, alpha);
        welcomeAnimator.setDuration(800);
        welcomeAnimator.setStartDelay(400);
        welcomeAnimator.start();

        // Animar subtítulo
        ObjectAnimator subtitleAlpha = ObjectAnimator.ofFloat(tvSubtitle, "alpha", 0f, 1f);
        ObjectAnimator subtitleTranslate = ObjectAnimator.ofFloat(tvSubtitle, "translationY", 50f, 0f);
        AnimatorSet subtitleAnimator = new AnimatorSet();
        subtitleAnimator.playTogether(subtitleAlpha, subtitleTranslate);
        subtitleAnimator.setDuration(600);
        subtitleAnimator.setStartDelay(800);
        subtitleAnimator.start();
    }

    private void animateCards() {
        // Animación del contenedor de cards similar al formulario en MainActivity
        ObjectAnimator translateY = ObjectAnimator.ofFloat(cardsContainer, "translationY", 300f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(cardsContainer, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cardsContainer, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(cardsContainer, "scaleY", 0.9f, 1f);

        AnimatorSet cardsAnimator = new AnimatorSet();
        cardsAnimator.playTogether(translateY, alpha, scaleX, scaleY);
        cardsAnimator.setDuration(800);
        cardsAnimator.setStartDelay(600);
        cardsAnimator.setInterpolator(new DecelerateInterpolator());
        cardsAnimator.start();

        // Animar cada card individualmente con retraso escalonado
        CardView[] cards = {cardPerfil, cardAdopciones, cardFundaciones, cardPortafolio};
        for (int i = 0; i < cards.length; i++) {
            animateIndividualCard(cards[i], i * 150 + 1000);
        }
    }

    private void animateIndividualCard(CardView card, long delay) {
        card.setAlpha(0f);
        card.setTranslationY(100f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(card, "translationY", 100f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.8f, 1f);

        AnimatorSet cardAnimator = new AnimatorSet();
        cardAnimator.playTogether(fadeIn, slideUp, scaleX, scaleY);
        cardAnimator.setDuration(600);
        cardAnimator.setStartDelay(delay);
        cardAnimator.setInterpolator(new DecelerateInterpolator());
        cardAnimator.start();
    }

    private void animatePawsDecoration() {
        // Animar decoración de huellas similar al MainActivity
        ObjectAnimator alpha = ObjectAnimator.ofFloat(pawsDecoration, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(pawsDecoration, "translationY", 50f, 0f);
        AnimatorSet pawsAnimator = new AnimatorSet();
        pawsAnimator.playTogether(alpha, translateY);
        pawsAnimator.setDuration(600);
        pawsAnimator.setStartDelay(1500);
        pawsAnimator.start();
    }

    private void setupCardClickListeners() {
        cardPerfil.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Toast.makeText(this, "Abriendo Perfil...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(PanelActivity.this, PerfilActivity.class);
                 intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
                 startActivity(intent);
                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cardAdopciones.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Toast.makeText(this, "Abriendo Mis Adopciones...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(PanelActivity.this, MisAdopcionesActivity.class);
                 intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
                 startActivity(intent);
                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cardFundaciones.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Toast.makeText(this, "Abriendo Fundaciones...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(PanelActivity.this, FundacionesActivity.class);
                 startActivity(intent);
                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cardPortafolio.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Toast.makeText(this, "Abriendo AdoptMe Portafolio...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(PanelActivity.this, AdoptMePortafolioActivity.class);
                 startActivity(intent);
                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Logout listener
        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                logout();
            }
        });
    }

    private void handleCardClick(String message, View view) {
        if (!isAnimating) {
            animateCardClick(view);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            // Aquí iría el código para iniciar la nueva actividad
        }
    }

    private void logout() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        animateExit();
    }

    private void animateCardClick(View card) {
        // Animación de click similar a la del MainActivity
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0.95f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 0.95f, 1.05f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", 1f, 0.8f, 1f);

        AnimatorSet clickAnimator = new AnimatorSet();
        clickAnimator.playTogether(scaleX, scaleY, alpha);
        clickAnimator.setDuration(300);
        clickAnimator.setInterpolator(new DecelerateInterpolator());
        clickAnimator.start();
    }

    private void animateExit() {
        if (isAnimating) return; // Evitar ejecutar la animación si ya está en curso
        isAnimating = true;

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(cardsContainer, "alpha", 1f, 0f);
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(cardsContainer, "translationY", 0f, 200f);

        AnimatorSet exitAnimator = new AnimatorSet();
        exitAnimator.playTogether(fadeOut, slideDown);
        exitAnimator.setDuration(400);

        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // La lógica de navegación ya es correcta
                Intent intent = new Intent(PanelActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        exitAnimator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reanudar animación de pulso si está disponible
        if (ivLogoPet.getScaleX() == 1f) {
            startPulseAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Limpiar animaciones para evitar memory leaks
        ivLogoPet.clearAnimation();
        if (cardsContainer != null) {
            cardsContainer.clearAnimation();
        }
    }
}