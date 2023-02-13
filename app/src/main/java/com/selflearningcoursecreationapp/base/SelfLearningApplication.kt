package com.selflearningcoursecreationapp.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.LifecycleObserver
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.db.AppDb
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

    private val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

    companion object {
        var instance: SelfLearningApplication? = null
        public var mDatabase: AppDb? = null
        var isViOn: Boolean? = false
        var themeFile: String? = null
        var fontId: Int = FontConstant.IBM
        var languageCode: String = LanguageConstant.ENGLISH
        var languageCodeId: Int = 1

        var token: String? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        mDatabase = AppDb.getAppDatabase(this)
        GlobalScope.launch {
            updatedThemeFile()
            updateToken()
        }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@SelfLearningApplication)
            modules(listOf(networkingModule, viewModelModule, repoModule))
        }

        createNotificationChannels()

    }


    public fun getAppDB(): AppDb {
        return mDatabase!!;
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.app_describe)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    suspend fun updatedThemeFile() {

        themeFile = PreferenceDataStore.getString(Constants.THEME_FILE)
        isViOn = PreferenceDataStore.getBoolean(Constants.VI_MODE)
        fontId = PreferenceDataStore.getInt(Constants.FONT_THEME) ?: FontConstant.IBM
        languageCode =
            PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LanguageConstant.ENGLISH
        languageCodeId =
            PreferenceDataStore.getInt(Constants.LANGUAGE_ID) ?: 1

    }

    suspend fun updateToken() {
        token = PreferenceDataStore.getString(Constants.USER_TOKEN)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

}