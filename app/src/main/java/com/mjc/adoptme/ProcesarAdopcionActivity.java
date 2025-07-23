package com.mjc.adoptme;

import android.os.Bundle;
import android.util.Log;
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

import com.google.gson.Gson;

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
            showErrorDialog("Error: No se pudo obtener la cédula del usuario");
            return;
        }

        try {
            // Create adoption request
            SolicitudAdopcion solicitud = new SolicitudAdopcion();
            solicitud.setAnimalId(animalId);
            solicitud.setCedulaAdoptante(cedulaAdoptante);
            solicitud.setMotivaciones(buildMotivaciones());
            solicitud.setCuidados(buildCuidados());

            // Log JSON request for debugging
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(solicitud);
            Log.d("AdopcionRequest", "JSON siendo enviado: " + jsonRequest);

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
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            showSuccessDialog("¡Solicitud enviada exitosamente! Nos pondremos en contacto contigo pronto.");
                            finish();
                        } else {
                            String errorMessage = "Error al enviar la solicitud. Por favor intenta nuevamente.";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("AdopcionError", "Error response: " + errorBody);
                                    Log.e("AdopcionError", "Response code: " + response.code());
                                }
                            } catch (Exception e) {
                                Log.e("AdopcionError", "Error reading error body", e);
                            }
                            showErrorDialog(errorMessage);
                        }
                    } catch (Exception e) {
                        Log.e("AdopcionError", "Error processing adoption response", e);
                        showErrorDialog("Error al procesar la respuesta de adopción.");
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
        } catch (Exception e) {
            Log.e("AdopcionError", "Error creating adoption request", e);
            showErrorDialog("Error al crear la solicitud. Inténtalo de nuevo.");
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Enviar Solicitud");
        }
    }

    private boolean validateForm() {
        // Basic validation for required text fields
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
        
        if (etAnosVida.getText().toString().trim().isEmpty()) {
            etAnosVida.setError("Este campo es obligatorio");
            etAnosVida.requestFocus();
            return false;
        }
        
        if (etPlanEnfermedad.getText().toString().trim().isEmpty()) {
            etPlanEnfermedad.setError("Este campo es obligatorio");
            etPlanEnfermedad.requestFocus();
            return false;
        }
        
        if (etResponsableCostos.getText().toString().trim().isEmpty()) {
            etResponsableCostos.setError("Este campo es obligatorio");
            etResponsableCostos.requestFocus();
            return false;
        }
        
        if (etPlanMalComportamiento.getText().toString().trim().isEmpty()) {
            etPlanMalComportamiento.setError("Este campo es obligatorio");
            etPlanMalComportamiento.requestFocus();
            return false;
        }
        
        // Validate required radio groups
        if (rgPlanViajes.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona una opción para el plan de viajes", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgLugarNecesidades.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona dónde hará sus necesidades fisiológicas", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgFrecuenciaBano.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona la frecuencia de baño", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgTipoAlimentacion.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona el tipo de alimentación", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgConoceToxicos.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor indica si conoces qué alimentos son tóxicos", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgPresupuestoMensual.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona tu presupuesto mensual estimado", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgRecursosVeterinarios.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor indica si cuentas con recursos para gastos veterinarios", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgAceptaVisitas.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor indica si aceptas visitas periódicas de la fundación", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgAceptaEsterilizacion.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor indica si estás de acuerdo con la esterilización", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgConoceBeneficios.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor indica si conoces los beneficios de la esterilización", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (rgConscienteMaltrato.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor confirma si estás consciente sobre el maltrato animal", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate that at least one care option is selected
        if (!cbPaseos.isChecked() && !cbJuguetes.isChecked() && !cbCollar.isChecked() && 
            !cbComida.isChecked() && !cbContacto.isChecked() && !cbClima.isChecked() && 
            !cbLugar.isChecked() && !cbCepillar.isChecked() && !cbDientes.isChecked() && 
            !cbVeterinario.isChecked()) {
            showErrorDialog("Por favor selecciona al menos un cuidado que estés dispuesto a brindar");
            return false;
        }
        
        // Validate numeric fields
        try {
            int horas = Integer.parseInt(etHorasSolo.getText().toString().trim());
            if (horas < 0 || horas > 24) {
                etHorasSolo.setError("Las horas deben estar entre 0 y 24");
                etHorasSolo.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etHorasSolo.setError("Por favor ingresa un número válido");
            etHorasSolo.requestFocus();
            return false;
        }
        
        try {
            int anos = Integer.parseInt(etAnosVida.getText().toString().trim());
            if (anos < 1 || anos > 30) {
                etAnosVida.setError("Los años deben estar entre 1 y 30");
                etAnosVida.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAnosVida.setError("Por favor ingresa un número válido");
            etAnosVida.requestFocus();
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

    private void showSuccessDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}