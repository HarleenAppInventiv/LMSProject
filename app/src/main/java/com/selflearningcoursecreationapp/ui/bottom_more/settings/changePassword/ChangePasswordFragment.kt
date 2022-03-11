package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentChangePasswordBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.showHidePassword
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_change_password
    }

    private val viewModel: ChangePasswordVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.viewModel=viewModel
        binding.etConfirm.showHidePassword()
        binding.etNew.showHidePassword()
        binding.etOld.showHidePassword()
        binding.tvOld.setSpanString(
            baseActivity.getString(R.string.old_password),
            endPos = 3,
            isBold = true
        )
        binding.tvNew.setSpanString(
            baseActivity.getString(R.string.new_password),
            endPos = 3,
            isBold = true
        )
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        showToastShort("Password changed")
    }

}