package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentResetPassBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ResetPassFragment : BaseFragment<FragmentResetPassBinding>() {
    private val viewModel: ResetViewModel by viewModel()



    override fun getLayoutRes() = R.layout.fragment_reset_pass

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner,this)
        binding.resetPass = viewModel

        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.reset_password)).endPos(5)
                .isBold().getSpanString()

        )
        binding.edtConfirmPass.showHidePassword()
        binding.etNewPass.showHidePassword()

    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        findNavController().navigateUp()
    }


}