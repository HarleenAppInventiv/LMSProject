package com.selflearningcoursecreationapp.ui.my_bank

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomAccountOptionsBinding
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountOptionsBottomSheet : BaseBottomSheetDialog<BottomAccountOptionsBinding>() {

    val viewModel: BankAccountVM by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.bottom_account_options
    }

    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        if (arguments?.getInt("id") != 0) {
            binding.tvSetAsPrimaryAccount.setOnClickListener {
                if (arguments?.getBoolean("isPrimeyChecked") == true) {
                    showToastShort(getString(R.string.this_account_is_already_primary))
                    dismiss()
                } else {
                    viewModel.makeAccountPrimaryPayload =
                        hashMapOf(
                            "id" to (arguments?.getInt("id") ?: 0),
                            "isPrimaryAccount" to true
                        )
                    viewModel.sendMakeAccountPrimaryRequest()
                }

            }

            binding.tvDelete.setOnClickListener {

                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.are_you_sure))
                    .description(getString(R.string.do_you_really_want_to_delete_your_account))
                    .positiveBtnText(getString(R.string.delete_acc))
                    .icon(R.drawable.ic_fogot_password)
                    .getCallback {
                        if (it) {
                            viewModel.makeAccountPrimaryPayload =
                                hashMapOf("id" to (arguments?.getInt("id") ?: 0))
                            viewModel.deleteBankAccount()
                        }
                    }.build()

            }


        }
        binding.ivClose.setOnClickListener {

            dismiss()
        }


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)

        when (apiCode) {
            ApiEndPoints.API_MAKE_ACCOUNT_PRIMARY -> {
                showToastLong(getString(R.string.bank_account_marked_primary))
                onDialogClick()
                dismiss()

            }
            ApiEndPoints.API_ADD_BANK_ACCOUNT + "/delete" -> {
                showToastShort((value as BaseResponse<Any>).message)
                onDialogClick()
                dismiss()
            }


        }


    }
}