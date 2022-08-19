package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.selflearningcoursecreationapp.databinding.FragmentQuizBaseBinding
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.ui.dialog.AssessmentReportDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizBaseFragment : BaseFragment<FragmentQuizBaseBinding>(), BaseDialog.IDialogClick {
    var adapterQuiz: QuizBaseAdapter? = null
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: TakeQuizVM by viewModel()
    private var alertDialog: AlertDialog? = null
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
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        myLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recylerQuiz.layoutManager = myLinearLayoutManager

        arguments?.let {
            viewModel.quizId = it.getInt("quizId")
            viewModel.courseId = it.getInt("courseId").toString()
            viewModel.type = it.getInt("type")
            binding.tvTitle.text = it.getString("title")

        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getQuizQues()


        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btNext.setOnClickListener {
            viewModel.isAnsSelected()
        }
        binding.btPrevious.setOnClickListener {
            if (viewModel.currentPos > 0) {
                viewModel.currentPos -= 1
                setButtonText()
            }
        }

        observeData()
    }


    private fun observeData() {
        viewModel.quizData.observe(viewLifecycleOwner, Observer {
            it?.let { quizData ->

                val startTime = System.currentTimeMillis()
                viewModel.endTime = startTime.plus(quizData.totalAssessmentTime ?: 0)
                viewModel.timerValue = quizData.totalAssessmentTime
                setTimer()
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
        countDownTimer?.start()
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.endTime.isNullOrZero()) viewModel.timerValue =
            viewModel.endTime?.minus(System.currentTimeMillis())
        setTimer()
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
                    viewModel.createSubmitPayload()

                } else {
                    viewModel.currentPos += 1
                    setButtonText()
                }
            }

            ApiEndPoints.API_SUBMIT_COURSE_QUIZ -> {
                countDownTimer?.cancel()
                (value as BaseResponse<QuizReportData>)?.resource?.let {
                    var desc = ""
                    var title = ""
                    var icon = 0
                    if (it.quizPassed == true) {
                        desc = String.format(
                            baseActivity.getString(R.string.quiz_passed_desc_text),
                            it.percentageScored, "%"
                        )
                        title = baseActivity.getString(R.string.you_passed)
                        icon = R.drawable.ic_celebration
                    } else {
                        desc = String.format(
                            baseActivity.getString(R.string.quiz_failed_desc_text),
                            it.percentageScored, "%"
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
                        .notCancellable()
                        .hideNegativeBtn(true)
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .getCallback {
                            findNavController().navigateUp()
                        }
                        .build()
                }
            }
            ApiEndPoints.API_ASSESSMENT_SUBMIT -> {

                countDownTimer?.cancel()
                (value as BaseResponse<QuizReportData>)?.resource?.let {
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
            binding.btNext.text = baseActivity?.getString(R.string.submit)
            binding.btNext.icon = null

        } else {
            binding.btNext.text = baseActivity.getString(R.string.next)
            binding.btNext.icon = ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_right)
            binding.btNext.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
        }

        binding.btPrevious.setSecondaryBtnDisabled(viewModel.currentPos != 0)
        lifecycleScope.launch {
            delay(200)
            baseActivity.runOnUiThread {
                binding.recylerQuiz.scrollToPosition(viewModel.currentPos)

            }

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
}