package com.example.pokedex

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokeResponse(
    @SerializedName("results")
    val results: List<PokemonListDto>
)