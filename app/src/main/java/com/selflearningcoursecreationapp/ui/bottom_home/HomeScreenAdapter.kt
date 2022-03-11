package com.selflearningcoursecreationapp.ui.bottom_home

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoursesViewBinding


class HomeScreenAdapter() : BaseAdapter<AdapterCoursesViewBinding>() {

    override fun getLayoutRes() = R.layout.adapter_courses_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
    }

    override fun getItemCount() = 5



}