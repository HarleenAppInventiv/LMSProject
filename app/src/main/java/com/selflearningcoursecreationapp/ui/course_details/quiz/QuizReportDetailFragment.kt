package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentQuizReportDetailBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.QUIZ
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizReportDetailFragment : BaseFragment<FragmentQuizReportDetailBinding>() {
    var markedAnswerCorrect: Boolean = false
    var assessmentId = ""
    private val viewModel: TakeQuizVM by viewModel()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_quiz_report_detail

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            markedAnswerCorrect = it.getBoolean("markedAnswerCorrect")
            viewModel.courseId = it.getString("courseId").toString()
            viewModel.attemptId = it.getString("attemptId").toString()
            assessmentId = it.getInt("assessmentId").toString()
        }

        baseActivity.setToolbar(title = if (markedAnswerCorrect) "Correct Answers" else "Wrong Answers")

        viewModel.getAssessmentReportStatus(markedAnswerCorrect, assessmentId)

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ASSESSMENT_REPORT_STATUS -> {
                (value as BaseResponse<QuizData>).let {
                    it.resource?.list?.forEach { question ->
                        question.optionList.forEach { option ->
                            question.markAnsList?.forEach { markAnswer ->
                                if (markAnswer.answere1 == option.id) {
                                    if (question.questionType == QUIZ.MATCH_COLUMN) {
                                        option.ansId = markAnswer.answere2

                                    } else {
                                        option.isSelected = true
                                        option.isCorrectAns = markedAnswerCorrect
                                    }

                                }
                            }

                        }

                    }
                    binding.rvList.adapter = QuizReportDetailAdapter(
                        it.resource?.list ?: ArrayList(),
                        markedAnswerCorrect
                    )
//                        .apply {
//                        forEach { question ->
//                            if (question.optionList.size > 0) question.optionList.get(0).isSelected =
//                                true
//                            if (question.optionList.size > 0) question.optionList.get(0).isCorrectAns =
//                                markedAnswerCorrect.toBoolean()
//                        }
//                    }, markedAnswerCorrect.toBoolean())
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
    }
}