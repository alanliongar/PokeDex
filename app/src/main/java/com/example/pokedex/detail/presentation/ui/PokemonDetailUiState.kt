package com.example.pokedex.detail.presentation.ui

import com.example.pokedex.common.data.remote.model.PokeDto
import androidx.compose.ui.graphics.Color

data class PokemonDetailUiState(
    val PokeDetail: PokeDto? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    var color: Color? = null,
    var textColor: Color? = Color.Black,
    val image: String = ""
)
