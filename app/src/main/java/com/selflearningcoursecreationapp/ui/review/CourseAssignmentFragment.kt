package com.selflearningcoursecreationapp.ui.review

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCourseAssignmentBinding


class CourseAssignmentFragment : BaseFragment<FragmentCourseAssignmentBinding>() {
    override fun getLayoutRes() = R.layout.fragment_course_assignment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    fun initUI() {

    }


}