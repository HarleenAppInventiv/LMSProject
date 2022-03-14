package com.selflearningcoursecreationapp.ui.bottom_more.cards

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogYearMonthBinding
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import java.util.*
import kotlin.collections.ArrayList

class YearMonthBottomDialog : BaseBottomSheetDialog<BottomDialogYearMonthBinding>(),
    BaseAdapter.IViewClick, HandleClick {
    private var list: ArrayList<String> = ArrayList()
    private var dialogType: Int = Constant.TYPE_MONTH
    lateinit var adapter: YearMonthAdapter
    override fun getLayoutRes() = R.layout.bottom_dialog_year_month

    override fun initUi() {
        list.clear()
        arguments?.let {
            dialogType = it.getInt("type")
        }

        binding.yearMonth = this

        when (dialogType) {
            Constant.TYPE_MONTH -> {
                binding.tvDialogTitle.text = getString(R.string.expiry_month)
                for (i in 0 until 12) {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.MONTH, i)
                    list.add(cal.time.getStringDate("MMM (MM)"))
                }
            }
            Constant.TYPE_YEAR -> {
                binding.tvDialogTitle.text = getString(R.string.expiry_year)
                for (i in 0 until 20) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.YEAR, i)
                    list.add(cal.time.getStringDate("YYYY"))
                }
            }
        }
        adapter = YearMonthAdapter(list, tag)
        binding.reyclerMonths.adapter = adapter
        adapter.setOnAdapterItemClickListener(this)

    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var type = items[0] as Int
            var position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {

                    onDialogClick(dialogType, list[position])
                    dismiss()
                }

            }
        }

    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var view = items[0] as View
            when (view.id) {
                R.id.iv_close -> {
                    dismiss()
                }

            }
        }
    }


}