package com.selflearningcoursecreationapp.ui.search

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.ItemPopularSearchBinding

class PopularSearchesAdapter : BaseAdapter<ItemPopularSearchBinding>() {
    override fun getLayoutRes() = R.layout.item_popular_search


    override fun getItemCount() = 5
}