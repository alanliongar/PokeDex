package com.example.pokedex.battle.data.remote

import com.example.pokedex.battle.data.model.BattlePromptProvider
import com.example.pokedex_hacksprint_2025.battle.data.model.Message
import com.example.pokedex_hacksprint_2025.battle.data.model.OpenAiRequest
import javax.inject.Inject


class AIPokeBattleRemoteDataSource @Inject constructor(
    private val openAiService: OpenAiService,
    private val promptProvider: BattlePromptProvider
): RemoteDataSource {
    override suspend fun battleResult(
        firstPokeName: String,
        secondPokeName: String
    ): Result<String> {

        val request = OpenAiRequest(
            messages = listOf(
                Message("system", promptProvider.systemRole()),
                Message("user", promptProvider.battlePrompt(firstPokeName, secondPokeName))
            )
        )

        return try {
            val response = openAiService.getBattleResult(request)

            if (response.isSuccessful) {
                val battleResult = response.body()
                    ?.choices
                    ?.firstOrNull()
                    ?.message
                    ?.content
                    ?: promptProvider.errorGenerate()

                Result.success(promptProvider.resultPrefix(battleResult))
            } else {
                val errorMessage = response.errorBody()?.string()
                    ?: promptProvider.errorUnknown()

                Result.failure(
                    Exception(promptProvider.requestError(errorMessage))
                )
            }
        } catch (e: Exception) {
            val message = e.localizedMessage ?: promptProvider.errorUnknown()
            Result.failure(
                Exception(promptProvider.unexpectedError(message))
            )
        }
    }
}

