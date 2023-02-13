package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.databinding.FragmentQuizBaseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.dialog.AssessmentReportDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizBaseFragment : BaseFragment<FragmentQuizBaseBinding>(), BaseDialog.IDialogClick {
    var adapterQuiz: QuizBaseAdapter? = null
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: TakeQuizVM by viewModel()
    private var alertDialog: AlertDialog? = null

    private var enableTimer = false
    override fun getLayoutRes() = R.layout.fragment_quiz_base

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quiz_menu, menu)
    }

    private fun initUI() {


        val myLinearLayoutManager = object : LinearLayoutManager(baseActivity) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        myLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recylerQuiz.layoutManager = myLinearLayoutManager

        arguments?.let {

            viewModel.quizId = it.getInt("quizId")
            viewModel.courseId = it.getInt("courseId")
//            if (arguments?.getInt("courseId").toString() == "0") {
//                viewModel.courseId = it.getString("courseId").toString()
//
//            } else {
//
//
//            }
//            viewModel.courseId = it.getInt("courseId").toString()
            viewModel.type = it.getInt("type")
            binding.tvTitle.text = it.getString("title")
            viewModel.isQuizReport = it.getBoolean("isQuizReport")
            viewModel.innerPosition = it.getInt("innerPosition")
            viewModel.position = it.getInt("position")
//            viewModel.sectionId = it.getInt("sectionId")
            viewModel.lectureId = it.getInt("lessonId")

            viewModel.lessonList =
                it.getParcelableArrayList<ChildModel>("lessonList") ?: java.util.ArrayList()
            viewModel.sectionList =
                it.getParcelableArrayList<SectionModel>("section") ?: java.util.ArrayList()


        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getQuizQues()
        if (arguments?.getBoolean("enableTimer") == true) {
            enableTimer = true

        }

        binding.ivBack.setOnClickListener {
            onClickBack()

        }

        if (arguments?.getInt("modType") == MODTYPE.LEARNER && viewModel.lessonList.size != 1) {
            viewModel.sectionId =
                viewModel.lessonList.get(arguments?.getInt("innerPosition") ?: 0)?.sectionId ?: 0

            binding.btNext.isEnabled = false
            binding.btPrevious.isEnabled = false
            if (((arguments?.getInt("position") ?: -1) == 0 && (arguments?.getInt("innerPosition")
                    ?: -1) == 0)
            ) {


                binding.childView.btnView.tvPrevious.alpha = .5f
                binding.childView.btnView.tvPrevious.isEnabled = false

            } else {
                binding.childView.btnView.tvPrevious.alpha = 1f
                binding.childView.btnView.tvPrevious.isEnabled = true

            }



            if (viewModel.lessonList.get(
                    arguments?.getInt("innerPosition") ?: -1
                )?.lectureId != viewModel.lessonList.last()?.lectureId


            ) {
                if (arguments?.getBoolean("freezeContent") == true && arguments?.getBoolean("isCompleted") != true) {
                    binding.childView.btnView.tvNext.alpha = .5f
                    binding.childView.btnView.tvNext.isEnabled = false
                } else {
                    binding.childView.btnView.tvNext.alpha = 1f
                    binding.childView.btnView.tvNext.isEnabled = true
                }


            } else {

//                binding.childView.btnView.tvNext.alpha = .5f
                binding.childView.btnView.tvNext.setText(R.string.finish)


            }


        } else {
            binding.rlChild.gone()
        }



        binding.btNext.setOnClickListener {
            if (viewModel.type != Constant.CLICK_ASSESSMENT_REVIEW) {
                viewModel.isAnsSelected()
            } else {
                viewModel.updateResponseObserver(
                    Resource.Success(
                        viewModel.currentPos + 1 == (viewModel.quizData.value?.list?.size ?: 0),
                        ApiEndPoints.VALID_DATA
                    )
                )
            }
        }

        binding.childView.btnView.tvPrevious.setOnClickListener(object : View.OnClickListener {
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


        binding.childView.btnView.tvNext.setOnClickListener(object : View.OnClickListener {
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

        binding.tvTalkback.setOnClickListener {
            baseActivity.checkAccessibilityService()
        }
        binding.btPrevious.setOnClickListener {
            if (viewModel.currentPos > 0) {
                viewModel.currentPos -= 1
                setButtonText()
            }
        }



        observeData()
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
                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                            "isQuizReport" to true,
                            "position" to position,
                            "section" to viewModel.sectionList,
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
                        "from" to 0,
                        "isQuizReport" to true,
                        "freezeContent" to arguments?.getBoolean("freezeContent")


                    )
                )
            }
            MediaType.TEXT -> {
                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
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
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "lessonId" to viewModel.lessonList.get(innerPosition)?.lectureId,
                        "modType" to MODTYPE.LEARNER,
                        "from" to 0,
                        "isQuizReport" to true,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "freezeContent" to arguments?.getBoolean("freezeContent")

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
                "section" to viewModel.sectionList,

                "title" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureTitle,
                "isCompleted" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureIsCompleted,
                "lessonList" to viewModel.lessonList,
                "innerPosition" to innerPosition,
                "position" to position,
                "section" to viewModel.sectionList,
                "modType" to MODTYPE.LEARNER,
                "isQuizReport" to true,

                "lessonId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId, "from" to 0,
                "freezeContent" to arguments?.getBoolean("freezeContent")


            )

        )
    }

    private fun observeData() {
        viewModel.quizData.observe(viewLifecycleOwner, Observer {
            it?.let { quizData ->
                val startTime = System.currentTimeMillis()
                viewModel.endTime = startTime.plus(quizData.totalAssessmentTime ?: 0)
                viewModel.timerValue = quizData.totalAssessmentTime
                binding.tvTitle.text = quizData.assessmentName
                if (viewModel.type != Constant.CLICK_ASSESSMENT_REVIEW) setTimer()
                setAdapter()
                setButtonText()
            }
        })
    }

    private fun setAdapter() {
        if (viewModel.quizData.value?.list.isNullOrEmpty()) {
            val recyclerViewState = binding.recylerQuiz.layoutManager?.onSaveInstanceState()
            adapterQuiz?.notifyDataSetChanged()
            binding.recylerQuiz.layoutManager?.onRestoreInstanceState(recyclerViewState)
            binding.recylerQuiz.adapter = adapterQuiz
            adapterQuiz = null
        } else {
            adapterQuiz?.notifyDataSetChanged() ?: kotlin.run {
                adapterQuiz = QuizBaseAdapter(
                    viewModel.quizData.value?.list ?: ArrayList(),
                    viewModel.quizData.value?.markOfCorrectAns ?: 0
                )

                val recyclerViewState = binding.recylerQuiz.layoutManager?.onSaveInstanceState()
                adapterQuiz?.notifyDataSetChanged()
                binding.recylerQuiz.layoutManager?.onRestoreInstanceState(recyclerViewState)
                binding.recylerQuiz.adapter = adapterQuiz
            }
        }
    }

    private fun setTimer() {
        countDownTimer?.cancel()
        if (viewModel.timerValue ?: 0 > 0) {

            countDownTimer = object : CountDownTimer(viewModel.timerValue ?: 0, 1000) {
                override fun onTick(p0: Long) {
                    viewModel.timerValue = p0
                    binding.tvTime.text = p0.getTime(baseActivity, true, false)
                }

                override fun onFinish() {
                    showLog("TIMER", "finished")
                    viewModel.createSubmitPayload()
                }

            }

        } else {
            showLog("TIMER", "time finished")
            if (viewModel.timerValue != null) viewModel.createSubmitPayload()
        }
        if (enableTimer) {
            countDownTimer?.start()

        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.endTime.isNullOrZero()) viewModel.timerValue =
            viewModel.endTime?.minus(System.currentTimeMillis())
        if (viewModel.type != Constant.CLICK_ASSESSMENT_REVIEW) setTimer()
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.VALID_DATA -> {
                if (value as Boolean)//submit click
                {
                    if (viewModel.type != Constant.CLICK_ASSESSMENT_REVIEW) {
                        binding.btNext.isEnabled = false
                        viewModel.createSubmitPayload()
                    } else {
                        findNavController().navigateUp()
                    }
                } else {
                    viewModel.currentPos += 1
                    setButtonText()
                }

            }

            ApiEndPoints.API_SUBMIT_COURSE_QUIZ -> {
                binding.btNext.isEnabled = true
                countDownTimer?.cancel()
                (value as BaseResponse<QuizReportData>).resource?.let { quizData ->
                    var desc = ""
                    var title = ""
                    var icon = 0
                    if (quizData.quizPassed == true) {
                        desc = String.format(
                            baseActivity.getString(R.string.quiz_passed_desc_text),
                            quizData.percentageScored, "%"
                        )
                        title = baseActivity.getString(R.string.you_passed)
                        icon = R.drawable.ic_celebration
                    } else {
                        desc = String.format(
                            baseActivity.getString(R.string.quiz_failed_desc_text),
                            quizData.percentageScored, "%"
                        )
                        title = baseActivity.getString(R.string.failed)
                        icon = R.drawable.ic_failed_assessment
                    }
                    if (alertDialog?.isShowing == true) {
                        alertDialog?.dismiss()
                    }
                    alertDialog = CommonAlertDialog.builder(baseActivity).description(
                        desc
                    ).title(title).icon(icon)
                        .notCancellable(false)
                        .hideNegativeBtn(true)
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .getCallback {
//                            findNavController().navigateUp()


                            if (viewModel.isQuizReport) {


                                findNavController().navigateTo(
                                    R.id.action_quizBaseFragment_to_quizReportFragment,
                                    bundleOf(
                                        "attemptId" to quizData.attemptedId,
                                        "isQuizReport" to viewModel.isQuizReport,
                                        "quizId" to viewModel.lessonList.get(
                                            viewModel.innerPosition
                                        )?.quizId,
                                        "title" to viewModel.lessonList.get(
                                            viewModel.innerPosition

                                        )?.lectureTitle,
                                        "courseId" to viewModel.courseId,
                                        "lessonList" to viewModel.lessonList,
                                        "innerPosition" to viewModel.innerPosition,
                                        "position" to arguments?.getInt("position"),
                                        "section" to viewModel.sectionList,
                                        "sectionId" to viewModel.sectionId,
                                        "from" to 0,

                                        "lessonId" to viewModel.lessonList.get(
                                            viewModel.innerPosition
                                        )?.lectureId,
                                        "modType" to MODTYPE.LEARNER
                                    )
                                )
                            } else {
                                findNavController().navigateUp()
                            }


                        }
                        .build()
                }
            }
            ApiEndPoints.API_ASSESSMENT_SUBMIT -> {

                binding.btNext.isEnabled = true
                countDownTimer?.cancel()
                (value as BaseResponse<QuizReportData>).resource?.let {
                    AssessmentReportDialog(it).apply {
                        isCancelable = false
                        setOnDialogClickListener(this@QuizBaseFragment)
                    }
                        .show(childFragmentManager, "")
//                    var desc = ""
//                    var title = ""
//                    var icon = 0
//                    if (it.quizPassed == true) {
//                        desc = String.format(
//                            baseActivity.getString(R.string.assessment_passed_desc_text),
//                            it.percentageScored, "%"
//                        )
//                        title = baseActivity.getString(R.string.you_passed)
//                        icon = R.drawable.ic_celebration
//                    } else {
//                        desc = String.format(
//                            baseActivity.getString(R.string.assessment_failed_desc_text),
//                            it.percentageScored, "%"
//                        )
//                        title = baseActivity.getString(R.string.failed)
//                        icon = R.drawable.ic_failed_assessment
//                    }
//                    if (alertDialog?.isShowing == true) {
//                        alertDialog!!.dismiss()
//                    }
//                    alertDialog = CommonAlertDialog.builder(baseActivity).description(
//                        desc
//                    ).title(title).icon(icon)
//                        .notCancellable()
//                        .hideNegativeBtn(true)
//                        .positiveBtnText(baseActivity.getString(R.string.okay))
//                        .getCallback {
//                            findNavController().navigateUp()
//                        }
//                        .build()
                }


            }


        }

    }


    private fun setButtonText() {
        if (viewModel.currentPos + 1 >= binding.recylerQuiz.adapter?.itemCount ?: 0) {
            binding.btNext.text =
                if (viewModel.type != Constant.CLICK_ASSESSMENT_REVIEW) baseActivity.getString(R.string.submit) else baseActivity.getString(
                    R.string.finish
                )
            binding.btNext.icon = null

        } else {
            binding.btNext.text = baseActivity.getString(R.string.next)
            binding.btNext.icon = ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_right)
            binding.btNext.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
        }

        binding.btPrevious.setSecondaryBtnDisabled(viewModel.currentPos != 0)
        binding.btPrevious.isEnabled = viewModel.currentPos != 0
        lifecycleScope.launch {
            delay(200)
            baseActivity.runOnUiThread {
                binding.recylerQuiz.scrollToPosition(viewModel.currentPos)

            }

        }

        binding.childView.btnQuiz.setOnClickListener {
            enableTimer = true
            binding.rlChild.gone()
            setTimer()

            binding.btNext.isEnabled = true
//            binding.btPrevious.isEnabled = true
        }
    }

    override fun onDialogClick(vararg items: Any) {
        val items = items[0] as Int
        when (items) {
            Constant.CLICK_VIEW -> {

                reset()
                viewModel.getQuizQues()
            }

        }
    }

    private fun reset() {
        countDownTimer?.cancel()
        countDownTimer = null
        viewModel.endTime = null
        viewModel.quizData.value?.list?.clear()
        adapterQuiz?.notifyDataSetChanged()
        binding.recylerQuiz.adapter?.notifyDataSetChanged()
        adapterQuiz = null
        binding.recylerQuiz.adapter = null
        viewModel.currentPos = 0
    }

    fun onClickBack() {
        if (arguments?.getInt("modType") == MODTYPE.LEARNER) {
            findNavController().popBackStack(R.id.courseDetailsFragment, false)

        } else {
            findNavController().popBackStack()

        }

    }
}