package com.example.pokedex.battle.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pokedex.battle.presentation.AIPokeBattleViewModel
import com.example.pokedex.common.ui.LoadingScreen
import com.example.pokedex.common.ui.PokeErrorImage
import com.example.pokedex.common.ui.PokeTitleImage

@Composable
fun BattleScreen(
    modifier: Modifier = Modifier,
    battleViewModel: AIPokeBattleViewModel = hiltViewModel(),
    pokeNameOne: String,
    pokeNameTwo: String,
    navController: NavHostController
) {
    battleViewModel.fetchBattleResult(pokeNameOne, pokeNameTwo)
    val battleUiState = battleViewModel.pokemonBattleResult.collectAsState().value
    BattleContent(modifier = modifier, battleUiState = battleUiState, navController = navController)
}

@Composable
private fun BattleContent(
    modifier: Modifier = Modifier,
    battleUiState: BattleUiState,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (battleUiState.isError == true) {
            PokeErrorImage(battleUiState.errorMessage)
        } else if (battleUiState.isLoading == true) {
            PokeTitleImage(navHostController = navController)
            LoadingScreen()
        } else {
            PokeTitleImage(navHostController = navController)
            Spacer(modifier = Modifier.size(24.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                battleUiState.battle?.let {
                    Text(
                        text = it.text,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BattleContentPreview(modifier: Modifier = Modifier) {
    val navHostController = NavHostController(context = LocalContext.current)
    val battleUiState = BattleUiState(
        battle = AnnotatedString("\uD83D\uDD25 Battle Result\n\n In a fierce battle between Lugia 🌊 and Mewtwo 🧠, Mewtwo unleashes overwhelming Psychic-type pressure and quickly takes control of the fight.\n\nLugia resists with impressive endurance and powerful aerial force, but Mewtwo’s superior offensive power and type advantage keep the legendary bird on the defensive.\n\nIn the end, Mewtwo claims victory through raw power, sharp strategy, and relentless psychic attacks ⚡"),
        isLoading = false,
        isError = false,
        errorMessage = null
    )
    BattleContent(
        modifier = modifier,
        battleUiState = battleUiState,
        navController = navHostController
    )
}