package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAssessmentDetailBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData


class AssessmentDetailFragment : BaseFragment<FragmentAssessmentDetailBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_assessment_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        arguments?.let {
            val quizData = it.getParcelable<QuizData>("quizData")
            baseActivity.setToolbar(quizData?.quizName, backIcon = R.drawable.ic_cross_grey)
            binding.rvAssessment.adapter = AssessmentDetailAdapter(
                quizData?.list ?: ArrayList(),
                quizData?.markOfCorrectAns ?: 0
            )
        }

    }

}