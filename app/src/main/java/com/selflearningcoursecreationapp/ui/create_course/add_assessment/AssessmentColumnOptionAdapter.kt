package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAssessmentColumnOptionBinding
import com.selflearningcoursecreationapp.extensions.getCharString
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData

class AssessmentColumnOptionAdapter(
    private var list: ArrayList<QuizOptionData>,
    private var showFirst: Boolean
) : BaseAdapter<AdapterAssessmentColumnOptionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_assessment_column_option
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAssessmentColumnOptionBinding
        val context = binding.root.context
        val data = list[position]
        val option = position.getCharString() + if (showFirst) "1" else "2"
        binding.tvOption.text = option
        binding.tvTitle.text = if (showFirst) data.option1 else data.option2


    }
}