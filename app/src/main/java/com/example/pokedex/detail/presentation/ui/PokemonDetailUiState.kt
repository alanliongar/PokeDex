package com.example.pokedex.detail.presentation.ui

import com.example.pokedex.common.data.remote.model.PokeDto
import androidx.compose.ui.graphics.Color

data class PokemonDetailUiState(
    val PokeDetail: PokeDto? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val color: Color? = null,
    val image: String = ""
)
