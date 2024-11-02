package com.example.pokedex.list.data.local

import com.example.pokedex.common.data.local.PokemonDao
import com.example.pokedex.common.data.local.PokemonEntity
import com.example.pokedex.common.data.model.Pokemon

class PokeListLocalDataSource(
    private val dao: PokemonDao
) {

    suspend fun updateLocalPokemonsList(pokemons: List<Pokemon>) {
        val pokemonsEntities = pokemons.map {
            PokemonEntity(
                id = it.id,
                name = it.name,
                image = it.image,
                color = it.color
            )
        }
        val update = dao.insertAllPokemons(pokemons = pokemonsEntities)
    }


    suspend fun getPokemonList(): List<Pokemon> {
        val pokemonsEntities = dao.getAllPokemons()
        return pokemonsEntities.map {
            Pokemon(
                id = it.id,
                name = it.name,
                image = it.image,
                color = it.color
            )
        }
    }
}