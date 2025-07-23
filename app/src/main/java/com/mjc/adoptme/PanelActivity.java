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

import com.google.android.material.card.MaterialCardView;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.AdoptionStats;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelActivity extends AppCompatActivity {

    // Vistas principales
    private LinearLayout headerContainer;
    private ImageView ivUserAvatar, ivLogoPet;
    private TextView tvUserName, tvGreeting, tvDate;
    private LinearLayout cardsContainer;
    private MaterialCardView cardPerfil, cardAdopciones, cardPortafolio;
    private LinearLayout statsContainer;
    private TextView tvTotalAdopciones, tvEnProceso, tvCompletadas;
    private TextView tvLogout;
    private View motivationalCard;

    // Dependencias
    private SessionManager sessionManager;
    private ApiService apiService;

    private boolean isAnimating = false;
    private AnimatorSet heartbeatAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        // --- INICIO DE LÓGICA DE SESIÓN ---
        sessionManager = new SessionManager(getApplicationContext());
        apiService = RetrofitClient.getApiService();

        // 1. Proteger la Activity: si no hay sesión, no se debe continuar.
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(PanelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Detiene la ejecución del resto del onCreate.
        }

        // 2. Obtener el nombre de usuario de la sesión guardada.
        String userName = sessionManager.getUserName();
        if (userName == null) {
            userName = "Usuario"; // Valor de respaldo
        }
        // --- FIN DE LÓGICA DE SESIÓN ---

        // 3. Inicializar todas las vistas.
        initViews();

        // 4. Configurar el UI con los datos obtenidos.
        setupHeader(userName);
        loadAdoptionStats();
        setupAnimations();
        setupCardClickListeners();
        setupBackButtonHandler();
    }

    private void initViews() {
        headerContainer = findViewById(R.id.headerContainer);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ivLogoPet = findViewById(R.id.ivLogoPet);
        tvUserName = findViewById(R.id.tvUserName); // Este es el TextView del encabezado
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);
        cardsContainer = findViewById(R.id.cardsContainer);
        cardPerfil = findViewById(R.id.cardPerfil);
        cardAdopciones = findViewById(R.id.cardAdopciones);
        cardPortafolio = findViewById(R.id.cardPortafolio);
        statsContainer = findViewById(R.id.statsContainer);
        tvTotalAdopciones = findViewById(R.id.tvTotalAdopciones);
        tvEnProceso = findViewById(R.id.tvEnProceso);
        tvCompletadas = findViewById(R.id.tvCompletadas);
        tvLogout = findViewById(R.id.tvLogout);
        motivationalCard = findViewById(R.id.motivationalCard);
    }

    private void setupBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Salir de la aplicación al presionar "atrás" en el panel principal.
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // El método ahora recibe el nombre de usuario como parámetro.
    private void setupHeader(String userName) {
        // Configurar nombre de usuario en el encabezado.
        tvUserName.setText(userName);

        // Configurar saludo según la hora del día.
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Buenos días";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Buenas tardes";
        } else {
            greeting = "Buenas noches";
        }
        tvGreeting.setText(greeting);

        // Configurar fecha actual.
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        String fecha = day + " de " + meses[month];
        tvDate.setText(fecha);
    }

    private void loadAdoptionStats() {
        String cedula = sessionManager.getCedula();
        if (cedula == null) {
            setDefaultStats();
            return;
        }

        Call<ApiResponse<AdoptionStats>> call = apiService.getAdoptionStats(cedula);
        call.enqueue(new Callback<ApiResponse<AdoptionStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdoptionStats>> call, Response<ApiResponse<AdoptionStats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AdoptionStats> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                        AdoptionStats stats = apiResponse.getData();
                        updateStatsUI(stats);
                    } else {
                        setDefaultStats();
                    }
                } else {
                    setDefaultStats();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AdoptionStats>> call, Throwable t) {
                setDefaultStats();
            }
        });
    }

    private void setDefaultStats() {
        tvTotalAdopciones.setText("0");
        tvEnProceso.setText("0");
        tvCompletadas.setText("0");
    }

    private void updateStatsUI(AdoptionStats stats) {
        tvTotalAdopciones.setText(String.valueOf(stats.getTotalAdopciones()));
        tvEnProceso.setText(String.valueOf(stats.getAdopcionesEnProceso()));
        tvCompletadas.setText(String.valueOf(stats.getAdopcionesCompletadas()));
        
        // Re-trigger animations with the new values
        animateStatsNumbers();
    }

    private void setupCardClickListeners() {
        cardPerfil.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Intent intent = new Intent(PanelActivity.this, PerfilActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cardAdopciones.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Intent intent = new Intent(PanelActivity.this, MisAdopcionesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        cardPortafolio.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Intent intent = new Intent(PanelActivity.this, AdoptMePortafolioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ivUserAvatar.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                Intent intent = new Intent(PanelActivity.this, PerfilActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvLogout.setOnClickListener(v -> {
            if (!isAnimating) {
                animateCardClick(v);
                logout();
            }
        });
    }

    private void logout() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        // El SessionManager se encarga de limpiar la sesión y redirigir.
        sessionManager.logoutUser();
    }

    // ===================================================================
    // MÉTODOS DE ANIMACIÓN (SIN CAMBIOS)
    // ===================================================================

    private void setupAnimations() {
        animateHeader();
        animateLogo();
        animateCards();
        animateStats();
        animateMotivationalCard();
    }

    private void animateHeader() {
        headerContainer.setAlpha(0f);
        headerContainer.setTranslationY(-50f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(headerContainer, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(headerContainer, "translationY", -50f, 0f);
        AnimatorSet headerAnimator = new AnimatorSet();
        headerAnimator.playTogether(alpha, translateY);
        headerAnimator.setDuration(800);
        headerAnimator.setInterpolator(new DecelerateInterpolator());
        headerAnimator.start();
        animateUserAvatar();
    }

    private void animateUserAvatar() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivUserAvatar, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivUserAvatar, "scaleY", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivUserAvatar, "rotation", -180f, 0f);
        AnimatorSet avatarAnimator = new AnimatorSet();
        avatarAnimator.playTogether(scaleX, scaleY, rotation);
        avatarAnimator.setDuration(600);
        avatarAnimator.setStartDelay(400);
        avatarAnimator.start();
    }

    private void animateLogo() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivLogoPet, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivLogoPet, "scaleY", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivLogoPet, "rotation", -360f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivLogoPet, "alpha", 0f, 1f);
        AnimatorSet logoAnimator = new AnimatorSet();
        logoAnimator.playTogether(scaleX, scaleY, rotation, alpha);
        logoAnimator.setDuration(1000);
        logoAnimator.setInterpolator(new DecelerateInterpolator());
        logoAnimator.setStartDelay(200);
        logoAnimator.start();
        startHeartbeatAnimation();
    }

    private void startHeartbeatAnimation() {
        // Cancelar animación anterior si existe
        if (heartbeatAnimator != null && heartbeatAnimator.isRunning()) {
            heartbeatAnimator.cancel();
        }
        
        ObjectAnimator beatX = ObjectAnimator.ofFloat(ivLogoPet, "scaleX", 1f, 1.1f, 0.9f, 1f);
        beatX.setRepeatCount(ValueAnimator.INFINITE);
        beatX.setRepeatMode(ValueAnimator.RESTART);
        ObjectAnimator beatY = ObjectAnimator.ofFloat(ivLogoPet, "scaleY", 1f, 1.1f, 0.9f, 1f);
        beatY.setRepeatCount(ValueAnimator.INFINITE);
        beatY.setRepeatMode(ValueAnimator.RESTART);
        
        heartbeatAnimator = new AnimatorSet();
        heartbeatAnimator.playTogether(beatX, beatY);
        heartbeatAnimator.setDuration(1500);
        heartbeatAnimator.setStartDelay(2000);
        heartbeatAnimator.start();
    }

    private void animateCards() {
        cardsContainer.setAlpha(0f);
        cardsContainer.setTranslationY(100f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(cardsContainer, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(cardsContainer, "translationY", 100f, 0f);
        AnimatorSet containerAnimator = new AnimatorSet();
        containerAnimator.playTogether(alpha, translateY);
        containerAnimator.setDuration(600);
        containerAnimator.setStartDelay(600);
        containerAnimator.start();
        MaterialCardView[] cards = {cardPerfil, cardAdopciones, cardPortafolio};
        for (int i = 0; i < cards.length; i++) {
            animateIndividualCard(cards[i], i * 100 + 800);
        }
    }

    private void animateIndividualCard(MaterialCardView card, long delay) {
        card.setAlpha(0f);
        card.setScaleX(0.8f);
        card.setScaleY(0.8f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.8f, 1f);
        AnimatorSet cardAnimator = new AnimatorSet();
        cardAnimator.playTogether(fadeIn, scaleX, scaleY);
        cardAnimator.setDuration(500);
        cardAnimator.setStartDelay(delay);
        cardAnimator.setInterpolator(new DecelerateInterpolator());
        cardAnimator.start();
    }

    private void animateStats() {
        statsContainer.setAlpha(0f);
        statsContainer.setTranslationX(-50f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(statsContainer, "alpha", 0f, 1f);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(statsContainer, "translationX", -50f, 0f);
        AnimatorSet statsAnimator = new AnimatorSet();
        statsAnimator.playTogether(alpha, translateX);
        statsAnimator.setDuration(600);
        statsAnimator.setStartDelay(1200);
        statsAnimator.start();
        animateStatsNumbers();
    }

    private void animateStatsNumbers() {
        int totalValue = 0;
        int procesoValue = 0;
        int completadasValue = 0;
        
        try {
            totalValue = Integer.parseInt(tvTotalAdopciones.getText().toString());
            procesoValue = Integer.parseInt(tvEnProceso.getText().toString());
            completadasValue = Integer.parseInt(tvCompletadas.getText().toString());
        } catch (NumberFormatException e) {
            // Keep default values of 0
        }
        
        ValueAnimator totalAnimator = ValueAnimator.ofInt(0, totalValue);
        totalAnimator.setDuration(1500);
        totalAnimator.setStartDelay(1400);
        totalAnimator.addUpdateListener(animation -> tvTotalAdopciones.setText(String.valueOf(animation.getAnimatedValue())));
        totalAnimator.start();
        
        ValueAnimator procesoAnimator = ValueAnimator.ofInt(0, procesoValue);
        procesoAnimator.setDuration(1500);
        procesoAnimator.setStartDelay(1500);
        procesoAnimator.addUpdateListener(animation -> tvEnProceso.setText(String.valueOf(animation.getAnimatedValue())));
        procesoAnimator.start();
        
        ValueAnimator completadasAnimator = ValueAnimator.ofInt(0, completadasValue);
        completadasAnimator.setDuration(1500);
        completadasAnimator.setStartDelay(1600);
        completadasAnimator.addUpdateListener(animation -> tvCompletadas.setText(String.valueOf(animation.getAnimatedValue())));
        completadasAnimator.start();
    }

    private void animateMotivationalCard() {
        motivationalCard.setAlpha(0f);
        motivationalCard.setTranslationY(50f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(motivationalCard, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(motivationalCard, "translationY", 50f, 0f);
        AnimatorSet motivationalAnimator = new AnimatorSet();
        motivationalAnimator.playTogether(alpha, translateY);
        motivationalAnimator.setDuration(600);
        motivationalAnimator.setStartDelay(1400);
        motivationalAnimator.start();
    }

    private void animateCardClick(View card) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0.95f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 0.95f, 1.05f, 1f);
        AnimatorSet clickAnimator = new AnimatorSet();
        clickAnimator.playTogether(scaleX, scaleY);
        clickAnimator.setDuration(300);
        clickAnimator.setInterpolator(new DecelerateInterpolator());
        clickAnimator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Solo iniciar si no hay una animación corriendo
        if (ivLogoPet != null && (heartbeatAnimator == null || !heartbeatAnimator.isRunning())) {
            startHeartbeatAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancelar todas las animaciones al pausar
        if (heartbeatAnimator != null && heartbeatAnimator.isRunning()) {
            heartbeatAnimator.cancel();
        }
        if (ivLogoPet != null) {
            ivLogoPet.clearAnimation();
        }
        if (cardsContainer != null) {
            cardsContainer.clearAnimation();
        }
    }
}