package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentQuizSettingsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.LectureStatus
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class QuizSettingsFragment : BaseFragment<FragmentQuizSettingsBinding>(),
    CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_quiz_settings
    }

    private val viewModel: QuizSettingVM by viewModel()
    private var bundleArgs: QuizSettingsFragmentArgs? = null
    private var basePassingCriteria = 0
    private var baseQuizMandatoryB = false
    private var baseFreezeContentB = false
    private var isFirstTime = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.viewModel = viewModel
        binding.sbTime.max = 120
        binding.sbPass.setOnSeekBarChangeListener(this)
        binding.sbTime.setOnSeekBarChangeListener(this)
        getBundleData()
        binding.swFreeze.setOnCheckedChangeListener(this)
        binding.swMandatory.setOnCheckedChangeListener(this)

        binding.sbTime.progressDrawable
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.SRC_IN)
        binding.sbPass.progressDrawable
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.SRC_IN)
        binding.sbTime.thumb
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.MULTIPLY)
        binding.sbPass.thumb
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.MULTIPLY)

        binding.btNext.setOnClickListener {
            viewModel.saveSettings()
        }

    }

    private fun getBundleData() {
        arguments?.let {
            bundleArgs = QuizSettingsFragmentArgs.fromBundle(it)
            val quizData = bundleArgs?.quizData
            viewModel.isQuiz = bundleArgs?.lessonArgs?.isQuiz ?: true
            quizData?.courseId = bundleArgs?.lessonArgs?.courseId
            if (viewModel.isQuiz) {
                viewModel.getQuizQuestions(quizData?.quizId)
                if (bundleArgs?.lessonArgs?.type == Constant.CLICK_EDIT) {
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_quiz))
                }
                binding.mandatoryG.gone()
            } else {
                binding.mandatoryG.visible()
                quizData?.makeQuizMandatory = quizData?.makeAssessmentMandatory
                quizData?.quizName = quizData?.assessmentName
                if (!bundleArgs?.lessonArgs?.courseData?.assessmentPassingCriteria.isNullOrZero()) {
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_assessment))
                } else {
                    baseActivity.setToolbar(baseActivity.getString(R.string.add_assessment))
                }
            }
            viewModel.quizSettings.value = quizData
            binding.swMandatory.isChecked = quizData?.makeQuizMandatory ?: false
            binding.swFreeze.isChecked = quizData?.freezeContent ?: false

            viewModel.quizSettings.value?.let { settings ->
                showLog("QUIZ_SETTINGS", "settings set")

                settings.totalQues = quizData?.list?.size ?: 0
                binding.sbTime.progress =
                    TimeUnit.MILLISECONDS.toMinutes(settings.totalAssessmentTime ?: 0).toInt()
                binding.sbPass.progress = settings.passingCriteria ?: 0

            }
        }
    }

    private fun setBaseValueData() {
        basePassingCriteria = bundleArgs?.lessonArgs?.courseData?.passingCriteria ?: 0
        baseFreezeContentB = bundleArgs?.lessonArgs?.courseData?.freezeContent ?: false
        baseQuizMandatoryB = bundleArgs?.lessonArgs?.courseData?.makeQuizMandatory ?: false
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.sw_freeze -> {
                handleFreezeContent(p1)

            }
            R.id.sw_mandatory -> {

                handleQuizMandatory(p1)

            }
        }
    }

    private fun handleQuizMandatory(p1: Boolean) {
        if (!baseQuizMandatoryB && !p1 && viewModel.isQuiz) {
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.caution))
                .description(baseActivity.getString(R.string.update_quiz_mandatory_desc_text))
                .positiveBtnText(baseActivity.getString(R.string.continue_text))
                .icon(R.drawable.ic_alert_title)
                .negativeBtnText(baseActivity.getString(R.string.cancel))
                .getCallback {
                    if (it) {
                        baseQuizMandatoryB = false
                        viewModel.quizSettings.value?.makeQuizMandatory = p1

                    } else {
                        binding.swMandatory.isChecked = true

                        viewModel.quizSettings.value?.makeQuizMandatory = true

                    }
                }.build()
        } else {
            viewModel.quizSettings.value?.makeQuizMandatory = p1
        }
    }

    private var alertDialog: AlertDialog? = null
    private fun handleFreezeContent(p1: Boolean) {
        if (baseFreezeContentB && !p1 && viewModel.isQuiz) {
            alertDialog?.dismiss()
            alertDialog = CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.caution))
                .description(baseActivity.getString(R.string.update_freeze_content_desc_text))
                .positiveBtnText(baseActivity.getString(R.string.continue_text))
                .icon(R.drawable.ic_alert_title)
                .negativeBtnText(baseActivity.getString(R.string.cancel))
                .getCallback {
                    if (it) {
                        baseFreezeContentB = p1
                        viewModel.quizSettings.value?.freezeContent = p1

                    } else {
                        binding.swFreeze.isChecked = true
                        viewModel.quizSettings.value?.freezeContent = true

                    }
                }.build()
        } else {
            viewModel.quizSettings.value?.freezeContent = p1
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        when (p0?.id) {
            R.id.sb_pass -> {
                if (!basePassingCriteria.isNullOrZero() && basePassingCriteria != p1) {
                    alertDialog?.dismiss()
                    alertDialog = CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.caution))
                        .description(baseActivity.getString(R.string.update_passing_criteria_desc_text))
                        .positiveBtnText(baseActivity.getString(R.string.continue_text))
                        .icon(R.drawable.ic_alert_title)
                        .negativeBtnText(baseActivity.getString(R.string.cancel))
                        .getCallback {
                            if (it) {
                                basePassingCriteria = 0
                                viewModel.quizSettings.value?.passingCriteria = p1
                                binding.tvPassValue.visibleView(p1 != 0)
                                binding.tvPassValue.text = "$p1%"

                            } else {
                                binding.sbPass.progress = basePassingCriteria

                            }
                        }.build()
                } else {
                    viewModel.quizSettings.value?.passingCriteria = p1
                    binding.tvPassValue.visibleView(p1 != 0)
                    binding.tvPassValue.text = "$p1%"
                }
                binding.sbPass.contentDescription = "${binding.tvPassValue.text} Passing Criteria"

            }
            R.id.sb_time -> {
                viewModel.quizSettings.value?.totalAssessmentTime =
                    TimeUnit.MINUTES.toMillis(p1.toLong())
                binding.tvTimeValue.visibleView(p1 != 0)
                val count: String =
                    baseActivity.getQuantityString(
                        R.plurals.min_quantity,
                        p1
                    )
                binding.tvTimeValue.text = count
                binding.sbTime.contentDescription = binding.tvTimeValue.text

            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //implementation
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        // implementation

    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_QUIZ_SAVE -> {
                showToastShort((value as BaseResponse<ChildModel>).message)
                (value as BaseResponse<ChildModel>).resource?.let { resource ->

                    viewModel.quizSettings.value?.let { settings ->
                        resource.totalQuizQues = settings.totalQues
                        resource.allAnsMarked = true
                        resource.lectureStatusId = LectureStatus.COMPLETED


                        bundleArgs?.lessonArgs?.courseData?.freezeContent = settings.freezeContent
                        bundleArgs?.lessonArgs?.courseData?.makeQuizMandatory =
                            settings.makeQuizMandatory
                        bundleArgs?.lessonArgs?.courseData?.passingCriteria =
                            settings.passingCriteria

//                        if (!bundleArgs?.childPosition.isNullOrNegative()) {
//                            bundleArgs?.sectionData?.get(
//                                bundleArgs?.adapterPosition ?: 0
//                            )?.lessonList?.set(bundleArgs?.childPosition!!, resource)
//
//                        } else {
//
//                            bundleArgs?.sectionData?.get(
//                                bundleArgs?.adapterPosition ?: 0
//                            )?.lessonList?.set(
//                                bundleArgs?.sectionData?.get(
//                                    bundleArgs?.adapterPosition ?: 0
//                                )?.lessonList!!.size - 1, resource
//                            )
//                        }


                    }
                }

                findNavController().popBackStack(R.id.addCourseBaseFragment, false)
            }

            ApiEndPoints.API_ADD_ASSESSMENT_SAVE -> {
                showToastShort((value as BaseResponse<ChildModel>).message)

                (value as BaseResponse<ChildModel>).resource?.let {

                    viewModel.quizSettings.value?.let { settings ->


                        bundleArgs?.lessonArgs?.courseData?.assessmentFreezeContent =
                            settings.freezeContent
                        bundleArgs?.lessonArgs?.courseData?.assessmentMandatory =
                            settings.makeQuizMandatory
                        bundleArgs?.lessonArgs?.courseData?.assessmentPassingCriteria =
                            settings.passingCriteria
                        bundleArgs?.lessonArgs?.courseData?.assessmentName = settings.quizName


                    }
                }

                findNavController().popBackStack(R.id.addCourseBaseFragment, false)
            }
            ApiEndPoints.API_ADD_QUIZ + "/get" -> {
                (value as? BaseResponse<QuizData>)?.resource?.let {
                    bundleArgs?.lessonArgs?.courseData?.passingCriteria = it.passingCriteria
                    bundleArgs?.lessonArgs?.courseData?.freezeContent = it.freezeContent
                    bundleArgs?.lessonArgs?.courseData?.makeQuizMandatory = it.makeQuizMandatory
                    basePassingCriteria = it.passingCriteria ?: 0
                    baseFreezeContentB = it.freezeContent ?: false
                    baseQuizMandatoryB = it.makeQuizMandatory ?: false
                    showLog("QUIZ_SETTINGS", "api response")
                    viewModel.quizSettings.value?.freezeContent =
                        it.freezeContent
                    viewModel.quizSettings.value?.makeQuizMandatory =
                        it.makeQuizMandatory
                    viewModel.quizSettings.value?.passingCriteria =
                        it.passingCriteria
                    binding.swMandatory.isChecked =
                        it.makeQuizMandatory ?: false
                    binding.swFreeze.isChecked = it.freezeContent ?: false
                    binding.sbPass.progress = it.passingCriteria ?: 0
                }


            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.CO_AUTHOR_ACCESS_DENIED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_rejected_account)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)
                    }
                    .build()
            }
            HTTPCode.CONTENT_DELETED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .apply {

                        description(exception.message ?: "")

                    }
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        findNavController().popBackStack(R.id.addCourseBaseFragment, false)
                    }
                    .build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }

    }
}


