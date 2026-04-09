package com.example.pokedex.battle.data.model

import android.content.Context
import com.example.pokedexsimple.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidBattlePromptProvider @Inject constructor(
    @ApplicationContext private val context: Context
): BattlePromptProvider {

    override fun systemRole(): String {
        return context.getString(R.string.ai_battle_system_role)
    }

    override fun battlePrompt(firstPokeName: String, secondPokeName: String): String {
        return context.getString(
            R.string.ai_battle_prompt,
            firstPokeName,
            secondPokeName
        )
    }

    override fun resultPrefix(result: String): String {
        return context.getString(R.string.ai_battle_result_prefix, result)
    }

    override fun errorGenerate(): String {
        return context.getString(R.string.ai_battle_error_generate)
    }

    override fun errorUnknown(): String {
        return context.getString(R.string.ai_battle_error_unknown)
    }

    override fun requestError(message: String): String {
        return context.getString(R.string.ai_battle_error_request, message)
    }

    override fun unexpectedError(message: String): String {
        return context.getString(R.string.ai_battle_error_unexpected, message)
    }
}