package com.example.pokedex.common.data.local

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val image1: String,
    val image2: String,
    val image3: String,
    val color: Int,
    val page: Int,
)
