package com.selflearningcoursecreationapp.ui.splash

import android.os.Bundle
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivitySplashBinding
import com.selflearningcoursecreationapp.extensions.setTransparentStatusBar

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparentStatusBar()
        setAppTheme()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onBackPressed() {
        finishAffinity()
    }
}