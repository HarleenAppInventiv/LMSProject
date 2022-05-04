package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogMoreOptionsBinding
import com.selflearningcoursecreationapp.utils.Constant

class SectionMoreDialog : BaseBottomSheetDialog<BottomDialogMoreOptionsBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.bottom_dialog_more_options
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.tvRemoveSection.setOnClickListener {
            onDialogClick(Constant.CLICK_DELETE)
            dismiss()
        }
        binding.tvAddLecture.setOnClickListener {
            onDialogClick(Constant.CLICK_ADD)
            dismiss()
        }
        binding.tvEditSection.setOnClickListener {
            onDialogClick(Constant.CLICK_EDIT)
            dismiss()
        }
    }


}