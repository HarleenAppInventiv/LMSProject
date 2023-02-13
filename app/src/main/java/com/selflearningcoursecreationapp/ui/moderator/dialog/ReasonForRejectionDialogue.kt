package com.selflearningcoursecreationapp.ui.moderator.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogReasonRejectionBinding
import com.selflearningcoursecreationapp.extensions.content

class ReasonForRejectionDialogue : BaseBottomSheetDialog<DialogReasonRejectionBinding>() {


    override fun getLayoutRes(): Int {

        return R.layout.dialog_reason_rejection
    }

    override fun initUi() {


        binding.ivClose.setOnClickListener {
            dismiss()

        }
        binding.btnSave.setOnClickListener {
            if (binding.evEnterDescription.content().isNullOrEmpty()) {
                showToastShort(baseActivity.getString(R.string.plz_enter_reason_for_rejection))
            } else {
                onDialogClick(binding.evEnterDescription.content())
                dismiss()
            }

        }

    }


}

