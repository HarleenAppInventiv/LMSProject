package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.graphics.Typeface
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class AdapterCourseCategory(private val list: ArrayList<CategoryData>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvRemoveSection.apply {
            changeTextColor(if (list[position].isSelected) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_PRIMARY)
            typeface =
                if (list[position].isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT


            text = list[position].name
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, list[position])
            }
        }


    }

}