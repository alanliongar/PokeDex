package com.example.pokedex.battle.di

import com.example.pokedex.battle.data.remote.OpenAiService
import com.example.pokedex.di.OpenAiRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class AIPokeBattleModule {
    @Provides
    fun provideOpenAiService(
        @OpenAiRetrofit retrofit: Retrofit
    ): OpenAiService {
        return retrofit.create(OpenAiService::class.java)
    }
}