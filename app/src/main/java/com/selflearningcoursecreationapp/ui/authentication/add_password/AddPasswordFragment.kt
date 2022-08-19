package com.selflearningcoursecreationapp.ui.authentication.add_password

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddPasswordBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddPasswordFragment : BaseFragment<FragmentAddPasswordBinding>() {
    private val viewModel: AddPassViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        arguments?.let {
       viewModel.setUserData(it.getString("user_id").toString(), baseActivity.token)
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.setPass = viewModel

        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.set_password)).endPos(4)
                .isBold().getSpanString()

        )
        binding.edtConfirmPass.showHidePassword()
        binding.etPass.showHidePassword()

        binding.btnSave.setOnClickListener {
            viewModel.validate()
        }


    }

    override fun getLayoutRes() = R.layout.fragment_add_password


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_PASSWORD -> {

                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.successful))
                    .description(getString(R.string.you_have_added_pass_to_account))
                    .positiveBtnText(getString(R.string.okay))
                    .hideNegativeBtn(true)
                    .icon(R.drawable.ic_checked_logo)
                    .notCancellable()
                    .getCallback {
                        if (it) {
                            baseActivity.runOnUiThread {
                                findNavController().navigate(R.id.action_addPasswordFragment_to_preferencesFragment)
                            }
                        }
                    }.build()


            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }
}