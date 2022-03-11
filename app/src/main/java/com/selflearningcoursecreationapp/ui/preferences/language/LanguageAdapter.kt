package com.selflearningcoursecreationapp.ui.preferences.language

import android.graphics.Typeface
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectLanguageBinding
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.utils.Constant

class LanguageAdapter(private var list: ArrayList<ThemeData>) :
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
        binding.rbName.text = binding.root.context.getString(data.themeName)
        binding.rbName.isChecked = data.isSelected
        binding.rbName.typeface = if (data.isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.rbName.setOnClickListener { onItemClick(Constant.CLICK_VIEW, position) }
    }
}