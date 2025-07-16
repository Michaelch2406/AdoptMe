package com.mjc.adoptme.network;

import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.Ciudad;
import com.mjc.adoptme.models.DatosPersonalesData;
import com.mjc.adoptme.models.Domicilio;
import com.mjc.adoptme.models.LoginRequest;
import com.mjc.adoptme.models.Pais;
import com.mjc.adoptme.models.Parroquia;
import com.mjc.adoptme.models.ReferenciaPersonal;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.models.TipoAnimal;
import com.mjc.adoptme.models.UpdateDataRequest;
import com.mjc.adoptme.models.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- AUTENTICACIÓN Y REGISTRO ---
    @POST("v1/loginApp")
    Call<ApiResponse<UserData>> iniciarSesion(@Body LoginRequest loginRequest);

    @POST("v1/registerApp")
    Call<ApiResponse<UserData>> registrarUsuario(@Body RegistroCompleto datosDeRegistro);


    // --- OBTENER DATOS DEL PERFIL ---
    @GET("v1/updateUserData")
    Call<ApiResponse<DatosPersonalesData>> getUserPersonalData(@Query("cedula") String cedula, @Query("codigo") String codigo);

    @GET("v1/updateUserData")
    Call<ApiResponse<Domicilio>> getUserDomicilioData(@Query("cedula") String cedula, @Query("codigo") String codigo);

    @GET("v1/updateUserData")
    Call<ApiResponse<List<ReferenciaPersonal>>> getUserReferenciasData(@Query("cedula") String cedula, @Query("codigo") String codigo);


    // --- ACTUALIZAR DATOS DEL PERFIL ---
    @POST("v1/updateUserData")
    Call<ApiResponse<String>> updateUserData(@Body UpdateDataRequest request);

    @POST("v1/updateUserData")
    Call<ApiResponse<String>> updateUserPersonalData(@Body UpdateDataRequest<DatosPersonalesData> request);

    @POST("v1/updateUserData")
    Call<ApiResponse<String>> updateUserDomicilioData(@Body UpdateDataRequest<Domicilio> request);

    @POST("v1/updateUserData")
    Call<ApiResponse<String>> updateUserReferenciasData(@Body UpdateDataRequest<List<ReferenciaPersonal>> request);


    // --- ENDPOINTS PARA DROPDOWNS ---
    @GET("v1/paises")
    Call<ApiResponse<List<Pais>>> getPaises();

    @GET("v1/ciudades/{pais_id}")
    Call<ApiResponse<List<Ciudad>>> getCiudades(@Path("pais_id") int paisId);

    @GET("v1/parroquias/{ciudad_id}")
    Call<ApiResponse<List<Parroquia>>> getParroquias(@Path("ciudad_id") int ciudadId);

    @GET("v1/tipos-animales")
    Call<ApiResponse<List<TipoAnimal>>> getTiposAnimales();
}