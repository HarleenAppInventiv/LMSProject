package com.selflearningcoursecreationapp.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentReviewParentBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class ReviewTabFragment : BaseFragment<FragmentReviewParentBinding>() {

    override fun getLayoutRes() = R.layout.fragment_review_parent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }



    fun init() {
        val nameArray =
            arrayListOf(getString(R.string.course_content), getString(R.string.course_assignment))
        val fragList = arrayListOf<Fragment>(CourseContentFragment(), CourseAssignmentFragment())
        binding.vpCoursesType.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, fragList, this.lifecycle)


        TabLayoutMediator(binding.tlCourseType, binding.vpCoursesType) { tab, position ->
            tab.text = nameArray[position]

        }.attach()
        binding.tlCourseType.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))

    }

    override fun onApiRetry(apiCode: String) {

    }


}