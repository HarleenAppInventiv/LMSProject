package com.selflearningcoursecreationapp.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.di.networkingModule
import com.selflearningcoursecreationapp.di.repoModule
import com.selflearningcoursecreationapp.di.viewModelModule
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.FontConstant
import com.selflearningcoursecreationapp.utils.LanguageConstant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SelfLearningApplication : Application(), LifecycleObserver {

    init {
        instance = this

    }

    companion object {
        private var instance: SelfLearningApplication? = null
        var isViOn: Boolean? = false
        var themeFile: String? = null
        var fontId: Int = FontConstant.IBM
        var languageCode: String = LanguageConstant.ENGLISH

        var token: String? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            updatedThemeFile()
            updateToken()
        }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@SelfLearningApplication)
            modules(listOf(networkingModule, viewModelModule, repoModule))
        }


    }

    suspend fun updatedThemeFile() {

        themeFile = PreferenceDataStore.getString(Constants.THEME_FILE)
        isViOn = PreferenceDataStore.getBoolean(Constants.VI_MODE)
        fontId = PreferenceDataStore.getInt(Constants.FONT_THEME) ?: FontConstant.IBM
        languageCode =
            PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LanguageConstant.ENGLISH

    }

    suspend fun updateToken() {
        token = PreferenceDataStore.getString(Constants.USER_TOKEN)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}