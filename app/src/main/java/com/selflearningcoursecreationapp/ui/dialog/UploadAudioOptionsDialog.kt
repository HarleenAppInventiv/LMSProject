package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadAudioBinding
import com.selflearningcoursecreationapp.utils.*
import org.koin.android.ext.android.inject

class UploadAudioOptionsDialog : BaseBottomSheetDialog<DialogUploadAudioBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.dialog_upload_audio
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

        type = MEDIA_TYPE.AUDIO
        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.txtRecordAudio.setOnClickListener {
            dismiss()
            PermissionUtil.checkPermissions(
                baseActivity,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                ),
                Permission.GALLERY
            ) {
                if (it) {
                    onDialogClick(MEDIA_FROM.RECORDING)
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.RECORD_AUDIO
                        )
                    ) {
                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))

                    } else {
                        baseActivity.permissionDenied()
                    }
                }
            }

        }

        binding.txtTakeFromFile.setOnClickListener {
            dismiss()
//            PermissionUtil.checkPermissions(
//                baseActivity,
//                arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ),
//                Permission.GALLERY
//            ) {
//                if (it) {
//                    imagePickUtils.openAudioFile(
//                        baseActivity,
//                        this,
//                        registry = baseActivity.activityResultRegistry
//                    )
//
//                } else {
//                    if (shouldShowRequestPermissionRationale(
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
//                        shouldShowRequestPermissionRationale(
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    ) {
//                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))
//
//                    } else {
//                        baseActivity.permissionDenied()
//                    }
//                }
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!PermissionUtil.checkPermissionss(requireActivity())) {
                    requestPermission()
                } else {
                    imagePickUtils.openAudioFile(
                        baseActivity,
                        this,
                        registry = requireActivity().activityResultRegistry
                    )
                }

            } else {
                PermissionUtil.checkPermissions(
                    baseActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    Permission.DOC
                ) {
                    if (it) {
                        imagePickUtils.openAudioFile(
                            baseActivity,
                            this,
                            registry = requireActivity().activityResultRegistry
                        )
                    } else {
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) ||
                            shouldShowRequestPermissionRationale(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
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
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }

    private fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(
                String.format(
                    "package:%s",
                    requireContext().getApplicationContext().getPackageName()
                )
            )
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, 100)
        }

    }
}