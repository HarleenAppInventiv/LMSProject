package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseAssessmentBinding

class CourseAssessmentAdapter : BaseAdapter<AdapterCourseAssessmentBinding>() {
    override fun getLayoutRes() = R.layout.adapter_assignment_course
    override fun getItemCount() = 1
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

    }
}