package com.salvadormorado.pokedexmqs.ui.pokedex

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.salvadormorado.pokedexmqs.data.models.PokemonDetail
import com.salvadormorado.pokedexmqs.data.services.ApiServicePokemon
import com.salvadormorado.pokedexmqs.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.salvadormorado.pokedexmqs.PokedexApp

class PokedexViewModel(application: Application) : AndroidViewModel(application){
    private lateinit var apiService: ApiServicePokemon

    lateinit var listPokemons : ArrayList<PokemonDetail>
    var numberPokemon : Int = 0

    private val _dataPokemon = MutableLiveData<PokemonDetail>()
    val dataPokemon: LiveData<PokemonDetail> get() = _dataPokemon

    private val _distanceMoved = MutableLiveData<Float>()
    val distanceMoved: LiveData<Float> get() = _distanceMoved

    private val _flagProgress = MutableLiveData<Boolean>()
    val flagProgress: LiveData<Boolean> get() = _flagProgress

    private val _hasMoved = MutableLiveData<Boolean>()
    val hasMoved: LiveData<Boolean> = _hasMoved

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private var initialLocation: Location? = null

    init {
        listPokemons = ArrayList<PokemonDetail>()
        apiService = (application as PokedexApp).apiService
    }

    fun getPokemonFromAPI() {
        _flagProgress.value = true
        val idPokemon = Util.generateRandomNumber().toString()
        val call = apiService.getPokemon(idPokemon)
        call.enqueue(object : Callback<PokemonDetail> {
            override fun onResponse(call: Call<PokemonDetail>, response: Response<PokemonDetail>) {
                if (response.isSuccessful) {
                    Util.FLAG = true
                    numberPokemon++
                    response.body()?.imageBest = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${idPokemon}.png"
                    response.body()?.numberPokemon = numberPokemon
                    listPokemons.add(response.body()!!)
                    _dataPokemon.value = response.body()
                    Log.e("onResponse", response.body().toString())
                } else {
                    Log.e("onResponse", "ERROR")
                }
                _flagProgress.value = false
            }

            override fun onFailure(call: Call<PokemonDetail>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
                _flagProgress.value = false
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun checkMovement() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setMinUpdateIntervalMillis(1000)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.lastLocation?.let { currentLocation ->
                    if (initialLocation == null) {
                        // Esta es la primera ubicación recibida, establece la ubicación inicial
                        initialLocation = currentLocation
                        //Log.e("latitude", currentLocation.latitude.toString())
                        //Log.e("longitude", currentLocation.longitude.toString())
                    } else {
                        // Calcula la distancia entre la ubicación actual y la inicial
                        val distanceMoved = initialLocation!!.distanceTo(currentLocation)

                        // El usuario se ha movido al menos 10 metros desde la ubicación inicial
                        if (distanceMoved >= 10.0) {
                            initialLocation = currentLocation
                            _hasMoved.postValue(true)
                        }else{
                            _hasMoved.postValue(false)
                        }

                        _distanceMoved.value = distanceMoved

                        //Log.e("latitude", currentLocation.latitude.toString())
                        //Log.e("longitude", currentLocation.longitude.toString())
                        //Log.e("_distanceMoved", distanceMoved.toString())
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }
}