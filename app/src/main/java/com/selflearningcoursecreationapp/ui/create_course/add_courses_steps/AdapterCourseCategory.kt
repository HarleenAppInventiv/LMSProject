package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.utils.Constant

class AdapterCourseCategory(private val list: ArrayList<String>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvRemoveSection.apply {


            text = list[position]
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, list[position])
            }
        }


    }

}