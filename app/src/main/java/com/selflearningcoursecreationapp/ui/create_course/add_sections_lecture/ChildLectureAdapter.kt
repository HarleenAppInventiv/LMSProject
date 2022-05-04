package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureLayoutBinding
import com.selflearningcoursecreationapp.utils.Constant

class ChildLectureAdapter() : BaseAdapter<AdapterLectureLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_layout

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        var binding = holder.binding as AdapterLectureLayoutBinding

        binding.imgDeleteLecture.setOnClickListener {

            onItemClick(Constant.CLICK_DELETE, position)

        }
        binding.btUpload.setOnClickListener {
            onItemClick(Constant.CLICK_UPLOAD, position)
        }

    }

    override fun getItemCount() = 1


}