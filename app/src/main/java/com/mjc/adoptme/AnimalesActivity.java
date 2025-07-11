// En AnimalesActivity.java

package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.AnimalHistorial;
import com.mjc.adoptme.models.InfoAnimales;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient; // <-- USA NUESTRO CLIENTE

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalesActivity extends AppCompatActivity {

    private static final String TAG = "AnimalesActivity";

    private LinearLayout logoContainer, formContainer;
    private MaterialButton btnRegresar, btnFinalizar;
    private ProgressBar progressBar;
    private TextView tvInfo;
    private Map<CheckBox, TextInputLayout> animalMap;
    private CheckBox cbOtro;
    private TextInputLayout tilOtroEspecifique;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animales);

        initViews();
        populateDataFromRepository();
        setupListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        progressBar = findViewById(R.id.progressBar);
        tvInfo = findViewById(R.id.tvInfo);

        animalMap = new HashMap<>();
        animalMap.put(findViewById(R.id.cbPollos), findViewById(R.id.tilPollosCantidad));
        animalMap.put(findViewById(R.id.cbPatos), findViewById(R.id.tilPatosCantidad));
        animalMap.put(findViewById(R.id.cbCuis), findViewById(R.id.tilCuisCantidad));
        animalMap.put(findViewById(R.id.cbCerdos), findViewById(R.id.tilCerdosCantidad));
        animalMap.put(findViewById(R.id.cbCaballos), findViewById(R.id.tilCaballosCantidad));
        animalMap.put(findViewById(R.id.cbVacas), findViewById(R.id.tilVacasCantidad));
        animalMap.put(findViewById(R.id.cbChivos), findViewById(R.id.tilChivosCantidad));
        animalMap.put(findViewById(R.id.cbGatos), findViewById(R.id.tilGatosCantidad));
        animalMap.put(findViewById(R.id.cbPerros), findViewById(R.id.tilPerrosCantidad));
        animalMap.put(findViewById(R.id.cbOtro), findViewById(R.id.tilOtroCantidad));

        cbOtro = findViewById(R.id.cbOtro);
        tilOtroEspecifique = findViewById(R.id.tilOtroEspecifique);

        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    // En AnimalesActivity.java

    private void populateDataFromRepository() {
        InfoAnimales infoAnimales = RegistroRepository.getInstance().getRegistroData().getAnimales();
        if (infoAnimales == null || infoAnimales.getHistorial() == null || infoAnimales.getHistorial().isEmpty()) {
            return;
        }

        // Usaremos un mapa para contar cuántos animales de cada tipo hay en el historial
        Map<Integer, Integer> conteoPorTipo = new HashMap<>();
        String especificacionOtro = null;

        for (AnimalHistorial animal : infoAnimales.getHistorial()) {
            int tipoId = animal.getTipoAnimalId();
            conteoPorTipo.put(tipoId, conteoPorTipo.getOrDefault(tipoId, 0) + 1);
            if (animal.getEspecificacion() != null) {
                especificacionOtro = animal.getEspecificacion();
            }
        }

        // Ahora recorremos los CheckBox de la UI y los rellenamos
        for (Map.Entry<CheckBox, TextInputLayout> entry : animalMap.entrySet()) {
            CheckBox cb = entry.getKey();
            TextInputLayout til = entry.getValue();
            int tipoId = getTipoAnimalId(cb.getText().toString()); // Usamos el método que ya tienes

            if (conteoPorTipo.containsKey(tipoId)) {
                cb.setChecked(true);
                til.setVisibility(View.VISIBLE);
                if (til.getEditText() != null) {
                    til.getEditText().setText(String.valueOf(conteoPorTipo.get(tipoId)));
                }

                // Caso especial para el campo "Otro"
                if (cb.getId() == R.id.cbOtro) {
                    tilOtroEspecifique.setVisibility(View.VISIBLE);
                    if (tilOtroEspecifique.getEditText() != null) {
                        tilOtroEspecifique.getEditText().setText(especificacionOtro);
                    }
                }
            }
        }
    }
    private void setupListeners() {
        CompoundButton.OnCheckedChangeListener animalCheckboxListener = (buttonView, isChecked) -> {
            TextInputLayout tilCantidad = animalMap.get(buttonView);
            if (tilCantidad != null) {
                animateViewVisibility(tilCantidad, isChecked);
                if (!isChecked && tilCantidad.getEditText() != null) {
                    tilCantidad.getEditText().setText("");
                    tilCantidad.setError(null);
                }
            }
            if (buttonView.getId() == R.id.cbOtro) {
                animateViewVisibility(tilOtroEspecifique, isChecked);
                if (!isChecked && tilOtroEspecifique.getEditText() != null) {
                    tilOtroEspecifique.getEditText().setText("");
                    tilOtroEspecifique.setError(null);
                }
            }
        };

        for (CheckBox cb : animalMap.keySet()) {
            cb.setOnCheckedChangeListener(animalCheckboxListener);
        }

        btnFinalizar.setOnClickListener(v -> {
            if (validateForm()) {
                saveDataToRepository(); // 1. Guarda los datos de ESTA pantalla
                sendRegistrationData(); // 2. Envía TODO a la API
            }
        });
        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void saveDataToRepository() {
        RegistroRepository repository = RegistroRepository.getInstance();
        InfoAnimales infoAnimales = repository.getRegistroData().getAnimales();
        if (infoAnimales == null) { infoAnimales = new InfoAnimales(); }

        List<AnimalHistorial> historial = new ArrayList<>();

        for (Map.Entry<CheckBox, TextInputLayout> entry : animalMap.entrySet()) {
            CheckBox cb = entry.getKey();
            TextInputLayout til = entry.getValue();

            if (cb.isChecked() && til.getEditText() != null) {
                String cantidadStr = til.getEditText().getText().toString().trim();
                if (!cantidadStr.isEmpty()) {
                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        String tipoAnimalTexto = cb.getText().toString();
                        int tipoAnimalId = getTipoAnimalId(tipoAnimalTexto);

                        // --- LÓGICA DE NULL APLICADA (ESPECIFICACIÓN) ---
                        String especificacion = null;
                        if ("Otro".equals(tipoAnimalTexto) && tilOtroEspecifique.getEditText() != null) {
                            String espStr = tilOtroEspecifique.getEditText().getText().toString().trim();
                            if (!espStr.isEmpty()) {
                                especificacion = espStr;
                            }
                        }

                        for (int i = 0; i < cantidad; i++) {
                            AnimalHistorial animal = new AnimalHistorial();
                            animal.setTipoAnimalId(tipoAnimalId);
                            animal.setEdad(0);
                            animal.setRazon("Se perdió"); // Valor válido para la DB
                            animal.setEspecificacion(especificacion);
                            historial.add(animal);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parseando cantidad para " + cb.getText(), e);
                    }
                }
            }
        }

        infoAnimales.setHistorial(historial);
        repository.getRegistroData().setAnimales(infoAnimales);
        Log.d(TAG, "Datos de historial de animales guardados (con manejo de nulos).");
    }

    private int getTipoAnimalId(String tipo) {
        // !!! IMPORTANTE: Estos IDs deben coincidir con los de tu tabla `tipos_animales` en PostgreSQL !!!
        switch (tipo) {
            case "Perros": return 1;  // Asumiendo Canino = 1
            case "Gatos":  return 2;  // Asumiendo Felino = 2
            case "Pollos": return 3;  // Necesitas crear estos tipos en tu DB
            case "Patos":  return 4;
            case "Cuis":   return 5;
            case "Cerdos": return 6;
            case "Caballos": return 7;
            case "Vacas":  return 8;
            case "Chivos": return 9;
            case "Otro":
            default:
                return 10; // Un ID genérico para "Otro"
        }
    }

    private void sendRegistrationData() {
        progressBar.setVisibility(View.VISIBLE);
        btnFinalizar.setEnabled(false);
        btnRegresar.setEnabled(false);

        // 1. Obtén el objeto de datos COMPLETO desde el repositorio.
        RegistroCompleto datosParaEnviar = RegistroRepository.getInstance().getRegistroData();

        Log.d(TAG, "JSON A ENVIAR: " + new Gson().toJson(datosParaEnviar));

        // ** (Opcional) Para depuración: Imprime el JSON que se va a enviar
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonPayload = gson.toJson(datosParaEnviar);
        Log.d(TAG, "JSON Final a Enviar:\n" + jsonPayload);

        Log.d(TAG, "JSON A ENVIAR: " + new Gson().toJson(datosParaEnviar));
        // 2. Usa nuestro RetrofitClient para obtener el servicio
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.registrarUsuario(datosParaEnviar);

        // 4. Ejecuta la llamada de forma asíncrona
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    // ÉXITO: 200-299
                    Toast.makeText(AnimalesActivity.this, "¡Registro completado exitosamente!", Toast.LENGTH_LONG).show();
                    RegistroRepository.getInstance().limpiarDatos(); // Limpia el repo para un futuro registro
                    animateSuccessTransition();
                } else {
                    // ERROR: 4xx, 5xx
                    btnFinalizar.setEnabled(true);
                    btnRegresar.setEnabled(true);
                    String errorMsg = "Error en el registro. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += "\nMensaje: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear el cuerpo del error", e);
                    }
                    Log.e(TAG, errorMsg);
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // FALLO DE CONEXIÓN
                progressBar.setVisibility(View.GONE);
                btnFinalizar.setEnabled(true);
                btnRegresar.setEnabled(true);
                Log.e(TAG, "Fallo de conexión", t);
                showError("Fallo de conexión: " + t.getMessage());
            }
        });
    }

    private boolean validateForm() {
        // Tu lógica de validación aquí es correcta, no necesita cambios.
        boolean isValid = true;
        boolean atLeastOneAnimal = false;
        for (Map.Entry<CheckBox, TextInputLayout> entry : animalMap.entrySet()) {
            if (entry.getKey().isChecked()) {
                atLeastOneAnimal = true;
                if (entry.getValue().getEditText() != null && entry.getValue().getEditText().getText().toString().trim().isEmpty()) {
                    entry.getValue().setError("Cantidad");
                    isValid = false;
                } else {
                    entry.getValue().setError(null);
                }
            }
        }
        if (!atLeastOneAnimal) {
            showError("Debe seleccionar al menos un tipo de animal que haya tenido");
            return false;
        }
        if (cbOtro.isChecked() && tilOtroEspecifique.getEditText() != null && tilOtroEspecifique.getEditText().getText().toString().trim().isEmpty()) {
            tilOtroEspecifique.setError("Especifique");
            isValid = false;
        } else if (cbOtro.isChecked()) {
            tilOtroEspecifique.setError(null);
        }
        return isValid;
    }

    private void animateSuccessTransition() {
        // Tu animación de éxito para ir al MainActivity
        logoContainer.animate().alpha(0f).translationY(-50f).setDuration(400).start();
        formContainer.animate().alpha(0f).scaleY(0.9f).setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(AnimalesActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity(); // Cierra todas las actividades del flujo de registro
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }).start();
    }

    private void handleExitAnimation() {
        // Tu animación para regresar a la pantalla anterior
        logoContainer.animate().alpha(0f).translationY(-50f).setDuration(300).start();
        formContainer.animate().alpha(0f).translationY(50f).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(AnimalesActivity.this, RelacionAnimalesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }).start();
    }

    private void startAnimations() {
        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).start();
        formContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(200).start();
    }

    private void animateViewVisibility(final View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(200).setListener(null).start();
        } else {
            view.animate().alpha(0f).setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        }
    }

    private void showError(String message) {
        tvInfo.setText(message);
        tvInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        handler.postDelayed(() -> {
            tvInfo.setText("Seleccione los animales que tiene o ha tenido");
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.colorAccent3));
        }, 3000); // El mensaje de error dura 3 segundos
    }
}