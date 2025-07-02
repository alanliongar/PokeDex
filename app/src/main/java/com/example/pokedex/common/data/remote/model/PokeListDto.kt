package com.example.pokedex.common.data.remote.model

import android.content.Context
import com.google.gson.annotations.SerializedName

data class PokeListDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
) {
    val pokeId: Int get() = url.split("/").last { it.isNotEmpty() }.toInt()

    fun getPokeImgUrls(): List<String> {
        return listOf(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/${pokeId}.svg",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokeId}.png",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokeId}.png"
        )
    }

    suspend fun getPokeImg(context: Context): List<String> {
        val pokeImgUrl = getPokeImgUrls()
        val img1 = CommonFunctions().downloadAndSaveImageWithCoil(
            context = context,
            imageUrl = pokeImgUrl[0]
        )
        val img2 = CommonFunctions().downloadAndSaveImageWithCoil(
            context = context,
            imageUrl = pokeImgUrl[1]
        )
        val img3 = CommonFunctions().downloadAndSaveImageWithCoil(
            context = context,
            imageUrl = pokeImgUrl[2]
        )
        return listOf(img1, img2, img3)
    }
}