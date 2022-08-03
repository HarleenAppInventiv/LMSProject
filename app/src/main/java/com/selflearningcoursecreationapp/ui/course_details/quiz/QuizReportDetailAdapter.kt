package com.selflearningcoursecreationapp.ui.course_details.quiz

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterQuizReportDetailBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentColumnOptionAdapter
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentOptionAdapter
import com.selflearningcoursecreationapp.ui.create_course.quiz.MarkColumnOptionAdapter
import com.selflearningcoursecreationapp.utils.QUIZ
import com.selflearningcoursecreationapp.utils.SpanUtils

class QuizReportDetailAdapter(
    private val list: ArrayList<QuizQuestionData>,
    private val showRightAnswer: Boolean,
) : BaseAdapter<AdapterQuizReportDetailBinding>() {
    override fun getLayoutRes() = R.layout.adapter_quiz_report_detail
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterQuizReportDetailBinding
        val context = binding.root.context
        val data = list[position]

        binding.tvQuesTitle.text = "${position + 1}. ${data.title}"
        binding.ivHeader.visibleView(!data.questionImage.isNullOrEmpty())
        binding.ivHeader.loadImage(data.questionImage, R.drawable.ic_default_banner)


        if (showRightAnswer) {
            binding.tvColumnAns.setSpanString(
                SpanUtils.with(context, "Your answer is CORRECT").startPos(15)
                    .textColor(ContextCompat.getColor(context, R.color.accent_color_2FBF71))
                    .getSpanString()
            )
        } else {
            binding.tvColumnAns.setSpanString(
                SpanUtils.with(context, "Your answer is WRONG").startPos(15)
                    .textColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
                    .getSpanString()
            )

        }
        when (data.questionType) {
            QUIZ.MATCH_COLUMN -> {
                binding.columnG.visible()

                binding.tvOption1.text = context.getString(R.string.column_1)
                binding.rvOptions.adapter = AssessmentColumnOptionAdapter(data.optionList, true)
                binding.rvOption2.adapter = AssessmentColumnOptionAdapter(data.optionList, false)
                val hashmap = HashMap<String, ArrayList<QuizOptionData>>()
                data.optionList.forEachIndexed { index, quizOptionData ->
                    hashmap[String.format(
                        context.getString(R.string.option_no),
                        index.getCharString()
                    )] =
                        data.optionList.map { it.copy(isSelected = it.id == quizOptionData.ansId) } as ArrayList<QuizOptionData>
                }
                binding.rvColumnAns.adapter = MarkColumnOptionAdapter(
                    hashmap,
                    data.questionType ?: QUIZ.MATCH_COLUMN
                )
            }
            else -> {
                binding.columnG.gone()
                binding.rvOptions.adapter =
                    AssessmentOptionAdapter(data.optionList, true)

                binding.tvOption1.text = context.getString(R.string.your_answer_is)

            }
        }
    }
}