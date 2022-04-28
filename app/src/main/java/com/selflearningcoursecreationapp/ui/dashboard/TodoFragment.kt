package com.selflearningcoursecreationapp.ui.dashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTodoBinding
import com.selflearningcoursecreationapp.ui.bottom_course.MyCourseAdapter
import com.selflearningcoursecreationapp.utils.Constant


class TodoFragment : BaseFragment<FragmentTodoBinding>() {
    lateinit var adapterCoursesList: MyCourseAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        binding.recyclerCourses.apply {
            adapterCoursesList = MyCourseAdapter(Constant.COURSE_IN_PROGRESS)
            adapter = adapterCoursesList
        }
    }

    override fun getLayoutRes() = R.layout.fragment_todo


}