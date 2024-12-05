package com.example.pokedex.detail.data

import com.example.pokedex.common.data.remote.model.PokeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeDetailService {
    @GET("pokemon/{id}/")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokeDto> //Detalhes (vários)
}

class PokeDetailServiceImpl : PokeDetailService {
    override suspend fun getPokemonDetail(id: Int): Response<PokeDto> {
        return Response.success(
            PokeDto(
                name = "Bulbasaur",
                weight = 69,
                height = 7,
                pokeId = 1,
                baseExperience = 64,
                baseStats = listOf(
                    PokeDto.Stat(baseStat = 45),
                    PokeDto.Stat(baseStat = 49),
                    PokeDto.Stat(baseStat = 49)
                ),
                typesPok = listOf(
                    PokeDto.TypeSlot(
                        slot = 1,
                        type = PokeDto.Type(name = "Grass")
                    ),
                    PokeDto.TypeSlot(
                        slot = 2,
                        type = PokeDto.Type(name = "Poison")
                    )
                )
            )
        )
    }
}