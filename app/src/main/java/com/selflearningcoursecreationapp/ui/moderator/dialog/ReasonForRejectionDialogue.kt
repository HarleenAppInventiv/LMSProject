package com.selflearningcoursecreationapp.ui.moderator.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogReasonRejectionBinding

class ReasonForRejectionDialogue : BaseBottomSheetDialog<DialogReasonRejectionBinding>() {


    override fun getLayoutRes(): Int {

        return R.layout.dialog_reason_rejection
    }

    override fun initUi() {


        binding.ivClose.setOnClickListener {
            dismiss()

        }
        binding.btnSave.setOnClickListener {
            dismiss()

        }

    }


}

