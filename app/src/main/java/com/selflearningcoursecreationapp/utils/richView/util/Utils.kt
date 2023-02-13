package com.selflearningcoursecreationapp.utils.richView.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Utils {
    internal fun dpToPixel(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    internal fun getDimenFromString(value: String): Float {
        val end = if (value[value.length - 3] == 'd') 3 else 2
        return value.substring(0, value.length - end).toFloat()
    }

    fun drawableFromUrl(url: String?): Drawable? {
        var bitmap: Drawable? = null
        val thread = Thread {
            try {


                val x: Bitmap
                val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val input: InputStream = connection.inputStream
                x = BitmapFactory.decodeStream(input)
                bitmap = BitmapDrawable(Resources.getSystem(), x)
                //Your code goes here
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()


        return bitmap
    }

    internal fun getColorFromString(value: String): Int {
        var color = Color.TRANSPARENT
        if (value.length == 7 || value.length == 9) {
            color = Color.parseColor(value)
        } else if (value.length == 4) {
            color = Color.parseColor(
                "#"
                        + value[1]
                        + value[1]
                        + value[2]
                        + value[2]
                        + value[3]
                        + value[3]
            )
        } else if (value.length == 2) {
            color = Color.parseColor(
                "#"
                        + value[1]
                        + value[1]
                        + value[1]
                        + value[1]
                        + value[1]
                        + value[1]
                        + value[1]
                        + value[1]
            )
        }
        return color
    }
}