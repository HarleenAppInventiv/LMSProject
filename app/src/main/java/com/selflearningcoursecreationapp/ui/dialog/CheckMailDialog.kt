package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.DialogCheckYourMailBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.setSpanText
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class CheckMailDialog : BaseDialog<DialogCheckYourMailBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.dialog_check_your_mail
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        binding.tvDesc.setSpanText(baseActivity.getString(R.string.we_have_sent_password_recovery_instructions_to_your_email))
        binding.btnConfirmLater.setSpanText(baseActivity.getString(R.string.i_ll_confirm_later))
        arguments?.let {
            if (it.containsKey("title")) {
                binding.tvTitle.setSpanText(it.getString("title"))
            }
            if (it.containsKey("description")) {
                binding.tvDesc.setSpanText(it.getString("description"))
                binding.btnConfirmLater.setSpanText( baseActivity.getString(R.string.ok))
            }
        }


        binding.tvResend.setSpanString(
            baseActivity.getString(R.string.not_received_email_resend_again),
            startPos = 53,
            isBold = true,
            color = ThemeUtils.getAppColor(baseActivity),
            onClick = {
                showToastShort("resent")
            })
        binding.btnConfirmLater.setOnClickListener {
            onDialogClick()
            dismiss()
        }
    }


}