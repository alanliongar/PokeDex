package com.example.pokedex.common.data.remote.model

import android.content.Context
import androidx.compose.ui.graphics.Color

interface CommonFunctionsContract {

    suspend fun getDominantColorFromImage(
        context: Context,
        imageUrl: String?,
        index: Int,
        target: Int,
    ): Pair<Color?, Color?>

    suspend fun getRandomPokeImg(pokeId: Int): String

    fun inequalRandom(): Int {
        return (1..5).random()
    }


}