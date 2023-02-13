package com.selflearningcoursecreationapp.ui.bottom_more.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentSettingsBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.STATIC_PAGES_TYPE
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), HandleClick {

    private val viewModel: ProfileThumbViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }

    private fun initUi() {
        binding.handleClick = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        isDrfObserver()
        viewModel.viewUserProfile()
        initViewVisibility()


    }

    private fun isDrfObserver() {
        viewModel.isDRFEmployee.observe(viewLifecycleOwner, {
            if (it == true) {
                binding.txtDeleteAccount.gone()
            } else {
                binding.txtDeleteAccount.visible()

            }
        })
    }

    private fun initViewVisibility() {
        if (baseActivity is HomeActivity) {
//            binding.tvChangePassword.visible()
//            binding.tvLanguage.visible()
//            binding.tvCategory.visible()
//            binding.tvTheme.visible()
//            binding.tvTheme.visible()
//            binding.tvFont.visible()
//            binding.tvAboutUs.visible()
//            binding.tvPrivacyPolicy.visible()
//            binding.tvSupport.visible()
//            binding.tvTermsAndConditions.visible()
//            binding.tvHelp.visible()
            binding.tvCategory.visible()
            binding.tvTheme.visible()
            binding.tvFont.visible()
        } else {
//            binding.tvChangePassword.visible()
//            binding.tvLanguage.visible()
            binding.tvCategory.gone()
            binding.tvTheme.gone()
            binding.tvFont.gone()

        }
    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_changePassword -> {
                    findNavController().navigateTo(R.id.action_settingsFragment_to_changePasswordFragment)
                }
                R.id.tv_language -> {
                    val data = PreferenceData(
                        title = baseActivity.getString(R.string.change_language),
                        type = PREFERENCES.TYPE_LANGUAGE
                    )

                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )

                }
                R.id.tv_category -> {

                    val data = PreferenceData(
                        title = baseActivity.getString(R.string.change_categories),
                        type = PREFERENCES.TYPE_CATEGORY
                    )

                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )
                }
                R.id.tv_theme -> {
                    val data = PreferenceData(
                        title = baseActivity.getString(R.string.change_theme),
                        type = PREFERENCES.TYPE_THEME
                    )

                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )
                }
                R.id.tv_font -> {

                    val data = PreferenceData(
                        title = baseActivity.getString(R.string.change_font),
                        type = PREFERENCES.TYPE_FONT
                    )

                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )
                }
                R.id.tv_support -> {
                    findNavController().navigateTo(
                        SettingsFragmentDirections.actionSettingsFragmentToSupportFragment()
                    )
                }
                R.id.tv_privacy_policy -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.PRIVACY
                    )
                    findNavController().navigateTo(action)
                }
                R.id.tv_terms_and_conditions -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.TERMS
                    )
                    findNavController().navigateTo(action)

                }
                R.id.tv_help -> {
                    findNavController().navigateTo(R.id.action_settingsFragment_to_helpDialog)
                }
                R.id.tv_about_us -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.ABOUT_US
                    )
                    findNavController().navigateTo(action)

                }
                R.id.txt_delete_account -> {
                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.are_you_sure))
                        .description(getString(R.string.do_you_really_want_to_delete_your_account))
                        .positiveBtnText(getString(R.string.delete_acc))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.deleteAccount()
                            }
                        }.build()
                }

            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("uat_all_loggedin") //for UAT
//                   FirebaseMessaging.getInstance().unsubscribeFromTopic("production_all_loggedin") //for Production

                viewModel.getUserData().apply {
                    viewModel.userProfile?.roles?.forEach {
                        Log.d("varun", "onReceive: ${it.topicName}")
                        FirebaseMessaging.getInstance()
                            .unsubscribeFromTopic(it.topicName.toString())
                    }
                }
                baseActivity.goToInitialActivity()
            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                hideLoading()
                when (exception.statusCode) {
                    HTTPCode.COURSE_HAS_ENROLLED_USERS -> {
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.are_you_sure))
                            .description(exception.message ?: "")
                            .positiveBtnText(getString(R.string.delete_acc))
                            .icon(R.drawable.ic_fogot_password)
                            .getCallback {
                                if (it) {
                                    viewModel.deleteAccount(true)
                                }
                            }.build()
                    }

                    HTTPCode.CREATOR_HAS_PENDING_BALANCE -> {
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.are_you_sure))
                            .description(exception.message ?: "")
                            .positiveBtnText(getString(R.string.delete_acc))
                            .icon(R.drawable.ic_fogot_password)
                            .getCallback {
                                if (it) {
                                    viewModel.deleteAccount(true, true)
                                }
                            }.build()
                    }

                    else -> {
                        super.onException(isNetworkAvailable, exception, apiCode)

                    }
                }
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }
    }

}