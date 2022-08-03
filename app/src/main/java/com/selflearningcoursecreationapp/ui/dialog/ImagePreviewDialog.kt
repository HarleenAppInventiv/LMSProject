package com.selflearningcoursecreationapp.ui.dialog

import android.view.ViewGroup
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.DialogImagePreviewBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.utils.BundleConst

class ImagePreviewDialog : BaseDialog<DialogImagePreviewBinding>() {
    override fun getLayoutRes(): Int {

        return R.layout.dialog_image_preview

    }

    override fun initUi() {
        loadImage()

    }

    override fun onApiRetry(apiCode: String) {
    }

    private fun loadImage() {

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }


        arguments.let {
            binding.imgFullScreenDialog.loadImage(
                it?.getString(BundleConst.IMAGE),
                R.drawable.ic_default_banner
            )
        }
        binding.imgClose.setOnClickListener {
            dismiss()

        }


    }
}