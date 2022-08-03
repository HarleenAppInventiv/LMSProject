package com.selflearningcoursecreationapp.ui.moderator.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogAddCommentBinding

class AddCommentDialogue : BaseBottomSheetDialog<DialogAddCommentBinding>() {


    override fun getLayoutRes(): Int {

        return R.layout.dialog_add_comment
    }

    override fun initUi() {

        binding.ivClose.setOnClickListener {
            dismiss()

        }

    }


}

