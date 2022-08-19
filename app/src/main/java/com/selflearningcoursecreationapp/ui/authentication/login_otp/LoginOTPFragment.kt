package com.selflearningcoursecreationapp.ui.authentication.login_otp

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentLoginOTPBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginOTPFragment : BaseFragment<FragmentLoginOTPBinding>() {

    private val viewModel: LoginOTPViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    fun init() {

        binding.imageView.invalidate()
        binding.loginViaOTP = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        hideLoading()

        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.login_via_otp)).endPos(
                5
            ).isBold().getSpanString()
        )
        binding.txtSignUp.setSpanString(
            SpanUtils.with(
                baseActivity,
                baseActivity.getString(R.string.don_t_have_an_account_sign_up)
            ).startPos(
                22
            ).isBold().themeColor().getCallback {

                setFragmentResult("loginData", bundleOf("type" to 1))
                findNavController().navigateUp()
            }.getSpanString()


        )


        binding.countryCodePicker.apply {
            setAutoDetectedCountry(true)
        }


        binding.btnContinue.setOnClickListener {
            viewModel.countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
            viewModel.loginViaOTP()
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                val action =
                    LoginOTPFragmentDirections.actionLoginOTPFragmentToOTPVerifyFragment(
                        phone = binding.edtRegPhone.content(),
                        email = "",
                        type = OtpType.TYPE_LOGIN,
                        countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
                    )
                findNavController().navigate(action)
            }

        }
    }

    override fun getLayoutRes() = R.layout.fragment_login_o_t_p

    override fun onResume() {
        super.onResume()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}