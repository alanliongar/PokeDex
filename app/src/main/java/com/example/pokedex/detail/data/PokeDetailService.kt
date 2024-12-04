package com.example.pokedex.detail.data

import com.example.pokedex.common.data.remote.model.PokemonColorName
import com.example.pokedex.common.data.remote.model.PokeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeDetailService {
    @GET("pokemon/{id}/")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokeDto> //Detalhes (vários)
}