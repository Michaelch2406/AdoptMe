package com.mjc.adoptme.network;

import android.util.Log;

import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.DatosPersonalesData;
import com.mjc.adoptme.models.Domicilio;
import com.mjc.adoptme.models.ReferenciaPersonal;
import com.mjc.adoptme.models.UpdateDataRequest;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UpdateRepository {
    private static final String TAG = "UpdateRepository";
    private ApiService apiService;

    public UpdateRepository() {
        apiService = ApiClient.getApiService();
    }

    public interface UpdateCallback {
        void onSuccess(ApiResponse<String> response);
        void onError(String message);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public <T> void updateUserData(UpdateDataRequest<T> request, UpdateCallback callback) {
        Log.d(TAG, "Enviando actualización para código: " + request.getCodigo());
        Call<ApiResponse<String>> call = apiService.updateUserData(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, retrofit2.Response<ApiResponse<String>> response) {
                Log.d(TAG, "Respuesta actualización recibida - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    Log.d(TAG, "Actualización exitosa: " + apiResponse.getMessage());
                    callback.onSuccess(apiResponse);
                } else {
                    Log.e(TAG, "Error en actualización: " + response.message());
                    callback.onError("Error al actualizar datos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Log.e(TAG, "Error de conexión en actualización", t);
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void getUserPersonalData(String cedula, DataCallback<DatosPersonalesData> callback) {
        Log.d(TAG, "Solicitando datos personales para cédula: " + cedula);
        Call<ApiResponse<DatosPersonalesData>> call = apiService.getUserPersonalData(cedula, "PER");
        call.enqueue(new Callback<ApiResponse<DatosPersonalesData>>() {
            @Override
            public void onResponse(Call<ApiResponse<DatosPersonalesData>> call, retrofit2.Response<ApiResponse<DatosPersonalesData>> response) {
                Log.d(TAG, "Respuesta recibida - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<DatosPersonalesData> apiResponse = response.body();
                    Log.d(TAG, "Respuesta exitosa: " + apiResponse.getMessage());
                    
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        Log.w(TAG, "Data es null en la respuesta");
                        callback.onError("No se encontraron datos personales");
                    }
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.message());
                    callback.onError("Error al obtener datos personales: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DatosPersonalesData>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al obtener datos personales", t);
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void getUserDomicilioData(String cedula, DataCallback<Domicilio> callback) {
        Log.d(TAG, "Solicitando datos de domicilio para cédula: " + cedula);
        Call<ApiResponse<Domicilio>> call = apiService.getUserDomicilioData(cedula, "DOM");
        call.enqueue(new Callback<ApiResponse<Domicilio>>() {
            @Override
            public void onResponse(Call<ApiResponse<Domicilio>> call, retrofit2.Response<ApiResponse<Domicilio>> response) {
                Log.d(TAG, "Respuesta domicilio recibida - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Domicilio> apiResponse = response.body();
                    Log.d(TAG, "Respuesta domicilio exitosa: " + apiResponse.getMessage());
                    
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        Log.w(TAG, "Data de domicilio es null");
                        callback.onError("No se encontraron datos del domicilio");
                    }
                } else {
                    Log.e(TAG, "Error en respuesta domicilio: " + response.message());
                    callback.onError("Error al obtener datos del domicilio: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Domicilio>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al obtener datos del domicilio", t);
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void getUserReferenciasData(String cedula, DataCallback<List<ReferenciaPersonal>> callback) {
        Log.d(TAG, "Solicitando referencias para cédula: " + cedula);
        Call<ApiResponse<List<ReferenciaPersonal>>> call = apiService.getUserReferenciasData(cedula, "REF");
        call.enqueue(new Callback<ApiResponse<List<ReferenciaPersonal>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReferenciaPersonal>>> call, retrofit2.Response<ApiResponse<List<ReferenciaPersonal>>> response) {
                Log.d(TAG, "Respuesta referencias recibida - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ReferenciaPersonal>> apiResponse = response.body();
                    Log.d(TAG, "Respuesta referencias exitosa: " + apiResponse.getMessage());
                    
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        Log.w(TAG, "Data de referencias es null");
                        callback.onError("No se encontraron referencias personales");
                    }
                } else {
                    Log.e(TAG, "Error en respuesta referencias: " + response.message());
                    callback.onError("Error al obtener referencias personales: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReferenciaPersonal>>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al obtener referencias", t);
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void updatePersonalData(String cedula, DatosPersonalesData data, UpdateCallback callback) {
        Log.d(TAG, "Actualizando datos personales para cédula: " + cedula);
        UpdateDataRequest<DatosPersonalesData> request = new UpdateDataRequest<>(cedula, "PER", data);
        updateUserData(request, callback);
    }

    public void updateDomicilioData(String cedula, Domicilio data, UpdateCallback callback) {
        Log.d(TAG, "Actualizando datos de domicilio para cédula: " + cedula);
        UpdateDataRequest<Domicilio> request = new UpdateDataRequest<>(cedula, "DOM", data);
        updateUserData(request, callback);
    }

    public void updateReferenciasData(String cedula, List<ReferenciaPersonal> data, UpdateCallback callback) {
        Log.d(TAG, "Actualizando referencias para cédula: " + cedula);
        UpdateDataRequest<List<ReferenciaPersonal>> request = new UpdateDataRequest<>(cedula, "REF", data);
        updateUserData(request, callback);
    }
}