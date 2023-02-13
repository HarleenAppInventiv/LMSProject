package com.selflearningcoursecreationapp.utils

import android.media.AudioManager
import android.speech.tts.TextToSpeech
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.showLog
import java.util.*

class SpeechUtils : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    init {

        tts = TextToSpeech(getAppContext(), this, "com.google.android.tts")
    }

    override fun onInit(p0: Int) {
        showLog("TTS", "initialized >> $p0")

    }

    fun changeLanguage(locale: Locale) {
        tts?.language = locale
    }

    fun speakToUser(txt: String) {
        val bundle = bundleOf(TextToSpeech.Engine.KEY_PARAM_STREAM to AudioManager.STREAM_MUSIC);
        tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, bundle, null)

    }

}