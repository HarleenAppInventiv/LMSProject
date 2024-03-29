package com.selflearningcoursecreationapp.ui.preferences.language

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectLanguageBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class LanguageAdapter(
    private var list: ArrayList<CategoryData>,
    private var isSingleSelection: Boolean = true,
    private var fromModFilter: Boolean = false
) :
    BaseAdapter<AdapterSelectLanguageBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_language
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectLanguageBinding
        val data = list[position]
        binding.tvName.text = data.name
        binding.rbName.isChecked = data.isSelected
        binding.cbName.isChecked = data.isSelected
        binding.rbName.visibleView(isSingleSelection)
        binding.cbName.visibleView(!isSingleSelection)

        if (!fromModFilter) {
            binding.tvName.alpha = if (list[position].isFaded) 0.5f else 1f
            binding.rbName.setEnabled(!list[position].isFaded)
        } else {
            binding.tvName.alpha = 1f
            binding.rbName.setEnabled(true)
        }

        binding.tvName.changeFontType(if (data.isSelected && isSingleSelection) ThemeConstants.FONT_SEMI_BOLD else ThemeConstants.FONT_REGULAR)


        binding.rbName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.tvName.setOnClickListener {
            if (fromModFilter || !list[position].isFaded) {
                onItemClick(Constant.CLICK_VIEW, position)
            }

        }
        binding.cbName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)


        }
    }
}