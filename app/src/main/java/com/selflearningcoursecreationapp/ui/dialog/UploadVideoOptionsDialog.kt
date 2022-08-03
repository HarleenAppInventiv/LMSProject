package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadVideoBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.Permission
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


        type = MediaType.VIDEO
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.txtTakeCamera.setOnClickListener {
            dismiss()
            captureVideo()
        }

        binding.txtTakeFromGallary.setOnClickListener {
            dismiss()
            pickFromGallery()


        }
    }


    private fun pickFromGallery() {
        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .getCallBack { b, strings, _ ->
                if (b) {
                    imagePickUtils.openVideoFile(
                        baseActivity,
                        this,
                        registry = baseActivity.activityResultRegistry
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private fun captureVideo() {


        PermissionUtilClass.builder(baseActivity)
            .requestPermissions(arrayOf(Manifest.permission.CAMERA))
            .requestCode(Permission.CAPTURE_VIDEO)
            .getCallBack { b, strings, _ ->
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
            .build()
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")

    }


}