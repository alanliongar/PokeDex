package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.pokedex.battle.presentation.AIPokeBattleViewModel
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.list.presentation.PokeListViewModel

class MainActivity : ComponentActivity() {
    private val pokeListViewModel by viewModels<PokeListViewModel> { PokeListViewModel.Factory }
    private val pokeDetailViewModel by viewModels<PokeDetailViewModel> { PokeDetailViewModel.Factory }
    private val battleListViewModel by viewModels<AIPokeBattleViewModel> { AIPokeBattleViewModel.Factory }
    //Testing port
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PokeDexApp(pokeListViewModel, pokeDetailViewModel, battleListViewModel) }
    }
}