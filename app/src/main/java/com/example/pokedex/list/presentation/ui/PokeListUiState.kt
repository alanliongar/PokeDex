package com.example.pokedex.list.presentation.ui

import androidx.compose.ui.graphics.Color

//é uma mini regra de negócios
data class PokeListUiState(
    val pokemonUiDataList: List<PokemonUiData> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "Something went wrong"
)

data class PokemonUiData(
    val name: String? = "",
    var id: Int? = null,
    var color: Color? = null,
    var imageUrl: String? = null
)