package com.selflearningcoursecreationapp.ui.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterAssignmentCourseBinding

class AdapterAssignmentList : BaseAdapter<AdapterAssignmentCourseBinding>() {
    override fun getLayoutRes() = R.layout.adapter_assignment_course


    override fun getItemCount() = 5
}