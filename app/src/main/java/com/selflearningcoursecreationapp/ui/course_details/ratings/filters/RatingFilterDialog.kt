package com.selflearningcoursecreationapp.ui.course_details.ratings.filters

import android.graphics.Typeface
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectFilterBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant

class RatingFilterDialog(private var list: ArrayList<CategoryData>, private var type: Int) :
    BaseAdapter<AdapterSelectFilterBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_filter
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectFilterBinding
        val data = list[position]
        binding.rbName.text = data.name
        binding.rbName.isChecked = data.isSelected
        binding.rbName.typeface = if (data.isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.rbName.setOnClickListener { onItemClick(Constant.CLICK_VIEW, position, type) }
    }
}