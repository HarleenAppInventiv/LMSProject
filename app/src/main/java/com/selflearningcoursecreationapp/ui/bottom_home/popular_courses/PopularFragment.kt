package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPopularBinding


class PopularFragment : BaseFragment<FragmentPopularBinding>() {
    lateinit var adapterPopularCourses: AdapterPopularCourses
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.recyclerCoursesView.apply {
            adapterPopularCourses = AdapterPopularCourses()
            adapter = adapterPopularCourses
        }

    }

    override fun getLayoutRes() = R.layout.fragment_popular


}