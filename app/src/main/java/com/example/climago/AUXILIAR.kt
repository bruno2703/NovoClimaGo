package com.example.climago

import com.squareup.moshi.Json

class AUXILIAR {

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

    data class Cidade(
        val cidade: String
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

}
