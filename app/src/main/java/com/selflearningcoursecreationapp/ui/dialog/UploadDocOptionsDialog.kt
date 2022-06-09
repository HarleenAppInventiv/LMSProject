package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
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

            }
            R.id.cl_video -> {


                binding.btnCamera.visible()
                binding.btnGallary.visible()
            }
            R.id.cl_docs -> {
                dismiss()

                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .getCallBack { b, strings, i ->
                        if (b) {
                            onDialogClick(Lecture.CLICK_LESSON_DOCS)
                        } else {
                            baseActivity.handlePermissionDenied(strings)
                        }
                    }
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
                PermissionUtilClass.builder(baseActivity).requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    )
                )
                    .getCallBack { b, strings, i ->
                        if (b) {
                            imagePickUtils.openVideoFile(
                                requireActivity(),
                                this,
                                requireActivity().activityResultRegistry
                            )
                        } else {
                            baseActivity.handlePermissionDenied(strings)
                        }
                    }.build()


            }
            R.id.btn_gallary -> {
                type = MEDIA_TYPE.VIDEO

                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .getCallBack { b, strings, i ->
                        if (b) {
                            imagePickUtils.openVideoFile(
                                requireActivity(),
                                this,
                                requireActivity().activityResultRegistry
                            )
                        } else {
                            baseActivity.handlePermissionDenied(strings)
                        }
                    }


            }
            R.id.btn_record_audio -> {
                dismiss()
                type = Lecture.CLICK_LESSON_AUDIO
                PermissionUtilClass.builder(baseActivity)
                    .requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO))
                    .getCallBack { b, strings, i ->
                        if (b) {
                            onDialogClick(Lecture.CLICK_LESSON_AUDIO)
                        } else {
                            baseActivity.handlePermissionDenied(strings)
                        }
                    }


            }
            R.id.btn_from_file_manager -> {
                type = MEDIA_TYPE.AUDIO

                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .getCallBack { b, strings, i ->
                        if (b) {
                            imagePickUtils.openAudioFile(
                                baseActivity,
                                this,
                                registry = requireActivity().activityResultRegistry
                            )
                        } else {
                            baseActivity.handlePermissionDenied(strings)
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



}