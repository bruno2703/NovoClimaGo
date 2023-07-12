package com.example.climago.FragmentsNavBar

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.climago.Helper
import com.example.climago.R
import com.google.rpc.Help

class TelaInicialFragment : Fragment(R.layout.fragment_tela_inicial) {



    override fun onResume() {
        super.onResume()
        pegaInfo()
    }

    private fun pegaInfo() {
        val sharedPref = activity?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val cidade = sharedPref?.getString("cidade", "default")
        val temperatura = sharedPref?.getString("temperatura", "default")
        val estadoDoTempo = sharedPref?.getString("estadoDoTempo", "default")

        Log.d("dog", "Isso foi oq chegou $cidade---$temperatura---$estadoDoTempo")
    }

}