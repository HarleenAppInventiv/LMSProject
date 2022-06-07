package com.selflearningcoursecreationapp.ui.create_course.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSuggestionBinding
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.utils.Constant

class KeywordSuggestionAdapter(private var list: ArrayList<SingleChoiceData>) :
    BaseAdapter<AdapterSuggestionBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_suggestion
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSuggestionBinding
        val data = list[position]
        binding.rbName.text = data.title
        binding.rbName.setOnClickListener { onItemClick(Constant.CLICK_VIEW, data) }
    }
}