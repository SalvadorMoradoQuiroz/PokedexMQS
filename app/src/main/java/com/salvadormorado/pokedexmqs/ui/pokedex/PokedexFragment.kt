package com.salvadormorado.pokedexmqs.ui.pokedex

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salvadormorado.pokedexmqs.PokedexApp
import com.salvadormorado.pokedexmqs.R
import com.salvadormorado.pokedexmqs.data.models.PokemonDetail
import com.salvadormorado.pokedexmqs.data.services.ApiServicePokemon
import com.salvadormorado.pokedexmqs.databinding.FragmentPokedexBinding
import com.salvadormorado.pokedexmqs.ui.detailpokemon.DetailPokemonFragment
import com.salvadormorado.pokedexmqs.util.Util
import com.salvadormorado.pokedexmqs.util.adapters.AdapterRecyclerPokemons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokedexFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var vibrator: Vibrator
    private val MILLISECONDS = 1000L

    private var _binding: FragmentPokedexBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PokedexViewModel
    private lateinit var adapterRecyclerPokemons : AdapterRecyclerPokemons

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPokedexBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(PokedexViewModel::class.java)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.checkMovement()
                } else {
                    Toast.makeText(requireContext(), "Acepta los permisos de ubicación para encontrar pokémons cada 10m.", Toast.LENGTH_SHORT).show()
                }
            }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterRecyclerPokemons = AdapterRecyclerPokemons(requireContext(), viewModel.listPokemons)
        adapterRecyclerPokemons.onItemClick = {
            changeDetailPokemonFragment(it)
        }
        binding.recyclerViewPokemons.adapter = adapterRecyclerPokemons

        binding.progressBarMeters.max = 10

        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanCount = 2
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerViewPokemons.layoutManager = layoutManager

        vibrator = ContextCompat.getSystemService(requireContext(), Vibrator::class.java) as Vibrator

        binding.buttonShowPokemon.setOnClickListener {
            showPokemon()
        }

        viewModel.dataPokemon.observe(viewLifecycleOwner) {pokemon ->
            if(Util.FLAG){
                adapterRecyclerPokemons.notifyDataSetChanged()
                val vibrationEffect = VibrationEffect.createOneShot(MILLISECONDS, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
                var alert = Util.alertDialogCustom("Nuevo Pokémon", "Se encontró un nuevo pokémon", requireContext())
                alert.show()
                val buttonAccept = alert.findViewById<Button>(R.id.closeButton)
                buttonAccept.setOnClickListener {
                    changeDetailPokemonFragment(pokemon)
                    alert.dismiss()
                }
                Util.FLAG = false
            }
        }

        viewModel.flagProgress.observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.hasMoved.observe(viewLifecycleOwner) { hasMoved ->
            // El dispositivo se ha movido aproximadamente 10 metros
            if (hasMoved) {
                showPokemon()
            }
        }

        viewModel.distanceMoved.observe(viewLifecycleOwner) { meters ->
            binding.textViewMeters.setText("A " + Util.roundToTwoDecimalPlaces(meters) + "m de un pokémon")
            if(meters<10){
                binding.progressBarMeters.progress = meters.toInt()
            }else{
                binding.progressBarMeters.progress = 10
            }
        }

        if(Util.isLocationEnabled(requireContext())){
            checkLocationPermissions()
        }else{
            Toast.makeText(requireContext(), "Encender ubicación para el correcto funcionamiento.", Toast.LENGTH_SHORT).show()
            checkLocationPermissions()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Util.FLAG = false
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Util.FLAG = false
    }

    override fun onPause() {
        super.onPause()
        Util.FLAG = false
    }

    private fun changeDetailPokemonFragment(pokemon:PokemonDetail){
        val bundle = Bundle()
        bundle.putString("image", pokemon.imageBest)
        bundle.putString("number", pokemon.numberPokemon.toString())
        bundle.putString("name", pokemon.name)
        bundle.putString("height", pokemon.height.toString())
        bundle.putString("weight", pokemon.weight.toString())
        val fragmentDestino = DetailPokemonFragment()
        fragmentDestino.arguments = bundle
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragmentDestino)
            .addToBackStack(null)
            .commit()
    }

    private fun showPokemon(){
        if(Util.isInternetAvailable(requireContext())){
            viewModel.getPokemonFromAPI()
        }else{
            Toast.makeText(requireContext(), "Debes contar con acceso a internet para mostrar un pokémon.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun  checkLocationPermissions() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions()
        } else {
            viewModel.checkMovement()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted && coarseLocationGranted
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

}