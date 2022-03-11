package com.selflearningcoursecreationapp.ui.preferences.category

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectCategoryBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.utils.Constant

class CategoryAdapter(private val value: ArrayList<ThemeData>) :BaseAdapter<AdapterSelectCategoryBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_category
    }

    override fun getItemCount(): Int {
        return value.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding= holder.binding as AdapterSelectCategoryBinding
        binding.ivSelected.visibleView(value[position].isSelected)
        binding.tvTitle.text= value[position].languageId
        binding.parentCL.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW,position)
        }
    }

}