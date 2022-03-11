package com.selflearningcoursecreationapp.ui.authentication.forgotPass

import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.databinding.FragmentForgotPassBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyFragment
import com.selflearningcoursecreationapp.ui.dialog.CheckMailDialog
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPassFragment : BaseFragment<FragmentForgotPassBinding>() {
    val viewModel: ForgotPassViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    fun init() {
        binding.forgotPass = viewModel
        val msg = SpanUtils.with(baseActivity, baseActivity.getString(R.string.forgot_password2))
            .isBold().endPos(6).getSpanString()
        binding.textView.setSpanString(msg)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        onClickListners()
    }

    fun onClickListners() {


        binding.btnSubmit.setOnClickListener {


            if (binding.edtForgotphone.text.toString().trim().isBlank()) {
                viewModel.updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            errorString = baseActivity.getString(
                                R.string.plz_enter_phone_email
                            )
                        )
                    )
                )
            } else if (binding.edtForgotphone.content().isDigitsOnly()) {
                if (binding.edtForgotphone.text.toString().trim().length < 10) {
                    viewModel.updateResponseObserver(
                        Resource.Error(
                            ToastData(
                                errorString = baseActivity.getString(
                                    R.string.enter_valid_phone_number
                                )
                            )
                        )
                    )

                } else {
                    findNavController().navigate(
                        ForgotPassFragmentDirections.actionForgotPassFragmentToOTPVerifyFragment(
                            binding.edtForgotphone.content(), OTPVerifyFragment.TYPE_FORGOT
                        )
                    )
                }

            } else if (!binding.edtForgotphone.content().isValidEmail()) {
                viewModel.updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            errorString = baseActivity.getString(
                                R.string.enter_valid_email
                            )
                        )
                    )
                )

            } else {
                CheckMailDialog().show(childFragmentManager, "")
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_forgot_pass
    }


}