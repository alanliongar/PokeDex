package com.example.pokedex.list.data.remote

import android.content.Context
import com.example.pokedex.common.data.model.Pokemon

interface RemoteDataSource {
    @Throws(Exception::class)
    suspend fun getPokeList(context: Context, page: Int): Result<List<Pokemon>?>
}