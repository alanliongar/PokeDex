package com.example.pokedex.detail.di

import com.example.pokedex.detail.data.PokeDetailService
import com.example.pokedex.di.PokeApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class PokeDetailModule {
    @Provides
    fun providesDetailService(@PokeApiRetrofit retrofit: Retrofit): PokeDetailService {
        return retrofit.create(PokeDetailService::class.java)
    }
}