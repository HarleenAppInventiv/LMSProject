package com.selflearningcoursecreationapp.ui.moderator

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.ItemQualificationBinding

class AdapterBecomeMode : BaseAdapter<ItemQualificationBinding>() {
    override fun getLayoutRes() = R.layout.item_qualification


    override fun getItemCount() = 1
}