package com.example.pokedex.list.data.remote

import com.example.pokedex.common.data.remote.model.PokeListResponse
import retrofit2.Response
import retrofit2.http.GET

interface PokeListService {
    @GET("pokemon/?limit=20&offset=0")
    suspend fun getPokemonList(): Response<PokeListResponse> //LISTA
}