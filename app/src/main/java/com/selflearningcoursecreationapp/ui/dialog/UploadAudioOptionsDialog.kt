package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogUploadAudioBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MediaFrom
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
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

        type = MediaType.AUDIO
        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.txtRecordAudio.setOnClickListener {
            dismiss()
            onClickRecord()

        }

        binding.txtTakeFromFile.setOnClickListener {
            dismiss()
            onPickFile()

        }
    }

    private fun onPickFile() {

        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .getCallBack { b, strings, _ ->
                if (b) {
                    imagePickUtils.openAudioFile(
                        baseActivity,
                        this,
                        registry = requireActivity().activityResultRegistry
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()

    }

    private fun onClickRecord() {

        PermissionUtilClass.builder(baseActivity)
            .requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO))
            .getCallBack { b, strings, _ ->
                if (b) {
                    onDialogClick(MediaFrom.RECORDING)
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()

    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }


}