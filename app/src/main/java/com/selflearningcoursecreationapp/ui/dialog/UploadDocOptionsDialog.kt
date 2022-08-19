package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BotttomDialogUploadVdoAdoBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import org.koin.android.ext.android.inject


class UploadDocOptionsDialog : BaseBottomSheetDialog<BotttomDialogUploadVdoAdoBinding>(),
        (String?) -> Unit, HandleClick, (Boolean, Array<String>, Int) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

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
                binding.btnCamera.gone()
                binding.btnGallary.gone()
            }
            R.id.cl_video -> {

                binding.btnRecordAudio.gone()
                binding.btnFromFileManager.gone()
                binding.btnCamera.visible()
                binding.btnGallary.visible()
            }
            R.id.cl_docs -> {
                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .requestCode(Permission.DOC)
                    .getCallBack(this)
                    .build()
                dismiss()
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
                type = MediaType.VIDEO

                PermissionUtilClass.builder(baseActivity).requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    )
                )
                    .requestCode(Permission.CAPTURE_VIDEO)
                    .getCallBack(this)
                    .build()


            }
            R.id.btn_gallary -> {
                type = MediaType.VIDEO

                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .requestCode(Permission.GALLERY)
                    .getCallBack(this)
                    .build()


            }
            R.id.btn_record_audio -> {
                dismiss()
                type = Lecture.CLICK_LESSON_AUDIO
                PermissionUtilClass.builder(baseActivity)
                    .requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO))
                    .requestCode(Permission.RECORD_AUDIO)
                    .getCallBack(this)
                    .build()


            }
            R.id.btn_from_file_manager -> {
                type = MediaType.AUDIO

                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .getCallBack(this)
                    .requestCode(Permission.FILE_MANAGER)
                    .build()


            }
        }
    }


    override fun invoke(
        p1: String?,
    ) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }

    //permission invoke method
    override fun invoke(reqStatus: Boolean, perms: Array<String>, requestCode: Int) {
        when (reqStatus) {
            true -> {
                when (requestCode) {
                    Permission.DOC -> {
                        onDialogClick(Lecture.CLICK_LESSON_DOCS)
                    }
                    Permission.CAPTURE_VIDEO -> {
                        imagePickUtils.captureVideo(
                            requireActivity(),
                            this,
                            requireActivity().activityResultRegistry
                        )

                    }
                    Permission.GALLERY -> {
                        imagePickUtils.openVideoFile(
                            requireActivity(),
                            this,
                            requireActivity().activityResultRegistry
                        )

                    }
                    Permission.RECORD_AUDIO -> {
                        onDialogClick(Lecture.CLICK_LESSON_AUDIO)

                    }
                    Permission.FILE_MANAGER -> {
                        imagePickUtils.openAudioFile(
                            baseActivity,
                            this,
                            registry = requireActivity().activityResultRegistry
                        )
                    }
                }

            }
            else -> {

                baseActivity.handlePermissionDenied(perms)
            }
        }
    }


}