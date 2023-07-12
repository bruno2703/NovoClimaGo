package com.example.climago

import android.graphics.drawable.Drawable
import android.media.Image
import android.content.Context


class FunctionImage {


    fun ImageSelect(context: Context, description:String): Drawable?{


        val resourceId = context.resources.getIdentifier(description, "drawable", context.packageName)
        return context.resources.getDrawable(resourceId, null)

    }

}
