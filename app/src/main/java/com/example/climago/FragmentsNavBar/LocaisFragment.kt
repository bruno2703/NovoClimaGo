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
import com.example.climago.WeatherResponse
import com.example.climago.databinding.FragmentLocaisBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder

class LocaisFragment : Fragment(R.layout.fragment_locais) {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private lateinit var binding: FragmentLocaisBinding

    val cityNames = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocaisBinding.bind(view)

        initRecyclerView()

        binding.btMaps.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initRecyclerView(){
        binding.RCListaSeguidores.layoutManager = LinearLayoutManager(requireContext())
        binding.RCListaSeguidores.setHasFixedSize(true)
        binding.RCListaSeguidores.adapter = AdapterLocais(getList(),getTemp())
    }

    private fun getList() = listOf(
        "Fortaleza", "Juazeiro do Norte", "Caucaia", "Maracanaú", "Sobral"
    )

    private fun getTemp() = listOf(
        "22°C", "28°C", "15°C", "33°C", "19°C"
    )


    //Firebase

    val db = FirebaseFirestore.getInstance()


    private fun CarregarCidades(){


        db.collection("cidades")
            .get()
            .addOnSuccessListener { result ->
                for (city in result) {
                    city.data["cidade"]?.let {
                        cityNames.add(it.toString())
                    }
                }

                // Agora a variável cityNames contém todos os nomes das cidades
                Log.d("minha", cityNames.toString())
            }
            .addOnFailureListener { exception ->
                Log.w("minha", "Error getting documents.", exception)
            }
    }



    //Log.d("minha", "${city.id} => ${city.data}")

    override fun onResume() {
        super.onResume()
        CarregarCidades()
    }

    //API

    private fun getWeather(cityName: String?) {
        val encodedCityName = URLEncoder.encode(cityName, "utf-8")
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$encodedCityName&appid=207b8be31a9062d5eff256f1acb51668&units=metric")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("Error", "Network request failed", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val adapter = moshi.adapter(WeatherResponse::class.java)
                    val weatherResponse = adapter.fromJson(jsonData)

                    Log.d("Weather", "$weatherResponse")

                    val temperature = weatherResponse?.main?.temp
                    val weatherDescription = weatherResponse?.weather?.get(0)?.description

                    val weatherData = Pair(temperature, weatherDescription) // Pair of temperature and description

                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(activity, "Tempo em ${weatherResponse?.name}: $temperature°C, $weatherDescription", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



}