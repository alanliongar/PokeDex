package com.example.pokedex.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val POKE_BASE_URL = "https://pokeapi.co/api/v2/"

    // 🔹 PokeAPI Retrofit
    @PokeApiRetrofit
    @Provides
    fun providePokeApiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(POKE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}