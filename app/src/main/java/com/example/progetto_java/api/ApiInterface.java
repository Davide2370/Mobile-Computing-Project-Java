package com.example.progetto_java.api;

import com.example.progetto_java.ui.home.HomeViewModel;
import com.example.progetto_java.ui.profilo.ProfiloViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("users/")
        //quando uso users faccio partire la richiesta register con il tipo di dato SignUpResponse
    Call<SignUpResponse> register();

    @GET("objects/{id}")
    Call<ResponseMostroData> prendiMostro(@Path("id") int id, @Query("sid") String sid);

    @GET("objects/")
    Call<List<ResponseObjects>> prendiOggetti(@Query("sid") String sid, @Query("lat") double lat, @Query("lon") double lon);

    @GET("ranking/")
    Call<List<ResponseRanking>> prendiRanking(@Query("sid") String sid);
    @GET("users/")
    Call<List<ResponseUtentiVicini>> prendiUtentiVicini(@Query("sid") String sid, @Query("lat") double lat, @Query("lon") double lon);
    @POST("objects/{id}/activate")
    Call<ResponseAttivaOggetto> attivaOggetto(
            @Path("id") int id,
            @Body HomeViewModel.OggettoAttivazione bodyObject
    );

    @PATCH("users/{uid}")
    Call<Void> aggiornaUtente(
            @Path("uid") int id,
            @Body ProfiloViewModel.ResponseAggiornaUtente bodyObject
    );
    @GET("users/{uid}")
    Call<ResponseUtenteData> prendiUtente(@Path("uid") int uid, @Query("sid") String sid);
    @GET("objects/{id}")
    Call<ResponseOggettoData> prendiDatiOggetto(@Path("id") int id, @Query("sid") String sid);
}
