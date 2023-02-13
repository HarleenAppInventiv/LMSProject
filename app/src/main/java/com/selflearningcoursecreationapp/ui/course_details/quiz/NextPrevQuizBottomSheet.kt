package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.view.ViewGroup
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.BottomSheetRecordingBinding


class NextPrevQuizBottomSheet : BaseDialog<BottomSheetRecordingBinding>() {


    override fun getLayoutRes(): Int {

        return R.layout.bottom_sheet_quiz_next_prev
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun initUi() {

        val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
        val height: Int = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }


}