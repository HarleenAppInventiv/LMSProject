package com.selflearningcoursecreationapp.ui.payment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.BottomSheetCheckoutBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.ui.profile.edit_profile.StateListDialog
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutBottomSheet : BaseBottomSheetDialog<BottomSheetCheckoutBinding>(),
    BaseDialog.IDialogClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: CheckoutVM by viewModel()
    override fun getLayoutRes(): Int {
        return R.layout.bottom_sheet_checkout
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initUi() {

        arguments.let {
            viewModel.amount = it?.getString("courseFee").toString()
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setAmountWithGSTObserver()

        setBtnClickListener()
        binding.evChooseCat.setOnClickListener {
            StateListDialog().apply {
                arguments = bundleOf(
                    "title" to this@CheckoutBottomSheet.baseActivity.getString(R.string.select_state)
                )
                setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                    override fun onDialogClick(vararg items: Any) {
                        if (items.isNotEmpty()) {
                            val type = items[0] as Int
                            val value = items[1]
                            value as StateModel

                            binding.evChooseCat.setText(value.stateName)
                            viewModel.stateId = value.stateId

                            viewModel.amountWithGST()
                            binding.constPayment.visible()
                        }


                    }

                })
            }.show(childFragmentManager, "")

        }

    }


    private fun setBtnClickListener() {

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnPayment.setOnClickListener {
            if (!binding.evChooseCat.text.isNullOrEmpty()) {

                dismiss()
                viewModel.amountWithGSTLiveData.value?.stateId.toString().let { it1 ->
                    viewModel.amountWithGSTLiveData.value?.actualAmount.toString().let { it2 ->
                        onDialogClick(
                            DialogType.PAYMENT,
                            it1, it2
                        )
                    }
                }
            } else showToastLong(getString(R.string.select_state_first))
        }

    }

    private fun setAmountWithGSTObserver() {
        viewModel.amountWithGSTLiveData.observe(viewLifecycleOwner) {


            binding.tvCourseFee.text = "₹ ${it.actualAmount}"
            binding.tvCourseTax.text = "+ ₹ ${it.gstAmount}"
            binding.tvTotalAmount.text = "₹ ${it.amountWithGST}"
            binding.labelCourseTax.text = "${getString(R.string.gst)} ${it.gst}%"
        }
    }


    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)
        viewModel.onApiRetry(apiCode)
    }
}