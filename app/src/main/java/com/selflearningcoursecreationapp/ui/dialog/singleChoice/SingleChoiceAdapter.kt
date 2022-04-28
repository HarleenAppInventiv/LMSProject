package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import android.graphics.Typeface
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSingleChoiceBinding
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class SingleChoiceAdapter(private val list: List<SingleChoiceData>) :
    BaseAdapter<AdapterSingleChoiceBinding>() {
    override fun getLayoutRes() = R.layout.adapter_single_choice

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterSingleChoiceBinding
        binding.tvTitle.setText(list[position].title)
        binding.tvTitle.changeTextColor(if (list[position].isSelected == true) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_PRIMARY)
        binding.tvTitle.typeface =
            if (list[position].isSelected == true) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }


    }

    override fun getItemCount() = list.size
}