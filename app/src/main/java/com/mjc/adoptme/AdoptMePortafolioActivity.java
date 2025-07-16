package com.mjc.adoptme;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.adapters.AnimalesPortafolioAdapter;
import com.mjc.adoptme.models.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdoptMePortafolioActivity extends AppCompatActivity {

    // Vistas principales
    private LinearLayout logoContainer, contentContainer, layoutPaws, emptyStateContainer;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvEmptyMessage, tvResultCount, tvLocationInfo;
    private MaterialButton btnRegresar;
    private FloatingActionButton fabFilter;
    private RecyclerView rvAnimales;
    private TextInputLayout tilBuscar;
    private TextInputEditText etBuscar;

    // Chips para filtros rápidos
    private Chip chipTodos, chipPerros, chipGatos, chipCachorros, chipAdultos;

    // Adapter y listas
    private AnimalesPortafolioAdapter adapter;
    private List<Animal> animalesCompletos = new ArrayList<>();
    private List<Animal> animalesFiltrados = new ArrayList<>();

    // Filtros
    private String filtroTipo = "TODOS";
    private String filtroEdad = "TODOS";
    private float distanciaMaxima = 50f; // km
    private String filtroTamaño = "TODOS";
    private String filtroSexo = "TODOS";

    // Ubicación
    private Location ubicacionActual;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_me_portafolio);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupBackButtonHandler();
        setupSearchFilter();
        checkLocationPermission();
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
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        btnRegresar = findViewById(R.id.btnRegresar);
        fabFilter = findViewById(R.id.fabFilter);
        rvAnimales = findViewById(R.id.rvAnimales);
        tilBuscar = findViewById(R.id.tilBuscar);
        etBuscar = findViewById(R.id.etBuscar);
        chipTodos = findViewById(R.id.chipTodos);
        chipPerros = findViewById(R.id.chipPerros);
        chipGatos = findViewById(R.id.chipGatos);
        chipCachorros = findViewById(R.id.chipCachorros);
        chipAdultos = findViewById(R.id.chipAdultos);

        // Estado inicial de vistas
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        contentContainer.setAlpha(0f);
        contentContainer.setTranslationY(50f);
        emptyStateContainer.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new AnimalesPortafolioAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvAnimales.setLayoutManager(layoutManager);
        rvAnimales.setAdapter(adapter);

        adapter.setOnItemClickListener(animal -> showAnimalDetail(animal));
        adapter.setOnAdoptClickListener(animal -> startAdoptionProcess(animal));
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> handleExitAnimation());
        fabFilter.setOnClickListener(v -> showAdvancedFilters());

        chipTodos.setOnClickListener(v -> {
            selectTipoChip(chipTodos);
            filtroTipo = "TODOS";
            applyFilters();
        });

        chipPerros.setOnClickListener(v -> {
            selectTipoChip(chipPerros);
            filtroTipo = "PERRO";
            applyFilters();
        });

        chipGatos.setOnClickListener(v -> {
            selectTipoChip(chipGatos);
            filtroTipo = "GATO";
            applyFilters();
        });

        chipCachorros.setOnClickListener(v -> {
            selectEdadChip(chipCachorros);
            filtroEdad = "CACHORRO";
            applyFilters();
        });

        chipAdultos.setOnClickListener(v -> {
            selectEdadChip(chipAdultos);
            filtroEdad = "ADULTO";
            applyFilters();
        });
    }

    private void selectTipoChip(Chip selectedChip) {
        chipTodos.setChecked(selectedChip == chipTodos);
        chipPerros.setChecked(selectedChip == chipPerros);
        chipGatos.setChecked(selectedChip == chipGatos);

        animateChipSelection(selectedChip);
    }

    private void selectEdadChip(Chip selectedChip) {
        if (selectedChip.isChecked()) {
            // Si ya está seleccionado, deseleccionar
            selectedChip.setChecked(false);
            filtroEdad = "TODOS";
        } else {
            // Deseleccionar el otro chip
            chipCachorros.setChecked(selectedChip == chipCachorros);
            chipAdultos.setChecked(selectedChip == chipAdultos);
        }

        animateChipSelection(selectedChip);
    }

    private void animateChipSelection(Chip chip) {
        ObjectAnimator.ofFloat(chip, "scaleX", 1f, 1.1f, 1f).setDuration(200).start();
        ObjectAnimator.ofFloat(chip, "scaleY", 1f, 1.1f, 1f).setDuration(200).start();
    }

    private void setupSearchFilter() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        // Simulación de ubicación en Cayambe
        ubicacionActual = new Location("");
        ubicacionActual.setLatitude(0.0411);
        ubicacionActual.setLongitude(-78.1437);

        tvLocationInfo.setText("📍 Cayambe, Ecuador");
        tvLocationInfo.setVisibility(View.VISIBLE);

        // Animar entrada del texto de ubicación
        tvLocationInfo.setAlpha(0f);
        tvLocationInfo.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        applyFilters();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                tvLocationInfo.setText("📍 Ubicación no disponible");
                tvLocationInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showAdvancedFilters() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_avanzados, null);

        RangeSlider sliderDistancia = dialogView.findViewById(R.id.sliderDistancia);
        TextView tvDistanciaValue = dialogView.findViewById(R.id.tvDistanciaValue);
        Spinner spinnerTamaño = dialogView.findViewById(R.id.spinnerTamaño);
        Spinner spinnerSexo = dialogView.findViewById(R.id.spinnerSexo);

        // Configurar slider de distancia
        sliderDistancia.setValues(distanciaMaxima);
        tvDistanciaValue.setText(String.format(Locale.getDefault(), "%.0f km", distanciaMaxima));

        sliderDistancia.addOnChangeListener((slider, value, fromUser) -> {
            tvDistanciaValue.setText(String.format(Locale.getDefault(), "%.0f km", value));
        });

        // Configurar spinners
        ArrayAdapter<String> tamañoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos los tamaños", "Pequeño", "Mediano", "Grande"});
        tamañoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTamaño.setAdapter(tamañoAdapter);

        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Macho", "Hembra"});
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexo.setAdapter(sexoAdapter);

        // Establecer valores actuales
        switch (filtroTamaño) {
            case "PEQUEÑO":
                spinnerTamaño.setSelection(1);
                break;
            case "MEDIANO":
                spinnerTamaño.setSelection(2);
                break;
            case "GRANDE":
                spinnerTamaño.setSelection(3);
                break;
            default:
                spinnerTamaño.setSelection(0);
        }

        switch (filtroSexo) {
            case "MACHO":
                spinnerSexo.setSelection(1);
                break;
            case "HEMBRA":
                spinnerSexo.setSelection(2);
                break;
            default:
                spinnerSexo.setSelection(0);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Filtros avanzados")
                .setView(dialogView)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    distanciaMaxima = sliderDistancia.getValues().get(0);

                    int tamañoPos = spinnerTamaño.getSelectedItemPosition();
                    filtroTamaño = tamañoPos == 0 ? "TODOS" :
                            tamañoPos == 1 ? "PEQUEÑO" :
                                    tamañoPos == 2 ? "MEDIANO" : "GRANDE";

                    int sexoPos = spinnerSexo.getSelectedItemPosition();
                    filtroSexo = sexoPos == 0 ? "TODOS" :
                            sexoPos == 1 ? "MACHO" : "HEMBRA";

                    applyFilters();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Restablecer", (dialog, which) -> {
                    distanciaMaxima = 50f;
                    filtroTamaño = "TODOS";
                    filtroSexo = "TODOS";
                    applyFilters();
                });

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

    private void applyFilters() {
        String searchQuery = etBuscar.getText().toString().toLowerCase(Locale.getDefault()).trim();
        animalesFiltrados = new ArrayList<>();

        for (Animal animal : animalesCompletos) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    (animal.getNombre() != null && animal.getNombre().toLowerCase(Locale.getDefault()).contains(searchQuery)) ||
                    (animal.getRazaNombre() != null && animal.getRazaNombre().toLowerCase(Locale.getDefault()).contains(searchQuery));

            boolean matchesTipo = filtroTipo.equals("TODOS") || filtroTipo.equals(animal.getTipoAnimalNombre());
            boolean matchesEdad = filtroEdad.equals("TODOS") || filtroEdad.equals(animal.getEdadEtiqueta());
            boolean matchesTamaño = filtroTamaño.equals("TODOS") || filtroTamaño.equals(animal.getTamaño());
            boolean matchesSexo = filtroSexo.equals("TODOS") || filtroSexo.equals(animal.getGenero());

            boolean matchesDistancia = true;
            if (ubicacionActual != null && animal.getLatitud() != 0 && animal.getLongitud() != 0) {
                Location animalLocation = new Location("");
                animalLocation.setLatitude(animal.getLatitud());
                animalLocation.setLongitude(animal.getLongitud());
                float distancia = ubicacionActual.distanceTo(animalLocation) / 1000;
                matchesDistancia = distancia <= distanciaMaxima;
                animal.setDistancia(distancia);
            }

            if (matchesSearch && matchesTipo && matchesEdad && matchesTamaño && matchesSexo && matchesDistancia) {
                animalesFiltrados.add(animal);
            }
        }

        if (ubicacionActual != null) {
            animalesFiltrados.sort((a1, a2) -> Float.compare(a1.getDistancia(), a2.getDistancia()));
        }

        updateResultCount();
        showAnimales(animalesFiltrados);
    }

    private void updateResultCount() {
        String texto = animalesFiltrados.size() + " mascota" +
                (animalesFiltrados.size() != 1 ? "s" : "") + " encontrada" +
                (animalesFiltrados.size() != 1 ? "s" : "");
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
        animalesCompletos = new ArrayList<>();

        // Datos de ejemplo usando el NUEVO constructor corregido
        animalesCompletos.add(new Animal(
                1, "Max", "Golden Retriever", "PERRO", 24, "ADULTO",
                "GRANDE", "MACHO", "Muy amigable y juguetón. Le encanta correr y jugar con niños.",
                "Fundación Patitas Felices", "Quito",
                true, true, true, true, false,
                "https://example.com/max.jpg", -0.1807, -78.4678, 0f
        ));

        animalesCompletos.add(new Animal(
                2, "Luna", "Siamés", "GATO", 8, "CACHORRO",
                "PEQUEÑO", "HEMBRA", "Gatita cariñosa y tranquila. Ideal para departamentos.",
                "Refugio Animal Esperanza", "Cayambe",
                true, false, true, true, false,
                "https://example.com/luna.jpg", 0.0411, -78.1437, 0f
        ));

        animalesCompletos.add(new Animal(
                3, "Rocky", "Mestizo", "PERRO", 36, "ADULTO",
                "MEDIANO", "MACHO", "Perro guardián, muy leal y protector.",
                "Protectora de Animales Cayambe", "Cayambe",
                true, true, false, true, false,
                "https://example.com/rocky.jpg", 0.0450, -78.1400, 0f
        ));

        animalesCompletos.add(new Animal(
                4, "Mia", "Persa", "GATO", 48, "ADULTO",
                "PEQUEÑO", "HEMBRA", "Gata adulta muy tranquila, perfecta para personas mayores.",
                "Centro de Rescate Felino", "Quito",
                true, false, true, false, true,
                "https://example.com/mia.jpg", -0.2025, -78.4918, 0f
        ));

        animalesCompletos.add(new Animal(
                5, "Bruno", "Labrador", "PERRO", 72, "ADULTO",
                "GRANDE", "MACHO", "Perro adulto con mucha energía. Necesita espacio para correr.",
                "Fundación Huellitas con Amor", "Otavalo",
                true, true, true, true, false,
                "https://example.com/bruno.jpg", 0.2343, -78.2611, 0f
        ));

        animalesCompletos.add(new Animal(
                6, "Pelusa", "Angora", "GATO", 2, "CACHORRO",
                "PEQUEÑO", "HEMBRA", "Gatita bebé de 2 meses, muy juguetona.",
                "Protectora de Animales Cayambe", "Cayambe",
                true, false, true, true, false,
                "https://example.com/pelusa.jpg", 0.0390, -78.1450, 0f
        ));

        // Aplicar filtros iniciales
        animalesFiltrados = new ArrayList<>(animalesCompletos);
        chipTodos.setChecked(true);
        updateResultCount();
        showAnimales(animalesFiltrados);
    }

    private void showAnimales(List<Animal> animales) {
        if (animales.isEmpty()) {
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            animateEmptyState(false);
            animateRecyclerView(true);
            adapter.setAnimales(animales);
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
            rvAnimales.setVisibility(View.VISIBLE);
            rvAnimales.setAlpha(0f);
            rvAnimales.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        } else {
            rvAnimales.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rvAnimales.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void showAnimalDetail(Animal animal) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_animal_detail, null);

        ImageView ivAnimal = dialogView.findViewById(R.id.ivAnimal);
        TextView tvNombre = dialogView.findViewById(R.id.tvNombre);
        TextView tvRaza = dialogView.findViewById(R.id.tvRaza);
        TextView tvEdad = dialogView.findViewById(R.id.tvEdad);
        TextView tvSexo = dialogView.findViewById(R.id.tvSexo);
        TextView tvTamano = dialogView.findViewById(R.id.tvTamano);
        TextView tvDescripcion = dialogView.findViewById(R.id.tvDescripcion);
        TextView tvFundacion = dialogView.findViewById(R.id.tvFundacion);
        TextView tvUbicacion = dialogView.findViewById(R.id.tvUbicacion);
        TextView tvDistancia = dialogView.findViewById(R.id.tvDistancia);
        LinearLayout layoutCaracteristicas = dialogView.findViewById(R.id.layoutCaracteristicas);
        Chip chipVacunado = dialogView.findViewById(R.id.chipVacunado);
        Chip chipDesparasitado = dialogView.findViewById(R.id.chipDesparasitado);
        Chip chipEsterilizado = dialogView.findViewById(R.id.chipEsterilizado);
        Chip chipBuenoConNinos = dialogView.findViewById(R.id.chipBuenoConNinos);
        Chip chipBuenoConAnimales = dialogView.findViewById(R.id.chipBuenoConAnimales);

        // Configurar datos
        tvNombre.setText(animal.getNombre());
        tvRaza.setText(animal.getRazaNombre());
        tvEdad.setText(animal.getEdadAproximadaMeses() == 0 ? "Menos de 1 año" :
                animal.getEdadAproximadaMeses() + " año" + (animal.getEdadAproximadaMeses() > 1 ? "s" : ""));
        tvSexo.setText(animal.getGenero().equals("MACHO") ? "Macho" : "Hembra");
        tvTamano.setText("Tamaño " + animal.getTamaño().toLowerCase());
        tvDescripcion.setText(animal.getDescripcion());
        tvFundacion.setText(animal.getFundacionNombre());
        tvUbicacion.setText(animal.getUbicacionFundacion());

        if (animal.getDistancia() > 0) {
            tvDistancia.setText(String.format(Locale.getDefault(), "A %.1f km de ti", animal.getDistancia()));
            tvDistancia.setVisibility(View.VISIBLE);
        } else {
            tvDistancia.setVisibility(View.GONE);
        }

        // Mostrar características
        chipVacunado.setVisibility(animal.isVacunado() ? View.VISIBLE : View.GONE);
        chipDesparasitado.setVisibility(animal.isDesparasitado() ? View.VISIBLE : View.GONE);
        chipEsterilizado.setVisibility(animal.isEsterilizado() ? View.VISIBLE : View.GONE);
        chipBuenoConNinos.setVisibility(animal.isBuenoConNiños() ? View.VISIBLE : View.GONE);
        chipBuenoConAnimales.setVisibility(animal.isBuenoConAnimales() ? View.VISIBLE : View.GONE);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Adoptar", (dialog, which) -> startAdoptionProcess(animal))
                .setNegativeButton("Cerrar", null)
                .setNeutralButton("Compartir", (dialog, which) -> shareAnimal(animal));

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

    private void startAdoptionProcess(Animal animal) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Iniciar proceso de adopción")
                .setMessage("¿Deseas iniciar el proceso de adopción de " + animal.getNombre() + "?")
                .setPositiveButton("Sí, continuar", (dialog, which) -> {
                    // Aquí iría la navegación a la actividad de adopción
                    Snackbar.make(contentContainer,
                                    "Proceso de adopción iniciado",
                                    Snackbar.LENGTH_LONG)
                            .setAction("Ver detalles", v -> {
                                // Navegar a detalles de adopción
                            })
                            .show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void shareAnimal(Animal animal) {
        String shareText = "¡Mira esta mascota en adopción!\n\n" +
                "Nombre: " + animal.getNombre() + "\n" +
                "Raza: " + animal.getRazaNombre() + "\n" +
                "Edad: " + animal.getEdadAproximadaMeses() + " años\n" +
                "Ubicación: " + animal.getUbicacionFundacion() + "\n\n" +
                "Descarga AdoptMe para más información.";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Compartir mascota"));
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

        // Animar FAB
        fabFilter.setScaleX(0f);
        fabFilter.setScaleY(0f);
        fabFilter.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(800)
                .setInterpolator(new BounceInterpolator())
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
}