package com.selflearningcoursecreationapp.ui.bottom_more.settings

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSettingsBinding
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment
import com.selflearningcoursecreationapp.utils.HandleClick

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), HandleClick {


    override fun getLayoutRes(): Int {
        return R.layout.fragment_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.handleClick = this

    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_changePassword -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
                }
                R.id.tv_language -> {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(PreferencesFragment.TYPE_LANGUAGE,baseActivity.getString(R.string.change_language)))
                }
                R.id.tv_theme -> {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(PreferencesFragment.TYPE_THEME,baseActivity.getString(R.string.change_theme)))
                }
                R.id.tv_font -> {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPreferencesFragment(PreferencesFragment.TYPE_FONT,baseActivity.getString(R.string.change_font)))
                }
                R.id.tv_privacy_policy -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_privacyFragment)
                }
                R.id.tv_terms_and_conditions -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_termsFragment)
                }
                R.id.tv_help -> {
                    helpBottomDrawer()
                }

            }
        }
    }

    fun helpBottomDrawer() {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.select_help_layout)
        val faq = bottomSheetDialog.findViewById<TextView>(R.id.tv_faq)
        val contactUs = bottomSheetDialog.findViewById<TextView>(R.id.tv_contact_us)
        val chatUs = bottomSheetDialog.findViewById<TextView>(R.id.tv_chat_us)
        val imgCancel = bottomSheetDialog.findViewById<ImageView>(R.id.img_close)

        faq!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
            findNavController().navigate(R.id.action_settingsFragment_to_FAQFragment)
        }

        contactUs!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
        }

        chatUs!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
        }
        imgCancel?.setOnClickListener {
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.show()
    }


}