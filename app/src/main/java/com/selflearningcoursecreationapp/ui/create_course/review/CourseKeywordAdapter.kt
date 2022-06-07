package com.selflearningcoursecreationapp.ui.create_course.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterKeywordsBinding
import com.selflearningcoursecreationapp.utils.Constant

class CourseKeywordAdapter(
    private var list: ArrayList<String?>
) : BaseAdapter<AdapterKeywordsBinding>() {
    override fun getLayoutRes() = R.layout.adapter_keywords
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterKeywordsBinding
        val context = binding.root.context
        val data = list[position]

        binding.tvTitle.text = data
        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)

        }
    }
}