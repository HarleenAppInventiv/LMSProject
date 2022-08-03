package com.selflearningcoursecreationapp.ui.profile.requestTracker.coAuthorRequest

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemCoAuthorRequestBinding


class CoAuthorRequestAdapter : BaseAdapter<ItemCoAuthorRequestBinding>() {
    override fun getLayoutRes() = R.layout.item_co_author_request

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

    }

    override fun getItemCount() = 10
}