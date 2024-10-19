package com.example.pokedex

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun PokeDexApp(context: Context) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pokemonList") {
        composable(route = "pokemonList") {
            PokeListScreen(context = context, navController)
        }
        composable(
            route = "pokemonDetail" + "/{pokeId}",
            arguments = listOf(navArgument("pokeId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val pokeId = requireNotNull(backStackEntry.arguments?.getString("pokeId"))
            PokeDetailScreen(pokeId)
        }
    }
}