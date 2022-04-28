package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BotttomDialogUploadVdoAdoBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import org.koin.android.ext.android.inject

class UploadDocOptionsDialog : BaseBottomSheetDialog<BotttomDialogUploadVdoAdoBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.botttom_dialog_upload_vdo_ado
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        arguments?.let {
            type = it.getInt("type")
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.tvTakeVideo.setOnClickListener {
            dismiss()
        }

        binding.tvTakeAudio.setOnClickListener {
            dismiss()

        }
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }


}