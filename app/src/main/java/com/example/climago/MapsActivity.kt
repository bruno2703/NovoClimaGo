package com.example.climago

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String
)


data class Response(
    @Json(name = "plus_code") val plusCode: PlusCode,
    @Json(name = "results") val results: List<Any> // You can replace Any with the actual type of your results
)

data class PlusCode(
    @Json(name = "compound_code") val compoundCode: String,
    @Json(name = "global_code") val globalCode: String
)
data class Result(val address_components: List<AddressComponent>)
data class AddressComponent(val long_name: String, val types: List<String>)

// A classe MapsActivity herda de AppCompatActivity e implementa a interface OnMapReadyCallback
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val LOCATION_PERMISSION_REQUEST = 1
    // Declara uma variável do tipo GoogleMap
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    // O método onCreate é chamado quando a atividade é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setupMap()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            getLocationAndUpdateCityName()
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            // Código para salvar as informações]

        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocationAndUpdateCityName()
                } else {
                    // Permission denied. You can add logic here to handle this case
                    Toast.makeText(this@MapsActivity, "Permissão negada", Toast.LENGTH_SHORT).show()

                }
                return
            }
            else -> {
                // Ignore other request codes
                Toast.makeText(this@MapsActivity, "Erro em conseguir a localização", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndUpdateCityName() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,   // Tempo mínimo entre as atualizações: 1000 milissegundos (1 segundo)
            10f,     // Distância mínima entre as atualizações: 10 metros
            object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    Log.d(
                        "Ryu",
                    "haduken"
                    )
                    getCityName(location.latitude, location.longitude)
                }

            }
        )
    }




    // Método chamado quando o mapa está pronto para ser usado
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        // habilita a localização do usuário
        map.isMyLocationEnabled = true

        // Obtenha a última localização conhecida do usuário.
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // obtenha a latitude e a longitude da última localização conhecida
            val latLng = LatLng(location?.latitude!!, location.longitude)

            // adicione um marcador na última localização conhecida
            map.addMarker(MarkerOptions().position(latLng).title("Você está aqui"))

            // mova a câmera para a última localização conhecida
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        }
    }


    // Método para configurar o mapa
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)  // Solicita que o mapa seja carregado de forma assíncrona
    }

    private fun getWeather(cityName: String?) {
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=207b8be31a9062d5eff256f1acb51668&units=metric")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("ken4","http://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=207b8be31a9062d5eff256f1acb51668&units=metric")
                Log.e("Error", "Network request failed", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val adapter = moshi.adapter(WeatherResponse::class.java)
                    val weatherResponse = adapter.fromJson(jsonData)

                    Log.d("ken","$weatherResponse")

                    val temperature = weatherResponse?.main?.temp
                    val weatherDescription = weatherResponse?.weather?.get(0)?.description

                    val weatherData = Pair(temperature, weatherDescription) // Pair of temperature and description

                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "Weather in ${weatherResponse?.name}: $temperature°C, $weatherDescription", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }










    private fun getCityName(latitude: Double, longitude: Double) {
        val request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=AIzaSyBdsoPSf-UXk8p5uEr_OKpMgwKxCA-W4UQ")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val adapter = moshi.adapter(Response::class.java)
                    val geocodeResponse = adapter.fromJson(jsonData)

                    val cityNamePattern = " (?<city>[^-+,]+)".toRegex()
                    val cityNameMatchResult = cityNamePattern.find(geocodeResponse?.plusCode?.compoundCode ?: "")
                    val cityName = cityNameMatchResult?.groups?.get("city")?.value
                    Log.d("aaaaab", "$cityName")



                    Log.d("ryu", "$jsonData")
                    Log.d("mama", "$jsonData")
                    getWeather(cityName)
                    runOnUiThread {

                        //findViewById<TextView>(R.id.cityName).text = cityName
                        Toast.makeText(this@MapsActivity, "Cidade: $cityName", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }





}