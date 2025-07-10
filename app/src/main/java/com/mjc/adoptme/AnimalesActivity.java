package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
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
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.AnimalHistorial;
import com.mjc.adoptme.models.Animales;
import com.mjc.adoptme.models.RegistroGeneral;
import com.mjc.adoptme.network.ApiClient;
import com.mjc.adoptme.network.ApiService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalesActivity extends AppCompatActivity {

    private static final String TAG = "AnimalesActivity";

    private ApiService apiService;
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

        apiService = ApiClient.getClient().create(ApiService.class);
        initViews();
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
                collectAndSaveAnimalData(); // Primero guarda los datos de esta pantalla
                sendDataToApi();            // Luego envía todo
            }
        });
        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void collectAndSaveAnimalData() {
        RegistroRepository repository = RegistroRepository.getInstance();
        Animales animales = repository.getRegistroData().getAnimales();

        if (animales == null) {
            animales = new Animales();
        }

        List<AnimalHistorial> historial = animales.getHistorial();
        if (historial == null) {
            historial = new ArrayList<>();
        }
        // Limpiar el historial por si el usuario regresa y cambia de opinión
        historial.clear();

        for (Map.Entry<CheckBox, TextInputLayout> entry : animalMap.entrySet()) {
            CheckBox cb = entry.getKey();
            TextInputLayout til = entry.getValue();

            if (cb.isChecked() && til.getEditText() != null) {
                String cantidadStr = til.getEditText().getText().toString().trim();
                if (!cantidadStr.isEmpty()) {
                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        String tipoAnimal = cb.getText().toString();

                        int tipoAnimalId = getTipoAnimalId(tipoAnimal);
                        String especificacion = "Otro".equals(tipoAnimal) && tilOtroEspecifique.getEditText() != null ?
                                tilOtroEspecifique.getEditText().getText().toString().trim() : null;

                        for (int i = 0; i < cantidad; i++) {
                            // Suponemos que estos animales no tienen edad conocida (se usa 0) y la razón es genérica.
                            historial.add(new AnimalHistorial(tipoAnimalId, 0, "Animal de granja", especificacion));
                        }

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error al parsear cantidad para " + cb.getText(), e);
                    }
                }
            }
        }

        animales.setHistorial(historial);
        repository.getRegistroData().setAnimales(animales);
        Log.d(TAG, "Datos de animales de granja guardados en el historial.");
    }

    private int getTipoAnimalId(String tipo) {
        // !!! DEBES AJUSTAR ESTOS VALORES A LOS DE TU BASE DE DATOS !!!
        switch (tipo) {
            case "Perros": return 1;
            case "Gatos": return 2;
            case "Pollos": return 3;
            case "Patos": return 4;
            case "Cuis": return 5;
            case "Cerdos": return 6;
            case "Caballos": return 7;
            case "Vacas": return 8;
            case "Chivos": return 9;
            case "Otro":
            default:
                return 10; // Un ID genérico para "Otro" o no especificado
        }
    }


    private void sendDataToApi() {
        progressBar.setVisibility(View.VISIBLE);
        btnFinalizar.setEnabled(false);
        btnRegresar.setEnabled(false);

        RegistroRepository repository = RegistroRepository.getInstance();
        RegistroGeneral registroCompleto = repository.getRegistroData();

        // *** CORRECCIÓN CLAVE: USAR GETTERS Y SETTERS ***
        String plainPassword = registroCompleto.getPassword_hash(); // Usar getter
        if (plainPassword != null && !plainPassword.isEmpty()) {
            byte[] data = plainPassword.getBytes(StandardCharsets.UTF_8);
            String base64Password = Base64.encodeToString(data, Base64.NO_WRAP).trim();
            registroCompleto.setPassword_hash(base64Password); // Usar setter
        }

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(registroCompleto);
        Log.d(TAG, "JSON final a enviar: " + jsonPayload);

        Call<Void> call = apiService.registrarUsuario(registroCompleto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnFinalizar.setEnabled(true);
                btnRegresar.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(AnimalesActivity.this, "¡Registro completado exitosamente!", Toast.LENGTH_LONG).show();
                    repository.clearData();
                    animateSuccessTransition();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e(TAG, "Error en API. Código: " + response.code() + " | Cuerpo: " + errorBody);
                        Toast.makeText(AnimalesActivity.this, "Error en el registro: " + response.code() + ". " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear el error del API.", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnFinalizar.setEnabled(true);
                btnRegresar.setEnabled(true);
                Log.e(TAG, "Fallo de conexión", t);
                Toast.makeText(AnimalesActivity.this, "Fallo de conexión. Por favor, revisa tu internet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        boolean atLeastOneAnimal = false;

        for (Map.Entry<CheckBox, TextInputLayout> entry : animalMap.entrySet()) {
            if (entry.getKey().isChecked()) {
                atLeastOneAnimal = true;
                if (entry.getValue().getEditText() != null &&
                        entry.getValue().getEditText().getText().toString().trim().isEmpty()) {
                    entry.getValue().setError("Cantidad");
                    isValid = false;
                } else {
                    entry.getValue().setError(null);
                }
            }
        }

        if (!atLeastOneAnimal) {
            showError("Debe seleccionar al menos un tipo de animal");
            return false;
        }

        if (cbOtro.isChecked() && tilOtroEspecifique.getEditText() != null &&
                tilOtroEspecifique.getEditText().getText().toString().trim().isEmpty()) {
            tilOtroEspecifique.setError("Especifique el animal");
            isValid = false;
        } else if (cbOtro.isChecked()) {
            tilOtroEspecifique.setError(null);
        }

        return isValid;
    }

    private void animateSuccessTransition() {
        logoContainer.animate().alpha(0f).translationY(-50f).setDuration(400).start();
        formContainer.animate().alpha(0f).scaleY(0.9f).setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(AnimalesActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }).start();
    }

    private void handleExitAnimation() {
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
        }, 3000);
    }
}