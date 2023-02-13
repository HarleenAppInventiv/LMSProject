package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BotttomDialogUploadVdoAdoBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


class UploadDocOptionsDialog : BaseBottomSheetDialog<BotttomDialogUploadVdoAdoBinding>(),
        (String?) -> Unit, HandleClick, (Boolean, Array<String>, Int) -> Unit {

    private var progressDialog: FileProgressDialog? = null
    private val imagePickUtils: ImagePickUtils by inject()
    private var type: Int = 0
    private var courseId: Int = 0
    private var sectionId: Int = 0

    override fun getLayoutRes(): Int {
        return R.layout.botttom_dialog_upload_vdo_ado
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        binding.clickHandler = this
        arguments?.let {
            courseId = it.getInt("courseId")
            sectionId = it.getInt("sectionId")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvRecordScreen.visible()
            binding.screenV.visible()
        } else {
            binding.tvRecordScreen.gone()
            binding.screenV.gone()

        }


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
                type = MediaType.DOC
                PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                    .requestCode(Permission.DOC)
                    .getCallBack(this)
                    .build()
            }
            R.id.tv_record_screen -> {
                PermissionUtilClass.builder(baseActivity)
                    .requestPermissions(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
//                                Manifest.permission.CAMERA,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        } else {
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                    )
                    .getCallBack { b, strings, _ ->
                        if (b) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                findNavController().navigateTo(R.id.action_uploadDocOptionsDialog_to_screeRecordingOptions)
                            }
                        } else {
                            baseActivity.handlePermissionDenied(strings)
                        }
                    }.build()
            }

            R.id.tv_text -> {
                val lessonArgs = LessonArgs(
                    courseId = courseId,
                    type = Constant.CLICK_ADD,
                    sectionId = sectionId
                )

                val action =
                    UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToLessonTextFragment(
                        lessonArgs
                    )
                findNavController().navigateTo(action)
            }
            R.id.tv_quiz -> {
                val lessonArgs = LessonArgs(
                    courseId = courseId,
                    type = Constant.CLICK_ADD,
                    sectionId = sectionId,
                    isQuiz = true
                )

                val action =
                    UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToAddQuizFragment(
                        lessonArgs
                    )
                findNavController().navigateTo(action)
//                onDialogClick(Lecture.CLICK_LESSON_QUIZ)
            }
            R.id.btn_camera -> {
                type = MediaType.VIDEO

                PermissionUtilClass.builder(baseActivity).requestPermissions(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.CAMERA)
                    } else {
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }

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
//        onDialogClick(type, p1 ?: "")
        showLog("UPLOAD_DIALOG", "path >> $p1 ")
//        dismiss()
        when (type) {
            MediaType.DOC -> {
                val lessonArgs = LessonArgs(
                    courseId = courseId,
                    type = Constant.CLICK_ADD,
                    sectionId = sectionId,
                    filePath = p1
                )

                val action =
                    UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToDocLessonFragment(
                        lessonArgs
                    )
                findNavController().navigateTo(action)
//
//        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
//            sendSectionModel = viewModel.getSectionList()
//                ?.get(viewModel.sectionAdapterPosition),
//            lectureId = _lectureId,
//            type = Constant.CLICK_ADD,
//            childPosition = -1,
//            courseId = viewModel.courseData.value?.courseId ?: 0,
//            filePath = p1.toString()
//
//        )
            }


            MediaType.AUDIO -> {

//                if (p1.isFileLimitExceed(1024)) {
//                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
//                } else {
                val lessonArgs = LessonArgs(
                    courseId,
                    sectionId,
                    filePath = p1,
                    type = Constant.CLICK_ADD
                )

                val action =
                    UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToEditAudioFragment(
                        lessonArgs

                    )

                findNavController().navigateTo(action)


//                    viewModel.mediaType = MediaType.AUDIO
//                    mediaFrom = 2
//                    viewModel.sectionChildPosition = -1
//                    viewModel.addLecture()
//                }
            }
//            Lecture.CLICK_LESSON_DOCS -> {
//                viewModel.mediaType = MediaType.DOC
//                viewModel.sectionChildPosition = -1
//                viewModel.addLecture()
//
//            }
//            Lecture.CLICK_LESSON_AUDIO -> {
//                viewModel.mediaType = MediaType.AUDIO
//                mediaFrom = 1
//                viewModel.sectionChildPosition = -1
//                viewModel.addLecture()
//            }

            MediaType.EDITED_VIDEO -> {
                val lessonArgs = LessonArgs(
                    courseId = courseId,
                    type = Constant.CLICK_ADD,
                    sectionId = sectionId,
                    filePath = p1
                )

                val action =
                    UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToVideoLectureFragment(
                        lessonArgs
                    )
                findNavController().navigateTo(action)
            }

            MediaType.VIDEO -> {
                if (!p1.isNullOrEmpty()) {
                    type = MediaType.EDITED_VIDEO
                    try {

                        showFileProgressDialog()
                        CoroutineScope(Dispatchers.IO).launch {
                            val path =
                                Uri.parse(FileUtils.getPath(baseActivity, Uri.parse(p1)))
                                    ?: Uri.parse(
                                        p1
                                    )
                            withContext(Main)
                            {

                                imagePickUtils.editVideo(baseActivity, path)
                                progressDialog?.dismiss()

                            }

                        }
                    } catch (e: Exception) {
                        progressDialog?.dismiss()
                        showToastShort(getString(R.string.some_error_occure_please_try_again))
                    }
                }

//                if (p1.isFileLimitExceed(1024)) {
//                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
//                } else {


//               imagePickUtils.editVideo(baseActivity,p1?:"")

//            }
            }
        }
    }

    private fun showFileProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
        progressDialog = null
        if (progressDialog == null) {
            progressDialog = FileProgressDialog(
                baseActivity,
                baseActivity.getString(R.string.this_may_take_few_minute)
            )

            progressDialog?.let { dialog ->
                dialog.setCancelable(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            progressDialog?.show()
        }
    }

    //permission invoke method
    override fun invoke(reqStatus: Boolean, perms: Array<String>, requestCode: Int) {
        when (reqStatus) {
            true -> {
                when (requestCode) {
                    Permission.DOC -> {
                        imagePickUtils.openDocs(

                            baseActivity,
                            this
                        )

//                        onDialogClick(Lecture.CLICK_LESSON_DOCS)
                    }
                    Permission.CAPTURE_VIDEO -> {
                        imagePickUtils.captureVideo(
                            baseActivity,
                            this
                        )

                    }
                    Permission.GALLERY -> {
                        imagePickUtils.openVideoFile(
                            baseActivity,
                            this
                        )

                    }
                    Permission.RECORD_AUDIO -> {

                        val lessonArgs = LessonArgs(
                            courseId = courseId,
                            sectionId = sectionId,
                            type = Constant.CLICK_ADD
                        )

                        val action =
                            UploadDocOptionsDialogDirections.actionUploadDocOptionsDialogToRecordAudioFragment(
                                lessonArgs,
                                false
                            )
                        findNavController().navigateTo(action)
//                        onDialogClick(Lecture.CLICK_LESSON_AUDIO)

                    }
                    Permission.FILE_MANAGER -> {
                        imagePickUtils.openAudioFile(
                            baseActivity,
                            this
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