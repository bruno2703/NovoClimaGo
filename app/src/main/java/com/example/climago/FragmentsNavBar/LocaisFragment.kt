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
import com.example.climago.BuildConfig

class LocaisFragment : Fragment(R.layout.fragment_locais), CoroutineScope by MainScope() {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private lateinit var binding: FragmentLocaisBinding

    val cityNames = mutableListOf<String>()
    val TemperatureValues = mutableListOf<String>()
    val estadoTempo = mutableListOf<String>()

    val cityIds = mutableListOf<String>()

    private val tempoApiKey = BuildConfig.API_KEY_TEMPO

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
        Log.d("dog","Verificando; ${cityNames.last()} --- ${TemperatureValues.last()} --- ${estadoTempo.last()}")
        pacote(cityNames.last(),TemperatureValues.last(),estadoTempo.last())
        binding.RCListaLocais.layoutManager = LinearLayoutManager(requireContext())
        binding.RCListaLocais.setHasFixedSize(true)
        binding.RCListaLocais.adapter = AdapterLocais(requireContext(),getList(), getTemp(),getMain(), getIds())
    }

    private fun getList() = cityNames

    private fun getTemp() = TemperatureValues

    private fun getMain() = estadoTempo

    private fun getIds() = cityIds


    //Firebase

    val db = FirebaseFirestore.getInstance()


    private suspend fun CarregarCidades() {

        val result = db.collection("cidades").get().await()

        for (city in result) {
            city.data["cidade"]?.let {
                cityNames.add(it.toString())
                cityIds.add(city.id)

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
                .url("https://api.openweathermap.org/data/2.5/weather?q=$encodedCityName&appid=$tempoApiKey&units=metric")
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
            val weatherMain = weatherResponse?.weather?.get(0)?.main
            val weatherData = Pair(temperature, weatherMain) // Pair of temperature and description

            withContext(Dispatchers.Main) {
                TemperatureValues.add(temperature.toString())
                estadoTempo.add(weatherMain.toString())


                Log.d("Lista temperatura", "Nome da cidade $cityName --- Temperatura $temperature --- Mutable List; $TemperatureValues")
            }
        }
    }


    //Limpa as listas
    private fun ClearLists(){
        cityNames.clear()
        TemperatureValues.clear()
        estadoTempo.clear()

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

            // We add a delay here to make sure that all getWeather calls have completed.
            while (isActive && TemperatureValues.size < cityNames.size) {
                delay(1000)
            }

            // Chamamos initRecyclerView somente depois que CarregarCidades e getWeather tiverem terminado
            withContext(Dispatchers.Main) {
                initRecyclerView()
            }
        }
    }


private fun pacote(cidade: String, temperatura: String, tempo: String){
    Log.d("dog","Dentro do pacote; $cidade --- $temperatura --- $tempo")
    Helper.instance.cidade = cidade
    Helper.instance.temperatura = temperatura
    Helper.instance.estadoDoTempo = tempo


    Helper.instance.saveData()
}

}