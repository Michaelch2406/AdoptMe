package com.mjc.adoptme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mjc.adoptme.data.SessionManager;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.SaveSecurityAnswersRequest;
import com.mjc.adoptme.models.SecurityAnswer;
import com.mjc.adoptme.models.SecurityQuestion;
import com.mjc.adoptme.network.ApiService;
import com.mjc.adoptme.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityQuestionsActivity extends AppCompatActivity {

    private static final String TAG = "SecurityQuestions";

    // Views
    private ProgressBar progressBar;
    private LinearLayout questionsContainer;
    private TextView tvError;
    private MaterialButton btnRetry;
    private MaterialButton btnSaveAnswers;
    private MaterialButton btnSkip;

    // Question Views
    private TextView tvQuestion1, tvQuestion2, tvQuestion3, tvQuestion4;
    private TextInputLayout tilAnswer1, tilAnswer2, tilAnswer3, tilAnswer4;
    private TextInputEditText etAnswer1, etAnswer2, etAnswer3, etAnswer4;

    // Data
    private List<SecurityQuestion> securityQuestions = new ArrayList<>();
    private SessionManager sessionManager;
    private ApiService apiService;
    private String userCedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        userCedula = sessionManager.getCedula();

        if (userCedula == null || userCedula.isEmpty()) {
            showErrorDialog("Error: No se pudo obtener la cédula del usuario");
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadSecurityQuestions();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        questionsContainer = findViewById(R.id.questionsContainer);
        tvError = findViewById(R.id.tvError);
        btnRetry = findViewById(R.id.btnRetry);
        btnSaveAnswers = findViewById(R.id.btnSaveAnswers);
        btnSkip = findViewById(R.id.btnSkip);

        // Question views
        tvQuestion1 = findViewById(R.id.tvQuestion1);
        tvQuestion2 = findViewById(R.id.tvQuestion2);
        tvQuestion3 = findViewById(R.id.tvQuestion3);
        tvQuestion4 = findViewById(R.id.tvQuestion4);

        tilAnswer1 = findViewById(R.id.tilAnswer1);
        tilAnswer2 = findViewById(R.id.tilAnswer2);
        tilAnswer3 = findViewById(R.id.tilAnswer3);
        tilAnswer4 = findViewById(R.id.tilAnswer4);

        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        etAnswer4 = findViewById(R.id.etAnswer4);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> {
            hideError();
            loadSecurityQuestions();
        });

        btnSaveAnswers.setOnClickListener(v -> {
            if (validateAnswers()) {
                saveAnswers();
            }
        });

        btnSkip.setOnClickListener(v -> {
            // Skip security questions and go to login
            goToLogin();
        });
    }

    private void loadSecurityQuestions() {
        showLoading();

        Call<ApiResponse<List<SecurityQuestion>>> call = apiService.getRandomSecurityQuestions();
        call.enqueue(new Callback<ApiResponse<List<SecurityQuestion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SecurityQuestion>>> call, Response<ApiResponse<List<SecurityQuestion>>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<SecurityQuestion>> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                        securityQuestions = apiResponse.getData();
                        if (securityQuestions.size() >= 4) {
                            displayQuestions();
                        } else {
                            showError("No se recibieron suficientes preguntas de seguridad");
                        }
                    } else {
                        showError("Error al obtener las preguntas de seguridad");
                    }
                } else {
                    showError("Error del servidor al cargar las preguntas");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SecurityQuestion>>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Error loading security questions", t);
                showError("Error de conexión. Verifica tu conexión a internet.");
            }
        });
    }

    private void displayQuestions() {
        if (securityQuestions.size() >= 4) {
            tvQuestion1.setText("1. " + securityQuestions.get(0).getPregunta());
            tvQuestion2.setText("2. " + securityQuestions.get(1).getPregunta());
            tvQuestion3.setText("3. " + securityQuestions.get(2).getPregunta());
            tvQuestion4.setText("4. " + securityQuestions.get(3).getPregunta());

            questionsContainer.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateAnswers() {
        boolean isValid = true;

        // Clear previous errors
        tilAnswer1.setError(null);
        tilAnswer2.setError(null);
        tilAnswer3.setError(null);
        tilAnswer4.setError(null);

        // Validate each answer
        if (etAnswer1.getText() == null || etAnswer1.getText().toString().trim().isEmpty()) {
            tilAnswer1.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer2.getText() == null || etAnswer2.getText().toString().trim().isEmpty()) {
            tilAnswer2.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer3.getText() == null || etAnswer3.getText().toString().trim().isEmpty()) {
            tilAnswer3.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        if (etAnswer4.getText() == null || etAnswer4.getText().toString().trim().isEmpty()) {
            tilAnswer4.setError("Esta respuesta es obligatoria");
            isValid = false;
        }

        return isValid;
    }

    private void saveAnswers() {
        btnSaveAnswers.setEnabled(false);
        btnSaveAnswers.setText("Guardando...");

        // Create answers list
        List<SecurityAnswer> answers = new ArrayList<>();
        answers.add(new SecurityAnswer(securityQuestions.get(0).getId(), etAnswer1.getText().toString().trim()));
        answers.add(new SecurityAnswer(securityQuestions.get(1).getId(), etAnswer2.getText().toString().trim()));
        answers.add(new SecurityAnswer(securityQuestions.get(2).getId(), etAnswer3.getText().toString().trim()));
        answers.add(new SecurityAnswer(securityQuestions.get(3).getId(), etAnswer4.getText().toString().trim()));

        SaveSecurityAnswersRequest request = new SaveSecurityAnswersRequest(userCedula, answers);

        Call<ApiResponse<String>> call = apiService.saveSecurityAnswers(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                btnSaveAnswers.setEnabled(true);
                btnSaveAnswers.setText("Guardar Respuestas");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        showSuccessDialog("Preguntas de seguridad configuradas correctamente. Ahora puedes iniciar sesión.");
                    } else {
                        showErrorDialog("Error al guardar las respuestas: " + apiResponse.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error saving answers: " + errorBody);
                            showErrorDialog("Error al guardar las respuestas. Por favor intenta nuevamente.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        showErrorDialog("Error al guardar las respuestas. Por favor intenta nuevamente.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                btnSaveAnswers.setEnabled(true);
                btnSaveAnswers.setText("Guardar Respuestas");
                Log.e(TAG, "Network error saving answers", t);
                showErrorDialog("Error de conexión. Verifica tu conexión a internet.");
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        questionsContainer.setVisibility(View.GONE);
        hideError();
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        questionsContainer.setVisibility(View.GONE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSuccessDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    goToLogin();
                })
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

    @Override
    public void onBackPressed() {
        // Ask user if they want to skip security questions
        new android.app.AlertDialog.Builder(this)
                .setTitle("¿Salir?")
                .setMessage("¿Estás seguro de que quieres omitir la configuración de preguntas de seguridad? Podrás configurarlas más tarde.")
                .setPositiveButton("Sí, omitir", (dialog, which) -> goToLogin())
                .setNegativeButton("Continuar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}