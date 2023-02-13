package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAssessmentOptionBinding
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class AssessmentOptionAdapter(
    private var list: ArrayList<QuizOptionData>,
    private var showRightAns: Boolean = false
) :
    BaseAdapter<AdapterAssessmentOptionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_assessment_option
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAssessmentOptionBinding
        val context = binding.root.context
        val data = list[position]
        binding.ivOption.visibleView(!data.image.isNullOrEmpty())
        binding.ivCorrect.visibleView(data.isSelected == true && data.isCorrectAns == true)
        binding.tvTitle.visibleView(data.image.isNullOrEmpty())
        binding.tvTitle.text = data.option1

        binding.ivOption.loadImage(data.image, R.drawable.ic_default_banner)

        binding.rbChecked.isChecked = data.isSelected == true


        if (data.isSelected == true) {
//            binding.parentCL.background=  if (data.isCorrectAns == true || !showRightAns) context.getDrawable(R.drawable.accent_green_stroked_bg_5)
//            else context.getDrawable(R.drawable.accent_green_stroked_bg_5)


            binding.parentCL.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (data.isCorrectAns == true || !showRightAns) context.getAttrColor(R.attr.accentColor_Green) else context.getAttrColor(
                            R.attr.accentColor_Red
                        )
                    )
                )
            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.rbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.parentCL.backgroundTintList = ColorStateList.valueOf(
                context.getAttrResource(R.attr.viewBackgroundColor)
            )
            binding.tvTitle.changeTextColor(ThemeConstants.TYPE_HEADING)
            binding.rbChecked.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.hint_color_929292))

        }
    }
}