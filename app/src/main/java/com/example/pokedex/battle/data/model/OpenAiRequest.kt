package com.example.pokedex_hacksprint_2025.battle.data.model

data class OpenAiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String, // "system", "user" ou "assistant"
    val content: String
)

data class OpenAiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
