package com.selflearningcoursecreationapp.ui.practice_accent

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.LanguageConstant
import java.util.*

class PracticeAccentVM : BaseViewModel() {

    private var fromLanguage: String = LanguageConstant.ENGLISH
    private var toLanguage: String = ""
    private var translator: Translator? = null
    var translatedText = MutableLiveData<String>().apply {
        value = ""
    }


    fun getTranslator() = translator
    fun setFromLanguage(languageCode: String) {
        fromLanguage = languageCode
    }


    fun setToLanguage(languageCode: String) {
        toLanguage = languageCode
    }

    fun getToLanguage(): String {
        return toLanguage
    }

    fun getFromLanguage(): String {
        return fromLanguage
    }

    fun initTranslator() {
        translator?.close()

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(fromLanguage)
            .setTargetLanguage(toLanguage)
            .build()
        translator = Translation.getClient(options)
        downloadFiles()

    }

    private fun downloadFiles() {
        val conditions = DownloadConditions.Builder()
            .build()
        translator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {

                showLog("TRANSLATE", "success")
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            ?.addOnFailureListener { exception ->
                showException(exception)
                // Model couldn’t be downloaded or other internal error.
                // ...
            }
    }

    fun translateData(source: String) {
        showLog("LANGUAGE_CODE", "from >>$fromLanguage")
        showLog("LANGUAGE_CODE", "to >>$toLanguage")
        showLog("LANGUAGE_CODE", "source >>$source")

        translator?.translate(source)

            ?.addOnSuccessListener { text ->
                showLog("LANGUAGE_CODE", "translated >>$text")
                translatedText.postValue(text)
            }

            ?.addOnFailureListener { exception ->
                showException(exception)
            }
    }

    fun getLanguageCode(code: String?): String {
        return Locale(
            code ?: LanguageConstant.ENGLISH
        ).getDisplayLanguage(Locale(LanguageConstant.ENGLISH))
    }

    override fun onApiRetry(apiCode: String) {

    }


}