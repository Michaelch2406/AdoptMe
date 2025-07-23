package com.mjc.adoptme.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class ApiClient {
    private static final String BASE_URL = "https://api.adoptme.gaccet.com/";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static final String TAG = "ApiClient";

    public static Retrofit getClient() {
        if (retrofit == null) {
            try {
                // Interceptor para logging (muy útil para depuración)
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                // Configurar Gson para incluir campos null
                Gson gson = new GsonBuilder()
                        .serializeNulls()  // Incluir campos null en el JSON
                        .create();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Retrofit client", e);
                // Optionally re-throw as a runtime exception or handle more gracefully
                // throw new RuntimeException("Failed to initialize API client", e);
            }
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            try {
                apiService = getClient().create(ApiService.class);
            } catch (Exception e) {
                Log.e(TAG, "Error creating ApiService instance", e);
                // Optionally re-throw as a runtime exception or handle more gracefully
                // throw new RuntimeException("Failed to create API service", e);
            }
        }
        return apiService;
    }
}