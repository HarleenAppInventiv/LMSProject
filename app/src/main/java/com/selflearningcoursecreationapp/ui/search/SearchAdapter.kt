package com.selflearningcoursecreationapp.ui.search

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.ItemSearchBinding

class SearchAdapter : BaseAdapter<ItemSearchBinding>() {
    override fun getLayoutRes() = R.layout.item_search


    override fun getItemCount() = 5
}