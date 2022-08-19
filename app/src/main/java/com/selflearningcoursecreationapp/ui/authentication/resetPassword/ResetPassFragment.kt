package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentResetPassBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPassFragment : BaseFragment<FragmentResetPassBinding>() {
    private val viewModel: ResetViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_reset_pass

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }



    private fun initUi() {
        arguments?.let {
            viewModel.userId = it.getString("user_id").toString()
        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.resetPass = viewModel

        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.reset_password)).endPos(5)
                .isBold().getSpanString()

        )
        binding.edtConfirmPass.showHidePassword()
        binding.etNewPass.showHidePassword()

        binding.btnReset.setOnClickListener {
            viewModel.onReset()
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_RESET_PASS -> {
                CommonAlertDialog.builder(baseActivity).notCancellable().hideNegativeBtn(true)
                    .description(baseActivity.getString(R.string.password_reset_successfully))
                    .icon(R.drawable.ic_checked_logo)
                    .getCallback {
                        findNavController().navigateUp()
                    }.build()
            }
        }

    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}