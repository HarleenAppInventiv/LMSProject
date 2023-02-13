package com.selflearningcoursecreationapp.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.DialogUploadVideoBinding
import com.selflearningcoursecreationapp.utils.FileUtils
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.Permission
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.inject

class UploadVideoOptionsDialog : BaseBottomSheetDialog<DialogUploadVideoBinding>(),
        (String?) -> Unit {

    private var progressDialog: FileProgressDialog? = null
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
                        this
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private fun captureVideo() {


        PermissionUtilClass.builder(baseActivity)
            .requestPermissions(
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
            .getCallBack { b, strings, _ ->
                if (b) {
                    imagePickUtils.captureVideo(
                        baseActivity,
                        this
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }
            .build()
    }

    override fun invoke(p1: String?) {
        if (type == MediaType.VIDEO) {
            type = MediaType.EDITED_VIDEO

            showFileProgressDialog()
            CoroutineScope(Dispatchers.IO).launch {
                val path =
                    Uri.parse(FileUtils.getPath(baseActivity, Uri.parse(p1))) ?: Uri.parse(p1)
                withContext(Dispatchers.Main)
                {

                    imagePickUtils.editVideo(baseActivity, path)
                    progressDialog?.dismiss()
                }

            }
//            imagePickUtils.editVideo(baseActivity, Uri.parse(p1))
        } else {
            baseActivity.showProgressBar(baseActivity.getString(R.string.processing_file))
            CoroutineScope(IO).launch {
                val path = async { call(p1) }.await()
                updateUI(path, p1)
            }
        }

    }

    private fun showFileProgressDialog() {
        try {

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun call(p1: String?): String {
        return try {

            FileUtils.getPath(
                SelfLearningApplication.applicationContext(),
                Uri.parse(p1)
            ) ?: p1 ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private suspend fun updateUI(p1: String?, path: String?) {
        withContext(Main) {
            onDialogClick(MediaType.VIDEO, p1 ?: "", path ?: "")
            dismiss()
            baseActivity.hideProgressBar()
        }
    }
}