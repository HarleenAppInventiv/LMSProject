package com.selflearningcoursecreationapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentSplashBinding
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        lifecycleScope.launch {
            delay(2000)
            if (PreferenceDataStore.getBoolean(Constants.WALKTHROUGH_DONE) ?: false) {
                baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))
                baseActivity.finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_sliderFragment)
            }

        }
    }


    override fun getLayoutRes() = R.layout.fragment_splash

}