package com.salvadormorado.pokedexmqs

import android.app.Application
import com.salvadormorado.pokedexmqs.data.services.ApiServicePokemon
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokedexApp : Application() {
    val apiService: ApiServicePokemon by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiServicePokemon::class.java)
    }
}