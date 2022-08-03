package com.selflearningcoursecreationapp.ui.splash

import android.os.Bundle
import android.util.Log
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivitySplashBinding
import com.selflearningcoursecreationapp.extensions.setTransparentStatusBar

class SplashActivity : BaseActivity(), MessageListener {
//    private val serverUrl = "ws://sumant-001-site1.itempurl.com/weatherforecast/send"

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparentStatusBar()
        setAppTheme()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        thread {
//            kotlin.run {
//                WebSocketManager.init(serverUrl, this)
//
//                WebSocketManager.connect()
//
//                WebSocketManager.sendMessage("hi shuabdsad")
//
//            }
//        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onConnectSuccess() {
        Log.e("websocket", "onConnectSuccess")

    }

    override fun onConnectFailed() {
        Log.e("websocket", "onConnectFailed")

    }

    override fun onClose() {
        Log.e("websocket", "onClose")

    }

    override fun onMessage(text: String?) {
        Log.e("websocket", "$text")


    }
}