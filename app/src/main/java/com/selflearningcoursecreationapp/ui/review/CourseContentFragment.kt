package com.selflearningcoursecreationapp.ui.review

import android.os.Bundle
import android.util.Log
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCourseContentBinding

class CourseContentFragment : BaseFragment<FragmentCourseContentBinding>(),
    BaseAdapter.IViewClick {
    private var adapter: AdapterCourseAssmntList? = null
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
            adapter = AdapterCourseAssmntList()
            binding.recyclerCourseList.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        Log.d("main", "beforeTextChanged: ")
    }

}