package com.example.pokedex.battle.data.remote

import com.example.pokedex_hacksprint_2025.battle.data.model.OpenAiRequest
import com.example.pokedex_hacksprint_2025.battle.data.model.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getBattleResult(@Body request: OpenAiRequest): Response<OpenAiResponse>
}