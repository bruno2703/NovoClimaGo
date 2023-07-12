package com.example.climago.FragmentsNavBar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.climago.FunctionImage
import com.example.climago.Helper
import com.example.climago.R
import com.google.rpc.Help


class TelaInicialFragment : Fragment(R.layout.fragment_tela_inicial) {

    private lateinit var NomeCidade: TextView
    private lateinit var Temperatura: TextView
    private lateinit var ImagemTempo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tela_inicial, container, false)
        NomeCidade = view.findViewById(R.id.tvCidade)
        Temperatura = view.findViewById(R.id.tvTemperatura)
        ImagemTempo = view.findViewById(R.id.ivSol)

        return view
    }

    override fun onResume() {
        super.onResume()
        pegaInfo()
    }

    //Recebe os dados
    private fun pegaInfo() {
        val sharedPref = activity?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val cidade = sharedPref?.getString("cidade", "default")
        val temperatura = sharedPref?.getString("temperatura", "default")
        val estadoDoTempo = sharedPref?.getString("estadoDoTempo", "default")

        NomeCidade.text = cidade
        Temperatura.text = temperatura + "°C"

        //converte para string,chama função, pega context
        val string: String = estadoDoTempo.orEmpty()
        val functionImage = FunctionImage()
        val context = requireContext()

        val image = functionImage.ImageSelect(context, string)

        ImagemTempo.setImageDrawable(image)


        //Log.d("dog", "Isso foi oq chegou $cidade---$temperatura---$estadoDoTempo")
    }
}