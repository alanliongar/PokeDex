package com.example.pokedex.common.data.remote.model

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokeListDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
){
    val pokeId: Int get() = url.split("/").last{it.isNotEmpty()}.toInt()
    val pokeImgUrl: String
        get() {
        val urls = listOf(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/${pokeId}.svg",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokeId}.png",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokeId}.png"
        )
        return urls.random()
        }
}