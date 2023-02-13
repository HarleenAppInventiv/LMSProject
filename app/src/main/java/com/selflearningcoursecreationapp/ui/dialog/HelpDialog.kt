package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.SelectHelpLayoutBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.utils.STATIC_PAGES_TYPE

class HelpDialog : BaseBottomSheetDialog<SelectHelpLayoutBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.select_help_layout
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.tvContactUs.setOnClickListener {
            val action =
                HelpDialogDirections.actionHelpDialogToPrivacyFragment(
                    STATIC_PAGES_TYPE.CONTACT_US
                )
            findNavController().navigateTo(action)
            dismiss()
        }
        binding.tvChatUs.setOnClickListener {
            showDefaultDialog(baseActivity.getString(R.string.coming_soon))
            dismiss()
        }
        binding.tvFaq.setOnClickListener {
            val action =
                HelpDialogDirections.actionHelpDialogToPrivacyFragment(
                    STATIC_PAGES_TYPE.HELP
                )
            findNavController().navigateTo(action)
            dismiss()
        }
    }


}