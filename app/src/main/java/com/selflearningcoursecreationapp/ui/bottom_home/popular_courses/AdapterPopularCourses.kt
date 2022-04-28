package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoursesLayoutBinding


class AdapterPopularCourses : BaseAdapter<AdapterCoursesLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_courses_layout

    override fun getItemCount() = 10

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

    }
}