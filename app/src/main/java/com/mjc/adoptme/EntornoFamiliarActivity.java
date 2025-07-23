package com.mjc.adoptme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import com.mjc.adoptme.data.RegistroRepository;
import com.mjc.adoptme.models.EntornoFamiliar;
import com.mjc.adoptme.models.MiembroFamiliar;
import com.mjc.adoptme.models.RegistroCompleto;

import java.util.ArrayList;
import java.util.List;

public class EntornoFamiliarActivity extends AppCompatActivity {

    private static final String TAG = "EntornoFamiliar";
    // Views
    private LinearLayout logoContainer, formContainer, layoutPaws, containerPersonas;
    private ImageView ivLogo, paw1, paw2, paw3;
    private TextView tvAppName, tvSubtitle, tvInfo, tvTituloPersonas;
    private MaterialButton btnSiguiente, btnRegresar, btnAgregarPersona;
    private ProgressBar progressBar;
    private View progress1, progress2, progress3;

    // TextInputLayouts
    private TextInputLayout tilPersona1, tilEspecifique;

    // TextInputEditTexts
    private TextInputEditText etPersona1, etEspecifique;

    // RadioGroups y RadioButtons
    private RadioGroup rgBebe, rgDiscapacidad, rgDecisionFamiliar;
    private RadioButton rbBebeSi, rbBebeNo;
    private RadioButton rbDiscapacidadSi, rbDiscapacidadNo;
    private RadioButton rbTodosDeAcuerdo, rbAceptanPorDarmeGusto, rbLesDaIgual, rbNoEstanDeAcuerdo;

    // Lista para mantener referencias a las personas agregadas dinámicamente
    private List<View> personasLayouts = new ArrayList<>();
    private List<TextInputEditText> personasEditTexts = new ArrayList<>();
    private int personaCount = 1;

