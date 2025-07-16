package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.Ciudad;
import com.mjc.adoptme.models.DatosPersonalesData;
import com.mjc.adoptme.models.Pais;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.models.UpdateDataRequest;
import com.mjc.adoptme.network.ApiClient;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.UpdateRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatosPersonalesActivity extends AppCompatActivity {
    private static final String TAG = "DatosPersonalesActivity";

    // Vistas
    private LinearLayout logoContainer, formContainer, layoutPaws, progressIndicator;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo;
    private MaterialButton btnSiguiente, btnRegresar;
    private ProgressBar progressBar;

    // Campos del Formulario
    private TextInputLayout tilNombres, tilApellidos, tilEmail, tilCedula, tilFechaNacimiento, tilLugarNacimiento, tilNacionalidad;
    private TextInputLayout tilTelefonoConvencional, tilTelefonoMovil, tilOcupacion, tilNivelInstruccion, tilLugarTrabajo, tilTelefonoTrabajo, tilDireccionTrabajo;
    private TextInputLayout tilNivelInstruccionOtro;
    private TextInputEditText etNombres, etApellidos, etEmail, etCedula, etFechaNacimiento;
    private TextInputEditText etTelefonoConvencional, etTelefonoMovil, etOcupacion, etLugarTrabajo, etTelefonoTrabajo, etDireccionTrabajo;
    private TextInputEditText etNivelInstruccionOtro;
    private AutoCompleteTextView actvNivelInstruccion, actvLugarNacimiento, actvNacionalidad;
    private RadioGroup rgTrabaja;
    private RadioButton rbTrabajaSi, rbTrabajaNo;
    private LinearLayout layoutCamposTrabajo;

    // Formato de fecha
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isUpdateMode = false;
    private boolean isRegistrationMode = false;
    private SessionManager sessionManager;
    private UpdateRepository updateRepository;
    private ApiService apiService;
    
    // Datos para dropdowns
    private List<Pais> paises = new ArrayList<>();
    private List<Ciudad> ciudades = new ArrayList<>();
    private Pais paisSeleccionado;
    private Ciudad ciudadSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        sessionManager = new SessionManager(this);
        updateRepository = new UpdateRepository();
        apiService = ApiClient.getApiService();

        initViews();

        // Determinar el modo de la actividad
        isUpdateMode = getIntent().getBooleanExtra("IS_UPDATE_MODE", false);
        isRegistrationMode = !isUpdateMode && !sessionManager.isLoggedIn();

        configureUIForMode();
        logActivityState();

        if (isUpdateMode) {
            loadUserData();
        } else {
            populateDataFromRepository();
        }

        setupDropdowns();
        loadDropdownData();
        setupDatePicker();
        setupClickListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void configureUIForMode() {
        if (isUpdateMode) {
            // Modo actualización: mostrar todos los campos
            btnSiguiente.setText("Actualizar");
            tvSubtitle.setText("Actualizar Datos Personales");
            etCedula.setEnabled(false);
            tilCedula.setHelperText("La cédula no se puede modificar");
            
            // Mostrar todos los campos básicos
            tilNombres.setVisibility(View.VISIBLE);
            tilApellidos.setVisibility(View.VISIBLE);
            tilEmail.setVisibility(View.VISIBLE);
            tilCedula.setVisibility(View.VISIBLE);
            
        } else if (isRegistrationMode) {
            // Modo registro: ocultar nombres, apellidos, email (ya están en RegistroActivity)
            btnSiguiente.setText("Siguiente");
            tvSubtitle.setText("Datos Personales");
            
            // Ocultar campos que ya se capturaron en RegistroActivity
            tilNombres.setVisibility(View.GONE);
            tilApellidos.setVisibility(View.GONE);
            tilEmail.setVisibility(View.GONE);
            tilCedula.setVisibility(View.VISIBLE);
            
        } else {
            // Modo normal (usuario logueado navegando en el flujo)
            btnSiguiente.setText("Siguiente");
            tvSubtitle.setText("Datos Personales");
            
            // Mostrar todos los campos
            tilNombres.setVisibility(View.VISIBLE);
            tilApellidos.setVisibility(View.VISIBLE);
            tilEmail.setVisibility(View.VISIBLE);
            tilCedula.setVisibility(View.VISIBLE);
        }
    }

    private void logActivityState() {
        Log.d(TAG, "=== ESTADO DE LA ACTIVIDAD ===");
        Log.d(TAG, "isUpdateMode: " + isUpdateMode);
        Log.d(TAG, "isRegistrationMode: " + isRegistrationMode);
        Log.d(TAG, "sessionManager.isLoggedIn(): " + sessionManager.isLoggedIn());
        Log.d(TAG, "Visibilidad tilNombres: " + (tilNombres.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        Log.d(TAG, "Visibilidad tilApellidos: " + (tilApellidos.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        Log.d(TAG, "Visibilidad tilEmail: " + (tilEmail.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        if (isUpdateMode) {
            Log.d(TAG, "SessionManager - Cédula: " + sessionManager.getCedula());
        }
        Log.d(TAG, "==============================");
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        progressIndicator = findViewById(R.id.progressIndicator);
        ivLogo = findViewById(R.id.ivLogo);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);
        progressBar = findViewById(R.id.progressBar);
        tilNombres = findViewById(R.id.tilNombres);
        tilApellidos = findViewById(R.id.tilApellidos);
        tilEmail = findViewById(R.id.tilEmail);
        tilCedula = findViewById(R.id.tilCedula);
        tilFechaNacimiento = findViewById(R.id.tilFechaNacimiento);
        tilLugarNacimiento = findViewById(R.id.tilLugarNacimiento);
        tilNacionalidad = findViewById(R.id.tilNacionalidad);
        tilTelefonoConvencional = findViewById(R.id.tilTelefonoConvencional);
        tilTelefonoMovil = findViewById(R.id.tilTelefonoMovil);
        tilOcupacion = findViewById(R.id.tilOcupacion);
        tilNivelInstruccion = findViewById(R.id.tilNivelInstruccion);
        tilNivelInstruccionOtro = findViewById(R.id.tilNivelInstruccionOtro);
        tilLugarTrabajo = findViewById(R.id.tilLugarTrabajo);
        tilDireccionTrabajo = findViewById(R.id.tilDireccionTrabajo);
        tilTelefonoTrabajo = findViewById(R.id.tilTelefonoTrabajo);
        rgTrabaja = findViewById(R.id.rgTrabaja);
        rbTrabajaSi = findViewById(R.id.rbTrabajaSi);
        rbTrabajaNo = findViewById(R.id.rbTrabajaNo);
        layoutCamposTrabajo = findViewById(R.id.layoutCamposTrabajo);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etCedula = findViewById(R.id.etCedula);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        actvLugarNacimiento = findViewById(R.id.actvLugarNacimiento);
        actvNacionalidad = findViewById(R.id.actvNacionalidad);
        etTelefonoConvencional = findViewById(R.id.etTelefonoConvencional);
        etTelefonoMovil = findViewById(R.id.etTelefonoMovil);
        etOcupacion = findViewById(R.id.etOcupacion);
        etLugarTrabajo = findViewById(R.id.etLugarTrabajo);
        etDireccionTrabajo = findViewById(R.id.etDireccionTrabajo);
        etTelefonoTrabajo = findViewById(R.id.etTelefonoTrabajo);
        etNivelInstruccionOtro = findViewById(R.id.etNivelInstruccionOtro);
        actvNivelInstruccion = findViewById(R.id.actvNivelInstruccion);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnRegresar = findViewById(R.id.btnRegresar);

        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        String cedula = sessionManager.getCedula();

        updateRepository.getUserPersonalData(cedula, new UpdateRepository.DataCallback<DatosPersonalesData>() {
            @Override
            public void onSuccess(DatosPersonalesData data) {
                progressBar.setVisibility(View.GONE);
                populateForm(data);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error al cargar datos: " + message);
                showErrorDialog("Error al cargar los datos. Intente nuevamente.");
            }
        });
    }

    private void populateForm(DatosPersonalesData data) {
        // Rellenar campos básicos
        if (data.getCedula() != null) etCedula.setText(data.getCedula());
        if (data.getNombres() != null) etNombres.setText(data.getNombres());
        if (data.getApellidos() != null) etApellidos.setText(data.getApellidos());
        if (data.getEmail() != null) etEmail.setText(data.getEmail());

        // Fecha de nacimiento
        if (data.getFecha_nacimiento() != null) {
            try {
                Date date = apiDateFormat.parse(data.getFecha_nacimiento());
                etFechaNacimiento.setText(displayDateFormat.format(date));
                calendar.setTime(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error al parsear fecha: " + e.getMessage());
            }
        }

        if (data.getLugar_nacimiento() != null) {
            actvLugarNacimiento.setText(data.getLugar_nacimiento(), false);
            // Buscar ciudad por nombre y establecerla
            for (Ciudad ciudad : ciudades) {
                if (ciudad.getNombre().equalsIgnoreCase(data.getLugar_nacimiento())) {
                    ciudadSeleccionada = ciudad;
                    break;
                }
            }
        }
        if (data.getNacionalidad() != null) {
            actvNacionalidad.setText(data.getNacionalidad(), false);
            // Buscar país por nombre y establecerlo
            for (Pais pais : paises) {
                if (pais.getNombre().equalsIgnoreCase(data.getNacionalidad())) {
                    paisSeleccionado = pais;
                    // Cargar ciudades del país seleccionado
                    loadCiudades(pais.getId());
                    break;
                }
            }
        }
        if (data.getTelefono_convencional() != null) etTelefonoConvencional.setText(data.getTelefono_convencional());
        if (data.getTelefono_movil() != null) etTelefonoMovil.setText(data.getTelefono_movil());
        if (data.getOcupacion() != null) etOcupacion.setText(data.getOcupacion());
        if (data.getNivel_instruccion() != null) actvNivelInstruccion.setText(data.getNivel_instruccion(), false);

        // Datos de trabajo
        if (data.getLugar_trabajo() != null && !data.getLugar_trabajo().isEmpty()) {
            rbTrabajaSi.setChecked(true);
            layoutCamposTrabajo.setVisibility(View.VISIBLE);
            etLugarTrabajo.setText(data.getLugar_trabajo());
            if (data.getDireccion_trabajo() != null) etDireccionTrabajo.setText(data.getDireccion_trabajo());
            if (data.getTelefono_trabajo() != null) etTelefonoTrabajo.setText(data.getTelefono_trabajo());
        } else {
            rbTrabajaNo.setChecked(true);
            layoutCamposTrabajo.setVisibility(View.GONE);
        }
    }

    private void updateData() {
        if (!validateForm()) return;

        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);

        DatosPersonalesData data = new DatosPersonalesData();
        data.setCedula(etCedula.getText().toString().trim());
        
        // Log para debugging
        Log.d(TAG, "=== DATOS PARA ACTUALIZAR ===");
        Log.d(TAG, "Cédula: " + data.getCedula());
        Log.d(TAG, "País seleccionado: " + (paisSeleccionado != null ? paisSeleccionado.getNombre() : "null"));
        Log.d(TAG, "Ciudad seleccionada: " + (ciudadSeleccionada != null ? ciudadSeleccionada.getNombre() : "null"));
        Log.d(TAG, "===============================");
        
        // Solo incluir campos si están visibles
        if (tilNombres.getVisibility() == View.VISIBLE) {
            data.setNombres(etNombres.getText().toString().trim());
        }
        if (tilApellidos.getVisibility() == View.VISIBLE) {
            data.setApellidos(etApellidos.getText().toString().trim());
        }
        if (tilEmail.getVisibility() == View.VISIBLE) {
            data.setEmail(etEmail.getText().toString().trim());
        }

        // Convertir fecha al formato de la API
        try {
            Date date = displayDateFormat.parse(etFechaNacimiento.getText().toString());
            data.setFecha_nacimiento(apiDateFormat.format(date));
        } catch (ParseException e) {
            data.setFecha_nacimiento(null);
        }

        data.setLugar_nacimiento(ciudadSeleccionada != null ? ciudadSeleccionada.getNombre() : actvLugarNacimiento.getText().toString().trim());
        data.setNacionalidad(paisSeleccionado != null ? paisSeleccionado.getNombre() : actvNacionalidad.getText().toString().trim());

        String telConv = etTelefonoConvencional.getText().toString().trim();
        data.setTelefono_convencional(telConv.isEmpty() ? null : telConv);

        data.setTelefono_movil(etTelefonoMovil.getText().toString().trim());
        data.setOcupacion(etOcupacion.getText().toString().trim());

        String nivelInstruccion = actvNivelInstruccion.getText().toString().trim();
        if ("Otro".equals(nivelInstruccion)) {
            data.setNivel_instruccion(etNivelInstruccionOtro.getText().toString().trim());
        } else {
            data.setNivel_instruccion(nivelInstruccion);
        }

        // Datos de trabajo
        if (rbTrabajaSi.isChecked()) {
            data.setLugar_trabajo(etLugarTrabajo.getText().toString().trim());
            data.setDireccion_trabajo(etDireccionTrabajo.getText().toString().trim());
            String telTrabajo = etTelefonoTrabajo.getText().toString().trim();
            data.setTelefono_trabajo(telTrabajo.isEmpty() ? null : telTrabajo);
        } else {
            data.setLugar_trabajo(null);
            data.setDireccion_trabajo(null);
            data.setTelefono_trabajo(null);
        }

        updateRepository.updatePersonalData(sessionManager.getCedula(), data, new UpdateRepository.UpdateCallback() {
            @Override
            public void onSuccess(ApiResponse<String> response) {
                progressBar.setVisibility(View.GONE);
                btnSiguiente.setEnabled(true);
                showSuccessDialog();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                btnSiguiente.setEnabled(true);
                Log.e(TAG, "Error al actualizar: " + message);
                showErrorDialog("Error al actualizar los datos. Intente nuevamente.");
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage("Datos actualizados correctamente")
                .setPositiveButton("Aceptar", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    private void populateDataFromRepository() {
        RegistroCompleto data = RegistroRepository.getInstance().getRegistroData();
        if (data == null) return;

        // Campos básicos - solo llenar si están visibles
        if (tilNombres.getVisibility() == View.VISIBLE && data.getNombres() != null) {
            etNombres.setText(data.getNombres());
        }
        if (tilApellidos.getVisibility() == View.VISIBLE && data.getApellidos() != null) {
            etApellidos.setText(data.getApellidos());
        }
        if (tilEmail.getVisibility() == View.VISIBLE && data.getEmail() != null) {
            etEmail.setText(data.getEmail());
        }
        if (data.getCedula() != null) etCedula.setText(data.getCedula());
        
        // Campos adicionales
        if (data.getLugarNacimiento() != null) {
            actvLugarNacimiento.setText(data.getLugarNacimiento(), false);
            // Buscar ciudad por nombre
            for (Ciudad ciudad : ciudades) {
                if (ciudad.getNombre().equalsIgnoreCase(data.getLugarNacimiento())) {
                    ciudadSeleccionada = ciudad;
                    break;
                }
            }
        }
        if (data.getNacionalidad() != null) {
            actvNacionalidad.setText(data.getNacionalidad(), false);
            // Buscar país por nombre
            for (Pais pais : paises) {
                if (pais.getNombre().equalsIgnoreCase(data.getNacionalidad())) {
                    paisSeleccionado = pais;
                    loadCiudades(pais.getId());
                    break;
                }
            }
        }
        if (data.getTelefonoConvencional() != null) etTelefonoConvencional.setText(data.getTelefonoConvencional());
        if (data.getTelefonoMovil() != null) etTelefonoMovil.setText(data.getTelefonoMovil());
        if (data.getOcupacion() != null) etOcupacion.setText(data.getOcupacion());

        if (data.getFechaNacimiento() != null) {
            try {
                Date date = apiDateFormat.parse(data.getFechaNacimiento());
                etFechaNacimiento.setText(displayDateFormat.format(date));
            } catch (ParseException e) {
                Log.e(TAG, "No se pudo parsear la fecha guardada", e);
            }
        }

        if (data.getNivelInstruccion() != null) {
            actvNivelInstruccion.setText(data.getNivelInstruccion(), false);
        }

        if (data.getLugarTrabajo() != null) {
            rbTrabajaSi.setChecked(true);
            layoutCamposTrabajo.setVisibility(View.VISIBLE);
            etLugarTrabajo.setText(data.getLugarTrabajo());
            if(data.getDireccionTrabajo() != null) etDireccionTrabajo.setText(data.getDireccionTrabajo());
            if (data.getTelefonoTrabajo() != null) etTelefonoTrabajo.setText(data.getTelefonoTrabajo());
        } else {
            rbTrabajaNo.setChecked(true);
            layoutCamposTrabajo.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                if (isUpdateMode) {
                    updateData();
                } else {
                    saveDataAndContinue();
                }
            }
        });

        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void setupDropdowns() {
        // Configurar nivel de instrucción
        String[] niveles = getResources().getStringArray(R.array.niveles_instruccion);
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, niveles);
        actvNivelInstruccion.setAdapter(adapterNiveles);
        
        // Configurar listeners para AutoCompleteTextView de países y ciudades
        actvNacionalidad.setOnClickListener(v -> {
            if (paises.isEmpty()) {
                Toast.makeText(this, "Cargando países...", Toast.LENGTH_SHORT).show();
                return;
            }
            actvNacionalidad.showDropDown();
        });
        
        actvLugarNacimiento.setOnClickListener(v -> {
            if (ciudades.isEmpty()) {
                Toast.makeText(this, "Primero selecciona tu nacionalidad", Toast.LENGTH_SHORT).show();
                return;
            }
            actvLugarNacimiento.showDropDown();
        });

        rgTrabaja.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTrabajaSi) {
                animateViewVisibility(layoutCamposTrabajo, true);
            } else if (checkedId == R.id.rbTrabajaNo) {
                animateViewVisibility(layoutCamposTrabajo, false);
                etLugarTrabajo.setText("");
                etDireccionTrabajo.setText("");
                etTelefonoTrabajo.setText("");
                tilLugarTrabajo.setError(null);
                tilDireccionTrabajo.setError(null);
                tilTelefonoTrabajo.setError(null);
            }
        });

        actvNivelInstruccion.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = actvNivelInstruccion.getText().toString();
            if ("Otro".equals(selectedItem)) {
                animateViewVisibility(tilNivelInstruccionOtro, true);
            } else {
                animateViewVisibility(tilNivelInstruccionOtro, false);
                etNivelInstruccionOtro.setText("");
                tilNivelInstruccionOtro.setError(null);
            }
        });
    }

    private void loadDropdownData() {
        loadPaises();
    }

    private void loadPaises() {
        apiService.getPaises().enqueue(new Callback<ApiResponse<List<Pais>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Pais>>> call, Response<ApiResponse<List<Pais>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    paises = response.body().getData();
                    Log.d(TAG, "Países cargados: " + paises.size());
                    
                    // Configurar adapter para AutoCompleteTextView de países
                    ArrayAdapter<Pais> adapterPaises = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, paises);
                    actvNacionalidad.setAdapter(adapterPaises);
                    
                    // Configurar listener para selección de país
                    actvNacionalidad.setOnItemClickListener((parent, view, position, id) -> {
                        paisSeleccionado = paises.get(position);
                        loadCiudades(paisSeleccionado.getId());
                        // Limpiar ciudad seleccionada
                        actvLugarNacimiento.setText("");
                        ciudadSeleccionada = null;
                    });
                } else {
                    Log.e(TAG, "Error al cargar países: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Pais>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar países", t);
            }
        });
    }

    private void loadCiudades(int paisId) {
        apiService.getCiudades(paisId).enqueue(new Callback<ApiResponse<List<Ciudad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ciudad>>> call, Response<ApiResponse<List<Ciudad>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ciudades = response.body().getData();
                    Log.d(TAG, "Ciudades cargadas: " + ciudades.size());
                    
                    // Configurar adapter para AutoCompleteTextView de ciudades
                    ArrayAdapter<Ciudad> adapterCiudades = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, ciudades);
                    actvLugarNacimiento.setAdapter(adapterCiudades);
                    
                    // Configurar listener para selección de ciudad
                    actvLugarNacimiento.setOnItemClickListener((parent, view, position, id) -> {
                        ciudadSeleccionada = ciudades.get(position);
                    });
                } else {
                    Log.e(TAG, "Error al cargar ciudades: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ciudad>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar ciudades", t);
            }
        });
    }


    private void animateViewVisibility(final View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.setTranslationY(20f);
            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .start();
        } else {
            view.animate()
                    .alpha(0f)
                    .translationY(20f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void setupDatePicker() {
        View.OnClickListener datePickerListener = v -> showDatePicker();
        etFechaNacimiento.setOnClickListener(datePickerListener);
        tilFechaNacimiento.setEndIconOnClickListener(datePickerListener);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etFechaNacimiento.setText(displayDateFormat.format(calendar.getTime()));
            tilFechaNacimiento.setError(null);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void startAnimations() {
        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ivLogo.animate().rotation(360f).setDuration(800).setStartDelay(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        formContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        tvInfo.animate().alpha(0.8f).setDuration(600).setStartDelay(500).start();
        animateProgressIndicator();
        handler.postDelayed(this::animatePaws, 700);
    }

    private void animateProgressIndicator() {
        for (int i = 0; i < progressIndicator.getChildCount(); i++) {
            View child = progressIndicator.getChildAt(i);
            child.setScaleX(0f);
            child.animate().scaleX(1f).setDuration(300).setStartDelay(100L * i).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
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
        boolean isValid = true;

        // Validar campos básicos solo si están visibles
        if (tilNombres.getVisibility() == View.VISIBLE) {
            if (etNombres.getText().toString().trim().isEmpty()) {
                tilNombres.setError("Los nombres son obligatorios");
                shakeView(tilNombres);
                isValid = false;
            } else {
                tilNombres.setError(null);
            }
        }

        if (tilApellidos.getVisibility() == View.VISIBLE) {
            if (etApellidos.getText().toString().trim().isEmpty()) {
                tilApellidos.setError("Los apellidos son obligatorios");
                shakeView(tilApellidos);
                isValid = false;
            } else {
                tilApellidos.setError(null);
            }
        }

        if (tilEmail.getVisibility() == View.VISIBLE) {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                tilEmail.setError("El email es obligatorio");
                shakeView(tilEmail);
                isValid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Email inválido");
                shakeView(tilEmail);
                isValid = false;
            } else {
                tilEmail.setError(null);
            }
        }

        // Validación de cédula solo en modo registro
        if (!isUpdateMode) {
            String cedula = etCedula.getText().toString().trim();
            if (cedula.isEmpty()) {
                tilCedula.setError("La cédula es obligatoria");
                shakeView(tilCedula);
                isValid = false;
            } else if (cedula.length() != 10) {
                tilCedula.setError("La cédula debe tener 10 dígitos");
                shakeView(tilCedula);
                isValid = false;
            } else if (!validarCedulaEcuatoriana(cedula)) {
                tilCedula.setError("Cédula inválida");
                shakeView(tilCedula);
                isValid = false;
            } else {
                tilCedula.setError(null);
            }
        }

        if (etFechaNacimiento.getText().toString().trim().isEmpty()) {
            tilFechaNacimiento.setError("La fecha de nacimiento es obligatoria");
            shakeView(tilFechaNacimiento);
            isValid = false;
        } else {
            tilFechaNacimiento.setError(null);
        }

        if (actvLugarNacimiento.getText().toString().trim().isEmpty()) {
            tilLugarNacimiento.setError("El lugar de nacimiento es obligatorio");
            shakeView(tilLugarNacimiento);
            isValid = false;
        } else {
            tilLugarNacimiento.setError(null);
        }

        if (actvNacionalidad.getText().toString().trim().isEmpty()) {
            tilNacionalidad.setError("La nacionalidad es obligatoria");
            shakeView(tilNacionalidad);
            isValid = false;
        } else {
            tilNacionalidad.setError(null);
        }

        String telefonoMovil = etTelefonoMovil.getText().toString().trim();
        if (telefonoMovil.isEmpty()) {
            tilTelefonoMovil.setError("El teléfono móvil es obligatorio");
            shakeView(tilTelefonoMovil);
            isValid = false;
        } else if (telefonoMovil.length() != 10) {
            tilTelefonoMovil.setError("El teléfono debe tener 10 dígitos");
            shakeView(tilTelefonoMovil);
            isValid = false;
        } else if (!telefonoMovil.startsWith("09")) {
            tilTelefonoMovil.setError("El teléfono móvil debe empezar con 09");
            shakeView(tilTelefonoMovil);
            isValid = false;
        } else {
            tilTelefonoMovil.setError(null);
        }

        if (etOcupacion.getText().toString().trim().isEmpty()) {
            tilOcupacion.setError("La ocupación es obligatoria");
            shakeView(tilOcupacion);
            isValid = false;
        } else {
            tilOcupacion.setError(null);
        }

        if (actvNivelInstruccion.getText().toString().trim().isEmpty()) {
            tilNivelInstruccion.setError("El nivel de instrucción es obligatorio");
            shakeView(tilNivelInstruccion);
            isValid = false;
        } else {
            tilNivelInstruccion.setError(null);

            if ("Otro".equals(actvNivelInstruccion.getText().toString().trim())) {
                if (etNivelInstruccionOtro.getText().toString().trim().isEmpty()) {
                    tilNivelInstruccionOtro.setError("Debe especificar el nivel de instrucción");
                    shakeView(tilNivelInstruccionOtro);
                    isValid = false;
                } else {
                    tilNivelInstruccionOtro.setError(null);
                }
            }
        }

        if (rgTrabaja.getCheckedRadioButtonId() == -1) {
            shakeView(rgTrabaja);
            isValid = false;
        } else if (rgTrabaja.getCheckedRadioButtonId() == R.id.rbTrabajaSi) {
            if (etLugarTrabajo.getText().toString().trim().isEmpty()) {
                tilLugarTrabajo.setError("El lugar de trabajo es obligatorio");
                shakeView(tilLugarTrabajo);
                isValid = false;
            } else {
                tilLugarTrabajo.setError(null);
            }

            if (etDireccionTrabajo.getText().toString().trim().isEmpty()) {
                tilDireccionTrabajo.setError("La dirección de trabajo es obligatoria");
                shakeView(tilDireccionTrabajo);
                isValid = false;
            } else {
                tilDireccionTrabajo.setError(null);
            }
        }

        return isValid;
    }

    private boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula.length() != 10) return false;
        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) return false;
            int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
            if (tercerDigito > 5) return false;
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Integer.parseInt(cedula.substring(i, i + 1));
                int resultado = digito * coeficientes[i];
                if (resultado > 9) resultado -= 9;
                suma += resultado;
            }
            int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));
            int modulo = suma % 10;
            int verificadorCalculado = modulo == 0 ? 0 : 10 - modulo;
            return digitoVerificador == verificadorCalculado;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void shakeView(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).setDuration(500).start();
    }

    private void saveDataAndContinue() {
        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);
        btnRegresar.setEnabled(false);
        saveDataToRepository();
        handler.postDelayed(this::animateSuccessTransition, 1500);
    }

    private void saveDataToRepository() {
        RegistroRepository repository = RegistroRepository.getInstance();
        RegistroCompleto data = repository.getRegistroData();

        // Guardar campos básicos solo si están visibles (para evitar sobreescribir datos de RegistroActivity)
        if (tilNombres.getVisibility() == View.VISIBLE) {
            data.setNombres(etNombres.getText().toString().trim());
        }
        if (tilApellidos.getVisibility() == View.VISIBLE) {
            data.setApellidos(etApellidos.getText().toString().trim());
        }
        if (tilEmail.getVisibility() == View.VISIBLE) {
            data.setEmail(etEmail.getText().toString().trim());
        }
        
        // Siempre guardar cédula
        data.setCedula(etCedula.getText().toString().trim());
        
        // Guardar fecha de nacimiento
        try {
            Date date = displayDateFormat.parse(etFechaNacimiento.getText().toString());
            data.setFechaNacimiento(apiDateFormat.format(date));
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear la fecha", e);
            data.setFechaNacimiento(null);
        }
        
        // Guardar otros campos
        data.setLugarNacimiento(ciudadSeleccionada != null ? ciudadSeleccionada.getNombre() : actvLugarNacimiento.getText().toString().trim());
        data.setNacionalidad(paisSeleccionado != null ? paisSeleccionado.getNombre() : actvNacionalidad.getText().toString().trim());

        String telConvencional = etTelefonoConvencional.getText().toString().trim();
        data.setTelefonoConvencional(telConvencional.isEmpty() ? null : telConvencional);

        data.setTelefonoMovil(etTelefonoMovil.getText().toString().trim());
        data.setOcupacion(etOcupacion.getText().toString().trim());

        String nivelInstruccion = actvNivelInstruccion.getText().toString().trim();
        if ("Otro".equals(nivelInstruccion)) {
            String otroNivel = etNivelInstruccionOtro.getText().toString().trim();
            data.setNivelInstruccion(otroNivel.isEmpty() ? null : otroNivel);
        } else {
            data.setNivelInstruccion(nivelInstruccion);
        }

        if (rbTrabajaSi.isChecked()) {
            String lugarTrabajo = etLugarTrabajo.getText().toString().trim();
            String direccionTrabajo = etDireccionTrabajo.getText().toString().trim();
            String telefonoTrabajo = etTelefonoTrabajo.getText().toString().trim();

            data.setLugarTrabajo(lugarTrabajo);
            data.setDireccionTrabajo(direccionTrabajo);
            data.setTelefonoTrabajo(telefonoTrabajo.isEmpty() ? null : telefonoTrabajo);
        } else {
            data.setLugarTrabajo(null);
            data.setDireccionTrabajo(null);
            data.setTelefonoTrabajo(null);
        }

        Log.i(TAG, "Datos personales guardados en el repositorio");
    }

    private void animateSuccessTransition() {
        AnimatorSet successSet = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formScale = ObjectAnimator.ofFloat(formContainer, "scaleX", 1f, 0.9f);
        ObjectAnimator formScaleY = ObjectAnimator.ofFloat(formContainer, "scaleY", 1f, 0.9f);
        successSet.playTogether(logoAlpha, formAlpha, formScale, formScaleY);
        successSet.setDuration(400);
        successSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(DatosPersonalesActivity.this, ReferenciasPersonalesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        successSet.start();
    }

    private void handleExitAnimation() {
        AnimatorSet exitSet = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator logoTranslation = ObjectAnimator.ofFloat(logoContainer, "translationY", 0f, -50f);
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formTranslation = ObjectAnimator.ofFloat(formContainer, "translationY", 0f, 50f);
        exitSet.playTogether(logoAlpha, logoTranslation, formAlpha, formTranslation);
        exitSet.setDuration(300);
        exitSet.setInterpolator(new AccelerateDecelerateInterpolator());
        exitSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isUpdateMode) {
                    finish();
                } else {
                    Intent intent = new Intent(DatosPersonalesActivity.this, RegistroActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
        exitSet.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        ivLogo.clearAnimation();
        logoContainer.clearAnimation();
        formContainer.clearAnimation();
        layoutPaws.clearAnimation();
    }
}