package com.example.pokedex

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokeDto(
    val name: String,
    val weight: Int,
    val height: Int,
    @SerializedName("base_experience") val baseExperience: Int,
    @SerializedName("stats") val baseStats: List<Stat>,
    @SerializedName("types") val typesPok: List<TypeSlot>
) {
    data class Stat(
        @SerializedName("base_stat") val baseStat: Int
    )

    data class TypeSlot(
        val slot: Int,
        val type: Type
    )

    data class Type(
        @SerializedName("name") val name: String
    )

}



