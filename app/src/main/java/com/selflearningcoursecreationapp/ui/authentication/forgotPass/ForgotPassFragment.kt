package com.selflearningcoursecreationapp.ui.authentication.forgotPass

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentForgotPassBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.dialog.CheckMailDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgotPassFragment : BaseFragment<FragmentForgotPassBinding>(), BaseDialog.IDialogClick {
    private val viewModel: ForgotPassViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }


    fun initUI() {
        binding.forgotPass = viewModel
        val msg = SpanUtils.with(baseActivity, baseActivity.getString(R.string.forgot_password2))
            .isBold().endPos(6).getSpanString()
        binding.textView.setSpanString(msg)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)


        binding.edtForgotphone.doAfterTextChanged {
            if (it?.isDigitsOnly() == true) {
                binding.countryCodePicker.visible()
                limitEditText(ValidationConst.MAX_NO_LENGTH)
            } else {
                binding.countryCodePicker.gone()
                limitEditText(ValidationConst.MAX_EMAIL_LENGTH)
            }
            if (it.isNullOrEmpty()) {
                binding.countryCodePicker.gone()
            }
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.selectedCountryCodeWithPlus =
                binding.countryCodePicker.selectedCountryCodeWithPlus
            viewModel.validate()
        }
    }

    private fun limitEditText(maxLength: Int) {
        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = InputFilter.LengthFilter(maxLength)
        binding.edtForgotphone.filters = filters
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_forgot_pass
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                val isPhone = value as Boolean

                CheckMailDialog().apply {
                    arguments = bundleOf(
                        "title" to this@ForgotPassFragment.baseActivity.getString(R.string.check_your_mail_sms),
                        "description" to this@ForgotPassFragment.baseActivity.getString(R.string.check_mail_sms_instructions),
                        "email" to this@ForgotPassFragment.binding.edtForgotphone.content(),
                        "isPhone" to isPhone,
                        "countryCode" to this@ForgotPassFragment.binding.countryCodePicker.selectedCountryCodeWithPlus
                    )
                    setOnDialogClickListener(this@ForgotPassFragment)
                }.show(childFragmentManager, "")
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            when (items[0] as Int) {
                Constant.CLICK_VIEW -> {

                    findNavController().navigateTo(
                        ForgotPassFragmentDirections.actionForgotPassFragmentToOTPVerifyFragment(
                            phone = if (binding.edtForgotphone.content()
                                    .isDigitsOnly()
                            ) binding.edtForgotphone.content() else "",
                            email = if (!binding.edtForgotphone.content()
                                    .isDigitsOnly()
                            ) binding.edtForgotphone.content() else "",
                            type = OtpType.TYPE_FORGOT,
                            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
                        )
                    )
                }

            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}