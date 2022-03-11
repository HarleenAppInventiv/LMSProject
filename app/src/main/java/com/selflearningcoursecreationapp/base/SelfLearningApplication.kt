package com.selflearningcoursecreationapp.base

import android.app.Application
import android.content.Context
import android.text.style.LocaleSpan
import androidx.lifecycle.LifecycleObserver
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.di.networkingModule
import com.selflearningcoursecreationapp.di.repoModule
import com.selflearningcoursecreationapp.di.viewModelModule
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.FONT_CONSTANT
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.*

class SelfLearningApplication : Application(), LifecycleObserver {

    init {
        instance = this

    }

    companion object {
        private var instance: SelfLearningApplication? = null
        var themeFile: String? = null
        var fontId: Int = FONT_CONSTANT.IBM
        private var localeSpan: LocaleSpan? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            updatedThemeFile()
        }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@SelfLearningApplication)
            modules(listOf(networkingModule, viewModelModule, repoModule))
        }


    }

    suspend fun updatedThemeFile() {

        themeFile = PreferenceDataStore.getString(Constants.THEME_FILE)
        fontId = PreferenceDataStore.getInt(Constants.FONT_THEME)?:FONT_CONSTANT.IBM

    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}