package com.example.pokedex

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
//https://pokeapi.co/api/v2/pokemon/?limit=18&offset=0
    @GET("pokemon/?limit=18&offset=0")
    fun getPokemonList(): Call<PokeResponse> //LISTA

    @GET("pokemon/{name}/")
    suspend fun getPokemonId(@Path("name") name: String): Response<PokemonIdResponse> //ID

    @GET("pokemon/{id}/")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokeDto> //Detalhes (vários)

    @GET("pokemon-species/{name}/")
    suspend fun getPokemonColorAndSpecie(@Path("name") name: String): Response<PokemonColorAndSpecieResponse>
    //Detalhe: cor e espécie (outro serviço)

    //V-Tarefa: Buscar na documentação do kotlin, se essa p$#@% de linguagem suporta uma variável equivalente ao 'variant do VBA.
    //V-Suporta: variável "Any" resolveria o problema, apesar de ser mais limitada que a 'variant
    //C-takewp viu
    //Tarefa: quando puxar a lista, não pegar somente o nome do pokemon, puxar também seu devido ID
    //Parei nos 6:22 do vídeo "Organizando pacotes", revê-lo do começo.
}