package com.example.pokedex.list.data.local

import android.content.Context
import com.example.pokedex.common.data.model.Pokemon

interface LocalDataSource {

    suspend fun updateLocalPokemonsList(pokemons: List<Pokemon>, context: Context, page: Int)

    suspend fun getPokeCount(): Int

    fun getPokemonList(page: Int): List<Pokemon>
}