package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import android.content.Intent
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
import com.selflearningcoursecreationapp.extensions.otpHelper
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import com.selflearningcoursecreationapp.utils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class OTPVerifyFragment : BaseFragment<FragmentOTPVarifyBinding>() {
    private val viewModel: OTPVerifyViewModel by viewModel()

    private var startTime: Long = 120000
    private var timer: CountDownTimer? = null
    private var argBundle: OTPVerifyFragmentArgs? = null
    private var OTP_TIME = 60000L
    private var maxRequest = 5
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
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        argBundle?.let {

            if (it.phone?.isEmpty() == true) {
                binding.tvPhone.text = it.email
                binding.tvSendOtpText.setText(getString(R.string.enter_four_digit_from_mail))

            } else {
                binding.tvPhone.text = "${it.countryCode} ${it.phone}"
                binding.tvSendOtpText.setText(getString(R.string.enter_4_digit_number_that_sent_to_your_number))
            }
        }

        if (argBundle?.type == OTP_TYPE.TYPE_SIGNUP) {
            viewModel.reqOTP(argBundle)
        } else {
            startTimer()

        }

        onClickListners()
    }

    fun onClickListners() {
        binding.btnSubmitOtp.setOnClickListener {

            viewModel.otpVerify(argBundle)
        }

        binding.tvResend.setOnClickListener {

            viewModel.reqOTP(argBundle)
        }
    }

    override fun getLayoutRes() = R.layout.fragment_o_t_p_varify


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                binding.tvResend.typeface = Typeface.DEFAULT
                binding.tvResend.changeTextColor(ThemeConstants.TYPE_PRIMARY)
                startTimer()
                binding.tvResend.isEnabled = false
            }
            ApiEndPoints.API_OTP_VAL -> {
                (value as BaseResponse<UserResponse>)

                when (argBundle?.type) {
                    OTP_TYPE.TYPE_FORGOT -> {
                        var userId = value.resource?.user?.id.toString()
                        var action =
                            OTPVerifyFragmentDirections.actionOTPVerifyFragmentToResetPassFragment(
                                userId
                            )
                        findNavController().navigate(action)
                    }
                    OTP_TYPE.TYPE_LOGIN -> {
//                        lifecycleScope.launch {
//
//                            viewModel.saveUserDataInDB(userResponse)
//                            delay(2000)

//                        findNavController().navigate(R.id.homeActivity)
                        val userData = (value as BaseResponse<UserResponse>).resource
                        if (userData?.user?.passwordUpdated == false) {
                            var action =
                                OTPVerifyFragmentDirections.actionOTPVerifyFragmentToAddPasswordFragment(
                                    value.resource?.user?.id.toString()
                                )
                            findNavController().navigate(action)
                        }
//
                        else if (!(userData?.user?.languageUpdated
                                ?: false) || !(userData?.user?.fontUpdated
                                ?: false) || !(userData?.user?.themeUpdated
                                ?: false) || !(userData?.user?.categoryUpdated ?: false)
                        ) {
                            var level = when {
                                userData?.user?.languageUpdated ?: false -> 4
                                userData?.user?.fontUpdated ?: false -> 3
                                userData?.user?.themeUpdated ?: false -> 2
                                userData?.user?.categoryUpdated ?: false -> 1
                                else -> 0
                            }
                            if (level != 4) {
                                findNavController().navigate(
                                    OTPVerifyFragmentDirections.actionOTPVerifyFragmentToPreferencesFragment(
                                        currentSelection = level ?: 0
                                    )
                                )
                            } else {
                                activity?.startActivity(
                                    Intent(
                                        requireActivity(),
                                        HomeActivity::class.java
                                    )
                                )
                                activity?.finish()
                            }

                        } else {
                            activity?.startActivity(
                                Intent(
                                    requireActivity(),
                                    HomeActivity::class.java
                                )
                            )
                            activity?.finish()
                        }


//                        }

                    }
                    OTP_TYPE.TYPE_SIGNUP -> {


                        var action =
                            OTPVerifyFragmentDirections.actionOTPVerifyFragmentToAddPasswordFragment(
                                value.resource?.user?.id.toString()
                            )
                        findNavController().navigate(action)

//                        lifecycleScope.launch {
//                            viewModel.saveUserDataInDB(userResponse)
//                            delay(2000)
//                        }

                    }
                    OTP_TYPE.TYPE_EMAIL -> {
                        findNavController().navigateUp()
                    }
                }

            }
        }
    }


    private fun startTimer() {
        if (maxRequest == 0) {
            startTime = OTP_TIME.times(5)
        } else {
            startTime = OTP_TIME.times(2)
        }
        timer?.cancel()
        timer = object : CountDownTimer(startTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isVisible) {

                    var min = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished).toString()
                    if (min.length == 1) {
                        min = "0$min"
                    }

                    var sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                        .minus(
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                        ).toString()
                    if (sec.length == 1) {
                        sec = "0$sec"
                    }

                    val resendText =
                        baseActivity.getString(R.string.resend_code_in) + " $min : $sec"
                    binding.tvResend.setSpanString(
                        SpanUtils.with(baseActivity, resendText).startPos(16).textColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.google_btn_bg_color_fc6d5b
                            )
                        ).isBold().getSpanString()
                    )
                    binding.tvResend.isEnabled = false
                }
            }

            override fun onFinish() {
                if (isVisible) {
                    if (maxRequest == 0) {
                        maxRequest = 5
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


    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
    }


}