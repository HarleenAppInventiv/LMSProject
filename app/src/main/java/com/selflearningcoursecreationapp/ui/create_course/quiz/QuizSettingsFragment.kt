package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentQuizSettingsBinding
import com.selflearningcoursecreationapp.extensions.getQuantityString
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
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
    private var baseQuizMandatory = -1
    private var baseFreezeContent = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.viewModel = viewModel
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
//            if (!bundleArgs?.courseData?.passingCriteria.isNullOrZero() && bundleArgs?.courseData?.passingCriteria != viewModel.quizSettings.value?.passingCriteria && viewModel.isQuiz) {
//                CommonAlertDialog.builder(baseActivity)
//                    .title(baseActivity.getString(R.string.caution))
//                    .description(baseActivity.getString(R.string.update_passing_criteria_desc_text))
//                    .positiveBtnText(baseActivity.getString(R.string.continue_text))
//                    .icon(R.drawable.ic_alert)
//                    .negativeBtnText(baseActivity.getString(R.string.cancel))
//                    .getCallback {
//                        if (it) {
//                            viewModel.saveSettings()
//                        }
//                    }.build()
//            } else {
            viewModel.saveSettings()
//            }
        }
    }

    private fun getBundleData() {
        arguments?.let {
            bundleArgs = QuizSettingsFragmentArgs.fromBundle(it)
            val quizData = bundleArgs?.quizData
            viewModel.isQuiz = bundleArgs?.isQuiz ?: true

            if (viewModel.isQuiz) {
                baseFreezeContent =
                    if (bundleArgs?.courseData?.freezeContent == null) -1 else if (bundleArgs?.courseData?.freezeContent == true) 2 else 1
                baseQuizMandatory =
                    if (bundleArgs?.courseData?.makeQuizMandatory == null) -1 else if (bundleArgs?.courseData?.makeQuizMandatory == true) 2 else 1
                basePassingCriteria = bundleArgs?.courseData?.passingCriteria ?: 0
                quizData?.freezeContent = bundleArgs?.courseData?.freezeContent
                quizData?.makeQuizMandatory = bundleArgs?.courseData?.makeQuizMandatory
                quizData?.passingCriteria = bundleArgs?.courseData?.passingCriteria
            } else {
                quizData?.makeQuizMandatory = quizData?.makeAssessmentMandatory
                quizData?.quizName = quizData?.assessmentName
            }
            viewModel.quizSettings.value = quizData
            binding.swMandatory.isChecked = quizData?.makeQuizMandatory ?: false
            binding.swFreeze.isChecked = quizData?.freezeContent ?: false
            if (viewModel.isQuiz && !bundleArgs?.childPosition.isNullOrNegative()) {
                baseActivity.setToolbar(baseActivity.getString(R.string.update_quiz))

            } else if (!viewModel.isQuiz) {
                if (!bundleArgs?.courseData?.assessmentPassingCriteria.isNullOrZero()) {
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_assessment))

                } else {
                    baseActivity.setToolbar(baseActivity.getString(R.string.add_assessment))

                }
            }

            viewModel.quizSettings.value?.let { settings ->


                settings.totalQues = quizData?.list?.size ?: 0

                if (!settings.totalAssesmentTime.isNullOrZero()) {
                    binding.sbTime.progress =
                        TimeUnit.MILLISECONDS.toMinutes(settings.totalAssesmentTime!!).toInt()
                }
                if (!settings.passingCriteria.isNullOrZero()) {
                    binding.sbPass.progress = settings.passingCriteria!!
                }
            }
            //            viewModel.quizSettings.value?.apply {
            //                courseId = quizData?.courseId
            //                lectureId = quizData?.lectureId
            //                quizId = quizData?.quizId
            //                sectionId = quizData?.sectionId
            //                totalQues = quizData?.list?.size ?: 0
            //            }

        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.sw_freeze -> {
                val selected = if (p1) 2 else 1
                if (!baseFreezeContent.isNullOrNegative() && baseFreezeContent == 2 && selected != baseFreezeContent) {
                    CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.caution))
                        .description(baseActivity.getString(R.string.update_freeze_content_desc_text))
                        .positiveBtnText(baseActivity.getString(R.string.continue_text))
                        .icon(R.drawable.ic_alert)
                        .negativeBtnText(baseActivity.getString(R.string.cancel))
                        .getCallback {
                            if (it) {
                                baseFreezeContent = -1
                                viewModel.quizSettings.value?.freezeContent = p1

                            } else {
                                binding.swFreeze.isChecked = baseFreezeContent == 2
                                viewModel.quizSettings.value?.freezeContent = baseFreezeContent == 2

                            }
                        }.build()
                } else {
                    viewModel.quizSettings.value?.freezeContent = p1
                }

            }
            R.id.sw_mandatory -> {
                val selected = if (p1) 2 else 1

                if (!baseQuizMandatory.isNullOrNegative() && baseQuizMandatory == 2 && baseQuizMandatory != selected) {
                    CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.caution))
                        .description(baseActivity.getString(R.string.update_quiz_mandatory_desc_text))
                        .positiveBtnText(baseActivity.getString(R.string.continue_text))
                        .icon(R.drawable.ic_alert)
                        .negativeBtnText(baseActivity.getString(R.string.cancel))
                        .getCallback {
                            if (it) {
                                baseQuizMandatory = -1
                                viewModel.quizSettings.value?.makeQuizMandatory = p1

                            } else {
                                binding.swMandatory.isChecked = baseQuizMandatory == 2

                                viewModel.quizSettings.value?.makeQuizMandatory =
                                    baseQuizMandatory == 2

                            }
                        }.build()
                } else {
                    viewModel.quizSettings.value?.makeQuizMandatory = p1
                }

            }
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        when (p0?.id) {
            R.id.sb_pass -> {
                if (!basePassingCriteria.isNullOrZero() && basePassingCriteria != p1) {
                    CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.caution))
                        .description(baseActivity.getString(R.string.update_passing_criteria_desc_text))
                        .positiveBtnText(baseActivity.getString(R.string.continue_text))
                        .icon(R.drawable.ic_alert)
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
            }
            R.id.sb_time -> {
                viewModel.quizSettings.value?.totalAssesmentTime =
                    TimeUnit.MINUTES.toMillis(p1.toLong())
                binding.tvTimeValue.visibleView(p1 != 0)
                val count: String =
                    baseActivity.getQuantityString(
                        R.plurals.min_quantity,
                        p1
                    )
                binding.tvTimeValue.text = count

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


                        bundleArgs?.courseData?.freezeContent = settings.freezeContent
                        bundleArgs?.courseData?.makeQuizMandatory =
                            settings.makeQuizMandatory
                        bundleArgs?.courseData?.passingCriteria =
                            settings.passingCriteria

                        if (!bundleArgs?.childPosition.isNullOrNegative()) {
                            bundleArgs?.sectionData?.get(
                                bundleArgs?.adapterPosition ?: 0
                            )?.lessonList?.set(bundleArgs?.childPosition!!, resource)

                        } else {

                            bundleArgs?.sectionData?.get(
                                bundleArgs?.adapterPosition ?: 0
                            )?.lessonList?.set(
                                bundleArgs?.sectionData?.get(
                                    bundleArgs?.adapterPosition ?: 0
                                )?.lessonList!!.size - 1, resource
                            )

//                            bundleArgs?.sectionData?.get(
//                                bundleArgs?.adapterPosition ?: 0
//                            )?.lessonList?.add(resource)

                        }


                    }
                }


//                    requireActivity().supportFragmentManager.setFragmentResult(
//                        "response",
//                        bundleOf("value" to value.resource)
//                    )
                findNavController().popBackStack(R.id.addCourseBaseFragment, false)
            }

            ApiEndPoints.API_ADD_ASSESSMENT_SAVE -> {
                showToastShort((value as BaseResponse<ChildModel>).message)

                (value as BaseResponse<ChildModel>).resource?.let { resource ->

                    viewModel.quizSettings.value?.let { settings ->


                        bundleArgs?.courseData?.assessmentFreezeContent = settings.freezeContent
                        bundleArgs?.courseData?.assessmentMandatory = settings.makeQuizMandatory
                        bundleArgs?.courseData?.assessmentPassingCriteria = settings.passingCriteria
                        bundleArgs?.courseData?.assessmentName = settings.quizName


                    }
                }


//                    requireActivity().supportFragmentManager.setFragmentResult(
//                        "response",
//                        bundleOf("value" to value.resource)
//                    )
                findNavController().popBackStack(R.id.addCourseBaseFragment, false)
            }
        }
    }
}


