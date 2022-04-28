package com.selflearningcoursecreationapp.ui.profile.bookmark

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseBinding
import com.selflearningcoursecreationapp.ui.bottom_course.MyCourseAdapter
import com.selflearningcoursecreationapp.utils.Constant


class BookmarkedCoursesFragment : BaseFragment<FragmentMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    private fun initUi() {
        binding.rvCourse.adapter = MyCourseAdapter(Constant.COURSE_BOOKMARKED)
    }

}