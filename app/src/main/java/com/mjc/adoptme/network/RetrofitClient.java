package com.mjc.adoptme.network;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class RetrofitClient {

    private static final String BASE_URL = "https://api.adoptme.gaccet.com/";
    private static Retrofit retrofit = null;
    private static final String TAG = "RetrofitClient";

    public static ApiService getApiService() {
        if (retrofit == null) {
            try {
                // Creamos un interceptor para ver los logs de las llamadas
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                // Creamos un cliente OkHttp y le añadimos el interceptor
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build();

                // Creamos la instancia de Retrofit
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client) // Usamos el cliente con el logger
                        .addConverterFactory(GsonConverterFactory.create()) // Usamos Gson
                        .build();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Retrofit client", e);
                // Depending on the severity, you might want to throw a RuntimeException
                // throw new RuntimeException("Failed to initialize Retrofit client", e);
            }
        }
        // Ensure retrofit is not null before creating service
        if (retrofit != null) {
            return retrofit.create(ApiService.class);
        } else {
            Log.e(TAG, "Retrofit instance is null, cannot create ApiService.");
            // Return a dummy or throw an exception if ApiService cannot be created
            return null; // Or throw new RuntimeException("ApiService not available");
        }
    }
}