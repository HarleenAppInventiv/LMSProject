package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentOTPVarifyBinding
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.otpHelper
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class OTPVerifyFragment : BaseFragment<FragmentOTPVarifyBinding>() {
    private val viewModel: OTPVerifyViewModel by viewModel()

    private var startTime: Long = ValidationConst.OTP_TIME
    private var timer: CountDownTimer? = null
    private var argBundle: OTPVerifyFragmentArgs? = null
    private var maxRequest = ValidationConst.OTP_TIME_REQUEST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argBundle = arguments?.let {
            OTPVerifyFragmentArgs.fromBundle(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.etOtp1.otpHelper()
        binding.etOtp2.otpHelper()
        binding.etOtp3.otpHelper()
        binding.etOtp4.otpHelper()
        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.verify_with_otp)).endPos(7)
                .isBold().getSpanString()
        )
        binding.otpverify = viewModel
        viewModel.argBundle = argBundle
        viewModel.token = baseActivity.token
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        argBundle?.let {

            if (it.phone?.isEmpty() == true) {
                binding.tvPhone.text = it.email
                binding.tvSendOtpText.text = getString(R.string.enter_four_digit_from_mail)

            } else {
                binding.tvPhone.text = String.format("%s %s", it.countryCode, it.phone)
                binding.tvSendOtpText.text =
                    getString(R.string.enter_4_digit_number_that_sent_to_your_number)
            }
        }

        if (argBundle?.type == OtpType.TYPE_SIGNUP) {
            viewModel.reqOTP()
        } else {
            startTimer()

        }

        onClickListeners()
    }

    private fun onClickListeners() {
        binding.btnSubmitOtp.setOnClickListener {
            viewModel.otpVerify()
        }

        binding.tvResend.setOnClickListener {

            viewModel.reqOTP()
        }
    }

    override fun getLayoutRes() = R.layout.fragment_o_t_p_varify


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ, ApiEndPoints.API_ADD_EMAIL -> {
                binding.tvResend.typeface = Typeface.DEFAULT
                binding.tvResend.changeTextColor(ThemeConstants.TYPE_PRIMARY)
                startTimer()
                binding.tvResend.isEnabled = false
            }
            ApiEndPoints.API_OTP_VAL, ApiEndPoints.API_VERIFY_EMAIL -> {
                val userData = (value as? BaseResponse<UserResponse>)?.resource
                when (argBundle?.type) {
                    OtpType.TYPE_FORGOT -> {
                        val userId = userData?.user?.id.toString()
                        val action =
                            OTPVerifyFragmentDirections.actionOTPVerifyFragmentToResetPassFragment(
                                userId
                            )
                        findNavController().navigate(action)
                    }
                    OtpType.TYPE_LOGIN -> {
                        handleLoginResponse(userData)


                    }
                    OtpType.TYPE_SIGNUP -> {


                        val action =
                            OTPVerifyFragmentDirections.actionOTPVerifyFragmentToAddPasswordFragment(
                                userData?.user?.id.toString()
                            )
                        findNavController().navigate(action)

                    }
                    OtpType.TYPE_EMAIL -> {
                        findNavController().navigateUp()
                    }
                }

            }
        }
    }

    private fun handleLoginResponse(
        userData: UserResponse?
    ) {
        when {
            userData?.user?.passwordUpdated == false -> {
                val action =
                    OTPVerifyFragmentDirections.actionOTPVerifyFragmentToAddPasswordFragment(
                        userData.user?.id.toString()
                    )
                findNavController().navigate(action)
            }
            userData?.user?.getPreferenceValue() != 4 -> {
                val data =
                    PreferenceData(currentSelection = userData?.user?.getPreferenceValue() ?: 0)

                findNavController().navigate(
                    OTPVerifyFragmentDirections.actionOTPVerifyFragmentToPreferencesFragment(
                        preferenceData = data
                    )
                )


            }
            userData.user?.currentMode == 2 -> {
                baseActivity.goToModeratorActivity()
            }
            else -> {

                baseActivity.goToHomeActivity()
            }
        }
    }


    private fun startTimer() {
        setStartTime()
        timer?.cancel()
        timer = object : CountDownTimer(startTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isVisible) {

                    val min = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)

                    val sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                        .minus(
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                        )
                    val resendText = String.format(
                        "%s %02d : %02d",
                        baseActivity.getString(R.string.resend_code_in),
                        min,
                        sec
                    )
                    binding.tvResend.setSpanString(
                        SpanUtils.with(baseActivity, resendText).startPos(16).textColor(
                            ContextCompat.getColor(
                                baseActivity,
                                baseActivity.getAttrColor(R.attr.accentColor_Red)
                            )
                        ).isBold().getSpanString()
                    )
                    binding.tvResend.isEnabled = false
                }
            }

            override fun onFinish() {
                if (isVisible) {
                    if (maxRequest == 0) {
                        maxRequest = ValidationConst.OTP_TIME_REQUEST
                    } else {
                        maxRequest -= 1
                    }
                    binding.tvResend.typeface = Typeface.DEFAULT_BOLD
                    binding.tvResend.changeTextColor(ThemeConstants.TYPE_THEME)
                    binding.tvResend.text = baseActivity.getString(R.string.resend_otp)
                    binding.tvResend.isEnabled = true
                }
            }
        }

        timer?.start()
    }

    private fun setStartTime() {
        startTime = if (maxRequest == 0) {
            ValidationConst.OTP_TIME.times(5)
        } else {
            ValidationConst.OTP_TIME.times(1)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}