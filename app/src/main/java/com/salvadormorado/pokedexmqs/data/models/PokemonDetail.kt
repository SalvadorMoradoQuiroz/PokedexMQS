package com.salvadormorado.pokedexmqs.data.models

data class PokemonDetail (
    val name: String,
    val sprites: PokemonSprites,
    val weight: Int,
    val height: Int,
    var imageBest:String?,
    var numberPokemon:Int?
)

data class PokemonSprites(
    val front_default: String
)
