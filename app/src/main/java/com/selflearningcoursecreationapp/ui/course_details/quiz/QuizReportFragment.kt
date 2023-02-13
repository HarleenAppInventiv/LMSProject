package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentAssessmentReportBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.AssessmentReportData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.MediaType
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizReportFragment : BaseFragment<FragmentAssessmentReportBinding>() {
    private val viewModel: TakeQuizVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_assessment_report
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callMenu()
        initUi()

    }


    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.attemptId = it.getInt("attemptId").toString()
//            if (arguments?.getInt("courseId").toString() == "0") {
//                viewModel.courseId = it.getString("courseId").toString()
//
//            } else {

            viewModel.courseId = it.getInt("courseId")

//            if (arguments?.getInt("courseId").toString() == "0") {
//                viewModel.courseId = it.getString("courseId").toString()
//
//            } else {
//
//
//            }
//            viewModel.courseId = it.getInt("courseId").toString()

//            }


        }
        if (arguments?.getBoolean("isQuizReport") == true) {
            viewModel.isQuizReport = true
            viewModel.lectureId = arguments?.getInt("lessonId") ?: 0
//            viewModel.sectionId = it.getInt("sectionId")
            viewModel.lessonList =
                arguments?.getParcelableArrayList<ChildModel>("lessonList") ?: java.util.ArrayList()
            viewModel.sectionList =
                arguments?.getParcelableArrayList<SectionModel>("section") ?: java.util.ArrayList()


            viewModel.sectionId =
                viewModel.lessonList.get(arguments?.getInt("innerPosition") ?: 0)?.sectionId ?: 0
        }

        if (arguments?.getInt("modType") == MODTYPE.LEARNER && viewModel.lessonList.size != 1) {
            binding.childView.root.visible()
        }


        if (arguments?.getInt("modType") == MODTYPE.LEARNER && viewModel.lessonList.size != 1) {


            if (((arguments?.getInt("position") ?: -1) == 0 && (arguments?.getInt("innerPosition")
                    ?: -1) == 0)
            ) {

                binding.childView.tvPrevious.alpha = .5f
                binding.childView.tvPrevious.isEnabled = false

            } else {
                binding.childView.tvPrevious.alpha = 1f
                binding.childView.tvPrevious.isEnabled = true

            }


            if (viewModel.lessonList.get(
                    arguments?.getInt("innerPosition") ?: -1
                )?.lectureId != viewModel.lessonList.last()?.lectureId
            ) {

//
//                if (arguments?.getBoolean("freezeContent")==true){
//                    binding.childView.tvNext.alpha = .5f
//                    binding.childView.tvNext.isEnabled = false
//                }else{
//                    binding.childView.tvNext.alpha = 1f
//                    binding.childView.tvNext.isEnabled = true
//                }
//                binding.childView.tvNext.alpha = 1f
//                binding.childView.tvNext.isEnabled = true

            } else {

//                binding.childView.tvNext.alpha = .5f
//                binding.childView.tvNext.isEnabled = false
                binding.childView.tvNext.setText(R.string.finish)


            }


        }

        baseActivity.setToolbar(
            title = if (viewModel.isQuizReport) "${getString(R.string.quiz_report)}" else "${
                getString(
                    R.string.assessment_report
                )
            }"
        )
        viewModel.getAssessmentReport()


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

        binding.childView.tvPrevious.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if ((arguments?.getInt("position")
                        ?: -1) == 0 && (arguments?.getInt("innerPosition")
                        ?: -1) == 0
                )
                    return

                navigationToContent(
                    arguments?.getInt("position") ?: 0,
                    arguments?.getInt("innerPosition")?.minus(1) ?: 0,
                    MODTYPE.LEARNER,
                    true
                )
            }


        })


        binding.childView.tvNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (viewModel.lessonList.get(
                        arguments?.getInt("innerPosition") ?: -1
                    )?.lectureId != viewModel.lessonList.last()?.lectureId
                ) {


                    navigationToContent(
                        arguments?.getInt("position") ?: 0,
                        arguments?.getInt("innerPosition")?.plus(1) ?: 0,
                        MODTYPE.LEARNER,
                        true
                    )


                } else {

                    findNavController().popBackStack(R.id.courseDetailsFragment, false)

                }
            }

        })




        binding.cardTotalNumbers.setOnClickListener { }
//            .contentDescription = buildString {
//            append(viewModel.assessmentReportLiveData.value?.noOfQuesDisplayed)
//            append(getString(R.string.total_no_of_question))
//        }
        binding.cardTotalTime.setOnClickListener { }
