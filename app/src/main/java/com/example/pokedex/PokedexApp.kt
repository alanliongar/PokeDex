package com.example.pokedex

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.detail.presentation.ui.PokeDetailScreen
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.list.presentation.PokeListViewModel
import com.example.pokedex.list.presentation.ui.PokeListScreen

@Composable
fun PokeDexApp(
    pokeListViewModel: PokeListViewModel,
    pokeDetailViewModel: PokeDetailViewModel
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
    }
}

//fazer o botão popback com arrowback.