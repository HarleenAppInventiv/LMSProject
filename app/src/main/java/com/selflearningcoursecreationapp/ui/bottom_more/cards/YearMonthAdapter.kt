package com.selflearningcoursecreationapp.ui.bottom_more.cards

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterRecyclerYearMonthBinding
import com.selflearningcoursecreationapp.utils.Constant


class YearMonthAdapter(private val list: List<String>, private val tag: String?) :
    BaseAdapter<AdapterRecyclerYearMonthBinding>() {
    override fun getLayoutRes() = R.layout.adapter_recycler_year_month

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterRecyclerYearMonthBinding
        binding.tvMonthYearName.setText(list[position])
        binding.tvMonthYearName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }



    }

    override fun getItemCount() = list.size
}