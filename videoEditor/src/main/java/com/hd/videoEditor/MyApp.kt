package com.hd.videoEditor

import android.app.Application
import android.content.Context
import android.os.Handler
import com.hd.videoEditor.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp : Application() {

    init {
        instance = this

    }

    companion object {

        private var instance: MyApp? = null
        private var applicationHandler: Handler? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun appHandler(): Handler? {
            return applicationHandler
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApp)
            modules(listOf(viewModelModule))
        }


    }


    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}