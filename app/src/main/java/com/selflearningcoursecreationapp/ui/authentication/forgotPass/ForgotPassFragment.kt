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
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.dialog.CheckMailDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgotPassFragment : BaseFragment<FragmentForgotPassBinding>(), BaseDialog.IDialogClick {
    val viewModel: ForgotPassViewModel by viewModel()

    var number = ""
    var email = ""
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
            if (it!!.isDigitsOnly()) {
                number = it.toString()
                binding.countryCodePicker.visible()
//                Log.d("varun", "initUI email is empty: ${number}")
                limitEditText(16)
            } else {
                binding.countryCodePicker.gone()
                limitEditText(40)
                email = it.toString()
//                Log.d("varun", "initUI number is empty: ${email}")
            }
            if (it.length == 0) {
                binding.countryCodePicker.gone()
            }
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.validate(binding.countryCodePicker.selectedCountryCodeWithPlus)
        }
    }

    fun limitEditText(maxLength: Int) {
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
//                if (isPhone) {
//                    findNavController().navigate(
//                        ForgotPassFragmentDirections.actionForgotPassFragmentToOTPVerifyFragment(
//                            phone = binding.edtForgotphone.content(),
//                            email = "",
//                            type = OTP_TYPE.TYPE_FORGOT,
//                            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
//                        )
//                    )
//                } else {
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
//                    findNavController().navigate(
//                        ForgotPassFragmentDirections.actionForgotPassFragmentToOTPVerifyFragment(
//                            phone = "",
//                            email = binding.edtForgotphone.content(),
//                            type = OTP_TYPE.TYPE_FORGOT,
//                            countryCode = ""
//                        )
//                    )
//                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var view = items[0] as Int
            when (view) {
                Constant.CLICK_VIEW -> {

                    findNavController().navigate(
                        ForgotPassFragmentDirections.actionForgotPassFragmentToOTPVerifyFragment(
                            phone = if (binding.edtForgotphone.content()
                                    .isDigitsOnly()
                            ) binding.edtForgotphone.content() else "",
                            email = if (!binding.edtForgotphone.content()
                                    .isDigitsOnly()
                            ) binding.edtForgotphone.content() else "",
                            type = OTP_TYPE.TYPE_FORGOT,
                            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
                        )
                    )
                }

            }
        }
    }


}