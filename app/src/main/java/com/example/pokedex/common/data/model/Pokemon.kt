package com.example.pokedex.common.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val image: List<String>,
    val color: Int,
    val page: Int
)