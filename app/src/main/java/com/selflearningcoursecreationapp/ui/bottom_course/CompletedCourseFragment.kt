package com.selflearningcoursecreationapp.ui.bottom_course

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseBinding


class CompletedCourseFragment : BaseFragment<FragmentMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.rvCourse.adapter=MyCourseAdapter(true)
    }

}