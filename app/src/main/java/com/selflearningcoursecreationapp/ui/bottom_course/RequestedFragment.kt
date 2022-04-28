package com.selflearningcoursecreationapp.ui.bottom_course

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRequestedBinding

class RequestedFragment : BaseFragment<FragmentRequestedBinding>() {
    lateinit var adapterRequestCourse: AdapterRequestCourse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        adapterRequestCourse = AdapterRequestCourse()
        binding.recyclerRequestCourses.apply {
            adapter = adapterRequestCourse
        }

    }

    override fun getLayoutRes() = R.layout.fragment_requested


}