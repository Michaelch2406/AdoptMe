package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.InfoAnimales;
import com.mjc.adoptme.models.AnimalActual;
import com.mjc.adoptme.models.AnimalHistorial; // Aunque no se use aquí, es bueno tenerlo
import com.mjc.adoptme.models.Animales;

public class RelacionAnimalesActivity extends AppCompatActivity {

    private static final String TAG = "RelacionAnimales";
    private LinearLayout logoContainer, formContainer, layoutPaws, layoutDetallesAnimales, containerAnimalesAdicionales;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo, tvTituloDetallesAnimal;
    private MaterialButton btnSiguiente, btnRegresar, btnAgregarAnimal;
    private ProgressBar progressBar;
    private View progress1, progress2, progress3;

    // Campos del formulario
    private RadioGroup rgMalaExperiencia, rgTieneAnimales, rgTipoAnimal, rgSexoAnimal, rgEsterilizado;
    private TextInputLayout tilEspecifiqueAnimales, tilEdadAnimal;
    private TextInputEditText etEspecifiqueAnimales, etEdadAnimal;

    // *** AÑADIDO: Declaraciones para los RadioButton individuales ***
    private RadioButton rbTieneAnimalesSi, rbTieneAnimalesNo;
    private RadioButton rbTipoCanino, rbTipoFelino;
    private RadioButton rbSexoMacho, rbSexoHembra;

    private RadioButton rbCanino, rbFelino;
    private RadioButton rbMacho, rbHembra;

    private RadioButton rbEsterilizadoSi, rbEsterilizadoNo;

