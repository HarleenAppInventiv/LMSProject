package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadImageBinding
import com.selflearningcoursecreationapp.databinding.DialogViLayoutBinding
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.android.ext.android.inject

class UploadImageOptionsDialog : BaseBottomSheetDialog<DialogUploadImageBinding>(),
        (String?) -> Unit {

    private val imagePickUtils : ImagePickUtils by inject()

    override fun getLayoutRes(): Int {
        return R.layout.dialog_upload_image
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

       binding.imgClose.setOnClickListener() {
            dismiss()
        }

       binding.txtTakePhoto.setOnClickListener() {
         imagePickUtils.captureImage(baseActivity,this,registry = requireActivity().activityResultRegistry)

        }

        binding.txtTakeFromGallary.setOnClickListener() {
            imagePickUtils.openGallery(baseActivity,this,registry = requireActivity().activityResultRegistry)

        }
    }

    override fun invoke(p1: String?) {
        onDialogClick(Constant.CLICK_IMAGE, p1?:"")
        dismiss()
    }


}