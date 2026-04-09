package com.example.pokedex.battle.data.model

interface BattlePromptProvider {
    fun systemRole(): String
    fun battlePrompt(firstPokeName: String, secondPokeName: String): String
    fun resultPrefix(result: String): String
    fun errorGenerate(): String
    fun errorUnknown(): String
    fun requestError(message: String): String
    fun unexpectedError(message: String): String
}

