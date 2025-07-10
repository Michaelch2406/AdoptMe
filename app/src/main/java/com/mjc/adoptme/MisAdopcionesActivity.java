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
    private List<Adopcion> adopcionesEnProceso;
    private List<Adopcion> adopcionesHistorial;
    private List<Adopcion> adopcionesCanceladas;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_adopciones);

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
        // Simulación de datos - En producción vendría de una base de datos
        adopcionesEnProceso = new ArrayList<>();
        adopcionesHistorial = new ArrayList<>();
        adopcionesCanceladas = new ArrayList<>();

        // Datos de ejemplo - En Proceso
        adopcionesEnProceso.add(new Adopcion(
                1, "Max", "Golden Retriever",
                "En revisión de documentos",
                "2024-03-15", null,
                EstadoAdopcion.EN_PROCESO,
                "Tu solicitud está siendo revisada por nuestro equipo."
        ));

        adopcionesEnProceso.add(new Adopcion(
                2, "Luna", "Gato Siamés",
                "Esperando visita domiciliaria",
                "2024-03-10", null,
                EstadoAdopcion.EN_PROCESO,
                "Próxima visita programada para el 20 de marzo."
        ));

        // Datos de ejemplo - Historial
        adopcionesHistorial.add(new Adopcion(
                3, "Rocky", "Bulldog Francés",
                "Adopción completada",
                "2024-01-05", "2024-02-01",
                EstadoAdopcion.COMPLETADA,
                "¡Felicidades! Rocky encontró su hogar."
        ));

        // Datos de ejemplo - Canceladas
        adopcionesCanceladas.add(new Adopcion(
                4, "Bella", "Pastor Alemán",
                "Cancelada por el usuario",
                "2024-02-20", "2024-02-25",
                EstadoAdopcion.CANCELADA,
                "Adopción cancelada a petición del solicitante."
        ));

        // Mostrar adopciones en proceso por defecto
        chipEnProceso.setChecked(true);
        showAdopciones(adopcionesEnProceso);
    }

    private void showAdopciones(List<Adopcion> adopciones) {
        if (adopciones.isEmpty()) {
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            animateEmptyState(false);
            animateRecyclerView(true);
            adapter.setAdopciones(adopciones);
        }
    }

    private void animateEmptyState(boolean show) {
        if (show) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            emptyStateContainer.setAlpha(0f);
            emptyStateContainer.animate()
                    .alpha(1f)
                    .setDuration(300)
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
        if (show) {
            rvAdopciones.setVisibility(View.VISIBLE);
            rvAdopciones.setAlpha(0f);
            rvAdopciones.animate()
                    .alpha(1f)
                    .setDuration(300)
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

    private void showAdopcionDetail(Adopcion adopcion) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adopcion_detail, null);

        TextView tvMascotaNombre = dialogView.findViewById(R.id.tvMascotaNombre);
        TextView tvMascotaRaza = dialogView.findViewById(R.id.tvMascotaRaza);
        TextView tvEstado = dialogView.findViewById(R.id.tvEstado);
        TextView tvFechaSolicitud = dialogView.findViewById(R.id.tvFechaSolicitud);
        TextView tvFechaResolucion = dialogView.findViewById(R.id.tvFechaResolucion);
        TextView tvDetalles = dialogView.findViewById(R.id.tvDetalles);
        LinearLayout layoutFechaResolucion = dialogView.findViewById(R.id.layoutFechaResolucion);

        tvMascotaNombre.setText(adopcion.getNombreMascota());
        tvMascotaRaza.setText(adopcion.getRaza());
        tvEstado.setText(adopcion.getEstadoDescripcion());
        tvFechaSolicitud.setText("Fecha de solicitud: " + adopcion.getFechaSolicitud());
        tvDetalles.setText(adopcion.getDetalles());

        if (adopcion.getFechaResolucion() != null) {
            layoutFechaResolucion.setVisibility(View.VISIBLE);
            tvFechaResolucion.setText("Fecha de resolución: " + adopcion.getFechaResolucion());
        } else {
            layoutFechaResolucion.setVisibility(View.GONE);
        }

        // Colorear según estado
        int colorResId;
        switch (adopcion.getEstado()) {
            case EN_PROCESO:
                colorResId = R.color.colorAccent2;
                break;
            case COMPLETADA:
                colorResId = R.color.colorPrimary;
                break;
            case CANCELADA:
                colorResId = R.color.colorAccent3;
                break;
            default:
                colorResId = R.color.black;
        }
        tvEstado.setTextColor(ContextCompat.getColor(this, colorResId));

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

    private void showCancelDialog(Adopcion adopcion) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancelar adopción")
                .setMessage("¿Estás seguro de que deseas cancelar el proceso de adopción de " +
                        adopcion.getNombreMascota() + "?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    cancelarAdopcion(adopcion);
                })
                .setNegativeButton("No, mantener", null)
                .show();
    }

    private void cancelarAdopcion(Adopcion adopcion) {
        // Animar la eliminación del item
        int position = adopcionesEnProceso.indexOf(adopcion);
        if (position != -1) {
            adopcionesEnProceso.remove(position);

            // Actualizar estado y mover a canceladas
            adopcion.setEstado(EstadoAdopcion.CANCELADA);
            adopcion.setEstadoDescripcion("Cancelada por el usuario");
            adopcion.setFechaResolucion(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            adopcionesCanceladas.add(adopcion);

            adapter.notifyItemRemoved(position);

            if (adopcionesEnProceso.isEmpty()) {
                animateEmptyState(true);
                animateRecyclerView(false);
            }

            Snackbar.make(contentContainer, "Adopción cancelada", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer", v -> {
                        // Deshacer la cancelación
                        adopcionesCanceladas.remove(adopcion);
                        adopcion.setEstado(EstadoAdopcion.EN_PROCESO);
                        adopcion.setFechaResolucion(null);
                        adopcionesEnProceso.add(position, adopcion);
                        adapter.notifyItemInserted(position);

                        if (!adopcionesEnProceso.isEmpty()) {
                            animateEmptyState(false);
                            animateRecyclerView(true);
                        }
                    })
                    .show();
        }
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

    // Enum para estados
    enum EstadoAdopcion {
        EN_PROCESO, COMPLETADA, CANCELADA
    }

    // Clase modelo Adopcion
    static class Adopcion {
        private int id;
        private String nombreMascota;
        private String raza;
        private String estadoDescripcion;
        private String fechaSolicitud;
        private String fechaResolucion;
        private EstadoAdopcion estado;
        private String detalles;

        public Adopcion(int id, String nombreMascota, String raza, String estadoDescripcion,
                        String fechaSolicitud, String fechaResolucion, EstadoAdopcion estado,
                        String detalles) {
            this.id = id;
            this.nombreMascota = nombreMascota;
            this.raza = raza;
            this.estadoDescripcion = estadoDescripcion;
            this.fechaSolicitud = fechaSolicitud;
            this.fechaResolucion = fechaResolucion;
            this.estado = estado;
            this.detalles = detalles;
        }

        // Getters y setters
        public int getId() { return id; }
        public String getNombreMascota() { return nombreMascota; }
        public String getRaza() { return raza; }
        public String getEstadoDescripcion() { return estadoDescripcion; }
        public String getFechaSolicitud() { return fechaSolicitud; }
        public String getFechaResolucion() { return fechaResolucion; }
        public EstadoAdopcion getEstado() { return estado; }
        public String getDetalles() { return detalles; }

        public void setEstado(EstadoAdopcion estado) { this.estado = estado; }
        public void setEstadoDescripcion(String estadoDescripcion) { this.estadoDescripcion = estadoDescripcion; }
        public void setFechaResolucion(String fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    }
}