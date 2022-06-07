package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BotttomDialogUploadVdoAdoBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.*
import org.koin.android.ext.android.inject


class UploadDocOptionsDialog : BaseBottomSheetDialog<BotttomDialogUploadVdoAdoBinding>(),
        (String?) -> Unit, HandleClick {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0
    private var from: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.botttom_dialog_upload_vdo_ado
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        binding.clickHandler = this


    }


    override fun onHandleClick(vararg items: Any) {
        val view = items[0] as View
        when (view.id) {
            R.id.iv_close -> {
                dismiss()
            }

            R.id.cl_audio -> {
                binding.btnRecordAudio.visible()
                binding.btnFromFileManager.visible()


//                onDialogClick(Lecture.CLICK_LESSON_AUDIO)
            }
            R.id.cl_video -> {


                binding.btnCamera.visible()
                binding.btnGallary.visible()
//                onDialogClick(Lecture.CLICK_LESSON_VIDEO)
            }
            R.id.cl_docs -> {
                dismiss()
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!PermissionUtil.checkPermissions(requireActivity())) {
                        requestPermission()
                    } else {
                        onDialogClick(Lecture.CLICK_LESSON_DOCS)

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
                            onDialogClick(Lecture.CLICK_LESSON_DOCS)

                        } else {
                            if (shouldShowRequestPermissionRationale(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) ||
                                shouldShowRequestPermissionRationale(
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

//            type = Lecture.CLICK_LESSON_DOCS
//            imagePickUtils.openDocs(
//                baseActivity,
//                this,
//                registry = requireActivity().activityResultRegistry
//            )
//            onDialogClick(Constant.CLICK_LESSON_DOCS)
            }
            R.id.tv_record_screen -> {
                dismiss()
                onDialogClick(Lecture.CLICK_LESSON_SCREEN_RECORD)
            }
            R.id.tv_text -> {
                dismiss()
                onDialogClick(Lecture.CLICK_LESSON_TEXT)
            }
            R.id.tv_quiz -> {
                dismiss()
                onDialogClick(Lecture.CLICK_LESSON_QUIZ)
            }
            R.id.btn_camera -> {
                type = MEDIA_TYPE.VIDEO
                PermissionUtil.checkPermissions(
                    baseActivity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    Permission.DOC
                ) {
                    if (it) {
                        imagePickUtils.captureVideo(
                            requireActivity(),
                            this,
                            requireActivity().activityResultRegistry
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
            R.id.btn_gallary -> {

                type = MEDIA_TYPE.VIDEO
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!PermissionUtil.checkPermissions(requireActivity())) {
                        requestPermission()
                    } else {
                        imagePickUtils.openVideoFile(
                            requireActivity(),
                            this,
                            requireActivity().activityResultRegistry
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
                            imagePickUtils.openVideoFile(
                                requireActivity(),
                                this,
                                requireActivity().activityResultRegistry
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
            R.id.btn_record_audio -> {
                dismiss()
                type = Lecture.CLICK_LESSON_AUDIO

//                if (SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!PermissionUtil.checkPermissionss(requireActivity())) {
//                        requestPermission()
//                    } else {
//                        onDialogClick(Lecture.CLICK_LESSON_AUDIO)
//                    }
//                } else {
//                    PermissionUtil.checkPermissions(
//                        baseActivity,
//                        arrayOf(
//                            RECORD_AUDIO
//                        ), Permission.DOC
//                    ) {
//                        if (it) {
//                            onDialogClick(Lecture.CLICK_LESSON_AUDIO)
//                        } else {
//                            if (shouldShowRequestPermissionRationale(RECORD_AUDIO)
////                                || shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
//
//                            ) {
//                                showToastShort(baseActivity.getString(R.string.no_permission_accepted))
//
//                            } else {
//                                baseActivity.permissionDenied()
//                            }
//                        }
//                    }
//                }
                PermissionUtil.checkPermissions(
                    baseActivity,
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                    ),
                    Permission.GALLERY
                ) {
                    if (it) {
                        onDialogClick(Lecture.CLICK_LESSON_AUDIO)
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
            R.id.btn_from_file_manager -> {
                type = MEDIA_TYPE.AUDIO

                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!PermissionUtil.checkPermissions(requireActivity())) {
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
    }


    override fun invoke(
        p1: String?,
    ) {
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