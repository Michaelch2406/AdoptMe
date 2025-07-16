package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.Ciudad;
import com.mjc.adoptme.models.Domicilio;
import com.mjc.adoptme.models.Pais;
import com.mjc.adoptme.models.Parroquia;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.models.UpdateDataRequest;
import com.mjc.adoptme.network.ApiClient;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.UpdateRepository;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DomicilioActivity extends AppCompatActivity {
    private static final String TAG = "DomicilioActivity";

    // Vistas y Contenedores
    private LinearLayout logoContainer, formContainer, layoutArrendadaPrestada, layoutCerramiento;
    private TextView tvAppName, tvSubtitle, tvInfo;
    private View progress1, progress2, progress3;
    private MaterialButton btnSiguiente, btnRegresar;

    // Campos de Formulario
    private RadioGroup rgTipoVivienda, rgPropiedadVivienda, rgPermiteAnimales, rgCerramiento, rgPosibilidadEscape, rgFrecuenciaUso, rgTipoCerramiento;
    private TextInputLayout tilMetrosVivienda, tilMetrosAreaVerde, tilAreaComunal, tilAlturaCerramiento, tilNombresDueno, tilTelefonoDueno, tilEspecifiqueFrecuencia;
    private TextInputLayout tilParroquia;
    private TextInputEditText etMetrosVivienda, etMetrosAreaVerde, etAreaComunal, etAlturaCerramiento, etNombresDueno, etTelefonoDueno, etEspecifiqueFrecuencia;
    private AutoCompleteTextView actvParroquia;

    private RadioButton rbPropia, rbArrendada, rbPrestada;
    private RadioButton rbPermiteAnimalesSi, rbPermiteAnimalesNo;
    private RadioButton rbCerramientoSi, rbCerramientoNo;
    private RadioButton rbEscapeSi, rbEscapeNo;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isUpdateMode = false;
    private SessionManager sessionManager;
    private UpdateRepository updateRepository;
    private ApiService apiService;
    
    // Datos para dropdowns
    private List<Pais> paises = new ArrayList<>();
    private List<Ciudad> ciudades = new ArrayList<>();
    private List<Parroquia> parroquias = new ArrayList<>();
    private Pais paisSeleccionado;
    private Ciudad ciudadSeleccionada;
    private Parroquia parroquiaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilio);

        sessionManager = new SessionManager(this);
        updateRepository = new UpdateRepository();
        apiService = ApiClient.getApiService();

        initViews();

        isUpdateMode = getIntent().getBooleanExtra("IS_UPDATE_MODE", false);

        if (isUpdateMode) {
            btnSiguiente.setText("Actualizar");
            tvSubtitle.setText("Actualizar Datos de Domicilio");
            loadUserData();
        } else {
            populateDataFromRepository();
        }

        setupDropdowns();
        loadDropdownData();
        setupListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutArrendadaPrestada = findViewById(R.id.layoutArrendadaPrestada);
        layoutCerramiento = findViewById(R.id.layoutCerramiento);

        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);

        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnRegresar = findViewById(R.id.btnRegresar);

        // RadioGroups
        rgTipoVivienda = findViewById(R.id.rgTipoVivienda);
        rgPropiedadVivienda = findViewById(R.id.rgPropiedadVivienda);
        rgPermiteAnimales = findViewById(R.id.rgPermiteAnimales);
        rgCerramiento = findViewById(R.id.rgCerramiento);
        rgTipoCerramiento = findViewById(R.id.rgTipoCerramiento);
        rgPosibilidadEscape = findViewById(R.id.rgPosibilidadEscape);
        rgFrecuenciaUso = findViewById(R.id.rgFrecuenciaUso);

        // TextInputLayouts
        tilMetrosVivienda = findViewById(R.id.tilMetrosVivienda);
        tilMetrosAreaVerde = findViewById(R.id.tilMetrosAreaVerde);
        tilAreaComunal = findViewById(R.id.tilAreaComunal);
        tilAlturaCerramiento = findViewById(R.id.tilAlturaCerramiento);
        tilNombresDueno = findViewById(R.id.tilNombresDueno);
        tilTelefonoDueno = findViewById(R.id.tilTelefonoDueno);
        tilEspecifiqueFrecuencia = findViewById(R.id.tilEspecifiqueFrecuencia);
        tilParroquia = findViewById(R.id.tilParroquia);

        // TextInputEditTexts
        etMetrosVivienda = findViewById(R.id.etMetrosVivienda);
        etMetrosAreaVerde = findViewById(R.id.etMetrosAreaVerde);
        etAreaComunal = findViewById(R.id.etAreaComunal);
        etAlturaCerramiento = findViewById(R.id.etAlturaCerramiento);
        etNombresDueno = findViewById(R.id.etNombresDueno);
        etTelefonoDueno = findViewById(R.id.etTelefonoDueno);
        etEspecifiqueFrecuencia = findViewById(R.id.etEspecifiqueFrecuencia);
        actvParroquia = findViewById(R.id.actvParroquia);

        rbPropia = findViewById(R.id.rbPropia);
        rbArrendada = findViewById(R.id.rbArrendada);
        rbPrestada = findViewById(R.id.rbPrestada);

        rbPermiteAnimalesSi = findViewById(R.id.rbPermiteAnimalesSi);
        rbPermiteAnimalesNo = findViewById(R.id.rbPermiteAnimalesNo);

        rbCerramientoSi = findViewById(R.id.rbCerramientoSi);
        rbCerramientoNo = findViewById(R.id.rbCerramientoNo);

        rbEscapeSi = findViewById(R.id.rbEscapeSi);
        rbEscapeNo = findViewById(R.id.rbEscapeNo);

        // Ocultar secciones dinámicas al iniciar
        layoutArrendadaPrestada.setVisibility(View.GONE);
        layoutCerramiento.setVisibility(View.GONE);
        tilEspecifiqueFrecuencia.setVisibility(View.GONE);
    }

    private void setupDropdowns() {
        // Configurar listener para AutoCompleteTextView de parroquia
        actvParroquia.setOnClickListener(v -> {
            if (parroquias.isEmpty()) {
                Toast.makeText(this, "Cargando parroquias...", Toast.LENGTH_SHORT).show();
                return;
            }
            actvParroquia.showDropDown();
        });
        
        Log.d(TAG, "Configurando dropdowns para domicilio");
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
                    // Cargar ciudades del primer país (Ecuador por defecto)
                    if (!paises.isEmpty()) {
                        paisSeleccionado = paises.get(0); // Asumir Ecuador como primer país
                        loadCiudades(paisSeleccionado.getId());
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

    private void loadCiudades(int paisId) {
        apiService.getCiudades(paisId).enqueue(new Callback<ApiResponse<List<Ciudad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ciudad>>> call, Response<ApiResponse<List<Ciudad>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ciudades = response.body().getData();
                    Log.d(TAG, "Ciudades cargadas: " + ciudades.size());
                    // Cargar parroquias de la primera ciudad
                    if (!ciudades.isEmpty()) {
                        ciudadSeleccionada = ciudades.get(0);
                        loadParroquias(ciudadSeleccionada.getId());
                    }
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

    private void loadParroquias(int ciudadId) {
        apiService.getParroquias(ciudadId).enqueue(new Callback<ApiResponse<List<Parroquia>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Parroquia>>> call, Response<ApiResponse<List<Parroquia>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    parroquias = response.body().getData();
                    Log.d(TAG, "Parroquias cargadas: " + parroquias.size());
                    
                    // Configurar adapter para AutoCompleteTextView de parroquias
                    ArrayAdapter<Parroquia> adapterParroquias = new ArrayAdapter<>(DomicilioActivity.this, 
                            android.R.layout.simple_dropdown_item_1line, parroquias);
                    actvParroquia.setAdapter(adapterParroquias);
                    
                    // Configurar listener para selección de parroquia
                    actvParroquia.setOnItemClickListener((parent, view, position, id) -> {
                        parroquiaSeleccionada = parroquias.get(position);
                        Log.d(TAG, "Parroquia seleccionada: " + parroquiaSeleccionada.getNombre());
                    });
                    
                    // Seleccionar la primera parroquia por defecto
                    if (!parroquias.isEmpty()) {
                        parroquiaSeleccionada = parroquias.get(0);
                        actvParroquia.setText(parroquiaSeleccionada.getNombre(), false);
                        Log.d(TAG, "Parroquia seleccionada por defecto: " + parroquiaSeleccionada.getNombre());
                    }
                } else {
                    Log.e(TAG, "Error al cargar parroquias: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Parroquia>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar parroquias", t);
            }
        });
    }

    private void loadUserData() {
        String cedula = sessionManager.getCedula();
        updateRepository.getUserDomicilioData(cedula, new UpdateRepository.DataCallback<Domicilio>() {
            @Override
            public void onSuccess(Domicilio data) {
                populateForm(data);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error al cargar datos: " + message);
                showErrorDialog("Error al cargar los datos. Intente nuevamente.");
            }
        });
    }

    private void populateForm(Domicilio domicilio) {
        // Tipo de vivienda
        if (domicilio.getTipoVivienda() != null) {
            for (int i = 0; i < rgTipoVivienda.getChildCount(); i++) {
                RadioButton rb = (RadioButton) rgTipoVivienda.getChildAt(i);
                if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoVivienda())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }

        // Campos numéricos
        etMetrosVivienda.setText(String.valueOf(domicilio.getMetrosCuadradosVivienda()));
        etMetrosAreaVerde.setText(String.valueOf(domicilio.getMetrosCuadradosAreaVerde()));
        if (domicilio.getDimensionAreaComunal() != null) {
            etAreaComunal.setText(String.valueOf(domicilio.getDimensionAreaComunal()));
        }

        // Tipo de tenencia
        if (domicilio.getTipoTenencia() != null) {
            if (domicilio.getTipoTenencia().equalsIgnoreCase("Propia")) {
                rbPropia.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.GONE);
            } else if (domicilio.getTipoTenencia().equalsIgnoreCase("Arrendada")) {
                rbArrendada.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.VISIBLE);
            } else if (domicilio.getTipoTenencia().equalsIgnoreCase("Prestada")) {
                rbPrestada.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.VISIBLE);
            }

            if (layoutArrendadaPrestada.getVisibility() == View.VISIBLE) {
                if (domicilio.isPropietarioPermiteAnimales()) {
                    rbPermiteAnimalesSi.setChecked(true);
                } else {
                    rbPermiteAnimalesNo.setChecked(true);
                }
                if (domicilio.getNombrePropietario() != null) {
                    etNombresDueno.setText(domicilio.getNombrePropietario());
                }
                if (domicilio.getTelefonoPropietario() != null) {
                    etTelefonoDueno.setText(domicilio.getTelefonoPropietario());
                }
            }
        }

        // Cerramiento
        if (domicilio.isTieneCerramiento()) {
            rbCerramientoSi.setChecked(true);
            layoutCerramiento.setVisibility(View.VISIBLE);
            etAlturaCerramiento.setText(String.valueOf(domicilio.getAlturaCerramiento()));
            if (domicilio.getTipoCerramiento() != null) {
                for (int i = 0; i < rgTipoCerramiento.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) rgTipoCerramiento.getChildAt(i);
                    if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoCerramiento())) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        } else {
            rbCerramientoNo.setChecked(true);
            layoutCerramiento.setVisibility(View.GONE);
        }

        // Posibilidad de escape
        if (domicilio.isPuedeEscaparAnimal()) {
            rbEscapeSi.setChecked(true);
        } else {
            rbEscapeNo.setChecked(true);
        }

        // Tipo de residencia
        if (domicilio.getTipoResidencia() != null) {
            for (int i = 0; i < rgFrecuenciaUso.getChildCount(); i++) {
                RadioButton rb = (RadioButton) rgFrecuenciaUso.getChildAt(i);
                if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoResidencia())) {
                    rb.setChecked(true);
                    if (rb.getId() == R.id.rbVaDeVezEnCuando || rb.getId() == R.id.rbVaFinesSemana) {
                        tilEspecifiqueFrecuencia.setVisibility(View.VISIBLE);
                        if (domicilio.getEspecificacionResidencia() != null) {
                            etEspecifiqueFrecuencia.setText(domicilio.getEspecificacionResidencia());
                        }
                    }
                    break;
                }
            }
        }
    }

    private void updateData() {
        if (!validateForm()) return;

        btnSiguiente.setEnabled(false);

        Domicilio domicilio = new Domicilio();

        // Valores necesarios para la API
        domicilio.setParroquiaId(parroquiaSeleccionada != null ? parroquiaSeleccionada.getId() : 1);
        domicilio.setDireccion("Dirección de ejemplo");
        domicilio.setNumeroCasa("S/N");
        domicilio.setEsUrbanizacion(false);
        domicilio.setNombreUrbanizacion(null);
        domicilio.setNumeroBloque(null);

        // Tipo de vivienda
        int tipoViviendaId = rgTipoVivienda.getCheckedRadioButtonId();
        domicilio.setTipoVivienda(tipoViviendaId != -1 ? ((RadioButton) findViewById(tipoViviendaId)).getText().toString() : null);

        // Tipo de tenencia
        int tipoTenenciaId = rgPropiedadVivienda.getCheckedRadioButtonId();
        domicilio.setTipoTenencia(tipoTenenciaId != -1 ? ((RadioButton) findViewById(tipoTenenciaId)).getText().toString() : null);

        // Tipo de residencia
        int tipoResidenciaId = rgFrecuenciaUso.getCheckedRadioButtonId();
        domicilio.setTipoResidencia(tipoResidenciaId != -1 ? ((RadioButton) findViewById(tipoResidenciaId)).getText().toString() : null);

        // Campos numéricos
        try {
            domicilio.setMetrosCuadradosVivienda(Integer.parseInt(etMetrosVivienda.getText().toString().trim()));
        } catch (NumberFormatException e) {
            domicilio.setMetrosCuadradosVivienda(0);
        }

        try {
            domicilio.setMetrosCuadradosAreaVerde(Integer.parseInt(etMetrosAreaVerde.getText().toString().trim()));
        } catch (NumberFormatException e) {
            domicilio.setMetrosCuadradosAreaVerde(0);
        }

        String areaComunalStr = etAreaComunal.getText().toString().trim();
        domicilio.setDimensionAreaComunal(areaComunalStr.isEmpty() ? null : Integer.parseInt(areaComunalStr));

        // Datos de propietario (si es arrendada o prestada)
        if (tipoTenenciaId == R.id.rbArrendada || tipoTenenciaId == R.id.rbPrestada) {
            domicilio.setPropietarioPermiteAnimales(rgPermiteAnimales.getCheckedRadioButtonId() == R.id.rbPermiteAnimalesSi);
            String nombreProp = etNombresDueno.getText().toString().trim();
            domicilio.setNombrePropietario(nombreProp.isEmpty() ? null : nombreProp);
            String telProp = etTelefonoDueno.getText().toString().trim();
            domicilio.setTelefonoPropietario(telProp.isEmpty() ? null : telProp);
        } else {
            domicilio.setPropietarioPermiteAnimales(true);
            domicilio.setNombrePropietario(null);
            domicilio.setTelefonoPropietario(null);
        }

        // Cerramiento
        boolean tieneCerramiento = rgCerramiento.getCheckedRadioButtonId() == R.id.rbCerramientoSi;
        domicilio.setTieneCerramiento(tieneCerramiento);
        if (tieneCerramiento) {
            try {
                domicilio.setAlturaCerramiento(Double.parseDouble(etAlturaCerramiento.getText().toString().trim()));
            } catch (NumberFormatException e) {
                domicilio.setAlturaCerramiento(0.0);
            }
            int tipoCerramientoId = rgTipoCerramiento.getCheckedRadioButtonId();
            domicilio.setTipoCerramiento(tipoCerramientoId != -1 ? ((RadioButton) findViewById(tipoCerramientoId)).getText().toString() : null);
        } else {
            domicilio.setAlturaCerramiento(0.0);
            domicilio.setTipoCerramiento(null);
        }

        domicilio.setPuedeEscaparAnimal(rgPosibilidadEscape.getCheckedRadioButtonId() == R.id.rbEscapeSi);

        String especificacionResidencia = etEspecifiqueFrecuencia.getText().toString().trim();
        domicilio.setEspecificacionResidencia(especificacionResidencia.isEmpty() ? null : especificacionResidencia);

        updateRepository.updateDomicilioData(sessionManager.getCedula(), domicilio, new UpdateRepository.UpdateCallback() {
            @Override
            public void onSuccess(ApiResponse<String> response) {
                btnSiguiente.setEnabled(true);
                showSuccessDialog();
            }

            @Override
            public void onError(String message) {
                btnSiguiente.setEnabled(true);
                Log.e(TAG, "Error al actualizar: " + message);
                showErrorDialog("Error al actualizar los datos. Intente nuevamente.");
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage("Datos de domicilio actualizados correctamente")
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
                runBackPressedAnimation();
            }
        });
    }

    private void startAnimations() {
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);

        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).start();
        formContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(200).start();
        tvInfo.animate().alpha(1f).setDuration(300).setStartDelay(200).start();
    }

    private void setupListeners() {
        // Mostrar/ocultar sección de vivienda arrendada/prestada
        rgPropiedadVivienda.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbArrendada || checkedId == R.id.rbPrestada) {
                layoutArrendadaPrestada.setVisibility(View.VISIBLE);
            } else {
                layoutArrendadaPrestada.setVisibility(View.GONE);
                rgPermiteAnimales.clearCheck();
                etNombresDueno.setText("");
                etTelefonoDueno.setText("");
            }
        });

        // Mostrar/ocultar sección de detalles del cerramiento
        rgCerramiento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCerramientoSi) {
                layoutCerramiento.setVisibility(View.VISIBLE);
            } else {
                layoutCerramiento.setVisibility(View.GONE);
                etAlturaCerramiento.setText("");
                rgTipoCerramiento.clearCheck();
            }
        });

        // Mostrar/ocultar campo "especifique" de frecuencia
        rgFrecuenciaUso.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbVaDeVezEnCuando || checkedId == R.id.rbVaFinesSemana) {
                tilEspecifiqueFrecuencia.setVisibility(View.VISIBLE);
            } else {
                tilEspecifiqueFrecuencia.setVisibility(View.GONE);
                etEspecifiqueFrecuencia.setText("");
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                if (isUpdateMode) {
                    updateData();
                } else {
                    saveDataAndContinue();
                }
            }
        });

        btnRegresar.setOnClickListener(v -> runBackPressedAnimation());
    }

    private void populateDataFromRepository() {
        Domicilio domicilio = RegistroRepository.getInstance().getRegistroData().getDomicilio();
        if (domicilio == null) return;

        // Rellenar tipo de vivienda
        if (domicilio.getTipoVivienda() != null) {
            for (int i = 0; i < rgTipoVivienda.getChildCount(); i++) {
                RadioButton rb = (RadioButton) rgTipoVivienda.getChildAt(i);
                if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoVivienda())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }

        // Rellenar campos de texto de metros
        etMetrosVivienda.setText(String.valueOf(domicilio.getMetrosCuadradosVivienda()));
        etMetrosAreaVerde.setText(String.valueOf(domicilio.getMetrosCuadradosAreaVerde()));
        if (domicilio.getDimensionAreaComunal() != null) {
            etAreaComunal.setText(String.valueOf(domicilio.getDimensionAreaComunal()));
        }

        // Rellenar tipo de tenencia y la sección condicional
        if (domicilio.getTipoTenencia() != null) {
            if (domicilio.getTipoTenencia().equalsIgnoreCase("Propia")) {
                rbPropia.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.GONE);
            } else if (domicilio.getTipoTenencia().equalsIgnoreCase("Arrendada")) {
                rbArrendada.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.VISIBLE);
            } else if (domicilio.getTipoTenencia().equalsIgnoreCase("Prestada")) {
                rbPrestada.setChecked(true);
                layoutArrendadaPrestada.setVisibility(View.VISIBLE);
            }

            // Rellenar los sub-campos si la sección es visible
            if (layoutArrendadaPrestada.getVisibility() == View.VISIBLE) {
                if (domicilio.isPropietarioPermiteAnimales()) rbPermiteAnimalesSi.setChecked(true); else rbPermiteAnimalesNo.setChecked(true);
                etNombresDueno.setText(domicilio.getNombrePropietario());
                etTelefonoDueno.setText(domicilio.getTelefonoPropietario());
            }
        }

        // Rellenar cerramiento y la sección condicional
        if (domicilio.isTieneCerramiento()) {
            rbCerramientoSi.setChecked(true);
            layoutCerramiento.setVisibility(View.VISIBLE);
            etAlturaCerramiento.setText(String.valueOf(domicilio.getAlturaCerramiento()));
            if (domicilio.getTipoCerramiento() != null) {
                for (int i = 0; i < rgTipoCerramiento.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) rgTipoCerramiento.getChildAt(i);
                    if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoCerramiento())) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        } else {
            rbCerramientoNo.setChecked(true);
            layoutCerramiento.setVisibility(View.GONE);
        }

        // Rellenar posibilidad de escape
        if(domicilio.isPuedeEscaparAnimal()) rbEscapeSi.setChecked(true); else rbEscapeNo.setChecked(true);

        // Rellenar tipo de residencia y la especificación condicional
        if (domicilio.getTipoResidencia() != null) {
            for (int i = 0; i < rgFrecuenciaUso.getChildCount(); i++) {
                RadioButton rb = (RadioButton) rgFrecuenciaUso.getChildAt(i);
                if (rb.getText().toString().equalsIgnoreCase(domicilio.getTipoResidencia())) {
                    rb.setChecked(true);
                    if (rb.getId() == R.id.rbVaDeVezEnCuando || rb.getId() == R.id.rbVaFinesSemana) {
                        tilEspecifiqueFrecuencia.setVisibility(View.VISIBLE);
                        etEspecifiqueFrecuencia.setText(domicilio.getEspecificacionResidencia());
                    }
                    break;
                }
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (rgTipoVivienda.getCheckedRadioButtonId() == -1) {
            showError("Debe seleccionar el tipo de vivienda");
            shakeView(rgTipoVivienda);
            isValid = false;
        }

        if (etMetrosVivienda.getText().toString().trim().isEmpty()) {
            tilMetrosVivienda.setError("Campo obligatorio");
            shakeView(tilMetrosVivienda);
            isValid = false;
        } else {
            tilMetrosVivienda.setError(null);
        }

        if (etMetrosAreaVerde.getText().toString().trim().isEmpty()) {
            tilMetrosAreaVerde.setError("Campo obligatorio");
            shakeView(tilMetrosAreaVerde);
            isValid = false;
        } else {
            tilMetrosAreaVerde.setError(null);
        }

        int propiedadId = rgPropiedadVivienda.getCheckedRadioButtonId();
        if (propiedadId == -1) {
            showError("Debe indicar si la vivienda es propia, arrendada o prestada");
            shakeView(rgPropiedadVivienda);
            isValid = false;
        } else if (propiedadId == R.id.rbArrendada || propiedadId == R.id.rbPrestada) {
            if (rgPermiteAnimales.getCheckedRadioButtonId() == -1) {
                showError("Indique si el dueño permite animales");
                shakeView(rgPermiteAnimales);
                isValid = false;
            }
            if (etNombresDueno.getText().toString().trim().isEmpty()) {
                tilNombresDueno.setError("Campo obligatorio");
                shakeView(tilNombresDueno);
                isValid = false;
            } else {
                tilNombresDueno.setError(null);
            }
            if (etTelefonoDueno.getText().toString().trim().isEmpty()) {
                tilTelefonoDueno.setError("Campo obligatorio");
                shakeView(tilTelefonoDueno);
                isValid = false;
            } else {
                tilTelefonoDueno.setError(null);
            }
        }

        int cerramientoId = rgCerramiento.getCheckedRadioButtonId();
        if (cerramientoId == -1) {
            showError("Indique si la vivienda tiene cerramiento");
            shakeView(rgCerramiento);
            isValid = false;
        } else if (cerramientoId == R.id.rbCerramientoSi) {
            if (etAlturaCerramiento.getText().toString().trim().isEmpty()) {
                tilAlturaCerramiento.setError("Indique la altura");
                shakeView(tilAlturaCerramiento);
                isValid = false;
            } else {
                tilAlturaCerramiento.setError(null);
            }
            if (rgTipoCerramiento.getCheckedRadioButtonId() == -1) {
                showError("Seleccione el tipo de cerramiento");
                shakeView(rgTipoCerramiento);
                isValid = false;
            }
        }

        if (rgPosibilidadEscape.getCheckedRadioButtonId() == -1) {
            showError("Indique si el perro podría escapar");
            shakeView(rgPosibilidadEscape);
            isValid = false;
        }

        int frecuenciaId = rgFrecuenciaUso.getCheckedRadioButtonId();
        if (frecuenciaId == -1) {
            showError("Indique la frecuencia de uso del inmueble");
            shakeView(rgFrecuenciaUso);
            isValid = false;
        } else if (frecuenciaId == R.id.rbVaDeVezEnCuando || frecuenciaId == R.id.rbVaFinesSemana) {
            if (etEspecifiqueFrecuencia.getText().toString().trim().isEmpty()) {
                tilEspecifiqueFrecuencia.setError("Especifique la frecuencia");
                shakeView(tilEspecifiqueFrecuencia);
                isValid = false;
            } else {
                tilEspecifiqueFrecuencia.setError(null);
            }
        }

        return isValid;
    }

    private void showError(String message) {
        tvInfo.setText(message);
        tvInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        shakeView(tvInfo);

        handler.postDelayed(() -> {
            tvInfo.setText("Este campo es obligatorio");
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.colorAccent3));
        }, 3000);
    }

    private void shakeView(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
                .setDuration(500)
                .start();
    }

    private void saveDataAndContinue() {
        btnSiguiente.setEnabled(false);
        btnRegresar.setEnabled(false);
        saveDataToRepository();
        handler.postDelayed(this::animateSuccessTransition, 1000);
    }

    private void saveDataToRepository() {
        RegistroRepository repository = RegistroRepository.getInstance();
        RegistroCompleto data = repository.getRegistroData();
        Domicilio domicilio = new Domicilio();

        // IDs y campos fijos
        domicilio.setParroquiaId(parroquiaSeleccionada != null ? parroquiaSeleccionada.getId() : 1);
        domicilio.setDireccion("Dirección de ejemplo");
        domicilio.setNumeroCasa("S/N");
        domicilio.setEsUrbanizacion(false);
        domicilio.setNombreUrbanizacion(null);
        domicilio.setNumeroBloque(null);

        // Mapeo seguro de RadioGroups
        int tipoViviendaId = rgTipoVivienda.getCheckedRadioButtonId();
        domicilio.setTipoVivienda(tipoViviendaId != -1 ? ((RadioButton) findViewById(tipoViviendaId)).getText().toString() : null);

        int tipoTenenciaId = rgPropiedadVivienda.getCheckedRadioButtonId();
        domicilio.setTipoTenencia(tipoTenenciaId != -1 ? ((RadioButton) findViewById(tipoTenenciaId)).getText().toString() : null);

        int tipoResidenciaId = rgFrecuenciaUso.getCheckedRadioButtonId();
        domicilio.setTipoResidencia(tipoResidenciaId != -1 ? ((RadioButton) findViewById(tipoResidenciaId)).getText().toString() : null);

        // Campos numéricos
        try {
            domicilio.setMetrosCuadradosVivienda(Integer.parseInt(etMetrosVivienda.getText().toString().trim()));
        } catch (NumberFormatException e) {
            domicilio.setMetrosCuadradosVivienda(0);
        }

        try {
            domicilio.setMetrosCuadradosAreaVerde(Integer.parseInt(etMetrosAreaVerde.getText().toString().trim()));
        } catch (NumberFormatException e) {
            domicilio.setMetrosCuadradosAreaVerde(0);
        }

        String areaComunalStr = etAreaComunal.getText().toString().trim();
        domicilio.setDimensionAreaComunal(areaComunalStr.isEmpty() ? null : Integer.parseInt(areaComunalStr));

        // Lógica de null para vivienda arrendada
        if (tipoTenenciaId == R.id.rbArrendada || tipoTenenciaId == R.id.rbPrestada) {
            domicilio.setPropietarioPermiteAnimales(rgPermiteAnimales.getCheckedRadioButtonId() == R.id.rbPermiteAnimalesSi);
            String nombreProp = etNombresDueno.getText().toString().trim();
            domicilio.setNombrePropietario(nombreProp.isEmpty() ? null : nombreProp);
            String telProp = etTelefonoDueno.getText().toString().trim();
            domicilio.setTelefonoPropietario(telProp.isEmpty() ? null : telProp);
        } else {
            domicilio.setPropietarioPermiteAnimales(true);
            domicilio.setNombrePropietario(null);
            domicilio.setTelefonoPropietario(null);
        }

        // Lógica de null para cerramiento
        boolean tieneCerramiento = rgCerramiento.getCheckedRadioButtonId() == R.id.rbCerramientoSi;
        domicilio.setTieneCerramiento(tieneCerramiento);
        if (tieneCerramiento) {
            try {
                domicilio.setAlturaCerramiento(Double.parseDouble(etAlturaCerramiento.getText().toString().trim()));
            } catch (NumberFormatException e) {
                domicilio.setAlturaCerramiento(0.0);
            }
            int tipoCerramientoId = rgTipoCerramiento.getCheckedRadioButtonId();
            domicilio.setTipoCerramiento(tipoCerramientoId != -1 ? ((RadioButton) findViewById(tipoCerramientoId)).getText().toString() : null);
        } else {
            domicilio.setAlturaCerramiento(0.0);
            domicilio.setTipoCerramiento(null);
        }

        domicilio.setPuedeEscaparAnimal(rgPosibilidadEscape.getCheckedRadioButtonId() == R.id.rbEscapeSi);

        String especificacionResidencia = etEspecifiqueFrecuencia.getText().toString().trim();
        domicilio.setEspecificacionResidencia(especificacionResidencia.isEmpty() ? null : especificacionResidencia);

        data.setDomicilio(domicilio);
        Log.i(TAG, "Datos de domicilio guardados en el repositorio");
    }

    private void animateSuccessTransition() {
        AnimatorSet successSet = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formScaleX = ObjectAnimator.ofFloat(formContainer, "scaleX", 1f, 0.9f);
        ObjectAnimator formScaleY = ObjectAnimator.ofFloat(formContainer, "scaleY", 1f, 0.9f);

        successSet.playTogether(logoAlpha, formAlpha, formScaleX, formScaleY);
        successSet.setDuration(300);
        successSet.setInterpolator(new AccelerateDecelerateInterpolator());
        successSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(DomicilioActivity.this, RelacionAnimalesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        successSet.start();
    }

    private void runBackPressedAnimation() {
        AnimatorSet exitSet = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator logoTranslation = ObjectAnimator.ofFloat(logoContainer, "translationY", 0f, 50f);
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
                    Intent intent = new Intent(DomicilioActivity.this, EntornoFamiliarActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        exitSet.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        if (logoContainer != null) logoContainer.clearAnimation();
        if (formContainer != null) formContainer.clearAnimation();
    }
}