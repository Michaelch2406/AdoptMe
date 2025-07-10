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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.Domicilio;


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
    private TextInputEditText etMetrosVivienda, etMetrosAreaVerde, etAreaComunal, etAlturaCerramiento, etNombresDueno, etTelefonoDueno, etEspecifiqueFrecuencia;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilio);

        initViews();
        setupListeners();
        setupBackButtonHandler(); // <-- Se llama al gestor moderno del botón atrás
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutArrendadaPrestada = findViewById(R.id.layoutArrendadaPrestada);
        layoutCerramiento = findViewById(R.id.layoutCerramiento); // <-- Se necesita la referencia a este layout

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
        rgTipoCerramiento = findViewById(R.id.rgTipoCerramiento); // <-- CORREGIDO: Se referencia el RadioGroup
        rgPosibilidadEscape = findViewById(R.id.rgPosibilidadEscape);
        rgFrecuenciaUso = findViewById(R.id.rgFrecuenciaUso);

        // TextInputLayouts
        tilMetrosVivienda = findViewById(R.id.tilMetrosVivienda);
        tilMetrosAreaVerde = findViewById(R.id.tilMetrosAreaVerde);
        tilAreaComunal = findViewById(R.id.tilAreaComunal);
        tilAlturaCerramiento = findViewById(R.id.tilAlturaCerramiento);
        tilNombresDueno = findViewById(R.id.tilNombresDueno); // <-- CORREGIDO: el ID del XML es tilNombresDueno
        tilTelefonoDueno = findViewById(R.id.tilTelefonoDueno);
        tilEspecifiqueFrecuencia = findViewById(R.id.tilEspecifiqueFrecuencia);

        // TextInputEditTexts
        etMetrosVivienda = findViewById(R.id.etMetrosVivienda);
        etMetrosAreaVerde = findViewById(R.id.etMetrosAreaVerde);
        etAreaComunal = findViewById(R.id.etAreaComunal);
        etAlturaCerramiento = findViewById(R.id.etAlturaCerramiento);
        etNombresDueno = findViewById(R.id.etNombresDueno); // <-- CORREGIDO: el ID del XML es etNombresDueno
        etTelefonoDueno = findViewById(R.id.etTelefonoDueno);
        etEspecifiqueFrecuencia = findViewById(R.id.etEspecifiqueFrecuencia);

        // Ocultar secciones dinámicas al iniciar
        layoutArrendadaPrestada.setVisibility(View.GONE);
        layoutCerramiento.setVisibility(View.GONE);
        tilEspecifiqueFrecuencia.setVisibility(View.GONE);
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
        // ... (Tu código de animación es correcto)
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
            }
        });

        // Mostrar/ocultar sección de detalles del cerramiento
        rgCerramiento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCerramientoSi) {
                layoutCerramiento.setVisibility(View.VISIBLE);
            } else {
                layoutCerramiento.setVisibility(View.GONE);
            }
        });

        // Mostrar/ocultar campo "especifique" de frecuencia
        rgFrecuenciaUso.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbVaDeVezEnCuando || checkedId == R.id.rbVaFinesSemana) {
                tilEspecifiqueFrecuencia.setVisibility(View.VISIBLE);
            } else {
                tilEspecifiqueFrecuencia.setVisibility(View.GONE);
            }
        });


        btnSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                saveDataAndContinue();
            }
        });

        btnRegresar.setOnClickListener(v -> runBackPressedAnimation());
    }

    private boolean validateForm() {
        boolean isValid = true;
        // ... (Se adapta la validación a los cambios)
        if (rgTipoVivienda.getCheckedRadioButtonId() == -1) { showError("Debe seleccionar el tipo de vivienda"); shakeView(rgTipoVivienda); isValid = false; }
        if (etMetrosVivienda.getText().toString().trim().isEmpty()) { tilMetrosVivienda.setError("Campo obligatorio"); shakeView(tilMetrosVivienda); isValid = false; } else { tilMetrosVivienda.setError(null); }
        if (etMetrosAreaVerde.getText().toString().trim().isEmpty()) { tilMetrosAreaVerde.setError("Campo obligatorio"); shakeView(tilMetrosAreaVerde); isValid = false; } else { tilMetrosAreaVerde.setError(null); }
        int propiedadId = rgPropiedadVivienda.getCheckedRadioButtonId();
        if (propiedadId == -1) { showError("Debe indicar si la vivienda es propia, arrendada o prestada"); shakeView(rgPropiedadVivienda); isValid = false; }
        else if (propiedadId == R.id.rbArrendada || propiedadId == R.id.rbPrestada) {
            if (rgPermiteAnimales.getCheckedRadioButtonId() == -1) { showError("Indique si el dueño permite animales"); shakeView(rgPermiteAnimales); isValid = false; }
            if (etNombresDueno.getText().toString().trim().isEmpty()) { tilNombresDueno.setError("Campo obligatorio"); shakeView(tilNombresDueno); isValid = false; } else { tilNombresDueno.setError(null); }
            if (etTelefonoDueno.getText().toString().trim().isEmpty()) { tilTelefonoDueno.setError("Campo obligatorio"); shakeView(tilTelefonoDueno); isValid = false; } else { tilTelefonoDueno.setError(null); }
        }
        int cerramientoId = rgCerramiento.getCheckedRadioButtonId();
        if (cerramientoId == -1) { showError("Indique si la vivienda tiene cerramiento"); shakeView(rgCerramiento); isValid = false; }
        else if (cerramientoId == R.id.rbCerramientoSi) {
            if (etAlturaCerramiento.getText().toString().trim().isEmpty()) { tilAlturaCerramiento.setError("Indique la altura"); shakeView(tilAlturaCerramiento); isValid = false; } else { tilAlturaCerramiento.setError(null); }
            if (rgTipoCerramiento.getCheckedRadioButtonId() == -1) { showError("Seleccione el tipo de cerramiento"); shakeView(rgTipoCerramiento); isValid = false; } // <-- CORREGIDO
        }
        if (rgPosibilidadEscape.getCheckedRadioButtonId() == -1) { showError("Indique si el perro podría escapar"); shakeView(rgPosibilidadEscape); isValid = false; }
        int frecuenciaId = rgFrecuenciaUso.getCheckedRadioButtonId();
        if (frecuenciaId == -1) { showError("Indique la frecuencia de uso del inmueble"); shakeView(rgFrecuenciaUso); isValid = false; }
        else if (frecuenciaId == R.id.rbVaDeVezEnCuando || frecuenciaId == R.id.rbVaFinesSemana) {
            if (etEspecifiqueFrecuencia.getText().toString().trim().isEmpty()) { tilEspecifiqueFrecuencia.setError("Especifique la frecuencia"); shakeView(tilEspecifiqueFrecuencia); isValid = false; } else { tilEspecifiqueFrecuencia.setError(null); }
        }
        return isValid;
    }

    private void showError(String message) {
        tvInfo.setText(message);
        tvInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        shakeView(tvInfo);

        handler.postDelayed(() -> {
            tvInfo.setError("Este campo es obligatorio");
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
        Domicilio domicilio = new Domicilio();

        // Mapear RadioGroups a Strings
        domicilio.setTipo_vivienda(((RadioButton) findViewById(rgTipoVivienda.getCheckedRadioButtonId())).getText().toString());
        domicilio.setTipo_vivienda(((RadioButton) findViewById(rgPropiedadVivienda.getCheckedRadioButtonId())).getText().toString());
        domicilio.setTipo_cerramiento(((RadioButton) findViewById(rgTipoCerramiento.getCheckedRadioButtonId())).getText().toString());
        domicilio.setTipo_residencia(((RadioButton) findViewById(rgFrecuenciaUso.getCheckedRadioButtonId())).getText().toString());

        // Campos numéricos
        try {
            domicilio.setMetros_cuadrados_vivienda(Integer.parseInt(etMetrosVivienda.getText().toString()));
            domicilio.setMetros_cuadrados_area_verde(Integer.parseInt(etMetrosAreaVerde.getText().toString()));
            domicilio.setAltura_cerramiento(Double.parseDouble(etAlturaCerramiento.getText().toString()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error al parsear números", e);
        }

        // Campos de texto y booleanos
        domicilio.setDimension_area_comunal(etAreaComunal.getText().toString().trim());
        domicilio.setPropietario_permite_animales(((RadioButton)findViewById(R.id.rbPermiteAnimalesSi)).isChecked());
        domicilio.setNombre_propietario(etNombresDueno.getText().toString().trim());
        domicilio.setTelefono_propietario(etTelefonoDueno.getText().toString().trim());
        domicilio.setTiene_cerramiento(((RadioButton)findViewById(R.id.rbCerramientoSi)).isChecked());
        domicilio.setPuede_escapar_animal(((RadioButton)findViewById(R.id.rbEscapeSi)).isChecked());
        domicilio.setEspecificacion_residencia(etEspecifiqueFrecuencia.getText().toString().trim());

        // Campos que no están en la UI pero sí en el JSON - se establecen valores por defecto o null
        domicilio.setDireccion("Dirección a completar"); // Necesitarás un campo para esto
        domicilio.setParroquia_id(12); // Valor de ejemplo, necesitas una forma de obtenerlo
        domicilio.setEs_urbanizacion(false); // Valor de ejemplo
        domicilio.setNombre_urbanizacion(null);
        domicilio.setNumero_bloque(null);
        domicilio.setNumero_casa("C15"); // Necesitarás un campo para esto

        RegistroRepository.getInstance().getRegistroData().setDomicilio(domicilio);
        Log.i(TAG, "Datos de domicilio guardados en el repositorio.");
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
                // Navegar a RelacionConAnimalesActivity (o la siguiente pantalla)
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
                Intent intent = new Intent(DomicilioActivity.this, EntornoFamiliarActivity.class);
                // 2. (Opcional pero recomendado) Añadir flags para evitar crear nuevas instancias si ya existe
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // 3. Iniciar la actividad
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        exitSet.start();
    }

    // El método onBackPressed() obsoleto se elimina. El callback se encarga de todo.
}