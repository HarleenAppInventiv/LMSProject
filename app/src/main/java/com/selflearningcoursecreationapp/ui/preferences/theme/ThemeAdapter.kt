package com.selflearningcoursecreationapp.ui.preferences.theme

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectThemeBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.utils.Constant

class ThemeAdapter(private val list: ArrayList<ThemeData>) :
    BaseAdapter<AdapterSelectThemeBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_theme
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectThemeBinding
        val data = list[position]
        binding.tvPreview.setBackgroundColor(
            ContextCompat.getColor(binding.root.context,data.themeColor))
        binding.ivSelected.visibleView(data.isSelected)
        binding.parentCL.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
    }
}