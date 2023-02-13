package com.selflearningcoursecreationapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentSplashBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val viewModel: SplashVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

//        UploadDocOptionsDialog().show(childFragmentManager,"")


        lifecycleScope.launch {
            delay(2000)
            if (PreferenceDataStore.getString(Constants.USER_TOKEN).isNullOrEmpty()) {
                if (PreferenceDataStore.getBoolean(Constants.WALKTHROUGH_DONE) == true) {
                    baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))

                    baseActivity.finish()
                } else {
                    findNavController().navigateTo(R.id.action_splashFragment_to_sliderFragment)
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

            if (user?.getPreferenceValue() != 4) {

                lifecycleScope.launch {

                    viewModel.saveUser(null)
                    viewModel.saveUserToken("")
                }
                baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))
                baseActivity.finish()


            } else {
                if (user.currentMode == 1) {
                    baseActivity.goToHomeActivity()
                } else {
                    baseActivity.goToModeratorActivity()
                }
            }

        }


    }


    override fun getLayoutRes() = R.layout.fragment_splash
    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {
        baseActivity.goToHomeActivity()

    }
}