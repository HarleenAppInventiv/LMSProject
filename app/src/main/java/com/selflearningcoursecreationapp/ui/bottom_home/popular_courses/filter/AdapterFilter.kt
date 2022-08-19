package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterFilterLayoutBinding
import com.selflearningcoursecreationapp.extensions.visibleView

class AdapterFilter(
    private val mainList: ArrayList<FilterTypeData>?,
    var isFromFilter: Boolean = false
) :
    BaseAdapter<AdapterFilterLayoutBinding>() {
    //    lateinit var adapter: AdapterFilterUIAdapter
    override fun getLayoutRes() = R.layout.adapter_filter_layout

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterFilterLayoutBinding
        binding.tvTitle.text = mainList?.get(position)?.filterDisplayName ?: ""


        val adapter = AdapterFilterUIAdapter(
            mainList?.get(position)?.filterOptionData ?: ArrayList(),
            isFromFilter
        )
        binding.recylerFilterView.adapter = adapter
        binding.view12.visibleView(position + 1 != mainList?.size)

    }

    override fun getItemCount() = mainList?.size ?: 0






}