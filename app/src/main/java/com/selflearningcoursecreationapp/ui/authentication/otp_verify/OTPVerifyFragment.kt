package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentOTPVarifyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.setColor
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import java.util.concurrent.TimeUnit


class OTPVerifyFragment : BaseFragment<FragmentOTPVarifyBinding>() {
    private val viewModel: OTPVerifyViewModel by viewModel()

    private var startTime: Long = 60000
    private var timer: CountDownTimer? = null
    private var argBundle: OTPVerifyFragmentArgs? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argBundle = arguments?.let {
            OTPVerifyFragmentArgs.fromBundle(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        startTimer()
        binding.textView.setSpanString(
            baseActivity!!.getString(R.string.verify_with_otp),
            isBold = true,
            endPos = 7
        )
        binding.otpverify = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        onClickListners()

        argBundle?.phone?.let {
            binding.tvPhone.text = "+91 $it"
        }
    }

    fun onClickListners() {
        binding.btnSubmitOtp.setOnClickListener {
            when (argBundle?.type) {
                TYPE_FORGOT -> {
                    findNavController().navigate(R.id.action_OTPVerifyFragment_to_resetPassFragment)
                }
                TYPE_LOGIN -> {
                    findNavController().navigate(R.id.homeActivity)
                }
            }
        }

        binding.tvResend.setOnClickListener {
            startTime = 60000
            binding.tvResend.typeface = Typeface.DEFAULT
            binding.tvResend.changeTextColor(ThemeConstants.TYPE_PRIMARY)
            startTimer()
            binding.tvResend.isEnabled = false
        }
    }

    override fun getLayoutRes() = R.layout.fragment_o_t_p_varify


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        onClickListners()
    }


    private fun startTimer() {

        timer?.cancel()
        timer = object : CountDownTimer(startTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isVisible) {

                    var min= TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished).toString()
                    if (min.length==1)
                    {
                        min="0$min"
                    }

                   var sec= TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                       .minus(TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                               millisUntilFinished))).toString()
                    if (sec.length==1)
                    {
                        sec="0$sec"
                    }

                    val resendText = baseActivity.getString(R.string.resend_code_in) + " $min : $sec"
                    binding.tvResend.setSpanString(
                        resendText,
                        isBold = true,
                        color = ContextCompat.getColor(baseActivity,R.color.google_btn_bg_color_fc6d5b),
                        startPos = 16
                    )
                    binding.tvResend.isEnabled = false
                }
            }

            override fun onFinish() {
                if (isVisible) {
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

    companion object {
        const val TYPE_LOGIN = 1
        const val TYPE_FORGOT = 2
    }


}