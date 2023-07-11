package com.example.climago

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var marker: Marker? = null
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val LOCATION_PERMISSION_REQUEST = 1

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var NomeCidade = AUXILIAR.Cidade("Nenhuma")

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
            SalvarCidade(NomeCidade)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocationAndUpdateCityName()
                } else {
                    Toast.makeText(this@MapsActivity, "Permissão negada", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                Toast.makeText(this@MapsActivity, "Erro em conseguir a localização", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndUpdateCityName() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            10f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.d("Ryu", "haduken")

                    // Obter nome da cidade e tratar diretamente
                    getCityName(location.latitude, location.longitude) { cityName ->
                        runOnUiThread {
                            // Realizar as operações necessárias com o nome da cidade aqui
                            // Por exemplo, exibir um toast com o nome da cidade
                            Toast.makeText(this@MapsActivity, "Cidade: $cityName", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        )
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val latLng = LatLng(location?.latitude!!, location.longitude)
            map.addMarker(MarkerOptions().position(latLng).title("Você está aqui"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        // Adicionar listener de clique no mapa
        map.setOnMapClickListener { latLng ->
            // Remover marcador anterior, se existir
            marker?.remove()

            // Adicionar novo marcador
            marker = map.addMarker(MarkerOptions().position(latLng).title("Clique para salvar"))

            // Obter nome da cidade e exibir toast
            getCityName(latLng.latitude, latLng.longitude) { cityName ->
                runOnUiThread {
                    marker?.title = cityName // Definir o título do marcador como o nome da cidade

                    // Salvar a cidade
                    SalvarCidade(AUXILIAR.Cidade(cityName ?: ""))

                    Toast.makeText(this@MapsActivity, "Cidade: $cityName", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }






    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getWeather(cityName: String?) {
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=207b8be31a9062d5eff256f1acb51668&units=metric")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("Error", "Network request failed", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val adapter = moshi.adapter(AUXILIAR.WeatherResponse::class.java)
                    val weatherResponse = adapter.fromJson(jsonData)

                    Log.d("ken", "$weatherResponse")

                    val temperature = weatherResponse?.main?.temp
                    val weatherDescription = weatherResponse?.weather?.get(0)?.description

                    val weatherData = Pair(temperature, weatherDescription)

                    runOnUiThread {
                        Toast.makeText(
                            this@MapsActivity,
                            "Tempo em ${weatherResponse?.name}: $temperature°C, $weatherDescription",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun getCityName(latitude: Double, longitude: Double, callback: (String) -> Unit) {
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
                    val adapter = moshi.adapter(AUXILIAR.Response::class.java)
                    val geocodeResponse = adapter.fromJson(jsonData)

                    val cityNamePattern = " (?<city>[^-+,]+)".toRegex()
                    val cityNameMatchResult =
                        cityNamePattern.find(geocodeResponse?.plusCode?.compoundCode ?: "")
                    val cityName = cityNameMatchResult?.groups?.get("city")?.value

                    getWeather(cityName)

                    NomeCidade = AUXILIAR.Cidade(cityName ?: "")
                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "Cidade: $cityName", Toast.LENGTH_SHORT).show()
                    }

                    // Chamada do callback com o nome da cidade
                    callback(cityName ?: "")
                }
            }
        })
    }


    private fun SalvarCidade(cidade: AUXILIAR.Cidade) {
        val db = FirebaseFirestore.getInstance()
        val nomeCidade = marker?.title ?: return // Verificar se há um marcador

        val cidadeMarcador = AUXILIAR.Cidade(nomeCidade)
        db.collection("cidades")
            .whereEqualTo("cidade", cidadeMarcador.cidade)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    db.collection("cidades")
                        .add(cidadeMarcador)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            Toast.makeText(this, "Local Salvo: ${cidadeMarcador.cidade}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            Toast.makeText(this, "Falha em salvar: ${cidadeMarcador.cidade}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Cidade já existe: ${cidadeMarcador.cidade}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro na verificação da cidade", e)
                Toast.makeText(this, "Erro na verificação da cidade: ${cidadeMarcador.cidade}", Toast.LENGTH_SHORT).show()
            }
    }

}
