package com.selflearningcoursecreationapp.ui.bottom_home

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterHomeCourseTypeBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant


class AdapterCourseType(
    private var list: ArrayList<CategoryData>,
    private var isViOn: Boolean = false
) :
    BaseAdapter<AdapterHomeCourseTypeBinding>() {

    override fun getLayoutRes() = R.layout.adapter_home_course_type

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterHomeCourseTypeBinding
        binding.model = list[position];

        binding.tvCourseName.text = list[position].name

        val color = if (isViOn) {
            R.color.ViSecondaryColor

        } else {
            when (position % 4) {
                0 -> {
                    R.color.blue_indicator_color
                }
                1 -> {
                    R.color.color_green
                }
                2 -> {
                    R.color.coin_stroked_color
                }
                3 -> {
                    R.color.purple_700
                }
                else -> {

                    R.color.primaryColor
                }
            }
        }

        binding.tvCourseName.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, color))
        binding.root.setOnClickListener {


            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.executePendingBindings()
    }

    override fun getItemCount() = list.size


}
