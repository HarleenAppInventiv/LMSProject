package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep1Binding


class Step1Fragment : BaseFragment<FragmentStep1Binding>() {
//    private val viewModel: AddCourseViewModel by viewModels()
    private val viewModel: AddCourseViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
//        binding.step1 = viewModel

    }

    override fun getLayoutRes() = R.layout.fragment_step1


}