package com.mjc.adoptme.network;

import com.mjc.adoptme.models.AdopcionResponse;
import com.mjc.adoptme.models.AdopcionUsuario;
import com.mjc.adoptme.models.AdoptionStats;
import com.mjc.adoptme.models.AnimalAPI;
import com.mjc.adoptme.models.ApiResponse;
import com.mjc.adoptme.models.CancelAdoptionRequest;
import com.mjc.adoptme.models.Ciudad;
import com.mjc.adoptme.models.UpdateAdoptionStatusRequest;
import com.mjc.adoptme.models.DatosPersonalesData;
import com.mjc.adoptme.models.Domicilio;
import com.mjc.adoptme.models.Fundacion;
import com.mjc.adoptme.models.FundacionApp;
import com.mjc.adoptme.models.FundacionRequest;
import com.mjc.adoptme.models.LoginRequest;
import com.mjc.adoptme.models.Pais;
import com.mjc.adoptme.models.Parroquia;
import com.mjc.adoptme.models.Provincia;
import com.mjc.adoptme.models.ReferenciaPersonal;
import com.mjc.adoptme.models.RegistroCompleto;
import com.mjc.adoptme.models.SaveSecurityAnswersRequest;
import com.mjc.adoptme.models.SecurityQuestion;
import com.mjc.adoptme.models.SolicitudAdopcion;
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

    @GET("v1/provincias/{pais_id}")
    Call<ApiResponse<List<Provincia>>> getProvincias(@Path("pais_id") int paisId);

    @GET("v1/ciudades/{provincia_id}")
    Call<ApiResponse<List<Ciudad>>> getCiudades(@Path("provincia_id") int provinciaId);

    @GET("v1/parroquias/{ciudad_id}")
    Call<ApiResponse<List<Parroquia>>> getParroquias(@Path("ciudad_id") int ciudadId);

    @GET("v1/tipos-animales")
    Call<ApiResponse<List<TipoAnimal>>> getTiposAnimales();

    // --- ADOPCIÓN ---
    @GET("v1/fundaciones-cercanas")
    Call<ApiResponse<List<Fundacion>>> getFundacionesCercanas(@Query("lat") double latitud, @Query("lng") double longitud);

    @GET("v1/animales-por-fundacion")
    Call<ApiResponse<List<AnimalAPI>>> getAnimalesPorFundacion(@Query("ruc") String ruc, @Query("cedula") String cedula);

    @POST("v1/solicitarAdopcion")
    Call<ApiResponse<AdopcionResponse>> solicitarAdopcion(@Body SolicitudAdopcion solicitud);
    
    @GET("v1/adoptions/by-user-app/{cedula}")
    Call<ApiResponse<List<AdopcionUsuario>>> getAdopcionesPorUsuario(@Path("cedula") String cedula);
    
    // --- NUEVOS ENDPOINTS ---
    
    // Obtener fundaciones cercanas con nueva API
    @POST("v1/app/fundacionesApp")
    Call<ApiResponse<List<FundacionApp>>> getFundacionesApp(@Body FundacionRequest request);
    
    // Obtener estadísticas de adopciones del usuario
    @GET("v1/adoptions/stats-by-user/{cedula}")
    Call<ApiResponse<AdoptionStats>> getAdoptionStats(@Path("cedula") String cedula);
    
    // Cancelar adopción
    @POST("v1/adoptions/update-status/{id}")
    Call<ApiResponse<String>> updateAdoptionStatus(@Path("id") int adoptionId, @Body UpdateAdoptionStatusRequest request);
    
    // --- PREGUNTAS DE SEGURIDAD ---
    
    // Obtener preguntas de seguridad aleatorias
    @GET("v1/security-questions/random")
    Call<ApiResponse<List<SecurityQuestion>>> getRandomSecurityQuestions();
    
    // Guardar respuestas de seguridad
    @POST("v1/security-questions/save-answers")
    Call<ApiResponse<String>> saveSecurityAnswers(@Body SaveSecurityAnswersRequest request);
    
    // Obtener preguntas de seguridad del usuario
    @GET("v1/security-questions/user")
    Call<ApiResponse<List<SecurityQuestion>>> getUserSecurityQuestions(@Query("cedula") String cedula);
}