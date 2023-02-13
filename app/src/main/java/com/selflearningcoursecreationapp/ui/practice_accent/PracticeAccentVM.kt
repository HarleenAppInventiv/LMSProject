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

    //    private var fromLanguage: String = LanguageConstant.ENGLISH
    private var fromLanguage: String = ""
    private var toLanguage: String = LanguageConstant.ENGLISH
    private var toLanguageId: Int = 0
    private var translator: Translator? = null
    private var sourceString = ""
    var translatedText = MutableLiveData<String>().apply {
        value = ""
    }


    fun getTranslator() = translator
    fun setFromLanguage(languageCode: String) {
        fromLanguage = languageCode
    }

    fun setFromLanguageId(languageCode: Int) {
        toLanguageId = languageCode
    }

//    fun setToLanguage(languageCode: String) {
//        toLanguage = languageCode
//    }
//
//    fun setToLanguageId(languageCode: Int) {
//        toLanguageId = languageCode
//    }

    //    fun getToLanguage(): String {
//        return toLanguage
//    }
//
    fun getFromLanguageId(): Int {
        return toLanguageId
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
//        if (sourceString.isNotEmpty()) {
        translatedText.postValue("Translating...")
//        }
        val conditions = DownloadConditions.Builder()
            .build()
        translator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {

                translateData(sourceString)
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
        sourceString = source
        translatedText.postValue("Translating...")

        showLog("LANGUAGE_CODE", "from >>$fromLanguage")
        showLog("LANGUAGE_CODE", "to >>$toLanguage")
        showLog("LANGUAGE_CODE", "source >>$source")
        if (source.isNotEmpty()) {
            translator?.translate(source)

                ?.addOnSuccessListener { text ->
                    showLog("LANGUAGE_CODE", "translated >>$text")
                    translatedText.postValue(text)
                }

                ?.addOnFailureListener { exception ->
                    showException(exception)
                }
        } else {
            translatedText.postValue("")

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