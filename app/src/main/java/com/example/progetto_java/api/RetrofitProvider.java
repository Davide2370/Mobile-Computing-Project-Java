package com.example.progetto_java.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitProvider {
    private static ApiInterface apiInterface = null;

    public static ApiInterface getApinterface() {
        if (apiInterface == null) {
            String BASE_URL = "https://develop.ewlab.di.unimi.it/mc/mostri/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }
}
