package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentAssessmentReportBinding
import com.selflearningcoursecreationapp.extensions.getTimeInChar
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.AssessmentReportData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizReportFragment : BaseFragment<FragmentAssessmentReportBinding>() {
    private val viewModel: TakeQuizVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_assessment_report
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUi()

    }


    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.attemptId = it.getInt("attemptId").toString()
            viewModel.courseId = it.getInt("courseId").toString()
        }
        viewModel.getAssessmentReport()
//        binding.reportMain.visible()


        binding.cardWrongAnswers.setOnClickListener {
            if (viewModel.assessmentReportLiveData.value?.totalWrongAnswer != 0) {
                goNext(false)
            }
        }
        binding.btOk.setOnClickListener {
            findNavController().navigateUp()
//            (baseActivity as HomeActivity).setSelected(R.id.action_home)
        }
        binding.cardCorrectAnswers.setOnClickListener {
            if (viewModel.assessmentReportLiveData.value?.totalCorrectAnswer != 0) {
                goNext(true)

            }
        }

    }

    private fun goNext(status: Boolean) {
        findNavController().navigate(
            R.id.action_quizReportFragment_to_quizReportDetailFragment,
            bundleOf(
                "courseId" to viewModel.courseId,
                "attemptId" to viewModel.attemptId,
                "assessmentId" to viewModel.assessmentReportLiveData.value?.assessmentId,
                "markedAnswerCorrect" to status
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ASSESSMENT_REPORT -> {
                binding.reportMain.visible()
                (value as BaseResponse<AssessmentReportData>).let {

                    if (it.resource?.assessmentPassed == true) {
                        binding.imgCelebration.loadImage(R.drawable.ic_celebration)
                        binding.tvHurray.text = "Hurray!"
                        binding.tvDescription.text =
                            "you have passed the assessment with ${it.resource?.percentageScored}% marks in your assessment."
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.accent_color_2FBF71
                            )
                        )

                    } else {
                        binding.imgCelebration.loadImage(R.drawable.ic_failed_assessment)
                        binding.tvHurray.text = "Failed!"
                        binding.tvDescription.text =
                            "you have failed the assessment with ${it.resource?.percentageScored}% marks in your assessment."
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.accent_color_fc6d5b
                            )
                        )
                    }

                    binding.tvTotalNumbers.text = it.resource?.noOfQuesDisplayed.toString()
                    binding.tvCorrect.text = it.resource?.totalCorrectAnswer.toString()
                    binding.tvWrong.text = it.resource?.totalWrongAnswer.toString()
                    binding.tvTimer.text =
                        requireActivity().getTimeInChar(it.resource?.totalTimeTaken?.toLong())
                    binding.tvSelectedValue.text =
                        it.resource?.totalPoints.toString() + " " + getString(R.string.points)
                    binding.tvStatus.text =
                        if (it.resource?.assessmentPassed == true) "Pass" else "Fail"

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

}