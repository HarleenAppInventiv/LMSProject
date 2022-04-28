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
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddPasswordFragment : BaseFragment<FragmentAddPasswordBinding>() {
    private val viewModel: AddPassViewModel by viewModel()
    var userId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        arguments?.let {
            userId = it.getString("user_id").toString()
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.setPass = viewModel

        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.set_password)).endPos(5)
                .isBold().getSpanString()

        )
        binding.edtConfirmPass.showHidePassword()
        binding.etPass.showHidePassword()

        binding.btnSave.setOnClickListener {
            viewModel.onAdd(userId)
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
                    .icon(R.drawable.ic_success)
                    .notCancellable()
                    .getCallback {
                        if (it) {
                            baseActivity.runOnUiThread {
                                findNavController().navigate(R.id.action_addPasswordFragment_to_preferencesFragment)
                            }
                        }
                    }.build()


//                CommonAlertDialog.builder(baseActivity).notCancellable().hideNegativeBtn(true)
//                    .description(baseActivity.getString(R.string.password_reset_successfully))
//                    .getCallback {
//                        findNavController().navigateUp()
//                    }.build()
            }
        }
    }
}