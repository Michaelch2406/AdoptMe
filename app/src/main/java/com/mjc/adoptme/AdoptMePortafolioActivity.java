package com.mjc.adoptme;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.adapters.AnimalesPortafolioAdapter;
import com.mjc.adoptme.adapters.FundacionesPortafolioAdapter;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.Animal;
import com.mjc.adoptme.models.AnimalAPI;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.Fundacion;
import com.mjc.adoptme.models.FundacionApp;
import com.mjc.adoptme.models.FundacionRequest;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import java.io.IOException;
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


    // Adapter y listas
    private AnimalesPortafolioAdapter animalAdapter;
    private FundacionesPortafolioAdapter fundacionAdapter;
    private List<Animal> animalesCompletos = new ArrayList<>();
    private List<Animal> animalesFiltrados = new ArrayList<>();
    private List<Fundacion> fundacionesCompletas = new ArrayList<>();
    private List<Fundacion> fundacionesFiltradas = new ArrayList<>();
    
    // Estados de la actividad
    private boolean mostrandoFundaciones = true;
    private Fundacion fundacionSeleccionada;
    private SessionManager sessionManager;

    // Filtros
    private String filtroTipo = "TODOS";
    private String filtroEdad = "TODOS";
    private float distanciaMaxima = 50f; // km
    private String filtroTamaño = "TODOS";
    private String filtroSexo = "TODOS";

    // Ubicación
    private Location ubicacionActual;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLoadingFundaciones = false;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean locationRequested = false;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final long LOCATION_TIMEOUT = 30000; // 30 segundos

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_me_portafolio);

        sessionManager = new SessionManager(this);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupBackButtonHandler();
        setupSearchFilter();
        setupLocationServices();
        checkLocationPermission();
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

        // Estado inicial de vistas
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        contentContainer.setAlpha(0f);
        contentContainer.setTranslationY(50f);
        emptyStateContainer.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        // Configurar adapter de animales
        animalAdapter = new AnimalesPortafolioAdapter();
        animalAdapter.setOnItemClickListener(animal -> showAnimalDetail(animal));
        animalAdapter.setOnAdoptClickListener(animal -> startAdoptionProcess(animal));
        
        // Configurar adapter de fundaciones
        fundacionAdapter = new FundacionesPortafolioAdapter();
        fundacionAdapter.setOnItemClickListener(fundacion -> {
            try {
                Log.d("AdoptMePortafolio", "Foundation clicked: " + (fundacion != null ? fundacion.getNombreFundacion() : "null"));
                
                fundacionSeleccionada = fundacion;
                if (fundacion != null && fundacion.getRuc() != null && !fundacion.getRuc().trim().isEmpty()) {
                    Log.d("AdoptMePortafolio", "Loading animals for RUC: " + fundacion.getRuc());
                    loadAnimalsPorFundacion(fundacion.getRuc());
                } else {
                    String errorMsg = "Error: " + (fundacion == null ? "Fundación no válida" : 
                        "RUC de fundación no disponible");
                    Log.e("AdoptMePortafolio", errorMsg);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("AdoptMePortafolio", "Error clicking foundation", e);
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar animales: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        
        // Inicializar con fundaciones
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAnimales.setLayoutManager(layoutManager);
        rvAnimales.setAdapter(fundacionAdapter);
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> {
            if (mostrandoFundaciones) {
                handleExitAnimation();
            } else {
                volverAFundaciones();
            }
        });
        fabFilter.setOnClickListener(v -> showAdvancedFilters());

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

    private void setupLocationServices() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Configurar LocationRequest
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();
        
        // Callback para recibir ubicaciones
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    ubicacionActual = location;
                    onLocationReceived(location);
                    stopLocationUpdates();
                }
            }
        };
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Mostrar diálogo explicativo
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Ubicación requerida")
                    .setMessage("AdoptMe necesita acceso a tu ubicación para mostrar fundaciones cercanas. ¿Permitir acceso a la ubicación?")
                    .setPositiveButton("Permitir", (dialog, which) -> {
                        ActivityCompat.requestPermissions(this,
                                new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                },
                                LOCATION_PERMISSION_REQUEST);
                    })
                    .setNegativeButton("Usar ubicación por defecto", (dialog, which) -> {
                        usarUbicacionPorDefecto();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        if (locationRequested) {
            return;
        }
        
        locationRequested = true;
        
        // Mostrar indicador de carga
        tvLocationInfo.setText("📍 Obteniendo ubicación...");
        tvLocationInfo.setVisibility(View.VISIBLE);
        
        // Intentar múltiples métodos para obtener ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            // Método 1: Intentar obtener última ubicación conocida
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null && !isLocationTooOld(location)) {
                            ubicacionActual = location;
                            onLocationReceived(location);
                        } else {
                            // Método 2: Solicitar actualizaciones de ubicación
                            requestLocationUpdates();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        // Método 3: Usar LocationManager como fallback
                        tryLocationManager();
                    });
            
            // Timeout para ubicación
            handler.postDelayed(() -> {
                if (ubicacionActual == null) {
                    showInfoDialog("No se pudo obtener la ubicación, usando ubicación por defecto");
                    usarUbicacionPorDefecto();
                }
            }, LOCATION_TIMEOUT);
            
        } else {
            usarUbicacionPorDefecto();
        }
    }

    private boolean isLocationTooOld(Location location) {
        long locationAge = System.currentTimeMillis() - location.getTime();
        return locationAge > 5 * 60 * 1000; // 5 minutos
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void tryLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        ubicacionActual = location;
                        onLocationReceived(location);
                        locationManager.removeUpdates(this);
                    }
                }
            };
            
            // Intentar con GPS primero
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
                );
            }
            // Fallback a network provider
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0,
                    locationListener
                );
            } else {
                usarUbicacionPorDefecto();
            }
        } else {
            usarUbicacionPorDefecto();
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (locationManager != null) {
            locationManager.removeUpdates(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {}
            });
        }
    }

    private void onLocationReceived(Location location) {
        // Usar el nuevo método para obtener dirección legible
        getAddressFromLocation(location);
    }

    private void usarUbicacionPorDefecto() {
        // Ubicación por defecto: Quito, Ecuador
        ubicacionActual = new Location("default");
        ubicacionActual.setLatitude(-0.1807);
        ubicacionActual.setLongitude(-78.4678);

        // Usar el método de geocoding también para la ubicación por defecto
        getAddressFromLocation(ubicacionActual);
    }

    private void getAddressFromLocation(Location location) {
        if (!Geocoder.isPresent()) {
            // Geocoder no está disponible, usar coordenadas
            updateLocationDisplay(location, null);
            return;
        }

        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), 
                    location.getLongitude(), 
                    1
                );

                runOnUiThread(() -> {
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        updateLocationDisplay(location, address);
                    } else {
                        updateLocationDisplay(location, null);
                    }
                });
            } catch (IOException e) {
                Log.e("AdoptMePortafolio", "Error getting address from location", e);
                runOnUiThread(() -> updateLocationDisplay(location, null));
            }
        }).start();
    }

    private void updateLocationDisplay(Location location, Address address) {
        StringBuilder locationText = new StringBuilder();
        locationText.append("📍 ");
        
        if (address != null) {
            // Construir dirección detallada
            StringBuilder addressText = new StringBuilder();
            
            // Agregar dirección específica (calle)
            if (address.getThoroughfare() != null) {
                addressText.append(address.getThoroughfare());
            }
            
            // Agregar ciudad/localidad
            if (address.getLocality() != null) {
                if (addressText.length() > 0) addressText.append(", ");
                addressText.append(address.getLocality());
            } else if (address.getSubAdminArea() != null) {
                if (addressText.length() > 0) addressText.append(", ");
                addressText.append(address.getSubAdminArea());
            }
            
            // Agregar estado/provincia
            if (address.getAdminArea() != null) {
                if (addressText.length() > 0) addressText.append(", ");
                addressText.append(address.getAdminArea());
            }
            
            // Agregar país
            if (address.getCountryName() != null) {
                if (addressText.length() > 0) addressText.append(", ");
                addressText.append(address.getCountryName());
            }
            
            if (addressText.length() > 0) {
                locationText.append(addressText.toString());
            } else {
                locationText.append("Ubicación desconocida");
            }
        } else {
            locationText.append("Ubicación actual");
        }
        
        // Siempre agregar coordenadas en una nueva línea
        locationText.append("\n🌐 ");
        locationText.append(String.format(Locale.getDefault(), 
            "%.6f, %.6f", 
            location.getLatitude(), 
            location.getLongitude()
        ));
        
        tvLocationInfo.setText(locationText.toString());
        tvLocationInfo.setVisibility(View.VISIBLE);
        
        // Animar entrada del texto de ubicación
        tvLocationInfo.setAlpha(0f);
        tvLocationInfo.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        // Cargar fundaciones cercanas
        loadFundacionesCercanas();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            boolean fineLocationGranted = false;
            boolean coarseLocationGranted = false;
            
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    fineLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[i])) {
                    coarseLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
            
            if (fineLocationGranted || coarseLocationGranted) {
                obtenerUbicacion();
            } else {
                // Mostrar diálogo para usar ubicación por defecto
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Permisos denegados")
                        .setMessage("Sin permisos de ubicación, se usará la ubicación por defecto (Quito, Ecuador)")
                        .setPositiveButton("Continuar", (dialog, which) -> usarUbicacionPorDefecto())
                        .setCancelable(false)
                        .show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void showAdvancedFilters() {
        if (mostrandoFundaciones) {
            showFundacionesAdvancedFilters();
        } else {
            showAnimalesAdvancedFilters();
        }
    }

    private void showFundacionesAdvancedFilters() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_fundaciones, null);

        RangeSlider sliderDistancia = dialogView.findViewById(R.id.sliderDistancia);
        TextView tvDistanciaValue = dialogView.findViewById(R.id.tvDistanciaValue);

        // Configurar slider de distancia (máximo 50km para fundaciones)
        sliderDistancia.setValues(Math.min(distanciaMaxima, 50f));
        tvDistanciaValue.setText(String.format(Locale.getDefault(), "%.0f km", Math.min(distanciaMaxima, 50f)));

        sliderDistancia.addOnChangeListener((slider, value, fromUser) -> {
            tvDistanciaValue.setText(String.format(Locale.getDefault(), "%.0f km", value));
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Filtros de fundaciones")
                .setView(dialogView)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    distanciaMaxima = Math.min(sliderDistancia.getValues().get(0), 50f);
                    applyFilters();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Restablecer", (dialog, which) -> {
                    distanciaMaxima = 50f;
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

    private void showAnimalesAdvancedFilters() {
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
        // Si estamos mostrando fundaciones, aplicar filtros a fundaciones
        if (mostrandoFundaciones) {
            applyFundacionesFilters();
            return;
        }
        
        // Aplicar filtros a animales
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

    private void applyFundacionesFilters() {
        // Filtrar fundaciones por distancia
        String searchQuery = etBuscar.getText().toString().toLowerCase(Locale.getDefault()).trim();
        fundacionesFiltradas = new ArrayList<>();

        for (Fundacion fundacion : fundacionesCompletas) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    (fundacion.getNombreFundacion() != null && 
                     fundacion.getNombreFundacion().toLowerCase(Locale.getDefault()).contains(searchQuery)) ||
                    (fundacion.getNombreSucursal() != null && 
                     fundacion.getNombreSucursal().toLowerCase(Locale.getDefault()).contains(searchQuery));

            boolean matchesDistancia = true;
            if (ubicacionActual != null && fundacion.getLatitud() != 0 && fundacion.getLongitud() != 0) {
                Location fundacionLocation = new Location("");
                fundacionLocation.setLatitude(fundacion.getLatitud());
                fundacionLocation.setLongitude(fundacion.getLongitud());
                float distancia = ubicacionActual.distanceTo(fundacionLocation) / 1000;
                matchesDistancia = distancia <= distanciaMaxima;
                fundacion.setDistancia(distancia);
            }

            if (matchesSearch && matchesDistancia) {
                fundacionesFiltradas.add(fundacion);
            }
        }

        // Ordenar por distancia
        if (ubicacionActual != null) {
            fundacionesFiltradas.sort((f1, f2) -> Float.compare(f1.getDistancia(), f2.getDistancia()));
        }

        updateResultCount();
        showFundaciones(fundacionesFiltradas);
    }

    private void updateResultCount() {
        String texto;
        if (mostrandoFundaciones) {
            texto = fundacionesFiltradas.size() + " fundación" +
                    (fundacionesFiltradas.size() != 1 ? "es" : "") + " encontrada" +
                    (fundacionesFiltradas.size() != 1 ? "s" : "");
        } else {
            texto = animalesFiltrados.size() + " mascota" +
                    (animalesFiltrados.size() != 1 ? "s" : "") + " encontrada" +
                    (animalesFiltrados.size() != 1 ? "s" : "");
        }
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
                if (mostrandoFundaciones) {
                    handleExitAnimation();
                } else {
                    volverAFundaciones();
                }
            }
        });
    }

    private void loadFundacionesCercanas() {
        if (ubicacionActual == null) {
            Log.d("FundacionesAPI", "Ubicación no disponible");
            return;
        }

        if (isLoadingFundaciones) {
            Log.d("FundacionesAPI", "Ya hay una carga en progreso, ignorando solicitud");
            return;
        }

        String cedula = sessionManager.getCedula();
        if (cedula == null) {
            showErrorDialog("Error: Usuario no autenticado");
            return;
        }

        isLoadingFundaciones = true;

        try {
            ApiService apiService = RetrofitClient.getApiService();
            FundacionRequest request = new FundacionRequest(
                    ubicacionActual.getLatitude(),
                    ubicacionActual.getLongitude(),
                    cedula,
                    distanciaMaxima
            );

            Log.d("FundacionesAPI", "Making request with:");
            Log.d("FundacionesAPI", "Lat: " + request.getLat());
            Log.d("FundacionesAPI", "Lng: " + request.getLng());
            Log.d("FundacionesAPI", "Cedula: " + request.getCedula());
            Log.d("FundacionesAPI", "Distancia: " + request.getDistancia());

            Call<ApiResponse<List<FundacionApp>>> call = apiService.getFundacionesApp(request);

            call.enqueue(new Callback<ApiResponse<List<FundacionApp>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<FundacionApp>>> call, Response<ApiResponse<List<FundacionApp>>> response) {
                    try {
                        Log.d("FundacionesAPI", "Response code: " + response.code());
                        Log.d("FundacionesAPI", "Response successful: " + response.isSuccessful());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<FundacionApp>> apiResponse = response.body();
                            Log.d("FundacionesAPI", "API Status: " + apiResponse.getStatus());
                            Log.d("FundacionesAPI", "API Message: " + apiResponse.getMessage());

                            if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                                List<FundacionApp> fundacionesApp = apiResponse.getData();
                                Log.d("FundacionesAPI", "Fundaciones received: " + fundacionesApp.size());

                                if (!fundacionesApp.isEmpty()) {
                                    // Convertir FundacionApp a Fundacion
                                    fundacionesCompletas = convertirFundacionesApp(fundacionesApp);
                                    fundacionesFiltradas = new ArrayList<>(fundacionesCompletas);
                                    updateResultCount();
                                    showFundaciones(fundacionesFiltradas);
                                    Toast.makeText(AdoptMePortafolioActivity.this,
                                            "Se encontraron " + fundacionesCompletas.size() + " fundaciones cercanas",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // API respondió pero sin datos
                                    fundacionesCompletas = new ArrayList<>();
                                    fundacionesFiltradas = new ArrayList<>();
                                    showFundaciones(fundacionesFiltradas);
                                    showInfoDialog("No se encontraron fundaciones cercanas.");
                                }
                            } else {
                                // API devolvió status diferente a 200 o datos nulos
                                String errorMsg = "No se pudieron cargar las fundaciones";
                                if (apiResponse.getMessage() != null && !apiResponse.getMessage().isEmpty()) {
                                    errorMsg = apiResponse.getMessage();
                                }
                                Log.e("FundacionesAPI", "API Error: " + errorMsg + " (Status: " + apiResponse.getStatus() + ")");
                                showErrorDialog(errorMsg);
                            }
                        } else {
                            // Error HTTP o respuesta nula
                            String errorMsg = "Error al obtener fundaciones (HTTP " + response.code() + ")";
                            Log.e("FundacionesAPI", errorMsg);

                            boolean isServerError = false;
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("FundacionesAPI", "Error body: " + errorBody);

                                    // Check if it's the specific SQL error
                                    if (errorBody.contains("invalid input syntax for type integer") ||
                                            errorBody.contains("NOT IN ('')")) {
                                        isServerError = true;
                                        errorMsg = "Error del servidor. Las fundaciones no están disponibles temporalmente.";
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("FundacionesAPI", "Could not read error body: " + e.getMessage());
                            }

                            if (isServerError) {
                                // Try fallback to old API if available
                                tryFallbackFundacionesAPI();
                            } else {
                                showErrorDialog("Error al obtener fundaciones. Por favor intenta nuevamente.");
                            }
                        }
                    } finally {
                        // Siempre liberar el flag al final
                        isLoadingFundaciones = false;
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<FundacionApp>>> call, Throwable t) {
                    // En caso de error de conexión
                    Log.e("FundacionesAPI", "Network error: " + t.getMessage(), t);
                    showErrorDialog("Error de conexión. Por favor verifica tu conexión a internet.");
                    // Liberar el flag también en caso de fallo
                    isLoadingFundaciones = false;
                }
            });
        } catch (Exception e) {
            Log.e("FundacionesAPI", "Error calling fundaciones endpoint", e);
            showErrorDialog("Error al iniciar la solicitud. Inténtalo de nuevo.");
            isLoadingFundaciones = false;
        }
    }

    private void tryFallbackFundacionesAPI() {
        Log.d("FundacionesAPI", "Trying fallback API");

        if (ubicacionActual != null) {
            try {
                ApiService apiService = RetrofitClient.getApiService();
                Call<ApiResponse<List<Fundacion>>> fallbackCall =
                        apiService.getFundacionesCercanas(ubicacionActual.getLatitude(), ubicacionActual.getLongitude());

                fallbackCall.enqueue(new Callback<ApiResponse<List<Fundacion>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Fundacion>>> call, Response<ApiResponse<List<Fundacion>>> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<List<Fundacion>> apiResponse = response.body();
                                if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                                    fundacionesCompletas = apiResponse.getData();
                                    fundacionesFiltradas = new ArrayList<>(fundacionesCompletas);
                                    updateResultCount();
                                    showFundaciones(fundacionesFiltradas);
                                    Toast.makeText(AdoptMePortafolioActivity.this,
                                            "Fundaciones cargadas",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    showFallbackError();
                                }
                            } else {
                                showFallbackError();
                            }
                        } catch (Exception e) {
                            Log.e("FundacionesAPI", "Error processing fallback response", e);
                            showFallbackError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Fundacion>>> call, Throwable t) {
                        showFallbackError();
                    }
                });
            } catch (Exception e) {
                Log.e("FundacionesAPI", "Error calling fallback fundaciones endpoint", e);
                showFallbackError();
            }
        } else {
            showFallbackError();
        }
    }

    private void showFallbackError() {
        showErrorDialog("Las fundaciones no están disponibles temporalmente. Por favor intenta más tarde.");
    }

    private List<Fundacion> convertirFundacionesApp(List<FundacionApp> fundacionesApp) {
        List<Fundacion> fundaciones = new ArrayList<>();
        
        for (FundacionApp fundacionApp : fundacionesApp) {
            Fundacion fundacion = new Fundacion(
                    fundacionApp.getNombreFundacion(),
                    fundacionApp.getRuc(),
                    fundacionApp.getNombreSucursal(),
                    fundacionApp.getDireccion(),
                    fundacionApp.getLatitud(),
                    fundacionApp.getLongitud(),
                    fundacionApp.getCantidadAnimales()
            );
            fundaciones.add(fundacion);
        }
        
        return fundaciones;
    }


    private void loadAnimalsPorFundacion(String ruc) {
        mostrandoFundaciones = false;

        try {
            ApiService apiService = RetrofitClient.getApiService();
            String cedula = sessionManager.getCedula();
            Call<ApiResponse<List<AnimalAPI>>> call = apiService.getAnimalesPorFundacion(ruc, cedula);

            call.enqueue(new Callback<ApiResponse<List<AnimalAPI>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<AnimalAPI>>> call, Response<ApiResponse<List<AnimalAPI>>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            List<AnimalAPI> animalesAPI = response.body().getData();
                            if (animalesAPI != null && !animalesAPI.isEmpty()) {
                                animalesCompletos = convertirAnimales(animalesAPI);
                                animalesFiltrados = new ArrayList<>(animalesCompletos);
                                cambiarAVistaAnimales();
                                Toast.makeText(AdoptMePortafolioActivity.this,
                                        "Se encontraron " + animalesAPI.size() + " animales disponibles",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // API respondió pero sin animales
                                animalesCompletos = new ArrayList<>();
                                animalesFiltrados = new ArrayList<>();
                                cambiarAVistaAnimales();
                                Toast.makeText(AdoptMePortafolioActivity.this,
                                        "Esta fundación no tiene animales disponibles actualmente.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Error en la respuesta
                            Toast.makeText(AdoptMePortafolioActivity.this,
                                    "Error al obtener animales. Por favor intenta nuevamente.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("AdoptMePortafolio", "Error processing animals response", e);
                        Toast.makeText(AdoptMePortafolioActivity.this, "Error al procesar la respuesta de animales.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<AnimalAPI>>> call, Throwable t) {
                    // En caso de error de conexión
                    showErrorDialog("Error de conexión. Por favor verifica tu conexión a internet.");
                }
            });
        } catch (Exception e) {
            Log.e("AdoptMePortafolio", "Error calling animals endpoint", e);
            showErrorDialog("Error al iniciar la solicitud de animales. Inténtalo de nuevo.");
        }
    }


    private List<Animal> convertirAnimales(List<AnimalAPI> animalesAPI) {
        List<Animal> animales = new ArrayList<>();
        
        for (AnimalAPI animalAPI : animalesAPI) {
            Animal animal = new Animal(
                    animalAPI.getId(),
                    animalAPI.getNombre(),
                    "Raza desconocida", // TODO: obtener raza de lookup tables
                    animalAPI.getTipoAnimalId() == 1 ? "PERRO" : "GATO",
                    animalAPI.getEdadAproximada(),
                    animalAPI.getRangoEdadId() == 1 ? "CACHORRO" : "ADULTO",
                    animalAPI.getTamanoId() == 1 ? "PEQUEÑO" : animalAPI.getTamanoId() == 2 ? "MEDIANO" : "GRANDE",
                    animalAPI.getGeneroId() == 1 ? "MACHO" : "HEMBRA",
                    animalAPI.getDescripcion(),
                    fundacionSeleccionada.getNombreFundacion(),
                    fundacionSeleccionada.getDireccion(),
                    false, false, animalAPI.isEsterilizado(), false, false,
                    "",
                    fundacionSeleccionada.getLatitud(),
                    fundacionSeleccionada.getLongitud(),
                    0f
            );
            animales.add(animal);
        }
        
        return animales;
    }

    private void cambiarAVistaAnimales() {
        mostrandoFundaciones = false;
        
        // Cambiar el layout manager para animales (grid)
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rvAnimales.setLayoutManager(gridLayoutManager);
            rvAnimales.setAdapter(animalAdapter);
            Log.d("AdoptMePortafolio", "Switched to animal adapter");
        } catch (Exception e) {
            Log.e("AdoptMePortafolio", "Error switching to animal adapter", e);
        }
        
        // Actualizar el subtítulo
        tvSubtitle.setText("Animales de " + fundacionSeleccionada.getNombreFundacion());
        
        // Mostrar botón de regreso a fundaciones
        btnRegresar.setText("← Volver a fundaciones");
        
        updateResultCount();
        showAnimales(animalesFiltrados);
    }

    private void volverAFundaciones() {
        mostrandoFundaciones = true;
        
        // Clear any ongoing animations first
        rvAnimales.animate().cancel();
        rvAnimales.clearAnimation();
        rvAnimales.setVisibility(View.VISIBLE);
        rvAnimales.setAlpha(1f);
        
        // Cambiar el layout manager para fundaciones (linear)
        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvAnimales.setLayoutManager(linearLayoutManager);
            rvAnimales.setAdapter(fundacionAdapter);
            
            // Force refresh the adapter data
            fundacionAdapter.setFundaciones(fundacionesFiltradas);
            fundacionAdapter.notifyDataSetChanged();
            
            Log.d("AdoptMePortafolio", "Switched to foundation adapter with " + fundacionesFiltradas.size() + " items");
        } catch (Exception e) {
            Log.e("AdoptMePortafolio", "Error switching to foundation adapter", e);
        }
        
        // Restaurar el subtítulo
        tvSubtitle.setText("Primero selecciona una fundación");
        
        // Restaurar botón de regreso
        btnRegresar.setText("← Regresar");
        
        updateResultCount();
        showFundaciones(fundacionesFiltradas);
    }

    private void showFundaciones(List<Fundacion> fundaciones) {
        Log.d("AdoptMePortafolio", "showFundaciones called with " + fundaciones.size() + " items");
        
        if (fundaciones.isEmpty()) {
            Log.d("AdoptMePortafolio", "No foundations, showing empty state");
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            Log.d("AdoptMePortafolio", "Showing foundations, hiding empty state");
            animateEmptyState(false);
            animateRecyclerView(true);
            
            // Ensure RecyclerView is properly set up
            rvAnimales.setVisibility(View.VISIBLE);
            fundacionAdapter.setFundaciones(fundaciones);
            fundacionAdapter.notifyDataSetChanged();
            
            // Ensure the RecyclerView updates its layout
            rvAnimales.post(() -> rvAnimales.requestLayout());
            
            Log.d("AdoptMePortafolio", "Adapter item count: " + fundacionAdapter.getItemCount());
            Log.d("AdoptMePortafolio", "RecyclerView visibility: " + rvAnimales.getVisibility());
        }
    }

    private void showAnimales(List<Animal> animales) {
        if (animales.isEmpty()) {
            animateEmptyState(true);
            animateRecyclerView(false);
        } else {
            animateEmptyState(false);
            animateRecyclerView(true);
            animalAdapter.setAnimales(animales);
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
        // Clear any existing animations to avoid conflicts
        rvAnimales.animate().cancel();
        rvAnimales.clearAnimation();
        
        if (show) {
            rvAnimales.setVisibility(View.VISIBLE);
            rvAnimales.setAlpha(0f);
            rvAnimales.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null) // Clear any previous listeners
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
        ChipGroup layoutCaracteristicas = dialogView.findViewById(R.id.layoutCaracteristicas);
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
                    Intent intent = new Intent(AdoptMePortafolioActivity.this, ProcesarAdopcionActivity.class);
                    intent.putExtra("animal_id", animal.getId());
                    intent.putExtra("animal_nombre", animal.getNombre());
                    startActivity(intent);
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

    private void showErrorDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showInfoDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Información")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}