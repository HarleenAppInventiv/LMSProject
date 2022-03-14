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
    private var cardType: Int = Constant.TYPE_MONTH
    private var position: Int = 0
    override fun getLayoutRes() = R.layout.bottom_dialog_card_detail

  override fun initUi() {
      arguments?.let {
          cardType = it.getInt("type")
          position = it.getInt("position")
      }

      when (cardType) {
          Constant.CLICK_ADD -> {
              binding.evCardNumber.setText("")
              binding.evLoginEmail.setText("")
              binding.evExpMonth.setText("")
                binding.evExpYear.setText("")

            }
            Constant.CLICK_EDIT -> {
                binding.evCardNumber.setText("12345678909876")
                binding.evLoginEmail.setText("abcd@gmail.com")
                binding.evExpMonth.setText("02 (feb)")
                binding.evExpYear.setText("2023")
            }

        }

        binding.cardDetail = this
        yearMonthBottomDialog = YearMonthBottomDialog()
        yearMonthBottomDialog.setOnDialogClickListener(object : IDialogClick {
            override fun onDialogClick(vararg items: Any) {
                if (items.isNotEmpty()) {
                    val type = items[0] as Int
                    val data = items[1] as String
                    when (type) {
                        Constant.TYPE_MONTH -> {
                            binding.evExpMonth.setText(data)
                        }
                        Constant.TYPE_YEAR -> {
                            binding.evExpYear.setText(data)

                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.bt_save_card -> {
                    onDialogClick(cardType, position)
                    dismiss()
                }
                R.id.iv_close -> {
                    dismiss()
                }
                R.id.ev_exp_year -> {
                    yearMonthBottomDialog.apply {
                        arguments = bundleOf("type" to Constant.TYPE_YEAR)
                    }
                    yearMonthBottomDialog.show(childFragmentManager, "year")
                }
                R.id.ev_exp_month -> {
                    yearMonthBottomDialog.apply {
                        arguments = bundleOf("type" to Constant.TYPE_MONTH)
                    }
                    yearMonthBottomDialog.show(childFragmentManager, "month")
                }


            }
        }
    }
}