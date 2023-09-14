package com.salvadormorado.pokedexmqs.util.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salvadormorado.pokedexmqs.R
import com.salvadormorado.pokedexmqs.data.models.PokemonDetail

class AdapterRecyclerPokemons(private val context: Context, private val pokemonList: List<PokemonDetail>) :
    RecyclerView.Adapter<AdapterRecyclerPokemons.PokemonViewHolder>() {

    var onItemClick: ((PokemonDetail) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        holder.textViewPokemonName.text = pokemon.name

        holder.textViewNumberPokemon.text = "#" + pokemon.numberPokemon.toString()

        Glide.with(context)
            .load(pokemon.imageBest)
            .into(holder.imageViewPokemon)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPokemon: ImageView = itemView.findViewById(R.id.imageViewPokemon)
        val textViewPokemonName: TextView = itemView.findViewById(R.id.textViewPokemonName)
        val textViewNumberPokemon: TextView = itemView.findViewById(R.id.textViewNumberPokemon)
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(pokemonList[adapterPosition])
            }
        }
    }
}
