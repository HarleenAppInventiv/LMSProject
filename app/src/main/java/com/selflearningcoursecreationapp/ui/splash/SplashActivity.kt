package com.selflearningcoursecreationapp.ui.splash

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivitySplashBinding
import com.selflearningcoursecreationapp.extensions.setTransparentStatusBar

class SplashActivity : BaseActivity() {
    private var navController: NavController? = null

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setTransparentStatusBar()
        setAppTheme()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        navController = navHostFrag?.navController


    }

    override fun onBackPressed() {
        finishAffinity()
    }


}