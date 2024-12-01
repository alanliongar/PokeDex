package com.example.pokedex.list.data

import android.accounts.NetworkErrorException
import android.content.Context
import com.example.pokedex.common.data.model.Pokemon
import com.example.pokedex.list.data.local.PokeListLocalDataSource
import com.example.pokedex.list.data.remote.PokeListRemoteDataSource


class PokeListRepository(
    private val local: PokeListLocalDataSource,
    private val remote: PokeListRemoteDataSource
) {
    suspend fun getPokeList(context: Context, page: Int): Result<List<Pokemon>?> {
        return try {
            val result = remote.getPokeList(context = context, page = page)
            if (result.isSuccess) {
                val pokemonResponse = result.getOrNull() ?: emptyList()
                if (pokemonResponse.isNotEmpty()) {
                    local.updateLocalPokemonsList(pokemons = pokemonResponse, context = context, page = page)
                    return@getPokeList Result.success(local.getPokemonList(page = page))
                } else { //a chamada foi um sucesso, mas o resultado veio vazio.
                    val localData = local.getPokemonList(page = page)
                    if (localData.isEmpty()) {
                        return@getPokeList Result.failure(NetworkErrorException("Empty internet request and not local data found"))
                    } else {
                        return@getPokeList Result.success(localData)
                    }
                }
            } else {
                val localData = local.getPokemonList(page = page)
                if (localData.isEmpty()) {
                    return@getPokeList Result.failure(NetworkErrorException("Empty internet request and not local data found"))
                } else {
                    return@getPokeList Result.success(localData)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return@getPokeList Result.failure(ex)
        }
    }
    suspend fun getPokeCount(): Int{
        return local.getPokeCount()
    }
}