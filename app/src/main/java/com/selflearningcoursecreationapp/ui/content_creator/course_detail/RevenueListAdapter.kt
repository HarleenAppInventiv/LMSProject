package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterRevenueListBinding

class RevenueListAdapter : BaseAdapter<AdapterRevenueListBinding>() {
    override fun getLayoutRes() = R.layout.adapter_revenue_list
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
    }

    override fun getItemCount() = 5
}