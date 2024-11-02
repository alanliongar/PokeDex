package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.list.presentation.PokeListViewModel

class MainActivity : ComponentActivity() {

    private val pokeListViewModel by viewModels<PokeListViewModel> { PokeListViewModel.Factory }
    private val pokeDetailViewModel by viewModels<PokeDetailViewModel> { PokeDetailViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PokeDexApp(pokeListViewModel, pokeDetailViewModel) }
    }
}