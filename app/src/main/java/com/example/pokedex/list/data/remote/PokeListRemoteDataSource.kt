package com.example.pokedex.list.data.remote

import android.accounts.NetworkErrorException
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.pokedex.common.data.model.Pokemon
import com.example.pokedex.common.data.remote.model.CommonFunctions

class PokeListRemoteDataSource(
    private val pokeListService: PokeListService
) {
    suspend fun getPokeList(context: Context, page: Int): Result<List<Pokemon>?> {
        return try {
            val result =
                pokeListService.getPokemonList(offset = CommonFunctions().calculateOffset(page))
            if (result.isSuccessful) {
                val pokemons = result.body()?.results?.map {
                    Pokemon(
                        id = it.pokeId,
                        name = it.name,
                        image = it.getPokeImg(context = context),
                        color = Color.Red.value.toInt(),
                        page = page
                    )
                }
                if (pokemons != null) {
                    Result.success(pokemons)
                } else {
                    Result.failure(NetworkErrorException(result.message()))
                }
            } else {
                Result.failure(NetworkErrorException(result.message()))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.failure(ex)
        }
    }
}