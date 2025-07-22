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
import com.mjc.adoptme.models.Provincia;
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
    private TextInputLayout tilNombres, tilApellidos, tilEmail, tilCedula, tilFechaNacimiento, tilPais, tilProvincia, tilCiudad;
    private TextInputLayout tilTelefonoConvencional, tilTelefonoMovil, tilOcupacion, tilNivelInstruccion, tilLugarTrabajo, tilTelefonoTrabajo, tilDireccionTrabajo;
    private TextInputLayout tilNivelInstruccionOtro;
    private TextInputEditText etNombres, etApellidos, etEmail, etCedula, etFechaNacimiento;
    private TextInputEditText etTelefonoConvencional, etTelefonoMovil, etOcupacion, etLugarTrabajo, etTelefonoTrabajo, etDireccionTrabajo;
    private TextInputEditText etNivelInstruccionOtro;
    private AutoCompleteTextView actvNivelInstruccion, actvPais, actvProvincia, actvCiudad;
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
    private List<Provincia> provincias = new ArrayList<>();
    private List<Ciudad> ciudades = new ArrayList<>();
    private Pais paisSeleccionado;
    private Provincia provinciaSeleccionada;
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

        setupDropdowns();
        loadDropdownData();
        
        // Data population happens after dropdown data is loaded
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
        tilPais = findViewById(R.id.tilPais);
        tilProvincia = findViewById(R.id.tilProvincia);
        tilCiudad = findViewById(R.id.tilCiudad);
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
        actvPais = findViewById(R.id.actvPais);
        actvProvincia = findViewById(R.id.actvProvincia);
        actvCiudad = findViewById(R.id.actvCiudad);
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
        Log.d(TAG, "=== POBLANDO FORMULARIO ===");
        Log.d(TAG, "Nombres: " + data.getNombres());
        Log.d(TAG, "Apellidos: " + data.getApellidos());
        Log.d(TAG, "Email: " + data.getEmail());
        Log.d(TAG, "Fecha nacimiento: " + data.getFecha_nacimiento());
        Log.d(TAG, "Lugar nacimiento: " + data.getLugar_nacimiento());
        Log.d(TAG, "Nivel instrucción: " + data.getNivel_instruccion());
        Log.d(TAG, "Países disponibles: " + paises.size());
        
        // Rellenar campos básicos
        // Cédula se obtiene de session manager, no de los datos
        if (sessionManager.getCedula() != null) etCedula.setText(sessionManager.getCedula());
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

        // Parsear lugar de nacimiento: "País, Provincia, Ciudad"
        if (data.getLugar_nacimiento() != null && !data.getLugar_nacimiento().isEmpty()) {
            String[] partes = data.getLugar_nacimiento().split(",\\s*");
            if (partes.length >= 3) {
                String nombrePais = partes[0].trim();
                String nombreProvincia = partes[1].trim();
                String nombreCiudad = partes[2].trim();
                
                // Buscar y seleccionar país
                for (Pais pais : paises) {
                    if (pais.getNombre().equalsIgnoreCase(nombrePais)) {
                        paisSeleccionado = pais;
                        actvPais.setText(pais.getNombre(), false);
                        
                        // Cargar provincias y luego seleccionar la provincia
                        loadProvinciasAndSelect(pais.getId(), nombreProvincia, nombreCiudad);
                        break;
                    }
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
        // NO establecer cedula aquí, ya se envía en el nivel superior del JSON
        
        // Inicializar todos los campos para evitar nulls
        data.setNombres("");
        data.setApellidos("");
        data.setFecha_nacimiento("");
        data.setLugar_nacimiento("");
        data.setEmail("");
        data.setTelefono_convencional("");
        data.setTelefono_movil("");
        data.setOcupacion("");
        data.setNivel_instruccion("");
        data.setLugar_trabajo("");
        data.setDireccion_trabajo("");
        data.setTelefono_trabajo("");
        
        // Log para debugging
        Log.d(TAG, "=== DATOS PARA ACTUALIZAR ===");
        Log.d(TAG, "Cédula (session): " + sessionManager.getCedula());
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
            Log.e(TAG, "Error al parsear fecha", e);
            // Mantener string vacío ya inicializado
        }

        // Concatenar lugar de nacimiento: "País, Provincia, Ciudad"
        String lugarNacimiento = "";
        if (paisSeleccionado != null && provinciaSeleccionada != null && ciudadSeleccionada != null) {
            lugarNacimiento = paisSeleccionado.getNombre() + ", " + 
                             provinciaSeleccionada.getNombre() + ", " + 
                             ciudadSeleccionada.getNombre();
        } else {
            // Fallback en caso de que no se hayan seleccionado desde dropdowns
            String pais = actvPais.getText().toString().trim();
            String provincia = actvProvincia.getText().toString().trim();
            String ciudad = actvCiudad.getText().toString().trim();
            if (!pais.isEmpty() && !provincia.isEmpty() && !ciudad.isEmpty()) {
                lugarNacimiento = pais + ", " + provincia + ", " + ciudad;
            }
        }
        data.setLugar_nacimiento(lugarNacimiento);

        String telConv = etTelefonoConvencional.getText().toString().trim();
        data.setTelefono_convencional(telConv);

        data.setTelefono_movil(etTelefonoMovil.getText().toString().trim());
        data.setOcupacion(etOcupacion.getText().toString().trim());

        String nivelInstruccion = actvNivelInstruccion.getText().toString().trim();
        if ("Otro".equals(nivelInstruccion)) {
            data.setNivel_instruccion(etNivelInstruccionOtro.getText().toString().trim());
        } else {
            data.setNivel_instruccion(nivelInstruccion);
        }

        // Datos de trabajo - SIEMPRE enviar algo para evitar constraint violations
        if (rbTrabajaSi.isChecked()) {
            String lugarTrabajo = etLugarTrabajo.getText().toString().trim();
            String direccionTrabajo = etDireccionTrabajo.getText().toString().trim();
            String telTrabajo = etTelefonoTrabajo.getText().toString().trim();
            
            data.setLugar_trabajo(lugarTrabajo.isEmpty() ? "No especificado" : lugarTrabajo);
            data.setDireccion_trabajo(direccionTrabajo.isEmpty() ? "No especificado" : direccionTrabajo);
            data.setTelefono_trabajo(telTrabajo);
        } else {
            // Cuando no trabaja, mantener strings vacíos ya inicializados
            data.setLugar_trabajo("No trabaja");
            data.setDireccion_trabajo("No aplica");
            // telefono_trabajo queda como string vacío
        }

        // Log datos finales antes de enviar
        Log.d(TAG, "=== DATOS FINALES PARA ENVIAR ===");
        Log.d(TAG, "nombres: " + data.getNombres());
        Log.d(TAG, "apellidos: " + data.getApellidos());
        Log.d(TAG, "fecha_nacimiento: " + data.getFecha_nacimiento());
        Log.d(TAG, "lugar_nacimiento: " + data.getLugar_nacimiento());
        Log.d(TAG, "email: " + data.getEmail());
        Log.d(TAG, "telefono_convencional: " + data.getTelefono_convencional());
        Log.d(TAG, "telefono_movil: " + data.getTelefono_movil());
        Log.d(TAG, "ocupacion: " + data.getOcupacion());
        Log.d(TAG, "nivel_instruccion: " + data.getNivel_instruccion());
        Log.d(TAG, "lugar_trabajo: " + data.getLugar_trabajo());
        Log.d(TAG, "direccion_trabajo: " + data.getDireccion_trabajo());
        Log.d(TAG, "telefono_trabajo: " + data.getTelefono_trabajo());
        Log.d(TAG, "==============================");

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
        // Siempre llenar cédula desde el repository
        if (data.getCedula() != null) {
            etCedula.setText(data.getCedula());
        }
        
        // Parsear lugar de nacimiento: "País, Provincia, Ciudad"
        if (data.getLugarNacimiento() != null && !data.getLugarNacimiento().isEmpty()) {
            String[] partes = data.getLugarNacimiento().split(",\\s*");
            if (partes.length >= 3) {
                String nombrePais = partes[0].trim();
                String nombreProvincia = partes[1].trim();
                String nombreCiudad = partes[2].trim();
                
                // Buscar y seleccionar país
                for (Pais pais : paises) {
                    if (pais.getNombre().equalsIgnoreCase(nombrePais)) {
                        paisSeleccionado = pais;
                        actvPais.setText(pais.getNombre(), false);
                        
                        // Cargar provincias y luego seleccionar la provincia
                        loadProvinciasAndSelect(pais.getId(), nombreProvincia, nombreCiudad);
                        break;
                    }
                }
            }
        }
        if (data.getTelefonoConvencional() != null) etTelefonoConvencional.setText(data.getTelefonoConvencional());
        if (data.getTelefonoMovil() != null) etTelefonoMovil.setText(data.getTelefonoMovil());
        if (data.getOcupacion() != null) etOcupacion.setText(data.getOcupacion());

        if (data.getFechaNacimiento() != null && !data.getFechaNacimiento().trim().isEmpty()) {
            try {
                Date date = apiDateFormat.parse(data.getFechaNacimiento());
                etFechaNacimiento.setText(displayDateFormat.format(date));
            } catch (ParseException e) {
                Log.e(TAG, "No se pudo parsear la fecha guardada: '" + data.getFechaNacimiento() + "'", e);
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

        btnRegresar.setOnClickListener(v -> {
            // Save current data before going back
            if (isRegistrationMode) {
                saveDataToRepositoryForBackButton();
            }
            handleExitAnimation();
        });
    }

    private void setupDropdowns() {
        // Configurar nivel de instrucción
        String[] niveles = getResources().getStringArray(R.array.niveles_instruccion);
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, niveles);
        actvNivelInstruccion.setAdapter(adapterNiveles);
        
        // Configurar listeners para AutoCompleteTextView cascadeados
        actvPais.setOnClickListener(v -> {
            if (paises.isEmpty()) {
                Toast.makeText(this, "Cargando países...", Toast.LENGTH_SHORT).show();
                return;
            }
            actvPais.showDropDown();
        });
        
        actvProvincia.setOnClickListener(v -> {
            if (paisSeleccionado == null) {
                Toast.makeText(this, "Primero seleccione un país", Toast.LENGTH_SHORT).show();
                return;
            }
            if (provincias.isEmpty()) {
                Toast.makeText(this, "Cargando provincias...", Toast.LENGTH_SHORT).show();
                return;
            }
            actvProvincia.showDropDown();
        });
        
        actvCiudad.setOnClickListener(v -> {
            if (provinciaSeleccionada == null) {
                Toast.makeText(this, "Primero seleccione una provincia", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ciudades.isEmpty()) {
                Toast.makeText(this, "Cargando ciudades...", Toast.LENGTH_SHORT).show();
                return;
            }
            actvCiudad.showDropDown();
        });

        rgTrabaja.setOnCheckedChangeListener((group, checkedId) -> {
            // Cancel any existing animations and reset state
            layoutCamposTrabajo.animate().cancel();
            layoutCamposTrabajo.clearAnimation();
            
            if (checkedId == R.id.rbTrabajaSi) {
                // Show work fields with animation
                layoutCamposTrabajo.setVisibility(View.VISIBLE);
                layoutCamposTrabajo.setAlpha(0f);
                layoutCamposTrabajo.setTranslationY(20f);
                layoutCamposTrabajo.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setListener(null)
                    .start();
            } else if (checkedId == R.id.rbTrabajaNo) {
                // Clear work fields and hide with animation
                etLugarTrabajo.setText("");
                etDireccionTrabajo.setText("");
                etTelefonoTrabajo.setText("");
                tilLugarTrabajo.setError(null);
                tilDireccionTrabajo.setError(null);
                tilTelefonoTrabajo.setError(null);
                
                layoutCamposTrabajo.animate()
                    .alpha(0f)
                    .translationY(20f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            layoutCamposTrabajo.setVisibility(View.GONE);
                            layoutCamposTrabajo.setAlpha(1f);
                            layoutCamposTrabajo.setTranslationY(0f);
                        }
                    })
                    .start();
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
                    actvPais.setAdapter(adapterPaises);
                    
                    // Configurar listener para selección de país
                    actvPais.setOnItemClickListener((parent, view, position, id) -> {
                        paisSeleccionado = paises.get(position);
                        Log.d(TAG, "País seleccionado: " + paisSeleccionado.getNombre() + " (ID: " + paisSeleccionado.getId() + ")");
                        
                        // Limpiar provincia y ciudad seleccionadas
                        actvProvincia.setText("");
                        actvCiudad.setText("");
                        provinciaSeleccionada = null;
                        ciudadSeleccionada = null;
                        provincias.clear();
                        ciudades.clear();
                        
                        // Validar que el ID del país es válido antes de llamar a la API
                        if (paisSeleccionado.getId() > 0) {
                            loadProvincias(paisSeleccionado.getId());
                        } else {
                            Log.e(TAG, "ID de país inválido: " + paisSeleccionado.getId());
                            Toast.makeText(DatosPersonalesActivity.this, "Error: ID de país inválido", Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                    // Set Ecuador as default if available
                    for (Pais pais : paises) {
                        if ("Ecuador".equalsIgnoreCase(pais.getNombre())) {
                            paisSeleccionado = pais;
                            actvPais.setText(pais.getNombre(), false);
                            loadProvincias(pais.getId());
                            break;
                        }
                    }
                    
                    // Populate data after countries are loaded
                    if (isUpdateMode) {
                        // En modo actualización, cargar datos del usuario desde la API
                        loadUserData();
                    } else {
                        // En modo registro, cargar datos desde el repository local
                        populateDataFromRepository();
                    }
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

    private void loadProvincias(int paisId) {
        Log.d(TAG, "Cargando provincias para país ID: " + paisId);
        
        apiService.getProvincias(paisId).enqueue(new Callback<ApiResponse<List<Provincia>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Provincia>>> call, Response<ApiResponse<List<Provincia>>> response) {
                Log.d(TAG, "Respuesta recibida para provincias. Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    provincias = response.body().getData();
                    Log.d(TAG, "Provincias cargadas exitosamente: " + provincias.size());
                    
                    if (provincias.isEmpty()) {
                        Log.w(TAG, "No se encontraron provincias para el país ID: " + paisId);
                        Toast.makeText(DatosPersonalesActivity.this, 
                            "No se encontraron provincias para este país", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Configurar adapter para AutoCompleteTextView de provincias
                    ArrayAdapter<Provincia> adapterProvincias = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, provincias);
                    actvProvincia.setAdapter(adapterProvincias);
                    
                    // Configurar listener para selección de provincia
                    actvProvincia.setOnItemClickListener((parent, view, position, id) -> {
                        provinciaSeleccionada = provincias.get(position);
                        Log.d(TAG, "Provincia seleccionada: " + provinciaSeleccionada.getNombre());
                        
                        // Limpiar ciudad seleccionada
                        actvCiudad.setText("");
                        ciudadSeleccionada = null;
                        ciudades.clear();
                        
                        // Cargar ciudades de la provincia seleccionada
                        loadCiudades(provinciaSeleccionada.getId());
                    });
                } else {
                    Log.e(TAG, "Error al cargar provincias. Código: " + response.code() + 
                           ", Mensaje: " + response.message());
                    Toast.makeText(DatosPersonalesActivity.this, 
                        "Error al cargar provincias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Provincia>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar provincias para país ID: " + paisId, t);
                Toast.makeText(DatosPersonalesActivity.this, 
                    "Error de conexión. Verifique su internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProvinciasAndSelect(int paisId, String nombreProvincia, String nombreCiudad) {
        apiService.getProvincias(paisId).enqueue(new Callback<ApiResponse<List<Provincia>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Provincia>>> call, Response<ApiResponse<List<Provincia>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    provincias = response.body().getData();
                    
                    // Configurar adapter
                    ArrayAdapter<Provincia> adapterProvincias = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, provincias);
                    actvProvincia.setAdapter(adapterProvincias);
                    
                    // Buscar y seleccionar provincia
                    for (Provincia provincia : provincias) {
                        if (provincia.getNombre().equalsIgnoreCase(nombreProvincia)) {
                            provinciaSeleccionada = provincia;
                            actvProvincia.setText(provincia.getNombre(), false);
                            
                            // Cargar ciudades y luego seleccionar la ciudad
                            loadCiudadesAndSelect(provincia.getId(), nombreCiudad);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Provincia>>> call, Throwable t) {
                Log.e(TAG, "Error al cargar provincias para restaurar datos", t);
            }
        });
    }

    private void loadCiudadesAndSelect(int provinciaId, String nombreCiudad) {
        apiService.getCiudades(provinciaId).enqueue(new Callback<ApiResponse<List<Ciudad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ciudad>>> call, Response<ApiResponse<List<Ciudad>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ciudades = response.body().getData();
                    
                    // Configurar adapter
                    ArrayAdapter<Ciudad> adapterCiudades = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, ciudades);
                    actvCiudad.setAdapter(adapterCiudades);
                    
                    // Buscar y seleccionar ciudad
                    for (Ciudad ciudad : ciudades) {
                        if (ciudad.getNombre().equalsIgnoreCase(nombreCiudad)) {
                            ciudadSeleccionada = ciudad;
                            actvCiudad.setText(ciudad.getNombre(), false);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ciudad>>> call, Throwable t) {
                Log.e(TAG, "Error al cargar ciudades para restaurar datos", t);
            }
        });
    }

    private void loadCiudades(int provinciaId) {
        Log.d(TAG, "Cargando ciudades para provincia ID: " + provinciaId);
        
        apiService.getCiudades(provinciaId).enqueue(new Callback<ApiResponse<List<Ciudad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ciudad>>> call, Response<ApiResponse<List<Ciudad>>> response) {
                Log.d(TAG, "Respuesta recibida para ciudades. Código: " + response.code());
                
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "Cuerpo de respuesta no nulo");
                        ApiResponse<List<Ciudad>> apiResponse = response.body();
                        
                        if (apiResponse.getData() != null) {
                            ciudades = apiResponse.getData();
                            Log.d(TAG, "Ciudades cargadas exitosamente: " + ciudades.size());
                            
                            if (ciudades.isEmpty()) {
                                Log.w(TAG, "No se encontraron ciudades para la provincia ID: " + provinciaId);
                                Toast.makeText(DatosPersonalesActivity.this, 
                                    "No se encontraron ciudades para esta provincia", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            
                            // Configurar adapter para AutoCompleteTextView de ciudades
                            ArrayAdapter<Ciudad> adapterCiudades = new ArrayAdapter<>(DatosPersonalesActivity.this, 
                                    android.R.layout.simple_dropdown_item_1line, ciudades);
                            actvCiudad.setAdapter(adapterCiudades);
                            
                            // Configurar listener para selección de ciudad
                            actvCiudad.setOnItemClickListener((parent, view, position, id) -> {
                                ciudadSeleccionada = ciudades.get(position);
                                Log.d(TAG, "Ciudad seleccionada: " + ciudadSeleccionada.getNombre());
                            });
                        } else {
                            Log.e(TAG, "Datos de ciudades son nulos en la respuesta");
                            Toast.makeText(DatosPersonalesActivity.this, 
                                "Error: No se recibieron datos de ciudades", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Cuerpo de respuesta es nulo");
                        Toast.makeText(DatosPersonalesActivity.this, 
                            "Error: Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Error al cargar ciudades. Código: " + response.code() + 
                           ", Mensaje: " + response.message());
                    
                    String errorMessage = "Error al cargar ciudades";
                    if (response.code() == 500) {
                        errorMessage = "Error interno del servidor. Verifique el ID de la provincia.";
                    } else if (response.code() == 404) {
                        errorMessage = "No se encontraron ciudades para esta provincia.";
                    }
                    
                    Toast.makeText(DatosPersonalesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ciudad>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar ciudades para provincia ID: " + provinciaId, t);
                Toast.makeText(DatosPersonalesActivity.this, 
                    "Error de conexión. Verifique su internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void animateViewVisibility(final View view, boolean show) {
        // Clear any existing animation to prevent conflicts
        view.clearAnimation();
        view.animate().cancel();
        
        if (show) {
            // Ensure view is visible before animation
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.setTranslationY(20f);
            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setListener(null) // Clear any previous listeners
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
                            // Clear animation state
                            view.setAlpha(1f);
                            view.setTranslationY(0f);
                        }
                        
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            view.setVisibility(View.GONE);
                            view.setAlpha(1f);
                            view.setTranslationY(0f);
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

        if (actvPais.getText().toString().trim().isEmpty()) {
            tilPais.setError("El país es obligatorio");
            shakeView(tilPais);
            isValid = false;
        } else {
            tilPais.setError(null);
        }

        if (actvProvincia.getText().toString().trim().isEmpty()) {
            tilProvincia.setError("La provincia es obligatoria");
            shakeView(tilProvincia);
            isValid = false;
        } else {
            tilProvincia.setError(null);
        }

        if (actvCiudad.getText().toString().trim().isEmpty()) {
            tilCiudad.setError("La ciudad es obligatoria");
            shakeView(tilCiudad);
            isValid = false;
        } else {
            tilCiudad.setError(null);
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
            data.setFechaNacimiento("");
        }
        
        // Concatenar lugar de nacimiento: "País, Provincia, Ciudad"
        String lugarNacimiento = "";
        if (paisSeleccionado != null && provinciaSeleccionada != null && ciudadSeleccionada != null) {
            lugarNacimiento = paisSeleccionado.getNombre() + ", " + 
                             provinciaSeleccionada.getNombre() + ", " + 
                             ciudadSeleccionada.getNombre();
        } else {
            // Fallback en caso de que no se hayan seleccionado desde dropdowns
            String pais = actvPais.getText().toString().trim();
            String provincia = actvProvincia.getText().toString().trim();
            String ciudad = actvCiudad.getText().toString().trim();
            if (!pais.isEmpty() && !provincia.isEmpty() && !ciudad.isEmpty()) {
                lugarNacimiento = pais + ", " + provincia + ", " + ciudad;
            }
        }
        data.setLugarNacimiento(lugarNacimiento);

        String telConvencional = etTelefonoConvencional.getText().toString().trim();
        data.setTelefonoConvencional(telConvencional);

        data.setTelefonoMovil(etTelefonoMovil.getText().toString().trim());
        data.setOcupacion(etOcupacion.getText().toString().trim());

        String nivelInstruccion = actvNivelInstruccion.getText().toString().trim();
        if ("Otro".equals(nivelInstruccion)) {
            String otroNivel = etNivelInstruccionOtro.getText().toString().trim();
            data.setNivelInstruccion(otroNivel.isEmpty() ? "No especificado" : otroNivel);
        } else {
            data.setNivelInstruccion(nivelInstruccion);
        }

        if (rbTrabajaSi.isChecked()) {
            String lugarTrabajo = etLugarTrabajo.getText().toString().trim();
            String direccionTrabajo = etDireccionTrabajo.getText().toString().trim();
            String telefonoTrabajo = etTelefonoTrabajo.getText().toString().trim();

            data.setLugarTrabajo(lugarTrabajo.isEmpty() ? "No especificado" : lugarTrabajo);
            data.setDireccionTrabajo(direccionTrabajo.isEmpty() ? "No especificado" : direccionTrabajo);
            data.setTelefonoTrabajo(telefonoTrabajo);
        } else {
            data.setLugarTrabajo("No trabaja");
            data.setDireccionTrabajo("No aplica");
            data.setTelefonoTrabajo("");
        }

        Log.i(TAG, "Datos personales guardados en el repositorio");
        Log.d(TAG, "Datos guardados - Nombres: " + data.getNombres() + ", Apellidos: " + data.getApellidos() + ", Email: " + data.getEmail());
    }

    private void saveDataToRepositoryForBackButton() {
        RegistroRepository repository = RegistroRepository.getInstance();
        RegistroCompleto data = repository.getRegistroData();

        // Solo guardar campos básicos que el usuario pudo haber editado
        if (tilNombres.getVisibility() == View.VISIBLE) {
            String nombres = etNombres.getText().toString().trim();
            if (!nombres.isEmpty()) {
                data.setNombres(nombres);
                Log.d(TAG, "Guardando nombres al regresar: " + nombres);
            }
        }
        if (tilApellidos.getVisibility() == View.VISIBLE) {
            String apellidos = etApellidos.getText().toString().trim();
            if (!apellidos.isEmpty()) {
                data.setApellidos(apellidos);
                Log.d(TAG, "Guardando apellidos al regresar: " + apellidos);
            }
        }
        if (tilEmail.getVisibility() == View.VISIBLE) {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                data.setEmail(email);
                Log.d(TAG, "Guardando email al regresar: " + email);
            }
        }
        
        Log.d(TAG, "Datos básicos guardados para regresar a RegistroActivity");
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
                    // In update mode, just finish to return to previous activity
                    finish();
                } else if (isRegistrationMode) {
                    // In registration mode, just finish to return to RegistroActivity without creating new instance
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    // Default behavior for other modes
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