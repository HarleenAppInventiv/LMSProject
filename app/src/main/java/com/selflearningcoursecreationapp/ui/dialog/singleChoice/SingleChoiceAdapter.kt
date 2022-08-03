package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSingleChoiceBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class SingleChoiceAdapter(
    private val list: List<SingleChoiceData>,
    private var showRadio: Boolean = false
) :
    BaseAdapter<AdapterSingleChoiceBinding>() {
    override fun getLayoutRes() = R.layout.adapter_single_choice

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterSingleChoiceBinding
        binding.tvTitle.text = list[position].title
        binding.rbChecked.visibleView(showRadio)
        binding.rbChecked.isChecked = list[position].isSelected == true
        binding.tvTitle.changeTextColor(if (list[position].isSelected == true) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_PRIMARY)
        binding.tvTitle.changeFontType(
            if (list[position].isSelected == true) ThemeConstants.FONT_SEMI_BOLD else ThemeConstants.FONT_REGULAR
        )
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.rbChecked.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }


    }

    override fun getItemCount() = list.size
}