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
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.AdopcionUsuario;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MisAdopcionesActivity extends AppCompatActivity {

    // Vistas principales
    private LinearLayout logoContainer, contentContainer, layoutPaws, emptyStateContainer;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvEmptyMessage;
    private MaterialButton btnRegresar;
    private RecyclerView rvAdopciones;

    // Tabs
    private Chip chipEnProceso, chipHistorial, chipCanceladas;

    // Adapter
    private AdopcionesAdapter adapter;
    private List<AdopcionUsuario> adopcionesEnProceso;
    private List<AdopcionUsuario> adopcionesHistorial;
    private List<AdopcionUsuario> adopcionesCanceladas;
    private List<AdopcionUsuario> todasLasAdopciones;
    
    private SessionManager sessionManager;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_adopciones);

        sessionManager = new SessionManager(this);
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupBackButtonHandler();
        loadData();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        contentContainer = findViewById(R.id.contentContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        ivLogo = findViewById(R.id.ivLogo);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnRegresar = findViewById(R.id.btnRegresar);
        rvAdopciones = findViewById(R.id.rvAdopciones);
        chipEnProceso = findViewById(R.id.chipEnProceso);
        chipHistorial = findViewById(R.id.chipHistorial);
        chipCanceladas = findViewById(R.id.chipCanceladas);

        // Estado inicial de vistas
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        contentContainer.setAlpha(0f);
        contentContainer.setTranslationY(50f);
        emptyStateContainer.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new AdopcionesAdapter();
        rvAdopciones.setLayoutManager(new LinearLayoutManager(this));
        rvAdopciones.setAdapter(adapter);

        adapter.setOnItemClickListener(adopcion -> showAdopcionDetail(adopcion));
        adapter.setOnCancelClickListener(adopcion -> showCancelDialog(adopcion));
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> handleExitAnimation());

        chipEnProceso.setOnClickListener(v -> {
            selectChip(chipEnProceso);
            showAdopciones(adopcionesEnProceso);
            tvEmptyMessage.setText("No tienes adopciones en proceso");
        });

        chipHistorial.setOnClickListener(v -> {
            selectChip(chipHistorial);
            showAdopciones(adopcionesHistorial);
            tvEmptyMessage.setText("No tienes adopciones completadas");
        });

        chipCanceladas.setOnClickListener(v -> {
            selectChip(chipCanceladas);
            showAdopciones(adopcionesCanceladas);
            tvEmptyMessage.setText("No tienes adopciones canceladas");
        });
    }

    private void selectChip(Chip selectedChip) {
        chipEnProceso.setChecked(selectedChip == chipEnProceso);
        chipHistorial.setChecked(selectedChip == chipHistorial);
        chipCanceladas.setChecked(selectedChip == chipCanceladas);

        // Animar el chip seleccionado
        ObjectAnimator.ofFloat(selectedChip, "scaleX", 1f, 1.1f, 1f).setDuration(200).start();
        ObjectAnimator.ofFloat(selectedChip, "scaleY", 1f, 1.1f, 1f).setDuration(200).start();
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    private void loadData() {
        String cedula = sessionManager.getCedula();
        if (cedula == null) {
            showEmptyState();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<List<AdopcionUsuario>>> call = apiService.getAdopcionesPorUsuario(cedula);

        call.enqueue(new Callback<ApiResponse<List<AdopcionUsuario>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AdopcionUsuario>>> call, Response<ApiResponse<List<AdopcionUsuario>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AdopcionUsuario> adopciones = response.body().getData();
                    if (adopciones != null) {
                        processAdopciones(adopciones);
                    } else {
                        initializeEmptyLists();
                        showEmptyState();
                    }
                } else {
                    initializeEmptyLists();
                    Snackbar.make(findViewById(android.R.id.content), 
                            "Error al cargar adopciones", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AdopcionUsuario>>> call, Throwable t) {
                initializeEmptyLists();
                Snackbar.make(findViewById(android.R.id.content), 
                        "Error de conexión", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void processAdopciones(List<AdopcionUsuario> adopciones) {
        todasLasAdopciones = adopciones;
        adopcionesEnProceso = new ArrayList<>();
        adopcionesHistorial = new ArrayList<>();
        adopcionesCanceladas = new ArrayList<>();

        for (AdopcionUsuario adopcion : adopciones) {
            String estado = adopcion.getEstado();
            switch (estado) {
                case "SOLICITADA":
                case "EN_REVISION":
                case "EN_ESPERA":
                case "APROBADA":
                    adopcionesEnProceso.add(adopcion);
                    break;
                case "COMPLETADA":
                    adopcionesHistorial.add(adopcion);
                    break;
                case "RECHAZADA":
                case "CANCELADA":
                case "DEVUELTA":
                    adopcionesCanceladas.add(adopcion);
                    break;
            }
        }

        // Mostrar adopciones en proceso por defecto
        chipEnProceso.setChecked(true);
        showAdopciones(adopcionesEnProceso);
    }

    private void initializeEmptyLists() {
        todasLasAdopciones = new ArrayList<>();
        adopcionesEnProceso = new ArrayList<>();
        adopcionesHistorial = new ArrayList<>();
        adopcionesCanceladas = new ArrayList<>();
    }

    private void showEmptyState() {
        initializeEmptyLists();
        chipEnProceso.setChecked(true);
        showAdopciones(adopcionesEnProceso);
    }

    private void showAdopciones(List<AdopcionUsuario> adopciones) {
        if (adopciones.isEmpty()) {
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            animateEmptyState(false);
            animateRecyclerView(true);
            
            // Ensure RecyclerView is properly set up
            rvAdopciones.setVisibility(View.VISIBLE);
            adapter.setAdopciones(adopciones);
            adapter.notifyDataSetChanged();
        }
    }

    private void animateEmptyState(boolean show) {
        // Clear any existing animations to avoid conflicts
        emptyStateContainer.animate().cancel();
        emptyStateContainer.clearAnimation();
        
        if (show) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            emptyStateContainer.setAlpha(0f);
            emptyStateContainer.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null)
                    .start();
        } else {
            emptyStateContainer.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            emptyStateContainer.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void animateRecyclerView(boolean show) {
        // Clear any existing animations to avoid conflicts
        rvAdopciones.animate().cancel();
        rvAdopciones.clearAnimation();
        
        if (show) {
            rvAdopciones.setVisibility(View.VISIBLE);
            rvAdopciones.setAlpha(0f);
            rvAdopciones.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null) // Clear any previous listeners
                    .start();
        } else {
            rvAdopciones.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rvAdopciones.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void showAdopcionDetail(AdopcionUsuario adopcion) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adopcion_detail, null);

        TextView tvMascotaNombre = dialogView.findViewById(R.id.tvMascotaNombre);
        TextView tvMascotaRaza = dialogView.findViewById(R.id.tvMascotaRaza);
        TextView tvEstado = dialogView.findViewById(R.id.tvEstado);
        TextView tvFechaSolicitud = dialogView.findViewById(R.id.tvFechaSolicitud);
        TextView tvFechaResolucion = dialogView.findViewById(R.id.tvFechaResolucion);
        TextView tvDetalles = dialogView.findViewById(R.id.tvDetalles);
        LinearLayout layoutFechaResolucion = dialogView.findViewById(R.id.layoutFechaResolucion);

        tvMascotaNombre.setText(adopcion.getNombreAnimal());
        tvMascotaRaza.setText(""); // No hay raza en el modelo actual
        tvEstado.setText(adopcion.getEstadoDisplayText());
        tvFechaSolicitud.setText("Fecha de solicitud: " + formatearFecha(adopcion.getFechaSolicitud()));
        
        // Mostrar detalles según el estado
        String detalles = getDetallesSegunEstado(adopcion);
        tvDetalles.setText(detalles);

        // Mostrar fecha relevante según el estado
        String fechaRelevante = adopcion.getFechaRelevante();
        if (fechaRelevante != null && !fechaRelevante.equals(adopcion.getFechaSolicitud())) {
            layoutFechaResolucion.setVisibility(View.VISIBLE);
            tvFechaResolucion.setText(getTituloFechaSegunEstado(adopcion.getEstado()) + ": " + formatearFecha(fechaRelevante));
        } else {
            layoutFechaResolucion.setVisibility(View.GONE);
        }

        // Colorear según estado usando el color del helper
        String colorHex = adopcion.getEstadoColor();
        int color = android.graphics.Color.parseColor(colorHex);
        tvEstado.setTextColor(color);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Cerrar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Animar entrada del diálogo
        dialogView.setScaleX(0.8f);
        dialogView.setScaleY(0.8f);
        dialogView.setAlpha(0f);
        dialogView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void showCancelDialog(AdopcionUsuario adopcion) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancelar adopción")
                .setMessage("¿Estás seguro de que deseas cancelar el proceso de adopción de " +
                        adopcion.getNombreAnimal() + "?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    cancelarAdopcion(adopcion);
                })
                .setNegativeButton("No, mantener", null)
                .show();
    }

    private void cancelarAdopcion(AdopcionUsuario adopcion) {
        // En una implementación completa, aquí se haría una llamada a la API para cancelar
        // Por ahora, solo mostramos un mensaje informativo
        
        Snackbar.make(contentContainer, 
                "Para cancelar esta adopción, por favor contacta a la fundación.", 
                Snackbar.LENGTH_LONG).show();
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
                .setStartDelay(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        contentContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        handler.postDelayed(this::animatePaws, 700);
    }

    private void animatePaws() {
        animatePaw(paw1, 0, () ->
                animatePaw(paw2, 150, () ->
                        animatePaw(paw3, 150, this::startPawGlowAnimation)));
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

    private void handleExitAnimation() {
        AnimatorSet exitSet = new AnimatorSet();

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator logoTranslation = ObjectAnimator.ofFloat(logoContainer, "translationY", 0f, -50f);
        ObjectAnimator contentAlpha = ObjectAnimator.ofFloat(contentContainer, "alpha", 1f, 0f);
        ObjectAnimator contentTranslation = ObjectAnimator.ofFloat(contentContainer, "translationY", 0f, 50f);

        exitSet.playTogether(logoAlpha, logoTranslation, contentAlpha, contentTranslation);
        exitSet.setDuration(300);
        exitSet.setInterpolator(new AccelerateDecelerateInterpolator());
        exitSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        exitSet.start();
    }
    
    // Helper methods
    private String formatearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
            Date fecha = inputFormat.parse(fechaStr);
            return fecha != null ? outputFormat.format(fecha) : fechaStr.split(" ")[0];
        } catch (Exception e) {
            return fechaStr.split(" ")[0];
        }
    }
    
    private String getDetallesSegunEstado(AdopcionUsuario adopcion) {
        switch (adopcion.getEstado()) {
            case "SOLICITADA":
                return "Tu solicitud de adopción ha sido enviada y está esperando revisión.";
            case "EN_REVISION":
                return "Tu solicitud está siendo revisada por el equipo de la fundación.";
            case "EN_ESPERA":
                return "Tu solicitud está en espera de procesamiento.";
            case "APROBADA":
                return "¡Felicidades! Tu solicitud ha sido aprobada. Pronto te contactarán para coordinar la entrega.";
            case "COMPLETADA":
                return "¡Adopción completada exitosamente! Gracias por darle un hogar a esta mascota.";
            case "RECHAZADA":
                return "Tu solicitud ha sido rechazada. Puedes intentar con otra mascota.";
            case "CANCELADA":
                return "Esta adopción ha sido cancelada.";
            case "DEVUELTA":
                return "La mascota ha sido devuelta a la fundación.";
            default:
                return "Estado de adopción: " + adopcion.getEstadoDisplayText();
        }
    }
    
    private String getTituloFechaSegunEstado(String estado) {
        switch (estado) {
            case "APROBADA":
                return "Fecha de aprobación";
            case "COMPLETADA":
                return "Fecha de completado";
            case "RECHAZADA":
            case "CANCELADA":
                return "Fecha de cancelación";
            case "DEVUELTA":
                return "Fecha de devolución";
            default:
                return "Fecha de actualización";
        }
    }
}