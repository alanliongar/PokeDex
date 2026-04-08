
package com.example.pokedex.common.data.remote
/*
//Esse arquivo foi desativado, pois foi necessário fazer a gestão disso no hilt (NetworkModule), pq são dois retrofits,
//entao o hilt tem q ter seus qualifiers pra saber qual é qual.

import com.example.pokedexsimple.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL: String = "https://pokeapi.co/api/v2/"

object PokeRetrofitClient {
    val retrofitInstance: Retrofit
        get() {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
}

private const val OPENAI_BASE_URL = "https://api.openai.com/v1/"
private const val OPENAI_API_KEY = BuildConfig.API_KEY

private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("Authorization", "Bearer $OPENAI_API_KEY")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()
}

object RetrofitOpenAI {
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}*/
