package com.example.pokedex

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class PokemonListDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
){
    val id: Int get() = url.split("/").last{it.isNotEmpty()}.toInt()
}