package com.example.pokedex.battle.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedex.battle.presentation.AIPokeBattleViewModel
import com.example.pokedex.common.ui.LoadingScreen
import com.example.pokedex.common.ui.PokeErrorImage
import com.example.pokedex.common.ui.PokeTitleImage

@Composable
fun BattleScreen(
    modifier: Modifier = Modifier,
    battleViewModel: AIPokeBattleViewModel,
    pokeNameOne: String,
    pokeNameTwo: String,
    navController: NavController
) {
    battleViewModel.fetchBattleResult(pokeNameOne, pokeNameTwo)
    val battleUiState = battleViewModel.pokemonBattleResult.collectAsState().value
    BattleContent(modifier = modifier, battleUiState = battleUiState, navController = navController)
}

@Composable
private fun BattleContent(
    modifier: Modifier = Modifier,
    battleUiState: BattleUiState,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (battleUiState.isError == true) {
            PokeErrorImage(battleUiState.errorMessage)
        } else if (battleUiState.isLoading == true) {
            PokeTitleImage()
            LoadingScreen()
        } else {
            PokeTitleImage()
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                ,
                contentAlignment = Alignment.Center
            ) {
                battleUiState.battle?.let {
                    Text(
                        text = it.text,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f)
                    )
                }
            }
        }
    }

}