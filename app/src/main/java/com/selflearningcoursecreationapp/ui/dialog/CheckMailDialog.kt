package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.DialogCheckYourMailBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.setSpanText
import com.selflearningcoursecreationapp.ui.authentication.forgotPass.ForgotPassViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckMailDialog : BaseDialog<DialogCheckYourMailBinding>() {
    val viewModel: ForgotPassViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.dialog_check_your_mail
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.tvDesc.setSpanText(baseActivity.getString(R.string.we_have_sent_password_recovery_instructions_to_your_email))
        binding.btnConfirmLater.setSpanText(baseActivity.getString(R.string.okay))
        arguments?.let {
            if (it.containsKey("title")) {
                binding.tvTitle.setSpanText(it.getString("title"))
            }
            if (it.containsKey("description")) {
                binding.tvDesc.setSpanText(it.getString("description"))
                binding.btnConfirmLater.setSpanText(baseActivity.getString(R.string.okay))
            }

        }

        val msg = SpanUtils.with(
            baseActivity,
            baseActivity.getString(R.string.not_received_instructions_resend_again)
        ).startPos(33).isBold().themeColor().getCallback {
            if (arguments?.containsKey("email") == true) {
                viewModel.emailPhone.value = arguments?.getString("email") ?: ""
                viewModel.selectedCountryCodeWithPlus = arguments?.getString("countryCode") ?: ""
                viewModel.forgotApi(
                    arguments?.getBoolean("isPhone") ?: false
                )
            }

        }.getSpanString()
        binding.tvResend.setSpanString(msg)
        binding.btnConfirmLater.setOnClickListener {
            onDialogClick(Constant.CLICK_VIEW)
            dismiss()
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                showToastShort(baseActivity.getString(R.string.otp_sent_successfully))
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}