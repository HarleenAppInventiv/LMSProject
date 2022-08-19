package com.selflearningcoursecreationapp.ui.preferences.category

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectCategoryBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant

class CategoryAdapter(
    private val value: ArrayList<CategoryData>,
    private var showRadio: Boolean = false,
    private var type: Int = 0
) :
    BaseAdapter<AdapterSelectCategoryBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_category
    }

    override fun getItemCount(): Int {
        return value.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectCategoryBinding
        if (showRadio) {
            binding.rbSelected.visible()
            binding.ivSelected.gone()
            binding.rbSelected.isChecked = (value[position].isSelected)

        } else {
            binding.rbSelected.gone()
            binding.ivSelected.visibleView(value[position].isSelected)
        }
        binding.tvTitle.text = value[position].name
        binding.ivPreview.loadImage(value[position].imageUrl, R.drawable.ic_science_dummy)

        binding.parentCL.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position, type)
        }
        binding.rbSelected.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position, type)
        }
    }

}