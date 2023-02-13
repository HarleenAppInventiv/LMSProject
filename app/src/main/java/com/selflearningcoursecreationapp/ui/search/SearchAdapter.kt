package com.selflearningcoursecreationapp.ui.search

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemSearchBinding
import com.selflearningcoursecreationapp.utils.Constant

class SearchAdapter(var list: ArrayList<String>) : BaseAdapter<ItemSearchBinding>() {
    override fun getLayoutRes() = R.layout.item_search


    override fun getItemCount() = if (list.size > 5) 5 else list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as ItemSearchBinding
        val context = binding.root.context
        var itemPosition = if (list.size > 5) (position + (list.size - 5)) else position

        binding.tvSearchTitle.text = list[itemPosition]

        binding.imgCross.setOnClickListener {
            onItemClick(Constant.SEARCH_CROSS, itemPosition)
        }


        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, itemPosition)
        }

    }
}