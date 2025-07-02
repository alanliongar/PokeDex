package com.example.pokedex.common.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonColorName(
    @SerializedName("color") val color: ColorDetail,
) {
    data class ColorDetail(
        @SerializedName("name") val name: String //quando puxar da API, só esse campo me interessa no momento. *REVISAO
    )
}

