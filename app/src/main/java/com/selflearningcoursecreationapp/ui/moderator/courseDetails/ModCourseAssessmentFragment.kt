package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModCourseAssessmentBinding


class ModCourseAssessmentFragment : BaseFragment<FragmentModCourseAssessmentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_mod_course_assessment
    }

    override fun onApiRetry(apiCode: String) {
    }

}