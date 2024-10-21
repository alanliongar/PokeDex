package com.example.pokedex

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {


    @GET("pokemon/?limit=18&offset=0")
    fun getPokemonList(): Call<PokeResponse>

    @GET("pokemon/{name}/")
    suspend fun getPokemonId(@Path("name") name: String): Response<PokemonIdResponse>

    @GET("pokemon/{id}/")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokeDto>

    @GET("pokemon-species/{name}/")
    suspend fun getPokemonColorAndSpecie(@Path("name") name: String): Response<PokemonColorAndSpecieResponse>
}