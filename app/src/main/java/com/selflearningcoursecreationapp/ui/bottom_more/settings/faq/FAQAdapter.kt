package com.selflearningcoursecreationapp.ui.bottom_more.settings.faq

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.FaqListViewBinding
import com.selflearningcoursecreationapp.utils.Constant


class FAQAdapter(private val list: MutableList<String>) :
    BaseAdapter<FaqListViewBinding>() {

    override fun getLayoutRes() = R.layout.faq_list_view


    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        binding.tvQuestions.text = list[position]

        binding.tvQuestions.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

    }
}