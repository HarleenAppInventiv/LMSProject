package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectFilterBinding

class AdapterFilterUIAdapter(
    private val filterOptionData: ArrayList<FilterOptionData>,
    var isFromFilter: Boolean
) :
    BaseAdapter<AdapterSelectFilterBinding>() {
    override fun getLayoutRes() = R.layout.adapter_select_filter
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSelectFilterBinding
        val context = binding.root.context
        binding.rbName.isChecked = filterOptionData[position].isSelected
//        if (isFromFilter){
//            binding.rbName.text = filterOptionData[position].filterOptionDisplayName+"(${filterOptionData[position].filterOptionCount})"
//
//        }else{
        binding.rbName.text = filterOptionData[position].filterOptionDisplayName

//        }
        binding.rbName.setOnClickListener {
            filterOptionData.forEach {
                it.isSelected = false
            }
            filterOptionData[position].isSelected = true

            notifyDataSetChanged()
        }
        binding.rbName.setTextColor(
            ContextCompat.getColor(
                context,
                if (filterOptionData[position].isSelected) R.color.radioSelectedTextColor else R.color.radioTextColor
            )
        )

    }

    override fun getItemCount() = filterOptionData.size
}