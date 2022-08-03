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
        binding.tvNoData.text = "No complete course yet"
        binding.tvNoDataDesc.text =
            "You do not have any courses in your complete list yet. Enroll and complete your courses to add in the list."
//        binding.tvNoData.setOnClickListener {
//            binding.llNoWishlist.gone()
//            binding.rvCourse.visible()
//            binding.rvCourse.adapter = MyCourseAdapter(Constant.COURSE_COMPLETED)
//
//        }
    }

    override fun onApiRetry(apiCode: String) {

    }

}