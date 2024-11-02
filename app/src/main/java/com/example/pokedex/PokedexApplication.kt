package com.example.pokedex

import android.app.Application
import androidx.room.Room
import com.example.pokedex.common.data.remote.PokeRetrofitClient
import com.example.pokedex.common.data.local.PokedexDataBase
import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.data.local.PokeListLocalDataSource
import com.example.pokedex.list.data.remote.PokeListRemoteDataSource
import com.example.pokedex.list.data.remote.PokeListService

class PokedexApplication : Application() {
    val db by lazy {Room.databaseBuilder(applicationContext,PokedexDataBase::class.java,"pokedex-localDatabase_v2").build()}
    private val pokeListService by lazy {PokeRetrofitClient.retrofitInstance.create(PokeListService::class.java)}
    private val remoteDataService by lazy {PokeListRemoteDataSource(pokeListService, context = applicationContext)}
    private val localDataSource: PokeListLocalDataSource by lazy {PokeListLocalDataSource(db.getPokemonDao())}
    val repository: PokeListRepository by lazy {PokeListRepository(local = localDataSource, remote = remoteDataService)}
}