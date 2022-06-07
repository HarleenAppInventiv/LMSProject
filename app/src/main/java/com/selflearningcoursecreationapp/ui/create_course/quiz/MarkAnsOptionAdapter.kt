package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAnsOptionsBinding
import com.selflearningcoursecreationapp.extensions.getCharString
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import java.util.*

class MarkAnsOptionAdapter(private var list: ArrayList<QuizOptionData>, private var type: Int) :
    BaseAdapter<AdapterAnsOptionsBinding>() {

    override fun getLayoutRes() = R.layout.adapter_ans_options

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAnsOptionsBinding
        binding.tvTitle.text = position.getCharString()
        if (list[position].isSelected == true) {
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.white
                )
            )
            binding.ivBg.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.accent_color_2FBF71
                )
            )
        } else {
            binding.ivBg.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.progress_bg
                )
            )
            binding.tvTitle.changeTextColor(ThemeConstants.TYPE_PRIMARY)
        }


        binding.root.setOnClickListener {

            onItemClick(Constant.CLICK_VIEW, position)
        }
    }

    override fun getItemCount() = list.size


}