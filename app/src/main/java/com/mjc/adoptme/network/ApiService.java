package com.mjc.adoptme.network;

import com.mjc.adoptme.models.LoginRequest;  // <-- Añadir import
import com.mjc.adoptme.models.LoginResponse; // <-- Añadir import
import com.mjc.adoptme.models.RegistroCompleto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("v1/registerApp")
    Call<Void> registrarUsuario(@Body RegistroCompleto datosDeRegistro);

    // --- AÑADE ESTA NUEVA LÍNEA ---
    @POST("v1/loginApp")
    Call<LoginResponse> iniciarSesion(@Body LoginRequest loginRequest);
    // ----------------------------
}