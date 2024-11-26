package com.example.pokedex.list.data.remote

import com.example.pokedex.common.data.remote.model.PokeListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeListService {
    @GET("pokemon/")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 12,
        @Query("offset") offset: Int = 0
    ): Response<PokeListResponse>
}