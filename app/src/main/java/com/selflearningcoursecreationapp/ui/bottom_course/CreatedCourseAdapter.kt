package com.selflearningcoursecreationapp.ui.bottom_course

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCreatedCourseBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.visible


class CreatedCourseAdapter() :BaseAdapter<AdapterCreatedCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_created_course
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding= holder.binding as AdapterCreatedCourseBinding
        val context= binding.root.context

        when(position)
        {
            0->{
                binding.tvStudents.visible()
                binding.tvPrice.visible()
                binding.btCourse.gone()
                binding.tvState.gone()
                binding.tvAssessment.visible()

            }
            1->{
                binding.tvAssessment.gone()

                binding.tvStudents.gone()
                binding.tvPrice.gone()
                binding.btCourse.visible()
                binding.tvState.text="Pending"
                binding.tvState.setTextColor(ContextCompat.getColor(context,R.color.coin_stroked_color))
                binding.btCourse.text="Edit Course"
            }
            else->{
                binding.tvAssessment.gone()

                binding.tvStudents.gone()
                binding.tvPrice.gone()
                binding.btCourse.visible()
                binding.tvState.text="Rejected"
                binding.tvState.setTextColor(ContextCompat.getColor(context,R.color.google_btn_bg_color_fc6d5b))
                binding.btCourse.text="Update Course"
            }

        }

        binding.tvPrice.setSpanString("Revenue: \$2000",startPos = 9,color = ContextCompat.getColor(binding.root.context,R.color.priceColor))

    }
}