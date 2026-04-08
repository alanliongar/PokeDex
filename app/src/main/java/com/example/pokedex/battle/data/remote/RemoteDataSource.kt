package com.example.pokedex.battle.data.remote

interface RemoteDataSource {
    suspend fun battleResult(firstPokeName: String, secondPokeName: String): Result<String>
}