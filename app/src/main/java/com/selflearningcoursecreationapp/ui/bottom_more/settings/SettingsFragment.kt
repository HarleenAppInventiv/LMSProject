package com.selflearningcoursecreationapp.ui.bottom_more.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSettingsBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.STATIC_PAGES_TYPE

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), HandleClick {


    override fun getLayoutRes(): Int {
        return R.layout.fragment_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        binding.handleClick = this

        initViewVisibility()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_changePassword -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
                }
                R.id.tv_language -> {
                    findNavController().navigate(
                        SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_LANGUAGE,
                            baseActivity.getString(R.string.change_language)
                        )
                    )
                }
                R.id.tv_category -> {
                    findNavController().navigate(
                        SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_CATEGORY,
                            baseActivity.getString(R.string.change_categories)
                        )
                    )
                }
                R.id.tv_theme -> {
                    findNavController().navigate(
                        SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_THEME,
                            baseActivity.getString(R.string.change_theme)
                        )
                    )
                }
                R.id.tv_font -> {
                    findNavController().navigate(
                        SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_FONT,
                            baseActivity.getString(R.string.change_font)
                        )
                    )
                }
                R.id.tv_support -> {
//                    findNavController().navigate(
//                        SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(
//                            PREFERENCES.TYPE_ALL,
//                            baseActivity.getString(R.string.your_preferences)
//                        )
//                    )
                }
                R.id.tv_privacy_policy -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.PRIVACY
//                        ,
//                        ApiEndPoints.LINK_PRIVACY_POL
                    )
                    findNavController().navigate(action)
//                    baseActivity.getString(R.string.privacy_policy),
                }
                R.id.tv_terms_and_conditions -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.TERMS
//                        ApiEndPoints.LINK_TERM_COND
                    )
                    findNavController().navigate(action)
//                    baseActivity.getString(R.string.terms_amp_conditions),

                }
                R.id.tv_help -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_helpDialog)
                }
                R.id.tv_about_us -> {
                    val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(
                        STATIC_PAGES_TYPE.ABOUT_US
//                        ApiEndPoints.LINK_ABOUT_US
                    )
                    findNavController().navigate(action)
//                    baseActivity.getString(R.string.about_us),

                }

            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }


}