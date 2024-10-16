package com.example.pokedex

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokemonColorAndSpecieResponse(
    @SerializedName("color") val color: ColorDetail, // Aqui você usa SerializedName
    @SerializedName("name") val specie: String
) {
    data class ColorDetail(
        @SerializedName("name") val name: String,
        @SerializedName("url") val url: String
    )
}

