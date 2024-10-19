package com.example.pokedex

@kotlinx.serialization.Serializable
data class PokeDto(
    val name: String
)

//https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png

//Proximo passo: verificar quais dados eu preciso pra detalhar o pokemon
/*
exemplo: Id 6
        nome = charizard
        tipo = fire e flying
        infos: peso e altura
        base stats: hp, atk, def, spd, exp*/
//Em apenas um serviço, consigo tirar todas essas informações, será possível?