package com.example.climago.FragmentsNavBar

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climago.AdapterLocais
import com.example.climago.MapsActivity
import com.example.climago.R
import com.example.climago.databinding.FragmentLocaisBinding
import com.google.firebase.firestore.FirebaseFirestore

class LocaisFragment : Fragment(R.layout.fragment_locais) {

    private lateinit var binding: FragmentLocaisBinding

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
                    Log.d("minha", "${city.id} => ${city.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("minha", "Error getting documents.", exception)
            }
    }

    override fun onResume() {
        super.onResume()
        CarregarCidades()
    }
}