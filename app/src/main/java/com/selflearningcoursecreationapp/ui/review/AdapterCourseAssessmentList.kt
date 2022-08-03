package com.selflearningcoursecreationapp.ui.review

import android.view.View
import androidx.core.view.isVisible
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseAssmntListBinding

class AdapterCourseAssessmentList : BaseAdapter<AdapterCourseAssmntListBinding>() {
    override fun getLayoutRes() = R.layout.adapter_course_assmnt_list

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterCourseAssmntListBinding

        binding.tvExpandDetails.setOnClickListener {
            if (binding.rvCourseContentDetails.isVisible) {
                binding.rvCourseContentDetails.visibility = View.GONE
            } else {
                binding.rvCourseContentDetails.visibility = View.VISIBLE
            }
        }

        binding.rvCourseContentDetails.adapter = AdapterCourseAssListDetail()


    }

    override fun getItemCount() = 5
}