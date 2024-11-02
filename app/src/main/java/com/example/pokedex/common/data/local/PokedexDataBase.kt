package com.example.pokedex.common.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database([PokemonEntity::class], version = 1)
abstract class PokedexDataBase: RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
}