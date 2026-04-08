package com.example.pokedex.battle.data.remote

import com.example.pokedex.battle.data.OpenAiService
import com.example.pokedex_hacksprint_2025.battle.data.model.Message
import com.example.pokedex_hacksprint_2025.battle.data.model.OpenAiRequest
import kotlin.Result

class AIPokeBattleRemoteDataSource(
    private val openAiService: OpenAiService
) {
    suspend fun battleResult(firstPokeName: String, secondPokeName: String): Result<String> {

        val prompt = """
            Simule uma batalha rápida entre $firstPokeName e $secondPokeName. 
            Explique em no máximo 3 frases quem vence e por quê (narrativa no tempo presente),
            considerando vantagens de tipo e poder de ataque.
        
            ✅ **IMPORTANTE:**
            - Cada frase deve ser separada por **duas quebras de linha (`\n\n`)**.
            - Retorne o texto já formatado corretamente, para exibição direta na tela.
            - Mantenha o tom **narrativo e envolvente**, usando emojis para representar os Pokémon e os golpes.
        """.trimIndent()

        val request = OpenAiRequest(
            messages = listOf(
                Message("system", "Você é um narrador especialista em batalhas Pokémon."),
                Message("user", prompt)
            )
        )

        return try {
            val response = openAiService.getBattleResult(request) // ✅ Agora chamamos a API direto
            if (response.isSuccessful) {
                val battleResult = response.body()?.choices?.firstOrNull()?.message?.content
                    ?: "Erro ao gerar batalha."
                Result.success("🔥 Resultado da batalha: \n\n $battleResult")
            } else {
                Result.failure(
                    Exception(
                        "❌ Requisition error: ${
                            response.errorBody()?.string() ?: "Unknown error."
                        }"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(Exception("🚨 Unexpected error: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}

