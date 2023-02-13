package com.hd.videoEditor.customViews.trimmer

import android.content.Context
import android.util.DisplayMetrics


object UnitConverter {
    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun convertSecondsToTime(seconds: Long): String? {
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        var second = 0
        if (seconds <= 0) {
            return "00:00"
        } else {
            minute = seconds.toInt() / 60
            if (minute < 60) {
                second = seconds.toInt() % 60
                timeStr = "00:" + unitFormat(minute).toString() + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99) return "99:59:59"
                minute = minute % 60
                second = ((seconds - hour * 3600 - minute * 60) as Long).toInt()
                timeStr =
                    unitFormat(hour).toString() + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }


    private fun unitFormat(i: Int): String? {
        var retStr: String? = null
        retStr = if (i >= 0 && i < 10) {
            "0" + Integer.toString(i)
        } else {
            "" + i
        }
        return retStr
    }
}