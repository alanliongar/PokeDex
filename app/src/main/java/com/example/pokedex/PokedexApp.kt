package com.example.pokedex

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.battle.presentation.AIPokeBattleViewModel
import com.example.pokedex.detail.presentation.ui.PokeDetailScreen
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.list.presentation.PokeListViewModel
import com.example.pokedex.list.presentation.ui.PokeListScreen
import com.example.pokedex.battle.presentation.ui.BattleScreen

@Composable
fun PokeDexApp(
    pokeListViewModel: PokeListViewModel,
    pokeDetailViewModel: PokeDetailViewModel,
    battleListViewModel: AIPokeBattleViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pokemonList") {
        composable(route = "pokemonList") {
            PokeListScreen(navController, pokeListViewModel)
        }
        composable(
            route = "pokemonDetail" + "/{pokeId}",
            arguments = listOf(navArgument("pokeId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val pokeId = requireNotNull(backStackEntry.arguments?.getString("pokeId"))
            PokeDetailScreen(
                pokeId,
                viewModel = pokeDetailViewModel,
                )
        }
        composable(
            route = "battle_screen/{pokeNameOne}/{pokeNameTwo}",
            arguments = listOf(
                navArgument("pokeNameOne") { type = NavType.StringType },
                navArgument("pokeNameTwo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pokeNameOne = backStackEntry.arguments?.getString("pokeNameOne") ?: ""
            val pokeNameTwo = backStackEntry.arguments?.getString("pokeNameTwo") ?: ""
            BattleScreen(
                battleViewModel = battleListViewModel,
                pokeNameOne = pokeNameOne,
                pokeNameTwo = pokeNameTwo,
                navController = navController
            )
        }
    }
}

//fazer o botão popback com arrowback.