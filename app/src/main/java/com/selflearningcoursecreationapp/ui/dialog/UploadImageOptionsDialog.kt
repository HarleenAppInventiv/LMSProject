package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.net.Uri
import android.os.Build
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadImageBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.FileUtils
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class UploadImageOptionsDialog : BaseBottomSheetDialog<DialogUploadImageBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0
    private var fixCrop: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.dialog_upload_image
    }

    override fun initUi() {
        arguments?.let {
            type = it.getInt("type")
            if (it.getInt("from") == 10) {
                binding.groupDelete.visible()
            }


        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.txtDelete.setOnClickListener {
            dismiss()
            onDialogClick(10, "")

        }

        binding.txtTakePhoto.setOnClickListener {
            dismiss()
            captureImage(fixCrop)

        }

        binding.txtTakeFromGallary.setOnClickListener {
            dismiss()
            selectImage(fixCrop)


        }

    }


    private fun selectImage(fixCrop: Int) {
        PermissionUtilClass.builder(baseActivity).requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

        ).getCallBack { b, strings, _ ->
            if (b) {
                clickType = 2
                if (type == DialogType.CLICK_BANNER) {
                    imagePickUtils.openBannerGallery(
                        baseActivity,
                        this
                    )
                } else {
                    imagePickUtils.openGallery(
                        baseActivity,
                        this
                    )
                }

            } else {
                baseActivity.handlePermissionDenied(strings)
            }
        }.build()
    }

    private fun captureImage(fixCrop: Int) {

        PermissionUtilClass.builder(baseActivity).requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf()
            } else {
                arrayOf(
                    Manifest.permission.CAMERA
                )
            }

        )
            .getCallBack { b, strings, _ ->
                if (b) {
                    clickType = 1
                    if (type == DialogType.CLICK_BANNER) {
                        imagePickUtils.captureBannerImage(
                            baseActivity,
                            this
                        )
                    } else {
                        imagePickUtils.captureImage(
                            baseActivity,
                            this
                        )
                    }

                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private var croppedResult = false
    private var clickType = 1
    override fun invoke(p1: String?) {
        if (croppedResult) {
            onDialogClick(type, p1 ?: "")
            dismiss()
        } else {
            croppedResult = true

            if (clickType == 2) {
                baseActivity.showProgressBar()
                CoroutineScope(Dispatchers.IO).launch {
                    val path = FileUtils.getPath(baseActivity, Uri.parse(p1))
                    withContext(Dispatchers.Main)
                    {
                        baseActivity.hideProgressBar()

                        imagePickUtils.cropImage(
                            baseActivity,
                            path,
                            type == DialogType.CLICK_BANNER, this@UploadImageOptionsDialog
                        )
                    }
                }
            } else {
                imagePickUtils.cropImage(baseActivity, p1, type == DialogType.CLICK_BANNER, this)
            }
        }

    }


}