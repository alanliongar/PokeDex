package com.example.pokedex.battle.di

import com.example.pokedex.di.OpenAiRetrofit
import com.example.pokedexsimple.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AIPokeBattleSTModule {

    private const val OPENAI_BASE_URL = "https://api.openai.com/v1/"

    // 🔹 OpenAI Retrofit
    @OpenAiRetrofit
    @Provides
    fun provideOpenAiRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 🔹 OkHttp com header da OpenAI
    @Provides
    fun provideOpenAiHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.API_KEY}")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}