    private Handler handler = new Handler();
    private boolean isProgressAnimated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entorno_familiar);

        initViews();
        populateDataFromRepository();
        setupListeners();
        setupBackButtonHandler();
        startAnimations();
    }

    private void initViews() {
        // Containers
        logoContainer = findViewById(R.id.logoContainer);
        formContainer = findViewById(R.id.formContainer);
        layoutPaws = findViewById(R.id.layoutPaws);
        containerPersonas = findViewById(R.id.containerPersonas);

        // Progress indicators
        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);

        // ImageViews
        ivLogo = findViewById(R.id.ivLogo);
        paw1 = findViewById(R.id.paw1);
        paw2 = findViewById(R.id.paw2);
        paw3 = findViewById(R.id.paw3);

        // TextViews
        tvAppName = findViewById(R.id.tvAppName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvTituloPersonas = findViewById(R.id.tvTituloPersonas);

        // Progress
        progressBar = findViewById(R.id.progressBar);

        // TextInputLayouts
        tilPersona1 = findViewById(R.id.tilPersona1);
        tilEspecifique = findViewById(R.id.tilEspecifique);

        // EditTexts
        etPersona1 = findViewById(R.id.etPersona1);
        etEspecifique = findViewById(R.id.etEspecifique);
        personasEditTexts.add(etPersona1); // Agregar la primera persona a la lista

        // RadioGroups
        rgBebe = findViewById(R.id.rgBebe);
        rgDiscapacidad = findViewById(R.id.rgDiscapacidad);
        rgDecisionFamiliar = findViewById(R.id.rgDecisionFamiliar);

        // RadioButtons
        rbBebeSi = findViewById(R.id.rbBebeSi);
        rbBebeNo = findViewById(R.id.rbBebeNo);
        rbDiscapacidadSi = findViewById(R.id.rbDiscapacidadSi);
        rbDiscapacidadNo = findViewById(R.id.rbDiscapacidadNo);
        rbTodosDeAcuerdo = findViewById(R.id.rbTodosDeAcuerdo);
        rbAceptanPorDarmeGusto = findViewById(R.id.rbAceptanPorDarmeGusto);
        rbLesDaIgual = findViewById(R.id.rbLesDaIgual);
        rbNoEstanDeAcuerdo = findViewById(R.id.rbNoEstanDeAcuerdo);

        // Buttons
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnAgregarPersona = findViewById(R.id.btnAgregarPersona);

        // Set initial states
        logoContainer.setAlpha(0f);
        logoContainer.setTranslationY(-50f);
        formContainer.setAlpha(0f);
        formContainer.setTranslationY(50f);
        tvInfo.setAlpha(0f);
        tvTituloPersonas.setAlpha(0f);


    }

    // En EntornoFamiliarActivity.java

    private void populateDataFromRepository() {
        try {
            EntornoFamiliar entorno = RegistroRepository.getInstance().getRegistroData().getEntorno();
            if (entorno == null) return;

            // Rellenar preguntas de Si/No
            if (entorno.isEsperaBebe()) rbBebeSi.setChecked(true); else rbBebeNo.setChecked(true);
            if (entorno.isTieneDiscapacidadFobia()) {
                rbDiscapacidadSi.setChecked(true);
                tilEspecifique.setVisibility(View.VISIBLE);
                etEspecifique.setText(entorno.getEspecificacionDiscapacidad());
            } else {
                rbDiscapacidadNo.setChecked(true);
            }

            // Rellenar decisión familiar
            String decision = entorno.getDecisionFamiliar();
            if (decision != null) {
                if (decision.equals("Están todos de acuerdo")) rbTodosDeAcuerdo.setChecked(true);
                else if (decision.equals("Lo aceptan por darme gusto")) rbAceptanPorDarmeGusto.setChecked(true);
                else if (decision.equals("Les da igual")) rbLesDaIgual.setChecked(true);
                else if (decision.equals("No están de acuerdo")) rbNoEstanDeAcuerdo.setChecked(true);
            }

            // Rellenar miembros de la familia (incluyendo los dinámicos)
            List<MiembroFamiliar> miembros = entorno.getMiembros();
            if (miembros != null && !miembros.isEmpty()) {
                // Rellenar el primer miembro (estático)
                MiembroFamiliar primerMiembro = miembros.get(0);
                String textoPrimerMiembro = String.format("%s %s / %d / %s",
                        primerMiembro.getNombres(), primerMiembro.getApellidos(), primerMiembro.getEdad(), primerMiembro.getParentesco()).trim();
                etPersona1.setText(textoPrimerMiembro);

                // Crear y rellenar los miembros adicionales (dinámicos)
                for (int i = 1; i < miembros.size(); i++) {
                    MiembroFamiliar miembroAdicional = miembros.get(i);
                    agregarPersona(); // Llama a tu método para crear un nuevo campo
                    // Obtiene el último EditText que se acaba de crear
                    TextInputEditText etNuevo = personasEditTexts.get(personasEditTexts.size() - 1);
                    String textoMiembroAdicional = String.format("%s %s / %d / %s",
                            miembroAdicional.getNombres(), miembroAdicional.getApellidos(), miembroAdicional.getEdad(), miembroAdicional.getParentesco()).trim();
                    etNuevo.setText(textoMiembroAdicional);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating data from repository", e);
        }
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Llama al método de animación de salida
                handleExitAnimation();
            }
        });
    }
    private void setupListeners() {
        // Listener para mostrar/ocultar campo de especificación
        rgDiscapacidad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbDiscapacidadSi) {
                    animateViewVisibility(tilEspecifique, true);
                } else {
                    animateViewVisibility(tilEspecifique, false);
                    etEspecifique.setText("");
                    tilEspecifique.setError(null);
                }
            }
        });

        // Botón agregar persona
        btnAgregarPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPersona();
            }
        });

        // Botones de navegación
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    saveDataAndContinue();
                }
            }
        });

        btnRegresar.setOnClickListener(v -> handleExitAnimation());
    }

    private void agregarPersona() {
        personaCount++;

        // Inflar el layout para una nueva persona
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout nuevaPersonaLayout = new LinearLayout(this);
        nuevaPersonaLayout.setOrientation(LinearLayout.VERTICAL);
        nuevaPersonaLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Crear TextInputLayout
        TextInputLayout tilNuevaPersona = new TextInputLayout(this);
        tilNuevaPersona.setLayoutParams(tilPersona1.getLayoutParams());
        tilNuevaPersona.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
        tilNuevaPersona.setBoxBackgroundColor(getResources().getColor(R.color.white));
        tilNuevaPersona.setBoxCornerRadii(12f, 12f, 12f, 12f);
        tilNuevaPersona.setHintTextColor(getResources().getColorStateList(R.color.colorPrimary));
        tilNuevaPersona.setBoxStrokeColor(getResources().getColor(R.color.colorAccent2));
        tilNuevaPersona.setErrorEnabled(true);

        // Crear TextInputEditText
        TextInputEditText etNuevaPersona = new TextInputEditText(tilNuevaPersona.getContext());
        etNuevaPersona.setHint("Nombres y apellidos / Edad / Parentesco");
        etNuevaPersona.setTextColor(getResources().getColor(R.color.black));
        etNuevaPersona.setTextSize(16);
        etNuevaPersona.setPadding(
                etPersona1.getPaddingLeft(),
                dpToPx(16),
                etPersona1.getPaddingRight(),
                dpToPx(16)
        );

        tilNuevaPersona.addView(etNuevaPersona);

        // Crear botón eliminar
        MaterialButton btnEliminar = new MaterialButton(this);
        btnEliminar.setText("Eliminar");
        btnEliminar.setTextSize(12);
        btnEliminar.setAllCaps(false);
        btnEliminar.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent3));
        btnEliminar.setCornerRadius(dpToPx(16));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, dpToPx(8), 0, dpToPx(16));
        btnEliminar.setLayoutParams(btnParams);

        // Agregar al layout
        nuevaPersonaLayout.addView(tilNuevaPersona);
        nuevaPersonaLayout.addView(btnEliminar);

        // Guardar referencias
        personasLayouts.add(nuevaPersonaLayout);
        personasEditTexts.add(etNuevaPersona);

        // Listener para eliminar
        final View layoutToRemove = nuevaPersonaLayout;
        final TextInputEditText editTextToRemove = etNuevaPersona;
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRemoveView(layoutToRemove, editTextToRemove);
            }
        });

        // Animar la adición
        nuevaPersonaLayout.setAlpha(0f);
        nuevaPersonaLayout.setTranslationY(20f);
        containerPersonas.addView(nuevaPersonaLayout);

        nuevaPersonaLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateRemoveView(final View viewToRemove, final TextInputEditText editTextToRemove) {
        viewToRemove.animate()
                .alpha(0f)
                .translationX(-viewToRemove.getWidth())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        containerPersonas.removeView(viewToRemove);
                        personasLayouts.remove(viewToRemove);
                        personasEditTexts.remove(editTextToRemove);
                        personaCount--;
                    }
                })
                .start();
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

    private void startAnimations() {
        // Animación del contenedor del logo
        logoContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación del logo con rotación
        ivLogo.animate()
                .rotation(360f)
                .setDuration(800)
                .setStartDelay(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación del formulario
        formContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animación del título
        tvTituloPersonas.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .start();

        // Animación del texto informativo
        tvInfo.animate()
                .alpha(0.8f)
                .setDuration(600)
                .setStartDelay(500)
                .start();

        // Animación del indicador de progreso
        animateProgressTransition();

        // Animación de las huellas
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animatePaws();
            }
        }, 700);
    }

    private void animateProgressTransition() {
        if (!isProgressAnimated) {
            isProgressAnimated = true;

            // Animar la transición del progreso al tercer segmento
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Primero, hacer que el segundo segmento se desvanezca
                    ObjectAnimator fadeOut2 = ObjectAnimator.ofFloat(progress2, "alpha", 0.3f, 0f);
                    fadeOut2.setDuration(300);

                    fadeOut2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progress2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            progress2.setAlpha(0.3f);

                            // Animar el tercer segmento
                            AnimatorSet moveAnimation = new AnimatorSet();

                            ValueAnimator colorMove = ValueAnimator.ofFloat(0f, 1f);
                            colorMove.setDuration(500);
                            colorMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float value = (float) animation.getAnimatedValue();
                                    progress3.setScaleX(1f + (0.1f * value));
                                    progress3.setScaleY(1f + (0.2f * value));
                                }
                            });

                            colorMove.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    progress3.setScaleX(1f);
                                    progress3.setScaleY(1f);

                                    // Efecto de brillo continuo
                                    ObjectAnimator glow = ObjectAnimator.ofFloat(progress3, "alpha", 1f, 0.7f, 1f);
                                    glow.setDuration(1000);
                                    glow.setRepeatCount(ValueAnimator.INFINITE);
                                    glow.start();
                                }
                            });

                            moveAnimation.play(colorMove);
                            moveAnimation.start();
                        }
                    });

                    fadeOut2.start();
                }
            }, 500);
        }
    }

    private void animatePaws() {
        animatePaw(paw1, 0, new Runnable() {
            @Override
            public void run() {
                animatePaw(paw2, 150, new Runnable() {
                    @Override
                    public void run() {
                        animatePaw(paw3, 150, new Runnable() {
                            @Override
                            public void run() {
                                startPawGlowAnimation();
                            }
                        });
                    }
                });
            }
        });
    }

    private void animatePaw(final ImageView paw, long delay, final Runnable onComplete) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, delay);
    }

    private void startPawGlowAnimation() {
        final ValueAnimator glowAnimator = ValueAnimator.ofFloat(0.3f, 1f, 0.3f);
        glowAnimator.setDuration(2000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);

        glowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                paw1.setAlpha(value);
                paw2.setAlpha(value * 0.8f);
                paw3.setAlpha(value * 0.6f);
            }
        });

        glowAnimator.start();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validar primera persona (obligatoria)
        String primeraPersona = etPersona1.getText().toString().trim();
        if (primeraPersona.isEmpty()) {
            tilPersona1.setError("Este campo es obligatorio");
            shakeView(tilPersona1);
            isValid = false;
        } else if (!validatePersonaFormat(primeraPersona)) {
            tilPersona1.setError("Formato: Nombre / Edad / Parentesco");
            shakeView(tilPersona1);
            isValid = false;
        } else {
            tilPersona1.setError(null);
        }

        // Validar personas adicionales
        for (int i = 0; i < personasEditTexts.size(); i++) {
            if (i == 0) continue; // Ya validamos la primera

            TextInputEditText etPersona = personasEditTexts.get(i);
            String personaText = etPersona.getText().toString().trim();

            if (!personaText.isEmpty() && !validatePersonaFormat(personaText)) {
                // Si no está vacío pero tiene formato incorrecto
                View parentLayout = personasLayouts.get(i - 1);
                TextInputLayout til = (TextInputLayout) parentLayout.findViewById(etPersona.getId()).getParent().getParent();
                if (til != null) {
                    til.setError("Formato: Nombre / Edad / Parentesco");
                    shakeView(til);
                }
                isValid = false;
            }
        }

        // Validar pregunta sobre bebé
        if (rgBebe.getCheckedRadioButtonId() == -1) {
            shakeView(rgBebe);
            showError("Debe indicar si algún familiar espera un bebé");
            isValid = false;
        }

        // Validar pregunta sobre discapacidad
        if (rgDiscapacidad.getCheckedRadioButtonId() == -1) {
            shakeView(rgDiscapacidad);
            showError("Debe indicar si alguien tiene discapacidad, fobia, alergia o asma");
            isValid = false;
        } else if (rgDiscapacidad.getCheckedRadioButtonId() == R.id.rbDiscapacidadSi) {
            // Si seleccionó Sí, debe especificar
            if (etEspecifique.getText().toString().trim().isEmpty()) {
                tilEspecifique.setError("Debe especificar");
                shakeView(tilEspecifique);
                isValid = false;
            } else {
                tilEspecifique.setError(null);
            }
        }

        // Validar decisión familiar
        if (rgDecisionFamiliar.getCheckedRadioButtonId() == -1) {
            shakeView(rgDecisionFamiliar);
            showError("Debe indicar el nivel de acuerdo familiar");
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePersonaFormat(String text) {
        // Validar que tenga el formato: Nombre / Edad / Parentesco
        String[] parts = text.split("/");
        if (parts.length < 3) return false;

        // Verificar que cada parte tenga contenido
        for (String part : parts) {
            if (part.trim().isEmpty()) return false;
        }

        // Verificar que la edad sea un número
        try {
            Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private void showError(String message) {
        // Aquí podrías mostrar un Snackbar o Toast
        // Por simplicidad, usaremos el texto informativo
        tvInfo.setText(message);
        tvInfo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        shakeView(tvInfo);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvInfo.setText("Los campos marcados con * son obligatorios");
                tvInfo.setTextColor(getResources().getColor(R.color.colorAccent3));
            }
        }, 3000);
    }

    private void shakeView(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX",
                0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        shake.setDuration(500);
        shake.start();
    }

    private void saveDataAndContinue() {
        // Mostrar progress
        progressBar.setVisibility(View.VISIBLE);
        btnSiguiente.setEnabled(false);
        btnRegresar.setEnabled(false);
        saveDataToRepository();

        // Simular guardado de datos
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aquí guardarías los datos
                // SharedPreferences, base de datos, etc.

                // Animación de éxito y navegación
                animateSuccessTransition();
            }
        }, 1500);
    }

    // En EntornoFamiliarActivity.java

    private void saveDataToRepository() {
        try {
            RegistroRepository repository = RegistroRepository.getInstance();
            RegistroCompleto data = repository.getRegistroData();
            EntornoFamiliar entorno = new EntornoFamiliar();

            // Preguntas de Si/No
            entorno.setEsperaBebe(rbBebeSi.isChecked());
            entorno.setTieneDiscapacidadFobia(rbDiscapacidadSi.isChecked());

            // --- LÓGICA DE NULL APLICADA ---
            if (rbDiscapacidadSi.isChecked()) {
                String especificacion = etEspecifique.getText().toString().trim();
                // Si el usuario marcó "Sí" pero no escribió nada, enviamos null.
                entorno.setEspecificacionDiscapacidad(especificacion.isEmpty() ? null : especificacion);
            } else {
                // Si marcó "No", nos aseguramos de que el campo sea null.
                entorno.setEspecificacionDiscapacidad(null);
            }

            // Decisión familiar (ya era seguro, pero lo mantenemos consistente)
            int decisionId = rgDecisionFamiliar.getCheckedRadioButtonId();
            if (decisionId != -1) {
                RadioButton selectedDecision = findViewById(decisionId);
                entorno.setDecisionFamiliar(selectedDecision.getText().toString());
            } else {
                entorno.setDecisionFamiliar(null); // Si no se selecciona nada
            }

            // Miembros de la familia (ya era seguro)
            List<MiembroFamiliar> miembros = new ArrayList<>();
            for (TextInputEditText etPersona : personasEditTexts) {
                String textoCompleto = etPersona.getText().toString().trim();
                if (textoCompleto.isEmpty()) continue;

                String[] partes = textoCompleto.split("/");
                if (partes.length >= 3) {
                    try {
                        MiembroFamiliar miembro = new MiembroFamiliar();
                        String nombreCompleto = partes[0].trim();
                        String[] nombresApellidos = nombreCompleto.split(" ", 2);
                        miembro.setNombres(nombresApellidos.length > 0 ? nombresApellidos[0] : nombreCompleto);
                        miembro.setApellidos(nombresApellidos.length > 1 ? nombresApellidos[1] : "");
                        miembro.setEdad(Integer.parseInt(partes[1].trim()));
                        miembro.setParentesco(partes[2].trim());
                        miembros.add(miembro);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando miembro familiar: " + textoCompleto, e);
                    }
                }
            }
            entorno.setMiembros(miembros);

            data.setEntorno(entorno);
            Log.i(TAG, "Datos de entorno familiar guardados (con manejo de nulos).");
        } catch (Exception e) {
            Log.e(TAG, "Error saving data to repository", e);
        }
    }

    private void animateSuccessTransition() {
        AnimatorSet successSet = new AnimatorSet();

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f);
        ObjectAnimator formAlpha = ObjectAnimator.ofFloat(formContainer, "alpha", 1f, 0f);
        ObjectAnimator formScale = ObjectAnimator.ofFloat(formContainer, "scaleX", 1f, 0.9f);
        ObjectAnimator formScaleY = ObjectAnimator.ofFloat(formContainer, "scaleY", 1f, 0.9f);

        successSet.playTogether(logoAlpha, formAlpha, formScale, formScaleY);
        successSet.setDuration(300);
        successSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Navegar a la siguiente actividad (Domicilio)
                Intent intent = new Intent(EntornoFamiliarActivity.this, DomicilioActivity.class);
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
                // LÓGICA CORREGIDA PARA REGRESAR
                Intent intent = new Intent(EntornoFamiliarActivity.this, ReferenciasPersonalesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                // Usa una animación coherente para el retroceso
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        exitSet.start();
    }



    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}