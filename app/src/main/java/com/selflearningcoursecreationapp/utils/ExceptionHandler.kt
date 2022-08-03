package com.selflearningcoursecreationapp.utils

import android.app.Activity
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.selflearningcoursecreationapp.extensions.showException

class ExceptionHandler : Thread.UncaughtExceptionHandler {
    private var app: Activity? = null

    private var defaultUEH: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler()

    fun initialize(app: Activity) {
        this.app = app
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        var arr = e.stackTrace
        val report = StringBuilder(
            """
    $e
    
    
    """.trimIndent()
        )
        report.append("--------- Stack trace ---------\n\n")
        for (traceElement in arr) {
            report.append("    ").append(traceElement.toString()).append("\n")
        }
        report.append("-------------------------------\n\n")

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report.append("--------- Cause ---------\n\n")
        val cause = e.cause
        if (cause != null) {
            report.append(cause.toString()).append("\n\n")
            arr = cause.stackTrace
            for (stackTraceElement in arr) {
                report.append("    ").append(stackTraceElement.toString()).append("\n")
            }
        }
        report.append("-------------------------------\n\n")
        if (app == null) return
        val map = HashMap<String, String>()
        //        HomeUtil.addDefaultParams(map);
        val sendIntent = Intent(Intent.ACTION_SEND)
        val subject = "Crash Report Skillfy"
        val body = """
            Crash Logs: 
            ${prettyJson(map)}
            
            $report
            
            """.trimIndent()
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mohit.dhiman@appinventiv.com"))
        sendIntent.putExtra(Intent.EXTRA_TEXT, body)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.type = "text/plain"
        app!!.startActivity(Intent.createChooser(sendIntent, "Send Crash To:"))
        defaultUEH.uncaughtException(t, e)
    }

    private fun prettyJson(`object`: Any?): String {
        var json = ""
        if (/*BuildConfig.DEBUG &&*/ `object` != null) {
            val parser = GsonBuilder().setPrettyPrinting().create()
            try {
                json = parser.toJson(JsonParser().parse(objecttoJson(`object`)))
            } catch (e: Exception) {
                try {
                    json = parser.toJson(JsonParser().parse(`object`.toString()))
                } catch (ignore: Exception) {
                    showException(ignore)
                }
            }
            if (json.isEmpty() || json.equals("", ignoreCase = true)) json = objecttoJson(`object`)
        }
        return json
    }
//    @SuppressLint("StaticFieldLeak")
//    companion object {
//        private lateinit var defaultUEH: Thread.UncaughtExceptionHandler
//        private var exceptionHandler: ExceptionHandler? = null
//        fun getInstance(activity: Activity): ExceptionHandler? {
//            if (exceptionHandler == null) exceptionHandler = ExceptionHandler(activity)
//            return exceptionHandler
//        }
//
//        private fun objecttoJson(`object`: Any): String {
//            return Gson().toJson(`object`).replace("\\", "")
//        }
//    }


    private fun objecttoJson(`object`: Any): String {
        return Gson().toJson(`object`).replace("\\", "")
    }
}