package com.example.pokedex.detail

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.pokedex.common.data.remote.model.CommonFunctionsContract

class FakePokeCommonFunctions: CommonFunctionsContract {
    var returnColors: Pair<Color?, Color?> = Pair(Color(1), Color(1))
    override suspend fun getDominantColorFromImage(
        context: Context,
        imageUrl: String?,
        index: Int,
        target: Int
    ): Pair<Color?, Color?> {
        return returnColors
    }

    var getRandomPokeImg = "image1"
    override suspend fun getRandomPokeImg(pokeId: Int): String {
        return getRandomPokeImg
    }

    override fun inequalRandom(): Int {
        return super.inequalRandom()
    }
}