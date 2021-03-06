package com.selflearningcoursecreationapp.ui.authentication.add_email

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddEmailBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddEmailFragment : BaseFragment<FragmentAddEmailBinding>() {
    private val viewmodel: AddEmailVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.viewModel = viewmodel
        viewmodel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            if (it.containsKey("isEmailAdded") && it.getBoolean("isEmailAdded")) {
                val msg =
                    SpanUtils.with(baseActivity, baseActivity.getString(R.string.change_email))
                        .isBold().endPos(7).getSpanString()
                binding.textView.setSpanString(msg)
            } else {
                val msg = SpanUtils.with(baseActivity, baseActivity.getString(R.string.add_email))
                    .isBold().endPos(3).getSpanString()
                binding.textView.setSpanString(msg)
            }
        } ?: kotlin.run {
            val msg = SpanUtils.with(baseActivity, baseActivity.getString(R.string.add_email))
                .isBold().endPos(3).getSpanString()
            binding.textView.setSpanString(msg)
        }
    }

    override fun getLayoutRes() = R.layout.fragment_add_email
    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        findNavController().navigate(
            AddEmailFragmentDirections.actionAddEmailFragmentToOTPVerifyFragment2(
                phone = "",
                email = binding.etEmail.content(),
                type = OTP_TYPE.TYPE_EMAIL,
                countryCode = ""
            )
        )
    }

}