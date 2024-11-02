package com.example.pokedex.common.data.remote.model

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokeDto( //essa é uma resposta de DETALHE, ela retorna um elemento disso a cada chamada.
    var name: String,
    var weight: Int,
    var height: Int,
    @SerializedName("id") var pokeId: Int,
    @SerializedName("base_experience") var baseExperience: Int,
    @SerializedName("stats") var baseStats: List<Stat>,
    @SerializedName("types") var typesPok: List<TypeSlot>
) {
    data class Stat(
        @SerializedName("base_stat") var baseStat: Int
    )

    data class TypeSlot(
        var slot: Int,
        var type: Type
    )

    data class Type(
        @SerializedName("name") var name: String
    )

    val pokeImg: String
        get() {
            val urls = listOf(
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/${pokeId}.svg",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokeId}.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokeId}.png"
            )
            return urls.random()
        }
}