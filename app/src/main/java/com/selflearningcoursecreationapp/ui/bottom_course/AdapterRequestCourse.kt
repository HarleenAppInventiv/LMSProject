package com.selflearningcoursecreationapp.ui.bottom_course

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterRequestCoursesBinding

class AdapterRequestCourse : BaseAdapter<AdapterRequestCoursesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_request_courses
    override fun getItemCount() = 10
}