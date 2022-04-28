package com.selflearningcoursecreationapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentSplashBinding
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val viewModel: SplashVM by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        lifecycleScope.launch {
            delay(2000)
            if (PreferenceDataStore.getString(Constants.USER_TOKEN).isNullOrEmpty()) {
                if (PreferenceDataStore.getBoolean(Constants.WALKTHROUGH_DONE) ?: false) {
                    baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))

                    baseActivity.finish()
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_sliderFragment)
                }
            } else {
                viewModel.viewProfile()

            }


        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        if (apiCode == ApiEndPoints.API_VIEW_PROFILE) {
            val user = (value as BaseResponse<UserProfile>).resource

            if (!(user?.languageUpdated ?: false) || !(user?.fontUpdated
                    ?: false) || !(user?.themeUpdated ?: false) || !(user?.categoryUpdated ?: false)
            ) {
                var level = when {
                    user?.languageUpdated ?: false -> 4
                    user?.fontUpdated ?: false -> 3
                    user?.themeUpdated ?: false -> 2
                    user?.categoryUpdated ?: false -> 1
                    else -> 0
                }
                if (level != 4) {
                    lifecycleScope.launch {

                        viewModel.saveUser(null)
                        viewModel.saveUserToken("")
                    }
                    baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))
                    baseActivity.finish()
                } else {
                    activity?.startActivity(Intent(requireActivity(), HomeActivity::class.java))
                    activity?.finish()
                }

            } else {
                activity?.startActivity(Intent(requireActivity(), HomeActivity::class.java))
                activity?.finish()
            }

        }


    }


    override fun getLayoutRes() = R.layout.fragment_splash

}