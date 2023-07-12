package com.example.climago

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Helper : Application() {

    companion object {
        lateinit var instance: Helper
            private set
    }

    private val PREFS_NAME = "myPrefs"

    var cidade: String? = null
    var temperatura: String? = null
    var estadoDoTempo: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadData()
    }

    fun saveData() {
        with (sharedPreferences.edit()) {
            putString("cidade", cidade)
            putString("temperatura", temperatura)
            putString("estadoDoTempo", estadoDoTempo)
            apply()
        }
    }

    private fun loadData() {
        cidade = sharedPreferences.getString("cidade", null)
        temperatura = sharedPreferences.getString("temperatura", null)
        estadoDoTempo = sharedPreferences.getString("estadoDoTempo", null)
    }
}
