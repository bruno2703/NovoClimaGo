package com.example.climago

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import java.util.Locale

data class GeocodeResponse(val results: List<Result>)
data class Result(val address_components: List<AddressComponent>)
data class AddressComponent(val long_name: String, val types: List<String>)

// A classe MapsActivity herda de AppCompatActivity e implementa a interface OnMapReadyCallback
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // Declara uma variável do tipo GoogleMap
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    // O método onCreate é chamado quando a atividade é criada
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setupMap()
        setupPlaces()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Log.e(
            "TESTE",
            "Log d funciona"
        )

        showcity()
    }


    // Método chamado quando o mapa está pronto para ser usado
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        handleLocationPermission()

    }

    private val API_KEY = "AIzaSyBdsoPSf-UXk8p5uEr_OKpMgwKxCA-W4UQ"
    private fun setupPlaces(){
        // Initialize the SDK
        Places.initialize(this, API_KEY)


    }

    // Método para configurar o mapa
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)  // Solicita que o mapa seja carregado de forma assíncrona
    }





    // Define uma constante para o código de solicitação de permissão de localização.
    private companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    // Método para tratar da permissão de localização
    private fun handleLocationPermission() {
        // Verifica se a permissão foi concedida
        if (isLocationPermissionGranted()) {
            enableUserLocation()  // Se a permissão foi concedida, habilita a localização do usuário
            showUserLocationOnMap()
        } else {
            requestLocationPermission()  // Se a permissão não foi concedida, solicita a permissão
        }
    }
    // Método para verificar se a permissão de localização foi concedida
    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    // Método para solicitar a permissão de localização
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }
    // Método para habilitar a localização do usuário no mapa
    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        map.isMyLocationEnabled = true
    }
    // Método chamado quando o usuário responde à solicitação de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()  // Se a permissão foi concedida, habilita a localização do usuário
            showUserLocationOnMap()
        } else {
            Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_LONG).show()  // Se a permissão foi negada, exibe uma mensagem
        }
    }

    @SuppressLint("MissingPermission")
    private fun showUserLocationOnMap() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Got last known location. In some rare situations, this can be null.
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun showcity() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Got last known location. In some rare situations, this can be null.
            if (location != null) {

                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))


                val geocoder = Geocoder(this, Locale.getDefault())
                geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address>) {
                        if (addresses.isNotEmpty()) {

                            Log.e("Geocoder", "Entrou no if")

                            val cityName: String = addresses[0].locality

                            Log.e("Geocoder", "Entrou no if, e deu a cidade:$cityName")

                            runOnUiThread {
                                Toast.makeText(this@MapsActivity, "$cityName, Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Log.e("Geocoder", "Entrou no else")
                            runOnUiThread {
                                Toast.makeText(this@MapsActivity, "Nao foi possivel obter o local", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        Log.e("Geocoder", "Erro ao obter a cidade: $errorMessage")
                    }
                })


            }
        }
    }






}