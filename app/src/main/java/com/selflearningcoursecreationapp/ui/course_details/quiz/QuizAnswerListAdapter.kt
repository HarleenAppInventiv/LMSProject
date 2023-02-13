package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterQuizOptionBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.QUIZ
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class QuizAnswerListAdapter(private val list: ArrayList<QuizOptionData>, private val type: Int) :
    BaseAdapter<AdapterQuizOptionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_quiz_option
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterQuizOptionBinding
        val context = binding.root.context
        val data = list[position]
        binding.rbChecked.isChecked = data.isSelected == true
        binding.cbChecked.isChecked = data.isSelected == true
        binding.ivOption.loadImage(list[position].image, R.drawable.ic_default_banner)
        binding.ivOption.contentDescription = "Option ${position + 1} from list"
        binding.tvTitle.visibleView(list[position].image.isNullOrEmpty())
        binding.ivOption.visibleView(!list[position].image.isNullOrEmpty())
        binding.tvTitle.text = list[position].option1
        binding.rbChecked.visibleView(type != QUIZ.MULTIPLE_CHOICE)
        binding.cbChecked.visibleView(type == QUIZ.MULTIPLE_CHOICE)
        if (data.isSelected == true) {
            binding.parentCL.backgroundTintList =
                ColorStateList.valueOf(ThemeUtils.getAppColor(context))
            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.rbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            binding.cbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.parentCL.backgroundTintList = null
            binding.tvTitle.changeTextColor(ThemeConstants.TYPE_HEADING)
            binding.rbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.hint_color_929292))
            binding.cbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.hint_color_929292))

        }
        binding.rbChecked.setOnClickListener {
            if (type == QUIZ.SINGLE_CHOICE) {
                list.forEach {
                    it.isSelected = false
                }
                list[position].isSelected = true
                notifyDataSetChanged()

            } else {
                list[position].isSelected = !(list[position].isSelected ?: false)
                notifyDataSetChanged()
            }
        }
        binding.cbChecked.setOnClickListener {
            if (type == QUIZ.SINGLE_CHOICE) {
                list.forEach {
                    it.isSelected = false
                }
                list[position].isSelected = true
                notifyDataSetChanged()

            } else {
                list[position].isSelected = !(list[position].isSelected ?: false)
                notifyDataSetChanged()
            }
        }

        binding.root.setOnClickListener {
            if (type == QUIZ.SINGLE_CHOICE) {
                list.forEach {
                    it.isSelected = false
                }
                list[position].isSelected = true
                notifyDataSetChanged()


            } else {
                list[position].isSelected = !(list[position].isSelected ?: false)
                notifyDataSetChanged()
            }
        }

        binding.parentCL.setOnClickListener {
            if (type == QUIZ.SINGLE_CHOICE) {
                list.forEach {
                    it.isSelected = false
                }
                list[position].isSelected = true
                notifyDataSetChanged()


            } else {
                list[position].isSelected = !(list[position].isSelected ?: false)
                notifyDataSetChanged()
            }
        }

//        binding.cvCheck.background = if (list[position].isSelected) ThemeConstants.TYPE_BACKGROUND else ThemeConstants.TYPE_BACKGROUND
    }
}
