package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAssessmentQuesBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.create_course.quiz.MarkColumnOptionAdapter
import com.selflearningcoursecreationapp.utils.QUIZ

class AssessmentDetailAdapter(
    private var list: ArrayList<QuizQuestionData>,
    private var points: Int
) : BaseAdapter<AdapterAssessmentQuesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_assessment_ques
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAssessmentQuesBinding
        val context = binding.root.context
        val data = list[position]
        binding.tvQuesNo.text =
            String.format(context.getString(R.string.question_no), position + 1, list.size)
        binding.tvPoints.text = context.getQuantityString(R.plurals.point_quantity, points)
        binding.tvQuesTitle.text = data.title
        binding.ivHeader.visibleView(!data.questionImage.isNullOrEmpty())
        binding.ivHeader.loadImage(data.questionImage, R.drawable.ic_default_banner)

        when (data.questionType) {
            QUIZ.MATCH_COLUMN -> {
                binding.columnG.visible()

                binding.tvOption1.text = context.getString(R.string.column_1)
                binding.rvOptions.adapter = AssessmentColumnOptionAdapter(data.optionList, true)
                binding.rvOption2.adapter = AssessmentColumnOptionAdapter(data.optionList, false)
                var hashmap = HashMap<String, ArrayList<QuizOptionData>>()
                data.optionList.forEachIndexed { index, quizOptionData ->
                    hashmap.put(
                        String.format(context.getString(R.string.option_no), index.getCharString()),
                        data.optionList.map { it.copy(isSelected = it.id == quizOptionData.ansId) } as ArrayList<QuizOptionData>
                    )
                }
                binding.rvColumnAns.adapter = MarkColumnOptionAdapter(
                    hashmap,
                    data.questionType ?: QUIZ.MATCH_COLUMN
                )
            }
            else -> {
                binding.columnG.gone()
                binding.rvOptions.adapter = AssessmentOptionAdapter(data.optionList)

                binding.tvOption1.text = context.getString(R.string.your_answer_is)

            }
        }
    }
}