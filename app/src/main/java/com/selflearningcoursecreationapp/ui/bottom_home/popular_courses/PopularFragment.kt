package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPopularBinding


class PopularFragment : BaseFragment<FragmentPopularBinding>() {
    lateinit var adapterPopularCourses: AdapterPopularCourses
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    private fun initUI() {
        binding.recyclerCoursesView.apply {
            adapterPopularCourses = AdapterPopularCourses()
            adapter = adapterPopularCourses
        }

    }

    override fun getLayoutRes() = R.layout.fragment_popular


}