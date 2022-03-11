package com.selflearningcoursecreationapp.ui.bottom_more.cards

import android.view.View
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCardDetailBinding
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick

class CardDetailsBottomDialog() : BaseBottomSheetDialog<BottomDialogCardDetailBinding>(),
    HandleClick {
    lateinit var yearMonthBottomDialog: YearMonthBottomDialog
    override fun getLayoutRes() = R.layout.bottom_dialog_card_detail


  override  fun initUi() {
        binding.cardDetail = this
        yearMonthBottomDialog= YearMonthBottomDialog()
        yearMonthBottomDialog.setOnDialogClickListener(object : IDialogClick{
            override fun onDialogClick(vararg items: Any) {
                if (items.isNotEmpty())
                {
                    val type= items[0] as Int
                    val data= items[1] as String
                    when(type)
                    {
                        Constant.TYPE_MONTH->{
                            binding.evExpMonth.setText(data)
                        }
                        Constant.TYPE_YEAR->{
                            binding.evExpYear.setText(data)

                        }
                    }
                }
            }
        })

    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {

                R.id.iv_close -> {
                    dismiss()
                }
                R.id.ev_exp_year -> {
                    yearMonthBottomDialog.apply {
                        arguments= bundleOf("type" to Constant.TYPE_YEAR)
                    }
                     yearMonthBottomDialog.show(childFragmentManager,"year")
                }
                R.id.ev_exp_month -> {
                    yearMonthBottomDialog.apply {
                        arguments= bundleOf("type" to Constant.TYPE_MONTH)
                    }
                    yearMonthBottomDialog.show(childFragmentManager,"month")
                }

            }
        }
    }


}