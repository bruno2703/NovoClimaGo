package com.example.climago

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.climago.FragmentsNavBar.LocaisFragment

class AdapterLocais(
    private val context: Context,
    private val LocaisList: List<String>,
    private val TemperatureList: List<String>,
    private val ImageList: List<String>
) : RecyclerView.Adapter<AdapterLocais.MyViewHolder>() {
    private val DrawableList = mutableListOf<Drawable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_lista_locais, parent, false)

        for (element in ImageList) {
            val string: String = element.toString()
            val functionImage = FunctionImage()

            functionImage.ImageSelect(context, string)?.let { DrawableList.add(it) }
        }

        return MyViewHolder(itemView, LocaisList, TemperatureList,ImageList)
    }

    override fun getItemCount() = LocaisList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val Locais = LocaisList[position]
        val Temp = TemperatureList[position]

        holder.textLocais.text = Locais
        holder.textTemperature.text = Temp
        holder.ImagTempo.setImageDrawable(DrawableList[position])
    }

    class MyViewHolder(itemView: View, private val LocaisList: List<String>, private val TemperatureList: List<String>, private val ImageList: List<String>) : RecyclerView.ViewHolder(itemView) {
        val textLocais: TextView = itemView.findViewById(R.id.RVtvNameLocal)
        val textTemperature: TextView = itemView.findViewById(R.id.RVbtTemperatura)
        val ImagTempo: ImageView = itemView.findViewById(R.id.ivIconeTempo)

        init {
            itemView.setOnClickListener {
                val position = getAbsoluteAdapterPosition()
                val local = LocaisList[position]
                val temperatura = TemperatureList[position]
                val tempo = ImageList[position]

                Log.d("mesa","Local: $local, Temperatura: $temperatura, Tempo: $tempo")
                Helper.instance.cidade = local
                Helper.instance.temperatura = temperatura
                Helper.instance.estadoDoTempo = tempo


                Helper.instance.saveData()

            }
        }
    }

}