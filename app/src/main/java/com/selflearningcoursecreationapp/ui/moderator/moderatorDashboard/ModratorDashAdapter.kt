package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding

class ModratorDashAdapter : BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes() = R.layout.adapter_my_course
    override fun getItemCount() = 5
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)


    }

}