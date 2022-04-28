package com.selflearningcoursecreationapp.ui.bottom_home

import android.graphics.Paint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoursesViewBinding


class HomeCoursesAdapter() : BaseAdapter<AdapterCoursesViewBinding>() {

    override fun getLayoutRes() = R.layout.adapter_courses_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        var binding = holder.binding as AdapterCoursesViewBinding
        binding.appCompatTextView6.setPaintFlags(binding.appCompatTextView6.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        binding.cvPopularCourse.contentDescription =
            "selected course is about ux and web design created by allen wen, ba certified having 9 lessons for about 1 hour 22 min long.This course have 4.8 star rating for about 2000 $."

    }


    override fun getItemCount() = 4


}