package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAnsOptionsBinding
import com.selflearningcoursecreationapp.extensions.invisible
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import java.util.*

class MarkAnsOptionAdapter(private var list: ArrayList<QuizOptionData>, private var type: Int) :
    BaseAdapter<AdapterAnsOptionsBinding>() {

    override fun getLayoutRes() = R.layout.adapter_ans_options

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAnsOptionsBinding
        val letter = (position + 65).toChar()
        binding.tvTitle.text = letter.toString()
        if (list[position].isSelected == true) {
            binding.ivBg.visible()
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.white
                )
            )
        } else {
            binding.ivBg.invisible()
            binding.tvTitle.changeTextColor(ThemeConstants.TYPE_PRIMARY)
        }


        binding.root.setOnClickListener {

            onItemClick(Constant.CLICK_VIEW, position)
        }
    }

    override fun getItemCount() = list.size


}