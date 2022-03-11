package com.selflearningcoursecreationapp.ui.preferences.font


import androidx.core.content.res.ResourcesCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectFontBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.utils.Constant

class FontAdapter(private var list: ArrayList<ThemeData>) :
    BaseAdapter<AdapterSelectFontBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_select_font
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectFontBinding
        val data = list[position]
        binding.tvFont.setText(binding.root.context.getString(data.themeName))

        binding.tvFont.typeface = ResourcesCompat.getFont(binding.root.context, data.themeColor)
        binding.tvPreview.typeface = ResourcesCompat.getFont(binding.root.context, data.themeColor)
        binding.ivSelected.visibleView(data.isSelected)
        binding.parentCL.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

    }
}