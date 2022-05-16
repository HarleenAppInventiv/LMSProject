package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentQuizSettingsBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizSettingsFragment : BaseFragment<FragmentQuizSettingsBinding>(),
    CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_quiz_settings
    }

    private val viewModel: QuizSettingVM by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.viewModel = viewModel
        arguments?.let {
            val quizData = it.getParcelable<QuizData>("quizData")

            viewModel.quizSettings.value?.apply {
                courseId = quizData?.courseId
                lectureId = quizData?.lectureId
                quizId = quizData?.quizId
                sectionId = quizData?.sectionId
                totalQues = quizData?.list?.size ?: 0
            }
        }
        binding.swFreeze.setOnCheckedChangeListener(this)
        binding.swMandatory.setOnCheckedChangeListener(this)

        binding.sbPass.setOnSeekBarChangeListener(this)
        binding.sbTime.setOnSeekBarChangeListener(this)
        binding.sbTime.getProgressDrawable()
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.SRC_IN);
        binding.sbPass.getProgressDrawable()
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.SRC_IN);
        binding.sbTime.getThumb()
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.MULTIPLY);
        binding.sbPass.getThumb()
            .setColorFilter(ThemeUtils.getAppColor(baseActivity), PorterDuff.Mode.MULTIPLY);

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.sw_freeze -> {
                viewModel.quizSettings.value?.freezeContent = p1
            }
            R.id.sw_mandatory -> {
                viewModel.quizSettings.value?.makeQuizMandatory = p1

            }
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        when (p0?.id) {
            R.id.sb_pass -> {
                viewModel.quizSettings.value?.passingCriteria = p1
            }
            R.id.sb_time -> {
                viewModel.quizSettings.value?.totalAssesmentTime = p1

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
                (value as BaseResponse<UserProfile>)?.let {
                    requireActivity().supportFragmentManager.setFragmentResult(
                        "response",
                        bundleOf("value" to value.resource)
                    )
                    findNavController().popBackStack(R.id.addCourseBaseFragment, false)
                }
            }
        }

    }
}