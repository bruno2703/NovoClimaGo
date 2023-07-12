package com.example.climago

import android.content.Context
import android.graphics.drawable.Drawable

class FunctionImage {

    fun ImageSelect(context: Context, description: String): Drawable? {
        val chosenImage: String = when (description) {
            "Thunderstorm", "Drizzle", "Rain", "Snow" -> "rain_icon"
            "Clear" -> "sun_icon"
            else -> "sun_clouds_icon"
        }

        val resourceId = context.resources.getIdentifier(chosenImage, "drawable", context.packageName)
        return context.resources.getDrawable(resourceId, null)
    }
}

