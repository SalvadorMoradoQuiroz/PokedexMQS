package com.salvadormorado.pokedexmqs.ui.detailpokemon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.salvadormorado.pokedexmqs.databinding.FragmentDetailPokemonBinding

class DetailPokemonFragment : Fragment() {

    private var _binding: FragmentDetailPokemonBinding? = null
    private val binding get() = _binding!!

    private var image:String = ""
    private var number:String = ""
    private var name:String = ""
    private var height:String = ""
    private var weight:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailPokemonBinding.inflate(inflater, container, false)

        val bundle = arguments
        if (bundle != null) {
            image = bundle.getString("image").toString()
            name = bundle.getString("name").toString()
            number = bundle.getString("number").toString()
            height = bundle.getString("height").toString()
            weight = bundle.getString("weight").toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewNumberPokemonD.text = "#${number}"
        binding.textViewNamePokemonD.text = name
        binding.textViewHeightPokemonD.text = "Altura: ${height} m"
        binding.textViewWeightPokemonD.text = "Peso: ${weight} kg"

        Glide.with(requireContext())
            .load(image)
            .into(binding.imageViewPokemonD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}