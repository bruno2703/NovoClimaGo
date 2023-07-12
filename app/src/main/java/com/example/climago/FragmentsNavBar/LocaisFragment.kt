package com.example.climago.FragmentsNavBar

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climago.AdapterLocais
import com.example.climago.MapsActivity
import com.example.climago.R
import com.example.climago.AUXILIAR.WeatherResponse
import com.example.climago.Helper
import com.example.climago.databinding.FragmentLocaisBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.checkerframework.checker.units.qual.Temperature
import java.io.IOException
import java.net.URLEncoder
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class LocaisFragment : Fragment(R.layout.fragment_locais), CoroutineScope by MainScope() {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private lateinit var binding: FragmentLocaisBinding

    val cityNames = mutableListOf<String>()
    val TemperatureValues = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocaisBinding.bind(view)


        binding.btMaps.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intent)
        }



    }

    //manda as listas pro adapter
    private fun initRecyclerView(){
        Log.d("dog","Verificando; ${cityNames.last()} --- ${TemperatureValues.last()}")
        pacote(cityNames.last(),TemperatureValues.last())
        binding.RCListaLocais.layoutManager = LinearLayoutManager(requireContext())
        binding.RCListaLocais.setHasFixedSize(true)
        binding.RCListaLocais.adapter = AdapterLocais(getList(), getTemp())
    }

    private fun getList() = cityNames

    private fun getTemp() = TemperatureValues


    //Firebase

    val db = FirebaseFirestore.getInstance()


    private suspend fun CarregarCidades() {

        val result = db.collection("cidades").get().await()

        for (city in result) {
            city.data["cidade"]?.let {
                cityNames.add(it.toString())

                // Aguardamos a função getWeather
                getWeather(it.toString())
            }
            Log.d("Lista cidades","Mutable List; $cityNames")
        }

        Log.d("Lista cidades", cityNames.toString())
    }

    //API

    private suspend fun getWeather(cityName: String?) {
        withContext(Dispatchers.IO) {
            val encodedCityName = URLEncoder.encode(cityName, "utf-8")
            val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=$encodedCityName&appid=207b8be31a9062d5eff256f1acb51668&units=metric")
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("Error", "Network request failed: $response")
                return@withContext
            }

            val jsonData = response.body?.string()
            val adapter = moshi.adapter(WeatherResponse::class.java)
            val weatherResponse = adapter.fromJson(jsonData)

            Log.d("Weather", "$weatherResponse")

            val temperature = weatherResponse?.main?.temp
            val weatherDescription = weatherResponse?.weather?.get(0)?.description
            val weatherData = Pair(temperature, weatherDescription) // Pair of temperature and description

            withContext(Dispatchers.Main) {
                TemperatureValues.add(temperature.toString())
                Log.d("Lista temperatura", "Nome da cidade $cityName --- Temperatura $temperature --- Mutable List; $TemperatureValues")
            }
        }
    }


    //Limpa as listas
    private fun ClearLists(){
        cityNames.clear()
        TemperatureValues.clear()

    }

    override fun onResume() {
        super.onResume()
        manutencao()
    }

    private fun manutencao() {
        ClearLists()

        // Iniciamos uma corrotina para rodar as operações de rede e banco de dados
        launch {
            CarregarCidades()

            // Chamamos initRecyclerView somente depois que CarregarCidades tiver terminado
            initRecyclerView()

        }
    }

private fun pacote(cidade: String, temperatura: String){
    Log.d("dog","Dentro do pacote; $cidade --- $temperatura")
    Helper.instance.cidade = cidade
    Helper.instance.temperatura = temperatura
    Helper.instance.estadoDoTempo = "Ensolarado"


    Helper.instance.saveData()
}

}