package com.mjc.adoptme.network;

import com.mjc.adoptme.models.RegistroGeneral;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("v1/registerApp")
    Call<Void> registrarUsuario(@Body RegistroGeneral registroData);
}