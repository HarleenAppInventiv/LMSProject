package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadImageBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.Permission
import com.selflearningcoursecreationapp.utils.PermissionUtil
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
            PermissionUtil.checkPermissions(
                baseActivity,
                arrayOf(
                    Manifest.permission.CAMERA,
                ),
                Permission.TAKE_PHOTO
            ) {
                if (it) {
                    imagePickUtils.captureImage(
                        baseActivity,
                        this,
                        registry = baseActivity.activityResultRegistry
                    )

                } else {

                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        )
                    ) {
                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))
                    } else {
                        baseActivity.permissionDenied()
                    }
                }
            }


        }

        binding.txtTakeFromGallary.setOnClickListener {
            dismiss()
            PermissionUtil.checkPermissions(
                baseActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                Permission.GALLERY
            ) {
                if (it) {
                    imagePickUtils.openGallery(
                        baseActivity,
                        this,
                        registry = baseActivity.activityResultRegistry
                    )

                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )

                    ) {
                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))

                    } else {
                        baseActivity.permissionDenied()
                    }
                }
            }


        }
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }


}