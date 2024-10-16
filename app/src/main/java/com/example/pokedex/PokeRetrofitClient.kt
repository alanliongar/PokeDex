package com.example.pokedex

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokeRetrofitClient {
    private const val BASE_URL: String = "https://pokeapi.co/api/v2/"

    val retrofitInstance: Retrofit
        get() {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
}

