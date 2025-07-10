package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FundacionesActivity extends AppCompatActivity {

    // Vistas principales
    private LinearLayout logoContainer, contentContainer, layoutPaws, emptyStateContainer;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvEmptyMessage, tvResultCount;
    private MaterialButton btnRegresar;
    private RecyclerView rvFundaciones;
    private TextInputLayout tilBuscar;
    private TextInputEditText etBuscar;

    // Chips para filtros
    private Chip chipTodas, chipPerros, chipGatos, chipOtros;

    // Adapter y listas
    private FundacionesAdapter adapter;
    private List<Fundacion> fundacionesCompletas;
    private List<Fundacion> fundacionesFiltradas;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private String filtroActual = "TODAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundaciones);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupBackButtonHandler();
        setupSearchFilter();
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
        tvResultCount = findViewById(R.id.tvResultCount);
        btnRegresar = findViewById(R.id.btnRegresar);
        rvFundaciones = findViewById(R.id.rvFundaciones);
        tilBuscar = findViewById(R.id.tilBuscar);
        etBuscar = findViewById(R.id.etBuscar);
        chipTodas = findViewById(R.id.chipTodas);
        chipPerros = findViewById(R.id.chipPerros);
        chipGatos = findViewById(R.id.chipGatos);
        chipOtros = findViewById(R.id.chipOtros);

        // Estado inicial de vistas
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        contentContainer.setAlpha(0f);
        contentContainer.setTranslationY(50f);
        emptyStateContainer.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new FundacionesAdapter();
        rvFundaciones.setLayoutManager(new LinearLayoutManager(this));
        rvFundaciones.setAdapter(adapter);

        adapter.setOnItemClickListener(fundacion -> showFundacionDetail(fundacion));
        adapter.setOnContactClickListener(fundacion -> contactFundacion(fundacion));
        adapter.setOnLocationClickListener(fundacion -> openLocation(fundacion));
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> handleExitAnimation());

        chipTodas.setOnClickListener(v -> {
            selectChip(chipTodas);
            filtroActual = "TODAS";
            applyFilters();
        });

        chipPerros.setOnClickListener(v -> {
            selectChip(chipPerros);
            filtroActual = "PERROS";
            applyFilters();
        });

        chipGatos.setOnClickListener(v -> {
            selectChip(chipGatos);
            filtroActual = "GATOS";
            applyFilters();
        });

        chipOtros.setOnClickListener(v -> {
            selectChip(chipOtros);
            filtroActual = "OTROS";
            applyFilters();
        });
    }

    private void selectChip(Chip selectedChip) {
        chipTodas.setChecked(selectedChip == chipTodas);
        chipPerros.setChecked(selectedChip == chipPerros);
        chipGatos.setChecked(selectedChip == chipGatos);
        chipOtros.setChecked(selectedChip == chipOtros);

        // Animar el chip seleccionado
        ObjectAnimator.ofFloat(selectedChip, "scaleX", 1f, 1.1f, 1f).setDuration(200).start();
        ObjectAnimator.ofFloat(selectedChip, "scaleY", 1f, 1.1f, 1f).setDuration(200).start();
    }

    private void setupSearchFilter() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        String searchQuery = etBuscar.getText().toString().toLowerCase(Locale.getDefault()).trim();
        fundacionesFiltradas = new ArrayList<>();

        for (Fundacion fundacion : fundacionesCompletas) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    fundacion.getNombre().toLowerCase(Locale.getDefault()).contains(searchQuery) ||
                    fundacion.getUbicacion().toLowerCase(Locale.getDefault()).contains(searchQuery);

            boolean matchesFilter = false;
            switch (filtroActual) {
                case "TODAS":
                    matchesFilter = true;
                    break;
                case "PERROS":
                    matchesFilter = fundacion.isTienePerros();
                    break;
                case "GATOS":
                    matchesFilter = fundacion.isTieneGatos();
                    break;
                case "OTROS":
                    matchesFilter = fundacion.isTieneOtros();
                    break;
            }

            if (matchesSearch && matchesFilter) {
                fundacionesFiltradas.add(fundacion);
            }
        }

        updateResultCount();
        showFundaciones(fundacionesFiltradas);
    }

    private void updateResultCount() {
        String texto = fundacionesFiltradas.size() + " fundación" +
                (fundacionesFiltradas.size() != 1 ? "es" : "") + " encontrada" +
                (fundacionesFiltradas.size() != 1 ? "s" : "");
        tvResultCount.setText(texto);

        // Animar el contador
        tvResultCount.setScaleX(0.8f);
        tvResultCount.setScaleY(0.8f);
        tvResultCount.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();
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
        // Simulación de datos - En producción vendría de una base de datos o API
        fundacionesCompletas = new ArrayList<>();

        fundacionesCompletas.add(new Fundacion(
                1,
                "Fundación Patitas Felices",
                "Av. Simón Bolívar y Galo Plaza Lasso, Quito",
                "+593 2 234 5678",
                "info@patitasfelices.org",
                "Rescatamos y cuidamos animales abandonados, brindándoles amor y buscándoles un hogar.",
                120,
                45,
                20,
                true, true, false,
                "Lunes a Viernes: 9:00 - 18:00\nSábados: 9:00 - 14:00",
                -0.1807, -78.4678
        ));

        fundacionesCompletas.add(new Fundacion(
                2,
                "Refugio Animal Esperanza",
                "Calle Rafael Ramos y 10 de Agosto, Guayaquil",
                "+593 4 245 6789",
                "contacto@esperanzaanimal.org",
                "Dedicados al rescate y rehabilitación de animales en situación de calle.",
                85,
                30,
                10,
                true, true, true,
                "Lunes a Sábado: 8:00 - 17:00\nDomingos: 9:00 - 13:00",
                -2.1894, -79.8891
        ));

        fundacionesCompletas.add(new Fundacion(
                3,
                "Protectora de Animales Cayambe",
                "Av. Natalia Jarrín y Restauración, Cayambe",
                "+593 2 236 0123",
                "protectora@cayambe.org",
                "Protegemos a los animales vulnerables de nuestra comunidad.",
                40,
                25,
                15,
                true, true, true,
                "Martes a Domingo: 9:00 - 16:00",
                0.0411, -78.1437
        ));

        fundacionesCompletas.add(new Fundacion(
                4,
                "Fundación Huellitas con Amor",
                "Calle Larga y Hermano Miguel, Cuenca",
                "+593 7 282 3456",
                "huellitas@cuenca.org",
                "Brindamos segunda oportunidad a mascotas abandonadas con mucho amor.",
                65,
                35,
                5,
                true, true, false,
                "Lunes a Viernes: 8:30 - 17:30\nSábados: 9:00 - 13:00",
                -2.9001, -79.0059
        ));

        fundacionesCompletas.add(new Fundacion(
                5,
                "Centro de Rescate Felino",
                "Av. González Suárez y Coruña, Quito",
                "+593 2 254 7890",
                "gatitos@rescatefelino.org",
                "Especialistas en rescate y adopción de gatos.",
                0,
                80,
                0,
                false, true, false,
                "Lunes a Sábado: 10:00 - 18:00",
                -0.2025, -78.4918
        ));

        // Aplicar filtros iniciales
        fundacionesFiltradas = new ArrayList<>(fundacionesCompletas);
        chipTodas.setChecked(true);
        updateResultCount();
        showFundaciones(fundacionesFiltradas);
    }

    private void showFundaciones(List<Fundacion> fundaciones) {
        if (fundaciones.isEmpty()) {
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            animateEmptyState(false);
            animateRecyclerView(true);
            adapter.setFundaciones(fundaciones);
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
            rvFundaciones.setVisibility(View.VISIBLE);
            rvFundaciones.setAlpha(0f);
            rvFundaciones.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        } else {
            rvFundaciones.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rvFundaciones.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void showFundacionDetail(Fundacion fundacion) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fundacion_detail, null);

        TextView tvNombre = dialogView.findViewById(R.id.tvNombre);
        TextView tvDescripcion = dialogView.findViewById(R.id.tvDescripcion);
        TextView tvUbicacion = dialogView.findViewById(R.id.tvUbicacion);
        TextView tvTelefono = dialogView.findViewById(R.id.tvTelefono);
        TextView tvEmail = dialogView.findViewById(R.id.tvEmail);
        TextView tvHorario = dialogView.findViewById(R.id.tvHorario);
        TextView tvTotalAnimales = dialogView.findViewById(R.id.tvTotalAnimales);
        TextView tvPerros = dialogView.findViewById(R.id.tvPerros);
        TextView tvGatos = dialogView.findViewById(R.id.tvGatos);
        TextView tvOtros = dialogView.findViewById(R.id.tvOtros);
        LinearLayout layoutEstadisticas = dialogView.findViewById(R.id.layoutEstadisticas);

        tvNombre.setText(fundacion.getNombre());
        tvDescripcion.setText(fundacion.getDescripcion());
        tvUbicacion.setText(fundacion.getUbicacion());
        tvTelefono.setText(fundacion.getTelefono());
        tvEmail.setText(fundacion.getEmail());
        tvHorario.setText(fundacion.getHorario());

        int totalAnimales = fundacion.getCantidadPerros() + fundacion.getCantidadGatos() + fundacion.getCantidadOtros();
        tvTotalAnimales.setText("Total de animales: " + totalAnimales);
        tvPerros.setText("Perros: " + fundacion.getCantidadPerros());
        tvGatos.setText("Gatos: " + fundacion.getCantidadGatos());
        tvOtros.setText("Otros: " + fundacion.getCantidadOtros());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .setNeutralButton("Contactar", (dialog, which) -> contactFundacion(fundacion));

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

        // Animar las estadísticas
        animateStats(layoutEstadisticas);
    }

    private void animateStats(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            child.setAlpha(0f);
            child.setTranslationX(-30f);
            child.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(300)
                    .setStartDelay(i * 50L)
                    .start();
        }
    }

    private void contactFundacion(Fundacion fundacion) {
        CharSequence[] options = {"Llamar", "Enviar email"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Contactar fundación")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Llamar
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + fundacion.getTelefono()));
                        startActivity(intent);
                    } else {
                        // Email
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + fundacion.getEmail()));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre adopción");
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void openLocation(Fundacion fundacion) {
        Uri gmmIntentUri = Uri.parse("geo:" + fundacion.getLatitud() + "," + fundacion.getLongitud() +
                "?q=" + Uri.encode(fundacion.getNombre() + ", " + fundacion.getUbicacion()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Si no tiene Google Maps, abrir en el navegador
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                    Uri.encode(fundacion.getNombre() + ", " + fundacion.getUbicacion()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
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

    // Clase modelo Fundacion
    static class Fundacion {
        private int id;
        private String nombre;
        private String ubicacion;
        private String telefono;
        private String email;
        private String descripcion;
        private int cantidadPerros;
        private int cantidadGatos;
        private int cantidadOtros;
        private boolean tienePerros;
        private boolean tieneGatos;
        private boolean tieneOtros;
        private String horario;
        private double latitud;
        private double longitud;

        public Fundacion(int id, String nombre, String ubicacion, String telefono, String email,
                         String descripcion, int cantidadPerros, int cantidadGatos, int cantidadOtros,
                         boolean tienePerros, boolean tieneGatos, boolean tieneOtros,
                         String horario, double latitud, double longitud) {
            this.id = id;
            this.nombre = nombre;
            this.ubicacion = ubicacion;
            this.telefono = telefono;
            this.email = email;
            this.descripcion = descripcion;
            this.cantidadPerros = cantidadPerros;
            this.cantidadGatos = cantidadGatos;
            this.cantidadOtros = cantidadOtros;
            this.tienePerros = tienePerros;
            this.tieneGatos = tieneGatos;
            this.tieneOtros = tieneOtros;
            this.horario = horario;
            this.latitud = latitud;
            this.longitud = longitud;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getUbicacion() { return ubicacion; }
        public String getTelefono() { return telefono; }
        public String getEmail() { return email; }
        public String getDescripcion() { return descripcion; }
        public int getCantidadPerros() { return cantidadPerros; }
        public int getCantidadGatos() { return cantidadGatos; }
        public int getCantidadOtros() { return cantidadOtros; }
        public boolean isTienePerros() { return tienePerros; }
        public boolean isTieneGatos() { return tieneGatos; }
        public boolean isTieneOtros() { return tieneOtros; }
        public String getHorario() { return horario; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
    }
}