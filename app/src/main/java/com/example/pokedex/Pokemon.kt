package com.example.pokedex

import androidx.annotation.DrawableRes

data class Pokemon(
    val name: String,
    var id: Int? = null,
    var specie: String? = null,
    @DrawableRes var color: Int? = null,
    var imageUrl: String? = null
)

//https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png