package com.selflearningcoursecreationapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.github.dhaval2404.imagepicker.ImagePicker
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.showLog


class ImagePickUtils {

    private var getContent: ActivityResultLauncher<Intent>? = null
    private var imageCallback: ((String?) -> Unit)? = null
    private var mActivity: Activity? = null
    var type = 0

    fun captureImage(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true,
        registry: ActivityResultRegistry,
    ) {
        type = 1
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)

        ImagePicker.with(context).apply {
            cameraOnly()

            if (doCrop) {
                crop()
            }
            if (doCompress) {
                compress(1024)
                maxResultSize(1080, 1080)
            }
            createIntent { intent ->
                getContent?.launch(intent)
            }
        }
    }

    fun openGallery(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true,
        registry: ActivityResultRegistry,
    ) {
        type = 1
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)

        ImagePicker.with(context).apply {
            galleryOnly()
            if (doCrop) {
                crop()
            }
            if (doCompress) {
                compress(1024)
                maxResultSize(1080, 1080)
            }
            createIntent { intent ->
                getContent?.launch(intent)
            }
        }
    }


    fun openVideoFile(
        context: Activity,
        callBack: ((String?) -> Unit),
        registry: ActivityResultRegistry,
    ) {
        type = 4
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            type = "video/*"
        }

        getContent?.launch(intent)
    }

    fun captureVideo(
        context: Activity,
        callBack: ((String?) -> Unit),
        registry: ActivityResultRegistry,
    ) {
        type = 2
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)

        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(context.packageManager) != null) {
            getContent?.launch(takeVideoIntent)
        }
    }

    fun openAudioFile(
        context: Activity,
        callBack: ((String?) -> Unit),
        registry: ActivityResultRegistry,
    ) {
        type = 4
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)
//        if (Build.VERSION.SDK_INT < 19) {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "audio/*"
//            }
//            getContent?.launch(intent)
//
//        } else {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "audio/mpeg"
//            }
//            getContent?.launch(intent)
//
//        }

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "audio/*"
        }
        getContent?.launch(intent)


    }


    fun openDocs(
        context: Activity,
        callBack: ((String?) -> Unit),
        registry: ActivityResultRegistry,
    ) {
        type = 3
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.ms-powerpoint",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"

        )
        mActivity = context
        imageCallback = callBack
        registerForCallback(registry)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openRVersionPicker(mimeTypes)
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            getContent?.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openRVersionPicker(mimeTypes: Array<String>) {
        if (Environment.isExternalStorageManager()) {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            getContent?.launch(intent)
        } else {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", getAppContext().packageName))
                getContent?.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                getContent?.launch(intent)
            }
        }
//
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
//            if (mimeTypes.isNotEmpty()) {
//                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//            }
//        }

        // getContent?.launch(intent)

    }

    private fun registerForCallback(registry: ActivityResultRegistry) {
        getContent = registry.register(
            "file_selection",
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.data!!.also { uri ->
                        when (type) {
                            1 -> {
                                imageCallback?.invoke(uri.path)
                            }
                            2 -> {
                                imageCallback?.invoke(
                                    FileUtils.getDriveFilePath(
                                        uri,
                                        SelfLearningApplication.applicationContext()
                                    )
                                )
                            }
                            3 -> {
                                Log.d(
                                    "varun", "registerForCallback: ${
                                        FileUtils.getDriveFilePath(
                                            uri,
                                            SelfLearningApplication.applicationContext()
                                        )
                                    }"
                                )
                                imageCallback?.invoke(
                                    FileUtils.getDriveFilePath(
                                        uri,
                                        SelfLearningApplication.applicationContext()
                                    )
                                )
                            }
                            4 -> {
                                //                        URIPathHelper.apply {
                                val path =
                                    FileUtils.getPath(
                                        SelfLearningApplication.applicationContext(),
                                        uri
                                    )
                                imageCallback?.invoke(path)
                                //                        }
                            }
                            else -> {
                                imageCallback?.invoke(uri.toString())
                            }
                        }


                        showLog(
                            "IMAGE",
                            "profileUri >>> $uri ...... path >>>  ${uri.path}"
                        )

                    }


                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(mActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
//                    Toast.makeText(mActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}