package com.salvadormorado.pokedexmqs.data.services

import com.salvadormorado.pokedexmqs.data.models.PokemonDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServicePokemon {
    @GET("pokemon/{id_pokemon}")
    fun getPokemon(@Path("id_pokemon") idPokemon: String): Call<PokemonDetail>
}