//    .contentDescription = buildString {
//            append(viewModel.assessmentReportLiveData.value?.totalTimeTaken)
//            append(getString(R.string.total_time))
//        }

    }

    private fun goNext(status: Boolean) {
        findNavController().navigateTo(
            R.id.action_quizReportFragment_to_quizReportDetailFragment,
            bundleOf(
                "courseId" to viewModel.courseId,
                "attemptId" to viewModel.attemptId,
                "assessmentId" to viewModel.assessmentReportLiveData.value?.assessmentId,
                "markedAnswerCorrect" to status,
                "isQuizReport" to viewModel.isQuizReport
            )
        )
    }

    fun navigationToContent(position: Int, innerPosition: Int, modType: Int, b: Boolean) {


        when (viewModel.lessonList.get(
            innerPosition
        )?.mediaType) {
            MediaType.QUIZ -> {
                if (modType == MODTYPE.LEARNER) {


                    findNavController().navigateTo(
                        R.id.action_global_quizBaseFragment,
                        bundleOf(
                            "quizId" to viewModel.lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to innerPosition,
                            "position" to position,

                            "isQuizReport" to true, "section" to viewModel.sectionList,
                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                            "from" to 0,
                            "isCompleted" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureIsCompleted,
                            "lessonId" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureId, "modType" to MODTYPE.LEARNER,
                            "freezeContent" to arguments?.getBoolean("freezeContent")


                        )
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.action_global_quiZListForModCreatorFragment,
                        bundleOf(
                            "quizId" to viewModel.lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to innerPosition,
                            "position" to position,
                            "section" to viewModel.sectionList,
                            "from" to 0,
                            "modType" to MODTYPE.LEARNER,

                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,

                            )
                    )
                }
            }
            MediaType.DOC -> {


                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "quizId" to viewModel.lessonList.get(
                            innerPosition
                        )?.quizId,
                        "title" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureTitle,
                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to innerPosition,
                        "position" to position,

                        "isQuizReport" to true,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "modType" to MODTYPE.LEARNER,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "from" to 0,
                        "freezeContent" to arguments?.getBoolean("freezeContent")


                    )
                )
            }
            MediaType.TEXT -> {


                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "quizId" to viewModel.lessonList.get(
                            innerPosition
                        )?.quizId,
                        "title" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureTitle,
                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "modType" to MODTYPE.LEARNER,

                        "isQuizReport" to true,
                        "from" to 0,
                        "freezeContent" to arguments?.getBoolean("freezeContent")


//                        "name" to viewModel.sectionList.value?.get(position)?.lessonList?.get(
//                            innerPosition
//                        )?.lectureTitle,
//                        "duration" to viewModel.sectionList.value?.get(position)?.lessonList?.get(
//                            innerPosition
//                        )?.lectureContentDuration?.toInt()
                    )
                )
            }
            MediaType.VIDEO, MediaType.AUDIO -> {
                playVideo(position, innerPosition, modType)
            }


            Constant.CLICK_LESSON -> {
            }
        }
    }

    fun playVideo(position: Int, innerPosition: Int, modType: Int) {
        findNavController().navigateTo(
            R.id.action_global_videoBaseFragment,
            bundleOf(
                "courseId" to viewModel.courseId,
                "status" to viewModel.status,
                "type" to modType,
                "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                "lectureId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId,
                "title" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureTitle,
                "lessonList" to viewModel.lessonList,
                "innerPosition" to innerPosition,
                "position" to position,
                "section" to viewModel.sectionList,
                "modType" to MODTYPE.LEARNER,
                "isCompleted" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureIsCompleted,
                "lessonId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId,
                "from" to 0,

                "isQuizReport" to true,
                "freezeContent" to arguments?.getBoolean("freezeContent")


            )

        )
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ASSESSMENT_REPORT -> {
                binding.reportMain.visible()
                (value as BaseResponse<AssessmentReportData>).let {
                    val isQuizAssessment =
                        if (viewModel.isQuizReport) getString(R.string.quiz_) else getString(
                            R.string.assessment_
                        )
                    if (it.resource?.assessmentPassed == true) {
                        binding.imgCelebration.loadImage(R.drawable.ic_celebration)
                        binding.tvHurray.apply {
                            setOnClickListener { }
                            text = getString(R.string.hurray_exclamatory)
                            contentDescription = text
                        }
                        binding.tvDescription.apply {
                            setOnClickListener { }
                            text =
                                "${
                                    context?.let { it1 ->
                                        String.format(
                                            it1.getString(R.string.you_have_successfully_completed_the),
                                            isQuizAssessment
                                        )
                                    }
                                } ${it.resource?.percentageScored}% ${getString(R.string.mark_small)}"
                            contentDescription = text
                            binding.tvStatus.setColor(R.attr.accentColor_Green)
                        }
                    } else {
                        binding.imgCelebration.loadImage(R.drawable.ic_failed_assessment)
                        binding.tvHurray.apply {
                            text = "Failed!"
                            contentDescription = getString(R.string.failed)
                            setOnClickListener { }
                        }
                        binding.tvDescription.apply {
                            text =
                                "${
                                    context?.let { it1 ->
                                        String.format(
                                            it1.getString(R.string.you_have_failed_the),
                                            isQuizAssessment
                                        )
                                    }
                                } ${it.resource?.percentageScored}% ${getString(R.string.mark_small)}"
                            contentDescription = text
                            binding.tvStatus.setColor(R.attr.accentColor_Red)
                            setOnClickListener { }
                        }
                    }

                    binding.tvTotalNumbers.text = it.resource?.noOfQuesDisplayed.toString()
                    binding.tvCorrect.text = it.resource?.totalCorrectAnswer.toString()
                    binding.tvWrong.text = it.resource?.totalWrongAnswer.toString()
                    binding.tvTimer.text =
                        baseActivity.getTimeInChar(it.resource?.totalTimeTaken?.toLong())
                    binding.tvSelectedValue.apply {
                        text =
                            it.resource?.totalPoints.toString() + " " + getString(R.string.points)
                        setOnClickListener { }
                    }
                    binding.tvStatus.apply {
                        text =
                            if (it.resource?.assessmentPassed == true) getString(R.string.pass) else getString(
                                R.string.fail
                            )
                        setOnClickListener { }
                    }

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    fun onClickBack() {
        if (arguments?.getInt("modType") == MODTYPE.LEARNER) {
            findNavController().popBackStack(R.id.courseDetailsFragment, false)

        } else {
            findNavController().popBackStack()

        }

    }

}