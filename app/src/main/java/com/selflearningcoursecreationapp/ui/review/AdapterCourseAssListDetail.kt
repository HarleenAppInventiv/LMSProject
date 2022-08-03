package com.selflearningcoursecreationapp.ui.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterCourseContentDetailBinding

class AdapterCourseAssListDetail : BaseAdapter<AdapterCourseContentDetailBinding>() {
    override fun getLayoutRes() = R.layout.adapter_course_content_detail

    override fun getItemCount() = 1
}