package com.selflearningcoursecreationapp.ui.dashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentInProgressBinding
import com.selflearningcoursecreationapp.ui.bottom_course.MyCourseAdapter


class InProgressFragment : BaseFragment<FragmentInProgressBinding>() {
    private lateinit var adapterCoursesList: MyCourseAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
//        binding.recyclerCourses.apply {
//            adapterCoursesList = MyCourseAdapter(Constant.COURSE_IN_PROGRESS,
//                viewModel.courseLiveData.value!!)
//            adapter = adapterCoursesList
//        }
    }

    override fun getLayoutRes() = R.layout.fragment_in_progress
    override fun onApiRetry(apiCode: String) {

    }

}