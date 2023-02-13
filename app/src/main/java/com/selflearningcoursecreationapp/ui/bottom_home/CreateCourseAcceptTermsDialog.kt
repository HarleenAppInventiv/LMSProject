package com.selflearningcoursecreationapp.ui.bottom_home

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.CreateCourseAcceptTermsDialogBinding
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class CreateCourseAcceptTermsDialog : BaseDialog<CreateCourseAcceptTermsDialogBinding>(),
    BaseDialog.IDialogClick, BaseBottomSheetDialog.IDialogClick {

    override fun getLayoutRes(): Int {
        return R.layout.create_course_accept_terms_dialog
    }

    override fun initUi() {


        setSpanString()

        binding.btnDecline.setOnClickListener {
            dismiss()
        }

        binding.btnAccept.setOnClickListener {
            onDialogClick(Constant.CLICK_ADD)
            dismiss()
        }
    }

    fun setSpanString() {
        val msg =
            baseActivity.getString(R.string.you_are_accepting_terms_of_conditions_for_creator)
        val ss = SpannableString(msg)

        val termsSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                onDialogClick(Constant.CLICK_VIEW)
                dismiss()
            }

            @SuppressLint("ResourceType")
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false

                ds.color = ThemeUtils.getAppColor(baseActivity)
            }
        }

        ss.setSpan(termsSpan, 18, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerms.text = ss
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerms.highlightColor =
            ContextCompat.getColor(baseActivity, android.R.color.transparent)
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_WITHDRAW_REQUEST -> {

            }

        }
    }

    override fun onApiRetry(apiCode: String) {

    }
}