package com.selflearningcoursecreationapp.utils

import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.util.Log
import com.selflearningcoursecreationapp.di.getAppContext
import java.util.*

class SpeechUtils : TextToSpeech.OnInitListener {
    var tts: TextToSpeech? = null

    init {

        tts = TextToSpeech(getAppContext(), this)
    }

    override fun onInit(p0: Int) {
        Log.d("main", "")
    }

    fun changeLanguage(locale: Locale) {
        tts?.language = locale
    }

    fun speakToUser(txt: String) {
        tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, null)
    }

}