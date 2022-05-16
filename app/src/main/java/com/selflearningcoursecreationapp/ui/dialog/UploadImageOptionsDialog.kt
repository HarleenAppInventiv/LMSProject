package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadImageBinding
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import org.koin.android.ext.android.inject

class UploadImageOptionsDialog : BaseBottomSheetDialog<DialogUploadImageBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.dialog_upload_image
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        arguments?.let {
            type = it.getInt("type")
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.txtTakePhoto.setOnClickListener {
            dismiss()
            imagePickUtils.captureImage(
                baseActivity,
                this,
                registry = requireActivity().activityResultRegistry
            )

        }

        binding.txtTakeFromGallary.setOnClickListener {
            dismiss()
            imagePickUtils.openGallery(
                baseActivity,
                this,
                registry = requireActivity().activityResultRegistry
            )

        }
    }

    override fun invoke(p1: String?) {
        showLog("varun", p1.toString())
        onDialogClick(type, p1 ?: "")
        dismiss()
    }


}