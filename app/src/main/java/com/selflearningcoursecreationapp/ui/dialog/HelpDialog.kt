package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.SelectHelpLayoutBinding
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
            dismiss()
        }
        binding.tvChatUs.setOnClickListener {
            dismiss()
        }
        binding.tvFaq.setOnClickListener {
//            findNavController().navigate(R.id.action_helpDialog_to_FAQFragment)
            val action =
                HelpDialogDirections.actionHelpDialogToPrivacyFragment(
                    STATIC_PAGES_TYPE.HELP
//                    getString(R.string.question_we_ve_got_instant_answers),
//                    ApiEndPoints.LINK_FAQ
                )
            findNavController().navigate(action)
            dismiss()
        }
    }


}