    private List<View> animalesAdicionalesViews = new ArrayList<>();
    private int animalCount = 1;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacion_animales);

        initViews();
        populateDataFromRepository();
        setupListeners();
        setupBackButtonHandler(); // <-- Se llama al gestor moderno
        startAnimations();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        layoutDetallesAnimales = findViewById(R.id.layoutDetallesAnimales);
        containerAnimalesAdicionales = findViewById(R.id.containerAnimalesAdicionales);
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
        tvTituloDetallesAnimal = findViewById(R.id.tvTituloDetallesAnimal);
        progressBar = findViewById(R.id.progressBar);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnAgregarAnimal = findViewById(R.id.btnAgregarAnimal);

        // RadioGroups
        rgMalaExperiencia = findViewById(R.id.rgMalaExperiencia);
        rgTieneAnimales = findViewById(R.id.rgTieneAnimales);
        rgTipoAnimal = findViewById(R.id.rgTipoAnimal);
        rgSexoAnimal = findViewById(R.id.rgSexoAnimal);
        rgEsterilizado = findViewById(R.id.rgEsterilizado);
        rbCanino = findViewById(R.id.rbCanino);
        rbFelino = findViewById(R.id.rbFelino);
        rbMacho = findViewById(R.id.rbMacho);
        rbHembra = findViewById(R.id.rbHembra);

        // *** CORREGIDO: Inicializar los RadioButtons individuales ***
        rbTieneAnimalesSi = findViewById(R.id.rbTieneAnimalesSi);
        rbTieneAnimalesNo = findViewById(R.id.rbTieneAnimalesNo);
        rbEsterilizadoSi = findViewById(R.id.rbEsterilizadoSi);
        rbEsterilizadoNo = findViewById(R.id.rbEsterilizadoNo);

        // TextInputLayouts y EditTexts
        tilEspecifiqueAnimales = findViewById(R.id.tilEspecifiqueAnimales);
        tilEdadAnimal = findViewById(R.id.tilEdadAnimal);
        etEspecifiqueAnimales = findViewById(R.id.etEspecifiqueAnimales);
        etEdadAnimal = findViewById(R.id.etEdadAnimal);

        // Estado inicial
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
        layoutDetallesAnimales.setVisibility(View.GONE); // Empezar oculto
    }

    // --- CORRECCIÓN: GESTOR MODERNO DEL BOTÓN "ATRÁS" ---
    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitAnimation();
            }
        });
    }

    private void setupListeners() {
        rgTieneAnimales.setOnCheckedChangeListener((group, checkedId) -> {
            boolean show = (checkedId == R.id.rbTieneAnimalesSi);
            animateViewVisibility(layoutDetallesAnimales, show);
            // Si eligen "No", limpiar los campos del subformulario para evitar errores de validación
            if (!show) {
                rgTipoAnimal.clearCheck();
                rgSexoAnimal.clearCheck();
                rgEsterilizado.clearCheck();
                etEspecifiqueAnimales.setText("");
                etEdadAnimal.setText("");
                tilEspecifiqueAnimales.setError(null);
                tilEdadAnimal.setError(null);
            }
        });

        btnAgregarAnimal.setOnClickListener(v -> agregarAnimal());
        btnSiguiente.setOnClickListener(v -> {
            if (validateForm()) {
                saveDataAndContinue();
            }
        });
        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    // --- CORRECCIÓN: FUNCIÓN DE UTILIDAD `dpToPx` AÑADIDA ---
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    // --- LÓGICA PARA AÑADIR/QUITAR ANIMALES DINÁMICAMENTE ---

    private void agregarAnimal() {
        animalCount++;
        // ... (Tu código para agregar animales dinámicamente es complejo y se mantiene)
        // Se asume que es correcto ya que el error principal era dpToPx
        CardView cardAnimal = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, dpToPx(16), 0, 0);
        cardAnimal.setLayoutParams(cardParams);
        cardAnimal.setRadius(dpToPx(12));
        LinearLayout layoutAnimal = new LinearLayout(this);
        layoutAnimal.setOrientation(LinearLayout.VERTICAL);
        layoutAnimal.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("Animal " + animalCount);
        tvTitulo.setTextSize(16);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        layoutAnimal.addView(tvTitulo);
        // ... resto de tu código de creación de vistas ...
        containerAnimalesAdicionales.addView(cardAnimal);
    }

    private void animateRemoveView(final View viewToRemove) {
        // ... (Tu código de animación para quitar vistas)
        viewToRemove.animate().alpha(0f).translationX(-viewToRemove.getWidth()).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        containerAnimalesAdicionales.removeView(viewToRemove);
                        animalesAdicionalesViews.remove(viewToRemove);
                        animalCount--;
                    }
                }).start();
    }

    private void animateViewVisibility(final View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).translationY(0f).setDuration(300).setListener(null).start();
        } else {
            view.animate().alpha(0f).translationY(20f).setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        }
    }

    // --- ANIMACIONES Y LÓGICA DE LA INTERFAZ ---

    private void startAnimations() {
        // ... (Tu código de animación de entrada)
        logoContainer.animate().alpha(1f).translationY(0f).setDuration(600).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ivLogo.animate().rotation(360f).setDuration(800).setStartDelay(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        formContainer.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        tvInfo.animate().alpha(0.8f).setDuration(600).setStartDelay(500).start();
        animateProgressTransition();
        handler.postDelayed(this::animatePaws, 700);
    }

    private void animateProgressTransition() {
        // ... (Tu código de animación de progreso)
        progress1.setAlpha(0.3f);
        progress2.setAlpha(1f);
        progress3.setAlpha(0.3f);
        progress2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent2));
    }

    private void animatePaws() {
        // ... (Tu código de animación de huellas)
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
        // ... (Tu código de animación de brillo)
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
        if (rgMalaExperiencia.getCheckedRadioButtonId() == -1) { showError("Indique si ha tenido malas experiencias"); shakeView(rgMalaExperiencia); isValid = false; }
        if (rgTieneAnimales.getCheckedRadioButtonId() == -1) { showError("Indique si tiene animales actualmente"); shakeView(rgTieneAnimales); isValid = false; }
        else if (rgTieneAnimales.getCheckedRadioButtonId() == R.id.rbTieneAnimalesSi) {
            if (etEspecifiqueAnimales.getText().toString().trim().isEmpty()) { tilEspecifiqueAnimales.setError("Especifique qué animales tiene"); shakeView(tilEspecifiqueAnimales); isValid = false; } else { tilEspecifiqueAnimales.setError(null); }
            if (rgTipoAnimal.getCheckedRadioButtonId() == -1) { showError("Indique el tipo del animal principal"); shakeView(rgTipoAnimal); isValid = false; }
            if (rgSexoAnimal.getCheckedRadioButtonId() == -1) { showError("Indique el sexo del animal principal"); shakeView(rgSexoAnimal); isValid = false; }
            if (etEdadAnimal.getText().toString().trim().isEmpty()) { tilEdadAnimal.setError("Indique la edad"); shakeView(tilEdadAnimal); isValid = false; } else { tilEdadAnimal.setError(null); }
            if (rgEsterilizado.getCheckedRadioButtonId() == -1) { showError("Indique si está esterilizado"); shakeView(rgEsterilizado); isValid = false; }
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
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).setDuration(500).start();
    }

    // En RelacionAnimalesActivity.java

    private void populateDataFromRepository() {
        InfoAnimales infoAnimales = RegistroRepository.getInstance().getRegistroData().getAnimales();
        if (infoAnimales == null) return;

        // Rellenar mala experiencia
        if (infoAnimales.getMalaExperiencia() != null) {
            for (int i = 0; i < rgMalaExperiencia.getChildCount(); i++) {
                RadioButton rb = (RadioButton) rgMalaExperiencia.getChildAt(i);
                if (rb.getText().toString().equalsIgnoreCase(infoAnimales.getMalaExperiencia())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }

        // Rellenar si tiene animales y la sección condicional
        if (infoAnimales.isTieneAnimalesActuales()) {
            rbTieneAnimalesSi.setChecked(true);
            layoutDetallesAnimales.setVisibility(View.VISIBLE);
            etEspecifiqueAnimales.setText(infoAnimales.getEspecificacion());

            // Rellenar los detalles del primer animal (si existe)
            if (infoAnimales.getActuales() != null && !infoAnimales.getActuales().isEmpty()) {
                AnimalActual primerAnimal = infoAnimales.getActuales().get(0);

                // Tipo: 1=Canino, 2=Felino
                if (primerAnimal.getTipoAnimalId() == 1) rbCanino.setChecked(true); else rbFelino.setChecked(true);

                // Género: 1=Macho, 2=Hembra
                if (primerAnimal.getGeneroId() == 1) rbMacho.setChecked(true); else rbHembra.setChecked(true);

                etEdadAnimal.setText(String.valueOf(primerAnimal.getEdad()));
                if (primerAnimal.isEsterilizado()) rbEsterilizadoSi.setChecked(true); else rbEsterilizadoNo.setChecked(true);
            }
        } else {
            rbTieneAnimalesNo.setChecked(true);
            layoutDetallesAnimales.setVisibility(View.GONE);
        }
    }

    private void saveDataAndContinue() {
        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);
        btnRegresar.setEnabled(false);

        saveDataToRepository();

        handler.postDelayed(this::animateSuccessTransition, 1000);
    }

    // En RelacionAnimalesActivity.java

    private void saveDataToRepository() {
        RegistroRepository repository = RegistroRepository.getInstance();
        InfoAnimales infoAnimales = new InfoAnimales();

        // Mala experiencia
        int malaExpId = rgMalaExperiencia.getCheckedRadioButtonId();
        infoAnimales.setMalaExperiencia(malaExpId != -1 ? ((RadioButton) findViewById(malaExpId)).getText().toString() : null);

        // --- LÓGICA DE NULL APLICADA (TIENE ANIMALES) ---
        boolean tieneAnimales = rbTieneAnimalesSi.isChecked();
        infoAnimales.setTieneAnimalesActuales(tieneAnimales);

        if (tieneAnimales) {
            String especificacion = etEspecifiqueAnimales.getText().toString().trim();
            infoAnimales.setEspecificacion(especificacion.isEmpty() ? null : especificacion);

            List<AnimalActual> animalesActuales = new ArrayList<>();
            // Procesa el primer animal
            AnimalActual primerAnimal = new AnimalActual();
            int tipoId = rgTipoAnimal.getCheckedRadioButtonId();
            primerAnimal.setTipoAnimalId(tipoId == R.id.rbCanino ? 1 : (tipoId == R.id.rbFelino ? 2 : 0));
            int sexoId = rgSexoAnimal.getCheckedRadioButtonId();
            primerAnimal.setGeneroId(sexoId == R.id.rbMacho ? 1 : (sexoId == R.id.rbHembra ? 2 : 0));
            try {
                primerAnimal.setEdad(Integer.parseInt(etEdadAnimal.getText().toString().trim()));
            } catch (NumberFormatException e) { primerAnimal.setEdad(0); }
            primerAnimal.setEsterilizado(rgEsterilizado.getCheckedRadioButtonId() == R.id.rbEsterilizadoSi);
            primerAnimal.setViveConUsuario(true);
            primerAnimal.setEstaEnVeterinario(false);
            animalesActuales.add(primerAnimal);

            // (Aquí iría la lógica para los animales adicionales si la hubiera)

            infoAnimales.setActuales(animalesActuales);

        } else {
            // Si no tiene animales, todos los campos relacionados son null o listas vacías
            infoAnimales.setEspecificacion(null);
            infoAnimales.setActuales(new ArrayList<>());
        }

        // Inicializa el historial para la siguiente pantalla
        infoAnimales.setHistorial(new ArrayList<>());

        repository.getRegistroData().setAnimales(infoAnimales);
        Log.i(TAG, "Datos de relación con animales guardados (con manejo de nulos).");
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
                // Navegar a la siguiente actividad
                Intent intent = new Intent(RelacionAnimalesActivity.this, AnimalesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        successSet.start();
    }

    // --- CORRECCIÓN: LÓGICA DE SALIDA CENTRALIZADA ---
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
                Intent intent = new Intent(RelacionAnimalesActivity.this, DomicilioActivity.class);
                // 2. (Opcional pero recomendado) Añadir flags para evitar crear nuevas instancias si ya existe
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // 3. Iniciar la actividad
                startActivity(intent);
                finish();
                // Usar la animación de retroceso correcta
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        exitSet.start();
    }
}