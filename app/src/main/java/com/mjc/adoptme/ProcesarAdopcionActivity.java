package com.mjc.adoptme;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.AdopcionResponse;
import com.mjc.adoptme.models.SolicitudAdopcion;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcesarAdopcionActivity extends AppCompatActivity {

    // UI Components
    private TextView tvAnimalName, tvAnimalInfo;
    private LinearLayout formContainer;
    private MaterialButton btnSubmit, btnRegresar;
    
    // Form fields
    private EditText etMotivoAdopcion, etPlanCambio, etPlanViajesLargos, etHorasSolo;
    private EditText etUbicacionDia, etUbicacionNoche, etLugarDormir;
    private EditText etToxicosConocidos, etAnosVida, etPlanEnfermedad, etResponsableCostos;
    private EditText etRazonEsterilizacion, etPlanMalComportamiento;
    
    // Radio groups
    private RadioGroup rgPlanViajes, rgFrecuenciaBano, rgFrecuenciaCorte, rgTipoAlimentacion;
    private RadioGroup rgConoceToxicos, rgRecursosVeterinarios, rgAceptaVisitas;
    private RadioGroup rgAceptaEsterilizacion, rgConoceBeneficios, rgConscienteMaltrato;
    private RadioGroup rgLugarNecesidades, rgPresupuestoMensual;
    
    // Checkboxes for care options
    private CheckBox cbPaseos, cbJuguetes, cbCollar, cbComida, cbContacto;
    private CheckBox cbClima, cbLugar, cbCepillar, cbDientes, cbVeterinario;

    // Animal data
    private int animalId;
    private String animalNombre;
    
    // Session manager
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesar_adopcion);

        sessionManager = new SessionManager(this);
        
        // Get animal data from intent
        animalId = getIntent().getIntExtra("animal_id", 0);
        animalNombre = getIntent().getStringExtra("animal_nombre");
        
        initViews();
        setupClickListeners();
        setupForm();
    }

    private void initViews() {
        tvAnimalName = findViewById(R.id.tvAnimalName);
        tvAnimalInfo = findViewById(R.id.tvAnimalInfo);
        formContainer = findViewById(R.id.formContainer);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnRegresar = findViewById(R.id.btnRegresar);
        
        // Form fields
        etMotivoAdopcion = findViewById(R.id.etMotivoAdopcion);
        etPlanCambio = findViewById(R.id.etPlanCambio);
        etPlanViajesLargos = findViewById(R.id.etPlanViajesLargos);
        etHorasSolo = findViewById(R.id.etHorasSolo);
        etUbicacionDia = findViewById(R.id.etUbicacionDia);
        etUbicacionNoche = findViewById(R.id.etUbicacionNoche);
        etLugarDormir = findViewById(R.id.etLugarDormir);
        etToxicosConocidos = findViewById(R.id.etToxicosConocidos);
        etAnosVida = findViewById(R.id.etAnosVida);
        etPlanEnfermedad = findViewById(R.id.etPlanEnfermedad);
        etResponsableCostos = findViewById(R.id.etResponsableCostos);
        
        etRazonEsterilizacion = findViewById(R.id.etRazonEsterilizacion);
        etPlanMalComportamiento = findViewById(R.id.etPlanMalComportamiento);
        
        // Radio groups
        rgPlanViajes = findViewById(R.id.rgPlanViajes);
        rgFrecuenciaBano = findViewById(R.id.rgFrecuenciaBano);
        rgFrecuenciaCorte = findViewById(R.id.rgFrecuenciaCorte);
        rgTipoAlimentacion = findViewById(R.id.rgTipoAlimentacion);
        rgConoceToxicos = findViewById(R.id.rgConoceToxicos);
        rgRecursosVeterinarios = findViewById(R.id.rgRecursosVeterinarios);
        rgAceptaVisitas = findViewById(R.id.rgAceptaVisitas);
        rgAceptaEsterilizacion = findViewById(R.id.rgAceptaEsterilizacion);
        rgConoceBeneficios = findViewById(R.id.rgConoceBeneficios);
        rgConscienteMaltrato = findViewById(R.id.rgConscienteMaltrato);
        rgLugarNecesidades = findViewById(R.id.rgLugarNecesidades);
        rgPresupuestoMensual = findViewById(R.id.rgPresupuestoMensual);
        
        // Checkboxes
        cbPaseos = findViewById(R.id.cbPaseos);
        cbJuguetes = findViewById(R.id.cbJuguetes);
        cbCollar = findViewById(R.id.cbCollar);
        cbComida = findViewById(R.id.cbComida);
        cbContacto = findViewById(R.id.cbContacto);
        cbClima = findViewById(R.id.cbClima);
        cbLugar = findViewById(R.id.cbLugar);
        cbCepillar = findViewById(R.id.cbCepillar);
        cbDientes = findViewById(R.id.cbDientes);
        cbVeterinario = findViewById(R.id.cbVeterinario);
    }

    private void setupClickListeners() {
        btnRegresar.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> submitAdoptionForm());
        
        // Setup conditional visibility
        rgConoceToxicos.setOnCheckedChangeListener((group, checkedId) -> {
            etToxicosConocidos.setVisibility(checkedId == R.id.rbConoceToxicosSi ? View.VISIBLE : View.GONE);
        });
        
        rgAceptaEsterilizacion.setOnCheckedChangeListener((group, checkedId) -> {
            etRazonEsterilizacion.setVisibility(checkedId == R.id.rbAceptaEsterilizacionSi ? View.VISIBLE : View.GONE);
        });
    }

    private void setupForm() {
        tvAnimalName.setText("Adopción de " + (animalNombre != null ? animalNombre : "mascota"));
        tvAnimalInfo.setText("Complete el siguiente formulario para solicitar la adopción");
    }

    private void submitAdoptionForm() {
        if (!validateForm()) {
            return;
        }

        String cedulaAdoptante = sessionManager.getCedula();
        if (cedulaAdoptante == null || cedulaAdoptante.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo obtener la cédula del usuario", Toast.LENGTH_LONG).show();
            return;
        }

        // Create adoption request
        SolicitudAdopcion solicitud = new SolicitudAdopcion();
        solicitud.setAnimalId(animalId);
        solicitud.setCedulaAdoptante(cedulaAdoptante);
        solicitud.setMotivaciones(buildMotivaciones());
        solicitud.setCuidados(buildCuidados());

        // Submit to API
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<AdopcionResponse>> call = apiService.solicitarAdopcion(solicitud);

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Enviando...");

        call.enqueue(new Callback<ApiResponse<AdopcionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdopcionResponse>> call, Response<ApiResponse<AdopcionResponse>> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Enviar Solicitud");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProcesarAdopcionActivity.this, 
                            "¡Solicitud enviada exitosamente! Nos pondremos en contacto contigo pronto.", 
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ProcesarAdopcionActivity.this, 
                            "Error al enviar la solicitud. Por favor intenta nuevamente.", 
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AdopcionResponse>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Enviar Solicitud");
                
                Toast.makeText(ProcesarAdopcionActivity.this, 
                        "Error de conexión. Por favor intenta nuevamente.", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateForm() {
        // Basic validation
        if (etMotivoAdopcion.getText().toString().trim().isEmpty()) {
            etMotivoAdopcion.setError("Este campo es obligatorio");
            etMotivoAdopcion.requestFocus();
            return false;
        }
        
        if (etPlanCambio.getText().toString().trim().isEmpty()) {
            etPlanCambio.setError("Este campo es obligatorio");
            etPlanCambio.requestFocus();
            return false;
        }
        
        if (etHorasSolo.getText().toString().trim().isEmpty()) {
            etHorasSolo.setError("Este campo es obligatorio");
            etHorasSolo.requestFocus();
            return false;
        }
        
        // Validate radio groups
        if (rgPlanViajes.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona una opción para el plan de viajes", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgConscienteMaltrato.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor confirma si estás consciente sobre el maltrato animal", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private SolicitudAdopcion.Motivaciones buildMotivaciones() {
        SolicitudAdopcion.Motivaciones motivaciones = new SolicitudAdopcion.Motivaciones();
        
        motivaciones.setMotivoAdopcion(etMotivoAdopcion.getText().toString().trim());
        motivaciones.setPlanCambioDomicilio(etPlanCambio.getText().toString().trim());
        motivaciones.setPlanViajes(getSelectedRadioText(rgPlanViajes));
        motivaciones.setPlanViajesLargos(etPlanViajesLargos.getText().toString().trim());
        
        try {
            motivaciones.setHorasSoloDiarias(Integer.parseInt(etHorasSolo.getText().toString().trim()));
        } catch (NumberFormatException e) {
            motivaciones.setHorasSoloDiarias(0);
        }
        
        motivaciones.setUbicacionDia(etUbicacionDia.getText().toString().trim());
        motivaciones.setUbicacionNoche(etUbicacionNoche.getText().toString().trim());
        motivaciones.setLugarDormir(etLugarDormir.getText().toString().trim());
        motivaciones.setLugarNecesidades(getSelectedRadioText(rgLugarNecesidades));
        motivaciones.setFrecuenciaBano(getSelectedRadioText(rgFrecuenciaBano));
        motivaciones.setFrecuenciaCorte_pelo(getSelectedRadioText(rgFrecuenciaCorte));
        motivaciones.setTipoAlimentacion(getSelectedRadioText(rgTipoAlimentacion));
        
        motivaciones.setConoceToxicos(rgConoceToxicos.getCheckedRadioButtonId() == R.id.rbConoceToxicosSi);
        motivaciones.setToxicosConocidos(etToxicosConocidos.getText().toString().trim());
        
        try {
            motivaciones.setAnosVidaEstimados(Integer.parseInt(etAnosVida.getText().toString().trim()));
        } catch (NumberFormatException e) {
            motivaciones.setAnosVidaEstimados(10);
        }
        
        motivaciones.setPlanEnfermedad(etPlanEnfermedad.getText().toString().trim());
        motivaciones.setResponsableCostos(etResponsableCostos.getText().toString().trim());
        motivaciones.setPresupuestoMensual(getSelectedRadioText(rgPresupuestoMensual));
        motivaciones.setRecursosVeterinarios(rgRecursosVeterinarios.getCheckedRadioButtonId() == R.id.rbRecursosVeterinariosSi);
        motivaciones.setAceptaVisitas(rgAceptaVisitas.getCheckedRadioButtonId() == R.id.rbAceptaVisitasSi);
        motivaciones.setAceptaEsterilizacion(rgAceptaEsterilizacion.getCheckedRadioButtonId() == R.id.rbAceptaEsterilizacionSi);
        motivaciones.setRazonEsterilizacion(etRazonEsterilizacion.getText().toString().trim());
        motivaciones.setConoceBeneficiosEsterilizacion(rgConoceBeneficios.getCheckedRadioButtonId() == R.id.rbConoceBeneficiosSi);
        motivaciones.setPlanMalComportamiento(etPlanMalComportamiento.getText().toString().trim());
        motivaciones.setConscienteMaltrato(rgConscienteMaltrato.getCheckedRadioButtonId() == R.id.rbConscienteMaltratoSi);
        
        return motivaciones;
    }

    private List<String> buildCuidados() {
        List<String> cuidados = new ArrayList<>();
        
        if (cbPaseos.isChecked()) {
            cuidados.add("Paseos diarios al menos una vez o más.");
        }
        if (cbJuguetes.isChecked()) {
            cuidados.add("Proveerle juguetes, galletas o huesos para perro.");
        }
        if (cbCollar.isChecked()) {
            cuidados.add("Mantenerlo con collar y placa de identificación.");
        }
        if (cbComida.isChecked()) {
            cuidados.add("Proveerle comida adecuada a las necesidades de su especie, dos veces al día, así como agua fresca y limpia de manera permanente.");
        }
        if (cbContacto.isChecked()) {
            cuidados.add("Mantenerlo en contacto con la familia (no aislarlo en patios o azoteas).");
        }
        if (cbClima.isChecked()) {
            cuidados.add("Protegerlo de las inclemencias del clima.");
        }
        if (cbLugar.isChecked()) {
            cuidados.add("Proveerle un lugar abrigado y seguro para dormir.");
        }
        if (cbCepillar.isChecked()) {
            cuidados.add("Cepillar su pelo regularmente.");
        }
        if (cbDientes.isChecked()) {
            cuidados.add("Cepillar sus dientes conforme lo indique el veterinario.");
        }
        if (cbVeterinario.isChecked()) {
            cuidados.add("Llevarlo periódicamente al veterinario");
        }
        
        return cuidados;
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        }
        return "";
    }
}