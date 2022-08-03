package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.setSpanText
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.ui.authentication.forgotPass.ForgotPassViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AssessmentReportDialog(private val data: QuizReportData) :
    BaseDialog<com.selflearningcoursecreationapp.databinding.AssessmentReportDialogBinding>(),
    BaseDialog.IDialogClick {
    val viewModel: ForgotPassViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.assessment_report_dialog
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        settingDataToUI()

    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun settingDataToUI() {
        var desc = ""
        var title = ""
        var icon = 0
        if (data.quizPassed == true) {
            title = baseActivity.getString(R.string.hurray)
            icon = R.drawable.ic_celebration
        } else {
            title = baseActivity.getString(R.string.failed)
            icon = R.drawable.ic_failed_assessment
        }

        desc = String.format(
            baseActivity.getString(R.string.assessment_passed_desc_text),
            data.percentageScored, "%"
        )
        binding.tvTitle.setText(title)
        binding.ivHeader.setImageResource(icon)

        binding.tvDesc.setText(desc)
        binding.btnConfirmLater.setSpanText(baseActivity.getString(R.string.okay))


        val msg = SpanUtils.with(
            baseActivity,
            baseActivity.getString(R.string.re_take_assessment)
        ).startPos(44).isBold().themeColor()
            .getCallback {
//                if (arguments?.containsKey("RE-TAKE ASSESSMENT") == true) {
                onDialogClick(Constant.CLICK_VIEW)

                dismiss()
//                }
            }.getSpanString()

        binding.tvResend.setSpanString(msg)
        binding.btnConfirmLater.setOnClickListener {
            findNavController().navigateUp()
            dismiss()
        }
    }


}