package com.example.climago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterLocais(
    private val LocaisList: List<String>,
    private val TemperatureList: List<String>
): RecyclerView.Adapter<AdapterLocais.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_locais, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount()=LocaisList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val Locais = LocaisList[position]
        val Temp = TemperatureList[position]

        holder.textLocais.text = Locais
        holder.textTemperature.text = Temp
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textLocais: TextView = itemView.findViewById(R.id.RVtvNome)
        val textTemperature: TextView = itemView.findViewById(R.id.RVbtSeguir)
    }
}