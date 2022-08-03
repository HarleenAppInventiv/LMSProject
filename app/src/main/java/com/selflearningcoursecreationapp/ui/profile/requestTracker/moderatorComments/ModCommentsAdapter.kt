package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemModCommentsBinding

class ModCommentsAdapter(var isComments: Boolean, private var clickListener: OnClickListener) :
    BaseAdapter<ItemModCommentsBinding>() {


    override fun getLayoutRes() = R.layout.item_mod_comments

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        binding.cvParent.setOnClickListener {
            clickListener.onItemClick()
        }
    }

    override fun getItemCount() = 10

    interface OnClickListener {
        fun onItemClick()
    }
}