package com.example.pokedex.common.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPokemons(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemonentity")
    fun getAllPokemons(): List<PokemonEntity>

    @Query("SELECT * FROM PokemonEntity WHERE name = :name")
    fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT COUNT(*) FROM PokemonEntity")
    suspend fun getPokeCount(): Int
}