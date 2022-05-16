package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BotttomDialogUploadVdoAdoBinding
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.Lecture
import org.koin.android.ext.android.inject

class UploadDocOptionsDialog : BaseBottomSheetDialog<BotttomDialogUploadVdoAdoBinding>(),
        (String?) -> Unit {

    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.botttom_dialog_upload_vdo_ado
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        arguments?.let {
            type = it.getInt("type")
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.clVideo.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_VIDEO)
            dismiss()
        }

        binding.clAudio.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_AUDIO)
            dismiss()

        }
        binding.tvRecordScreen.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_SCREEN_RECORD)
            dismiss()
        }
        binding.tvText.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_TEXT)
            dismiss()

        }
        binding.tvQuiz.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_QUIZ)
            dismiss()
        }
        binding.clDocs.setOnClickListener {
            onDialogClick(Lecture.CLICK_LESSON_DOCS)

//            type = Lecture.CLICK_LESSON_DOCS
//            imagePickUtils.openDocs(
//                baseActivity,
//                this,
//                registry = requireActivity().activityResultRegistry
//            )
//            onDialogClick(Constant.CLICK_LESSON_DOCS)
            dismiss()
        }
    }

    override fun invoke(p1: String?) {
        onDialogClick(type, p1 ?: "")
        dismiss()
    }


}