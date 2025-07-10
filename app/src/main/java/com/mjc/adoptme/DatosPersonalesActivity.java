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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.data.RegistroRepository; // <-- AÑADIDO
import com.mjc.adoptme.models.RegistroGeneral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatosPersonalesActivity extends AppCompatActivity {
    private static final String TAG = "DatosPersonalesActivity";

    // Vistas
    private LinearLayout logoContainer, formContainer, layoutPaws, progressIndicator;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo;
    private MaterialButton btnSiguiente, btnRegresar;
    private ProgressBar progressBar;

    // Campos del Formulario (sin email)
    private TextInputLayout tilCedula, tilFechaNacimiento, tilLugarNacimiento, tilNacionalidad;
    private TextInputLayout tilTelefonoConvencional, tilTelefonoMovil, tilOcupacion, tilNivelInstruccion, tilLugarTrabajo, tilTelefonoTrabajo;
    private TextInputLayout tilNivelInstruccionOtro; // Campo para especificar otro nivel
    private TextInputEditText etCedula, etFechaNacimiento, etLugarNacimiento, etNacionalidad;
    private TextInputEditText etTelefonoConvencional, etTelefonoMovil, etOcupacion, etLugarTrabajo, etTelefonoTrabajo;
    private TextInputEditText etNivelInstruccionOtro; // EditText para especificar otro nivel
    private AutoCompleteTextView actvNivelInstruccion;
    private RadioGroup rgTrabaja;
    private RadioButton rbTrabajaSi, rbTrabajaNo;
    private LinearLayout layoutCamposTrabajo;

    // Formato de fecha para la API (YYYY-MM-DD)
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        initViews();
        setupDropdowns();
        setupDatePicker();
        setupClickListeners();
        setupBackButtonHandler();
        startAnimations();
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
        tilTelefonoTrabajo = findViewById(R.id.tilTelefonoTrabajo);
        rgTrabaja = findViewById(R.id.rgTrabaja);
        rbTrabajaSi = findViewById(R.id.rbTrabajaSi);
        rbTrabajaNo = findViewById(R.id.rbTrabajaNo);
        layoutCamposTrabajo = findViewById(R.id.layoutCamposTrabajo);
        etCedula = findViewById(R.id.etCedula);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etLugarNacimiento = findViewById(R.id.etLugarNacimiento);
        etNacionalidad = findViewById(R.id.etNacionalidad);
        etTelefonoConvencional = findViewById(R.id.etTelefonoConvencional);
        etTelefonoMovil = findViewById(R.id.etTelefonoMovil);
        etOcupacion = findViewById(R.id.etOcupacion);
        etLugarTrabajo = findViewById(R.id.etLugarTrabajo);
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

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    private void setupClickListeners() {
        btnSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                saveDataAndContinue();
            }
        });

        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void setupDropdowns() {
        String[] niveles = getResources().getStringArray(R.array.niveles_instruccion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, niveles);
        actvNivelInstruccion.setAdapter(adapter);

        // Listener para mostrar/ocultar campo "Especifique" cuando se seleccione "Otro"
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

        // Listener para mostrar/ocultar campos de trabajo
        rgTrabaja.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTrabajaSi) {
                animateViewVisibility(layoutCamposTrabajo, true);
            } else if (checkedId == R.id.rbTrabajaNo) {
                animateViewVisibility(layoutCamposTrabajo, false);
                // Limpiar campos cuando se ocultan
                etLugarTrabajo.setText("");
                etTelefonoTrabajo.setText("");
                tilLugarTrabajo.setError(null);
                tilTelefonoTrabajo.setError(null);
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
            etFechaNacimiento.setText(dateFormat.format(calendar.getTime()));
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

        if (etFechaNacimiento.getText().toString().trim().isEmpty()) {
            tilFechaNacimiento.setError("La fecha de nacimiento es obligatoria");
            shakeView(tilFechaNacimiento);
            isValid = false;
        } else {
            tilFechaNacimiento.setError(null);
        }

        if (etLugarNacimiento.getText().toString().trim().isEmpty()) {
            tilLugarNacimiento.setError("El lugar de nacimiento es obligatorio");
            shakeView(tilLugarNacimiento);
            isValid = false;
        } else {
            tilLugarNacimiento.setError(null);
        }

        if (etNacionalidad.getText().toString().trim().isEmpty()) {
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

            // Si seleccionó "Otro", validar el campo de especificación
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

        // Validar si trabaja
        if (rgTrabaja.getCheckedRadioButtonId() == -1) {
            shakeView(rgTrabaja);
            // Mostrar error visual en el RadioGroup
            isValid = false;
        } else if (rgTrabaja.getCheckedRadioButtonId() == R.id.rbTrabajaSi) {
            // Solo validar lugar de trabajo si indicó que trabaja
            if (etLugarTrabajo.getText().toString().trim().isEmpty()) {
                tilLugarTrabajo.setError("El lugar de trabajo es obligatorio");
                shakeView(tilLugarTrabajo);
                isValid = false;
            } else {
                tilLugarTrabajo.setError(null);
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
        RegistroGeneral data = repository.getRegistroData();

        data.setCedula(etCedula.getText().toString().trim());

        // Convertir fecha al formato YYYY-MM-DD
        try {
            Date date = displayDateFormat.parse(etFechaNacimiento.getText().toString());
            data.setFecha_nacimiento(apiDateFormat.format(date));
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear la fecha", e);
            data.setFecha_nacimiento(null); // o manejar el error
        }

        data.setLugar_nacimiento(etLugarNacimiento.getText().toString().trim());
        data.setNacionalidad(etNacionalidad.getText().toString().trim());
        data.setTelefono_convencional(etTelefonoConvencional.getText().toString().trim());
        data.setTelefono_movil(etTelefonoMovil.getText().toString().trim());
        data.setOcupacion(etOcupacion.getText().toString().trim());

        String nivelInstruccion = actvNivelInstruccion.getText().toString().trim();
        if ("Otro".equals(nivelInstruccion)) {
            data.setNivel_instruccion(etNivelInstruccionOtro.getText().toString().trim());
        } else {
            data.setNivel_instruccion(nivelInstruccion);
        }

        if (rbTrabajaSi.isChecked()) {
            data.setLugar_trabajo(etLugarTrabajo.getText().toString().trim());
            data.setTelefono_trabajo(etTelefonoTrabajo.getText().toString().trim());
            // Asumo que no tienes un campo de dirección de trabajo en esta UI, si lo tienes, añádelo
            data.setDireccion_trabajo(null);
        } else {
            data.setLugar_trabajo(null);
            data.setTelefono_trabajo(null);
            data.setDireccion_trabajo(null);
        }

        Log.i(TAG, "Datos personales guardados en el repositorio.");
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
                Intent intent = new Intent(DatosPersonalesActivity.this, RegistroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        exitSet.start();
    }
}