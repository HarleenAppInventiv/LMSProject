package com.selflearningcoursecreationapp.ui.review

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCourseContentBinding

@SuppressLint("NotifyDataSetChanged")

class CourseContentFragment : BaseFragment<FragmentCourseContentBinding>(),
    BaseAdapter.IViewClick {
    private var adapter: AdapterCourseAssessmentList? = null
    override fun getLayoutRes() = R.layout.fragment_course_content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun init() {
        setAdapter()
    }

    private fun setAdapter() {

        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = AdapterCourseAssessmentList()
            binding.recyclerCourseList.adapter = adapter
            adapter?.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        Log.d("main", "beforeTextChanged: ")
    }

    override fun onApiRetry(apiCode: String) {

    }

}