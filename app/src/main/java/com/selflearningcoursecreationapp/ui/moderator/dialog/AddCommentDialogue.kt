package com.selflearningcoursecreationapp.ui.moderator.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogAddCommentBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isBlank

class AddCommentDialogue(
    private val position: Int,
    private val type: Int,
    private val innerPosition: Int = 0,
    private val commentType: Int = 0
) :
    BaseBottomSheetDialog<DialogAddCommentBinding>() {


    override fun getLayoutRes() = R.layout.dialog_add_comment


    override fun initUi() {
        arguments?.let {
            binding.evEnterDescription.setText(it.getString("comment"))
        }
        binding.ivClose.setOnClickListener {
            dismiss()

        }
        binding.btnSubmit.setOnClickListener {
            if (binding.evEnterDescription.isBlank()) {
                showToastShort(getString(R.string.add_some_before_submit))
            } else {
                onDialogClick(
                    type,
                    position,
                    binding.evEnterDescription.content(),
                    innerPosition, commentType
                )
                dismiss()
            }

        }

    }


}

