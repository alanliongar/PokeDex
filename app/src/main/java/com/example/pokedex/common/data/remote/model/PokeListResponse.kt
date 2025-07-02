package com.example.pokedex.common.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokeListResponse(
    @SerializedName("results")
    val results: List<PokeListDto>
)