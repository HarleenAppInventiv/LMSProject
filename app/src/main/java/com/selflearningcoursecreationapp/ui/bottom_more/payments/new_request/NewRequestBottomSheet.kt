package com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request

import androidx.core.widget.doOnTextChanged
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomSheetNewRequestBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.setBtnEnabled
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewRequestBottomSheet : BaseDialog<BottomSheetNewRequestBinding>(),
    BaseDialog.IDialogClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: PaymentsVM by viewModel()
    var upperLimit = 0f
    var currencySymbol = ""
    override fun getLayoutRes(): Int {
        return R.layout.bottom_sheet_new_request
    }

    override fun initUi() {

        arguments.let {
            upperLimit = it?.getFloat("remainingWallet") ?: 0f
            currencySymbol = it?.getString("currencySymbol") ?: ""
        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.minWithdrawAmount()
        // api implementation for min amount is pending

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        setContinueClick()


        binding.btnContinue.isEnabled = false
        binding.btnContinue.setBtnEnabled(false)
    }

    private fun setContinueClick() {
        binding.tvError.apply {
            visible()
            text =
                if (upperLimit > viewModel.minAmount) "**${getString(R.string.amount_should_be_greater_than)} $currencySymbol${viewModel.minAmount} ${
                    getString(
                        R.string.and_less_than
                    )
                } $currencySymbol$upperLimit"
                else context.getString(R.string.do_not_have_suffiecient_balance)
        }
        binding.etAmount.doOnTextChanged { text, start, before, count ->

            val isEnable = if (text.toString().isNullOrEmpty()) {
                false
            } else if ((text.toString().toIntOrNull() ?: 0) < viewModel.minAmount) {
                false
            } else (text.toString().toIntOrNull() ?: 0) <= upperLimit

            binding.btnContinue.isEnabled = isEnable
            binding.btnContinue.setBtnEnabled(isEnable)
        }
        binding.btnContinue.setOnClickListener {
            viewModel.withdrawAmount = binding.etAmount.content().toIntOrNull() ?: 0
            viewModel.paymentWithdrawRequest()
//            when {
//                binding.etAmount.text.isNullOrEmpty() -> {
//                    binding.etAmount.setBackgroundResource(R.drawable.edt_bg_outline)
//                    binding.tvError.apply {
//                        visible()
//                        text = baseActivity.getString(R.string.please_enter_amount)
//                    }
//                }
//                (binding.etAmount.content().toIntOrNull() ?: 0) < viewModel.minAmount -> {
//                    binding.etAmount.setBackgroundResource(R.drawable.edt_bg_outline)
//                    binding.tvError.apply {
//                        visible()
//                        text =
//                            "${baseActivity.getString(R.string.plz_enter_amount_greater_than)} ${viewModel.minAmount}"
//                    }
//                }
//                (binding.etAmount.content().toIntOrNull() ?: 0) > upperLimit -> {
//                    binding.etAmount.setBackgroundResource(R.drawable.edt_bg_outline)
//                    binding.tvError.apply {
//                        visible()
//                        text =
//                            "${baseActivity.getString(R.string.plz_enter_amount_less_than)} ${upperLimit}"
//                    }
//                }
//                else -> {
//                    binding.etAmount.setBackgroundResource(R.drawable.edt_bg)
//                    binding.tvError.apply {
//                        gone()
//
//                    }
//                    viewModel.withdrawAmount = binding.etAmount.content().toIntOrNull() ?: 0
//                    viewModel.paymentWithdrawRequest()
//                }
//
//
//            }

//            if (binding.etAmount.text.isNullOrEmpty()) {
//                binding.etAmount.setBackgroundResource(R.drawable.edt_bg_outline)
//                binding.tvError.apply {
//                    visible()
//                    text = "${baseActivity.getString(R.string.please_enter_amount)}"
//                }
//            } else {
//                var amount = Integer.parseInt(binding.etAmount.text.toString())
//
//                if (amount >= viewModel.minAmount && amount < upperLimit) {
//                    viewModel.withdrawAmount = amount
//                    viewModel.paymentWithdrawRequest()
//
//                } else {
//                    binding.etAmount.setBackgroundResource(R.drawable.edt_bg_outline)
//                    binding.tvError.apply {
//                        visible()
//                        text =
//                            "${baseActivity.getString(R.string.plz_enter_amount_greater_than)} ${viewModel.minAmount}"
//                    }
//                }
//            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_WITHDRAW_REQUEST -> {
                if ((value as BaseResponse<Any>).success.equals(false))
                    showToastShort((value as BaseResponse<Any>).message)
                else
                    showToastShort(getString(R.string.new_request_submitted_successfully))

                dismiss()
            }
            ApiEndPoints.API_MIN_WITHDRAW_AMOUNT -> {
                binding.tvError.apply {
                    visible()
                    text =
                        if (upperLimit > viewModel.minAmount) "**${getString(R.string.amount_should_be_greater_than)} $currencySymbol${viewModel.minAmount} ${
                            getString(
                                R.string.and_less_than
                            )
                        } $currencySymbol$upperLimit"
                        else context.getString(R.string.do_not_have_suffiecient_balance)
//                        "**Amount should be greater than $currencySymbol${viewModel.minAmount} and less than $currencySymbol$upperLimit"

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
//        super.onApiRetry(apiCode)
        viewModel.onApiRetry(apiCode)
    }
}