package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.ReferenciaPersonal;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.models.UpdateDataRequest;
import com.mjc.adoptme.network.ApiClient;
import com.mjc.adoptme.network.UpdateRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferenciasPersonalesActivity extends AppCompatActivity {
    private static final String TAG = "ReferenciasPersonales";

    // Vistas
    private LinearLayout logoContainer, formContainer, layoutPaws;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo, tvTituloRef1, tvTituloRef2;
    private MaterialButton btnSiguiente, btnRegresar;
    private ProgressBar progressBar;
    private View progress1, progress2, progress3;

    // Campos de Formulario
    private TextInputLayout tilNombresRef1, tilParentescoRef1, tilTelefonoConvRef1, tilTelefonoMovilRef1, tilEmailRef1;
    private TextInputLayout tilNombresRef2, tilParentescoRef2, tilTelefonoConvRef2, tilTelefonoMovilRef2, tilEmailRef2;
    private TextInputEditText etNombresRef1, etParentescoRef1, etTelefonoConvRef1, etTelefonoMovilRef1, etEmailRef1;
    private TextInputEditText etNombresRef2, etParentescoRef2, etTelefonoConvRef2, etTelefonoMovilRef2, etEmailRef2;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isUpdateMode = false;
    private SessionManager sessionManager;
    private UpdateRepository updateRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referencias_personales);

        sessionManager = new SessionManager(this);
        updateRepository = new UpdateRepository();

        initViews();

        isUpdateMode = getIntent().getBooleanExtra("IS_UPDATE_MODE", false);

        if (isUpdateMode) {
            btnSiguiente.setText("Actualizar");
            tvSubtitle.setText("Actualizar Referencias Personales");
            loadUserData();
        } else {
            populateDataFromRepository();
        }

        setupClickListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        ivLogo = findViewById(R.id.ivLogo);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvTituloRef1 = findViewById(R.id.tvTituloRef1);
        tvTituloRef2 = findViewById(R.id.tvTituloRef2);
        progressBar = findViewById(R.id.progressBar);
        tilNombresRef1 = findViewById(R.id.tilNombresRef1);
        tilParentescoRef1 = findViewById(R.id.tilParentescoRef1);
        tilTelefonoConvRef1 = findViewById(R.id.tilTelefonoConvRef1);
        tilTelefonoMovilRef1 = findViewById(R.id.tilTelefonoMovilRef1);
        tilEmailRef1 = findViewById(R.id.tilEmailRef1);
        etNombresRef1 = findViewById(R.id.etNombresRef1);
        etParentescoRef1 = findViewById(R.id.etParentescoRef1);
        etTelefonoConvRef1 = findViewById(R.id.etTelefonoConvRef1);
        etTelefonoMovilRef1 = findViewById(R.id.etTelefonoMovilRef1);
        etEmailRef1 = findViewById(R.id.etEmailRef1);
        tilNombresRef2 = findViewById(R.id.tilNombresRef2);
        tilParentescoRef2 = findViewById(R.id.tilParentescoRef2);
        tilTelefonoConvRef2 = findViewById(R.id.tilTelefonoConvRef2);
        tilTelefonoMovilRef2 = findViewById(R.id.tilTelefonoMovilRef2);
        tilEmailRef2 = findViewById(R.id.tilEmailRef2);
        etNombresRef2 = findViewById(R.id.etNombresRef2);
        etParentescoRef2 = findViewById(R.id.etParentescoRef2);
        etTelefonoConvRef2 = findViewById(R.id.etTelefonoConvRef2);
        etTelefonoMovilRef2 = findViewById(R.id.etTelefonoMovilRef2);
        etEmailRef2 = findViewById(R.id.etEmailRef2);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnRegresar = findViewById(R.id.btnRegresar);

        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
        tvTituloRef1.setAlpha(0f);
        tvTituloRef2.setAlpha(0f);
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        String cedula = sessionManager.getCedula();

        updateRepository.getUserReferenciasData(cedula, new UpdateRepository.DataCallback<List<ReferenciaPersonal>>() {
            @Override
            public void onSuccess(List<ReferenciaPersonal> data) {
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

    private void populateForm(List<ReferenciaPersonal> referencias) {
        if (referencias.size() > 0) {
            ReferenciaPersonal ref1 = referencias.get(0);
            etNombresRef1.setText(String.format("%s %s",
                    ref1.getNombres() != null ? ref1.getNombres() : "",
                    ref1.getApellidos() != null ? ref1.getApellidos() : "").trim());
            if (ref1.getParentesco() != null) etParentescoRef1.setText(ref1.getParentesco());
            if (ref1.getTelefonoConvencional() != null) etTelefonoConvRef1.setText(ref1.getTelefonoConvencional());
            if (ref1.getTelefonoMovil() != null) etTelefonoMovilRef1.setText(ref1.getTelefonoMovil());
            if (ref1.getEmail() != null) etEmailRef1.setText(ref1.getEmail());
        }

        if (referencias.size() > 1) {
            ReferenciaPersonal ref2 = referencias.get(1);
            etNombresRef2.setText(String.format("%s %s",
                    ref2.getNombres() != null ? ref2.getNombres() : "",
                    ref2.getApellidos() != null ? ref2.getApellidos() : "").trim());
            if (ref2.getParentesco() != null) etParentescoRef2.setText(ref2.getParentesco());
            if (ref2.getTelefonoConvencional() != null) etTelefonoConvRef2.setText(ref2.getTelefonoConvencional());
            if (ref2.getTelefonoMovil() != null) etTelefonoMovilRef2.setText(ref2.getTelefonoMovil());
            if (ref2.getEmail() != null) etEmailRef2.setText(ref2.getEmail());
        }
    }

    private void updateData() {
        if (!validateForm()) return;

        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);

        List<ReferenciaPersonal> referencias = new ArrayList<>();

        // Referencia 1
        ReferenciaPersonal ref1 = new ReferenciaPersonal();
        String[] nombresApellidos1 = etNombresRef1.getText().toString().trim().split(" ", 2);
        ref1.setNombres(nombresApellidos1.length > 0 ? nombresApellidos1[0] : "");
        ref1.setApellidos(nombresApellidos1.length > 1 ? nombresApellidos1[1] : "");
        ref1.setParentesco(etParentescoRef1.getText().toString().trim());
        String telConv1 = etTelefonoConvRef1.getText().toString().trim();
        ref1.setTelefonoConvencional(telConv1.isEmpty() ? null : telConv1);
        ref1.setTelefonoMovil(etTelefonoMovilRef1.getText().toString().trim());
        ref1.setEmail(etEmailRef1.getText().toString().trim());
        referencias.add(ref1);

        // Referencia 2
        ReferenciaPersonal ref2 = new ReferenciaPersonal();
        String[] nombresApellidos2 = etNombresRef2.getText().toString().trim().split(" ", 2);
        ref2.setNombres(nombresApellidos2.length > 0 ? nombresApellidos2[0] : "");
        ref2.setApellidos(nombresApellidos2.length > 1 ? nombresApellidos2[1] : "");
        ref2.setParentesco(etParentescoRef2.getText().toString().trim());
        String telConv2 = etTelefonoConvRef2.getText().toString().trim();
        ref2.setTelefonoConvencional(telConv2.isEmpty() ? null : telConv2);
        ref2.setTelefonoMovil(etTelefonoMovilRef2.getText().toString().trim());
        ref2.setEmail(etEmailRef2.getText().toString().trim());
        referencias.add(ref2);

        updateRepository.updateReferenciasData(sessionManager.getCedula(), referencias, new UpdateRepository.UpdateCallback() {
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
                .setMessage("Referencias actualizadas correctamente")
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

    private void populateDataFromRepository() {
        RegistroCompleto data = RegistroRepository.getInstance().getRegistroData();
        if (data == null || data.getReferencias() == null || data.getReferencias().isEmpty()) {
            return;
        }

        List<ReferenciaPersonal> referencias = data.getReferencias();

        // Rellenar primera referencia (si existe)
        if (referencias.size() > 0) {
            ReferenciaPersonal ref1 = referencias.get(0);
            etNombresRef1.setText(String.format("%s %s", ref1.getNombres(), ref1.getApellidos()).trim());
            etParentescoRef1.setText(ref1.getParentesco());
            etTelefonoConvRef1.setText(ref1.getTelefonoConvencional());
            etTelefonoMovilRef1.setText(ref1.getTelefonoMovil());
            etEmailRef1.setText(ref1.getEmail());
        }

        // Rellenar segunda referencia (si existe)
        if (referencias.size() > 1) {
            ReferenciaPersonal ref2 = referencias.get(1);
            etNombresRef2.setText(String.format("%s %s", ref2.getNombres(), ref2.getApellidos()).trim());
            etParentescoRef2.setText(ref2.getParentesco());
            etTelefonoConvRef2.setText(ref2.getTelefonoConvencional());
            etTelefonoMovilRef2.setText(ref2.getTelefonoMovil());
            etEmailRef2.setText(ref2.getEmail());
        }
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
                if (isUpdateMode) {
                    updateData();
                } else {
                    saveDataAndContinue();
                }
            }
        });
        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void startAnimations() {
        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ivLogo.animate().rotation(360f).setDuration(800).setStartDelay(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        formContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        tvTituloRef1.animate().alpha(1f).setDuration(600).setStartDelay(400).start();
        tvTituloRef2.animate().alpha(1f).setDuration(600).setStartDelay(500).start();
        tvInfo.animate().alpha(0.8f).setDuration(600).setStartDelay(500).start();
        animateProgressTransition();
        handler.postDelayed(this::animatePaws, 700);
    }

    private void animateProgressTransition() {
        progress1.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        progress1.setAlpha(0.3f);
        progress2.setBackgroundColor(getResources().getColor(R.color.colorAccent2, getTheme()));
        progress2.setAlpha(1f);
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
                    if (onComplete != null) onComplete.run();
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

        // Validación primera referencia
        if (etNombresRef1.getText().toString().trim().isEmpty()) {
            tilNombresRef1.setError("Este campo es obligatorio");
            shakeView(tilNombresRef1);
            isValid = false;
        } else if (etNombresRef1.getText().toString().trim().split(" ").length < 2) {
            tilNombresRef1.setError("Ingrese nombres y apellidos completos");
            shakeView(tilNombresRef1);
            isValid = false;
        } else {
            tilNombresRef1.setError(null);
        }

        if (etParentescoRef1.getText().toString().trim().isEmpty()) {
            tilParentescoRef1.setError("Este campo es obligatorio");
            shakeView(tilParentescoRef1);
            isValid = false;
        } else {
            tilParentescoRef1.setError(null);
        }

        String telefonoMovil1 = etTelefonoMovilRef1.getText().toString().trim();
        if (telefonoMovil1.isEmpty()) {
            tilTelefonoMovilRef1.setError("Este campo es obligatorio");
            shakeView(tilTelefonoMovilRef1);
            isValid = false;
        } else if (telefonoMovil1.length() != 10) {
            tilTelefonoMovilRef1.setError("El teléfono debe tener 10 dígitos");
            shakeView(tilTelefonoMovilRef1);
            isValid = false;
        } else if (!telefonoMovil1.startsWith("09")) {
            tilTelefonoMovilRef1.setError("El teléfono móvil debe empezar con 09");
            shakeView(tilTelefonoMovilRef1);
            isValid = false;
        } else {
            tilTelefonoMovilRef1.setError(null);
        }

        String email1 = etEmailRef1.getText().toString().trim();
        if (email1.isEmpty()) {
            tilEmailRef1.setError("Este campo es obligatorio");
            shakeView(tilEmailRef1);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            tilEmailRef1.setError("Correo electrónico inválido");
            shakeView(tilEmailRef1);
            isValid = false;
        } else {
            tilEmailRef1.setError(null);
        }

        // Validación segunda referencia
        if (etNombresRef2.getText().toString().trim().isEmpty()) {
            tilNombresRef2.setError("Este campo es obligatorio");
            shakeView(tilNombresRef2);
            isValid = false;
        } else if (etNombresRef2.getText().toString().trim().split(" ").length < 2) {
            tilNombresRef2.setError("Ingrese nombres y apellidos completos");
            shakeView(tilNombresRef2);
            isValid = false;
        } else if (etNombresRef2.getText().toString().trim().equalsIgnoreCase(etNombresRef1.getText().toString().trim())) {
            tilNombresRef2.setError("Las referencias deben ser personas diferentes");
            shakeView(tilNombresRef2);
            isValid = false;
        } else {
            tilNombresRef2.setError(null);
        }

        if (etParentescoRef2.getText().toString().trim().isEmpty()) {
            tilParentescoRef2.setError("Este campo es obligatorio");
            shakeView(tilParentescoRef2);
            isValid = false;
        } else {
            tilParentescoRef2.setError(null);
        }

        String telefonoMovil2 = etTelefonoMovilRef2.getText().toString().trim();
        if (telefonoMovil2.isEmpty()) {
            tilTelefonoMovilRef2.setError("Este campo es obligatorio");
            shakeView(tilTelefonoMovilRef2);
            isValid = false;
        } else if (telefonoMovil2.length() != 10) {
            tilTelefonoMovilRef2.setError("El teléfono debe tener 10 dígitos");
            shakeView(tilTelefonoMovilRef2);
            isValid = false;
        } else if (!telefonoMovil2.startsWith("09")) {
            tilTelefonoMovilRef2.setError("El teléfono móvil debe empezar con 09");
            shakeView(tilTelefonoMovilRef2);
            isValid = false;
        } else {
            tilTelefonoMovilRef2.setError(null);
        }

        String email2 = etEmailRef2.getText().toString().trim();
        if (email2.isEmpty()) {
            tilEmailRef2.setError("Este campo es obligatorio");
            shakeView(tilEmailRef2);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
            tilEmailRef2.setError("Correo electrónico inválido");
            shakeView(tilEmailRef2);
            isValid = false;
        } else {
            tilEmailRef2.setError(null);
        }

        return isValid;
    }

    private void shakeView(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).setDuration(500).start();
    }

    private void saveDataAndContinue() {
        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);
        btnSiguiente.setText("");
        saveDataToRepository();
        handler.postDelayed(this::animateSuccessTransition, 1500);
    }

    private void saveDataToRepository() {
        List<ReferenciaPersonal> referencias = new ArrayList<>();

        // Referencia 1
        ReferenciaPersonal ref1 = new ReferenciaPersonal();
        String[] nombresApellidos1 = etNombresRef1.getText().toString().trim().split(" ", 2);
        ref1.setNombres(nombresApellidos1.length > 0 ? nombresApellidos1[0] : "");
        ref1.setApellidos(nombresApellidos1.length > 1 ? nombresApellidos1[1] : "");
        ref1.setParentesco(etParentescoRef1.getText().toString().trim());
        String telConv1 = etTelefonoConvRef1.getText().toString().trim();
        ref1.setTelefonoConvencional(telConv1.isEmpty() ? null : telConv1);
        ref1.setTelefonoMovil(etTelefonoMovilRef1.getText().toString().trim());
        ref1.setEmail(etEmailRef1.getText().toString().trim());
        referencias.add(ref1);

        // Referencia 2
        ReferenciaPersonal ref2 = new ReferenciaPersonal();
        String[] nombresApellidos2 = etNombresRef2.getText().toString().trim().split(" ", 2);
        ref2.setNombres(nombresApellidos2.length > 0 ? nombresApellidos2[0] : "");
        ref2.setApellidos(nombresApellidos2.length > 1 ? nombresApellidos2[1] : "");
        ref2.setParentesco(etParentescoRef2.getText().toString().trim());
        String telConv2 = etTelefonoConvRef2.getText().toString().trim();
        ref2.setTelefonoConvencional(telConv2.isEmpty() ? null : telConv2);
        ref2.setTelefonoMovil(etTelefonoMovilRef2.getText().toString().trim());
        ref2.setEmail(etEmailRef2.getText().toString().trim());
        referencias.add(ref2);

        // Guardamos la lista completa en el repositorio
        RegistroRepository.getInstance().getRegistroData().setReferencias(referencias);
        Log.i(TAG, "Referencias personales guardadas en el repositorio.");
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
                Intent intent = new Intent(ReferenciasPersonalesActivity.this, EntornoFamiliarActivity.class);
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
                    Intent intent = new Intent(ReferenciasPersonalesActivity.this, DatosPersonalesActivity.class);
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
        if (ivLogo != null) ivLogo.clearAnimation();
        if (logoContainer != null) logoContainer.clearAnimation();
        if (formContainer != null) formContainer.clearAnimation();
        if (layoutPaws != null) layoutPaws.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}