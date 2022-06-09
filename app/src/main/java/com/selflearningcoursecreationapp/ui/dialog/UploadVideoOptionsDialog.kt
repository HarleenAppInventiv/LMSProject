package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadVideoBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import com.selflearningcoursecreationapp.utils.PermissionUtilClass
import org.koin.android.ext.android.inject

class UploadVideoOptionsDialog : BaseBottomSheetDialog<DialogUploadVideoBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.dialog_upload_video
    }

    @SuppressLint("ResourceType")
    override fun initUi() {


        type = MEDIA_TYPE.VIDEO
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.txtTakeCamera.setOnClickListener {
            dismiss()

            PermissionUtilClass.builder(baseActivity)
                .requestPermissions(arrayOf(Manifest.permission.CAMERA))
                .getCallBack { b, strings, i ->
                    if (b) {
                        imagePickUtils.captureVideo(
                            baseActivity,
                            this,
                            registry = baseActivity.activityResultRegistry
                        )
                    } else {
                        baseActivity.handlePermissionDenied(strings)
                    }
                }
        }

        binding.txtTakeFromGallary.setOnClickListener {
            dismiss()

            PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                .getCallBack { b, strings, i ->
                    if (b) {
                        imagePickUtils.openVideoFile(
                            baseActivity,
                            this,
                            registry = baseActivity.activityResultRegistry
                        )
                    } else {
                        baseActivity.handlePermissionDenied(strings)
                    }
                }


        }
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")

    }


}