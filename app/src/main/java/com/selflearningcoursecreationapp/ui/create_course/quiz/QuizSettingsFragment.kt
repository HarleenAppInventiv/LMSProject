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
    private var baseQuizMandatoryB = false
    private var baseFreezeContentB = false

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
                setBaseValueData()
                quizData?.freezeContent = bundleArgs?.courseData?.freezeContent
                quizData?.makeQuizMandatory = bundleArgs?.courseData?.makeQuizMandatory
                quizData?.passingCriteria = bundleArgs?.courseData?.passingCriteria
                if (!bundleArgs?.childPosition.isNullOrNegative()) {
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_quiz))

                }
            } else {
                quizData?.makeQuizMandatory = quizData?.makeAssessmentMandatory
                quizData?.quizName = quizData?.assessmentName
                if (!bundleArgs?.courseData?.assessmentPassingCriteria.isNullOrZero()) {
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_assessment))

                } else {
                    baseActivity.setToolbar(baseActivity.getString(R.string.add_assessment))

                }
            }
            viewModel.quizSettings.value = quizData
            binding.swMandatory.isChecked = quizData?.makeQuizMandatory ?: false
            binding.swFreeze.isChecked = quizData?.freezeContent ?: false


            viewModel.quizSettings.value?.let { settings ->
                settings.totalQues = quizData?.list?.size ?: 0
                binding.sbTime.progress =
                    TimeUnit.MILLISECONDS.toMinutes(settings.totalAssesmentTime ?: 0).toInt()
                binding.sbPass.progress = settings.passingCriteria ?: 0

            }
        }
    }

    private fun setBaseValueData() {
        basePassingCriteria = bundleArgs?.courseData?.passingCriteria ?: 0
        baseFreezeContentB = bundleArgs?.courseData?.freezeContent ?: false
        baseQuizMandatoryB = bundleArgs?.courseData?.makeQuizMandatory ?: false
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.sw_freeze -> {
//                val selected = if (p1) 2 else 1
                handleFreezeContent(p1)

            }
            R.id.sw_mandatory -> {

                handleQuizMandatory(p1)

            }
        }
    }

    private fun handleQuizMandatory(p1: Boolean) {
        if (!baseQuizMandatoryB && !p1) {
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.caution))
                .description(baseActivity.getString(R.string.update_quiz_mandatory_desc_text))
                .positiveBtnText(baseActivity.getString(R.string.continue_text))
                .icon(R.drawable.ic_alert)
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

    private fun handleFreezeContent(p1: Boolean) {
        if (baseFreezeContentB && !p1) {
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.caution))
                .description(baseActivity.getString(R.string.update_freeze_content_desc_text))
                .positiveBtnText(baseActivity.getString(R.string.continue_text))
                .icon(R.drawable.ic_alert)
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

                (value as BaseResponse<ChildModel>).resource?.let {

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


