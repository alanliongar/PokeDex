package com.example.pokedex.battle.presentation.ui

import androidx.compose.ui.text.AnnotatedString

data class BattleUiState(
    val battle: AnnotatedString? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)