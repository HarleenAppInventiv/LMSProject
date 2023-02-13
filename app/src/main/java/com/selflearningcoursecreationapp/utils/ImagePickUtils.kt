package com.selflearningcoursecreationapp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.ui.create_course.CustomCameraActivity
import com.yalantis.ucrop.UCrop
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.backend.model.state.TrimSettings
import ly.img.android.pesdk.ui.activity.VideoEditorBuilder
import java.io.File
import java.util.concurrent.TimeUnit


class ImagePickUtils {

    private var openDoc: ActivityResultLauncher<Array<String>>? = null
    private var getContent: ActivityResultLauncher<Intent>? = null
    private var imageCallback: ((String?) -> Unit)? = null
    private var captureImageCallback: ((Bitmap?) -> Unit)? = null
    private var mActivity: Activity? = null
    private var isBanner: Boolean? = null
    var type = 0
    val EDITOR_REQUEST_CODE = 0x42

    fun captureImage(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true
    ) {
        type = RequestCode.CAPTURE_IMAGE
        mActivity = context
        imageCallback = callBack
        isBanner = false
        context.startActivityForResult(Intent(context, CustomCameraActivity::class.java), type)
//        ImagePicker.with(context).apply {
//            cameraOnly()
//
//            if (doCrop) {
//                crop()
//            }
//            if (doCompress) {
//                compress(1024)
//                maxResultSize(1080, 1080)
//            }
//            createIntent { intent ->
////                getContent?.launch(intent)
//                context.startActivityForResult(intent, type)
//
//            }
//        }
    }


    fun captureBannerImage(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true
    ) {
        type = RequestCode.CAPTURE_BANNER_IMAGE
        mActivity = context
        imageCallback = callBack

        context.startActivityForResult(Intent(context, CustomCameraActivity::class.java), type)

//        ImagePicker.with(context).apply {
//            cameraOnly()
//            if (doCrop) {
//                crop(16f, 9f)
//            }
//            if (doCompress) {
//                compress(1024)
//                maxResultSize(1080, 1080)
//            }
//            createIntent { intent ->
////                getContent?.launch(intent)
//                context.startActivityForResult(intent, type)
//
//            }
//        }
    }

    fun openGallery(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true
    ) {
        type = RequestCode.PICK_IMAGE
        mActivity = context
        imageCallback = callBack

        val i = Intent()
        i.type = "image/*"
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        i.action = Intent.ACTION_GET_CONTENT
        try {
            mActivity?.startActivityForResult(i, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                mActivity,
                "Some error occurred. Please try again",
                Toast.LENGTH_SHORT
            ).show()
        }

//        ImagePicker.with(context).apply {
//            galleryOnly()
//            if (doCrop) {
//                crop()
//            }
//            if (doCompress) {
//                compress(1024)
//                maxResultSize(1080, 1080)
//            }
//            createIntent { intent ->
////                getContent?.launch(intent)
//                context.startActivityForResult(intent, type)
//            }
//        }
    }

    fun openBannerGallery(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true
    ) {
        type = RequestCode.PICK_BANNER_IMAGE
        mActivity = context
        imageCallback = callBack
        val i = Intent()
        i.type = "image/*"
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        i.action = Intent.ACTION_GET_CONTENT

        try {
            mActivity?.startActivityForResult(i, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                mActivity,
                "Some error occurred. Please try again",
                Toast.LENGTH_SHORT
            ).show()
        }
//        ImagePicker.with(context).apply {
//            galleryOnly()
//            if (doCrop) {
//                crop(16f, 9f)
//            }
//            if (doCompress) {
//                compress(1024)
//                maxResultSize(1080, 1080)
//            }
//            createIntent { intent ->
////                getContent?.launch(intent)
//                context.startActivityForResult(intent, type)
//            }
//        }
    }


    fun openVideoFile(
        context: Activity,
        callBack: ((String?) -> Unit)
    ) {
        type = RequestCode.PICK_VIDEO
        mActivity = context
        imageCallback = callBack
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            type = "video/*"
//            val mimeTypes = arrayOf(
//                "video/webm",
//                "video/x-msvideo",
//                "video/quicktime",
//                "video/mp4",
//                "video/x-matroska",
//                "video/x-ms-wmv",
//                "video/avi",
//                "video/x-flv",
//                "AVCHD-video/mp2t",
//                "AVCHD- video/mp2t",
//            )
//            if (mimeTypes.isNotEmpty()) {
//                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//            }
        }

//        getContent?.launch(intent)
        try {

            context.startActivityForResult(intent, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Some error occurred. Please try again", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun captureVideo(
        context: Activity,
        callBack: ((String?) -> Unit)
    ) {
        type = RequestCode.CAPTURE_VIDEO
        mActivity = context
        imageCallback = callBack

        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(context.packageManager) != null) {
//            getContent?.launch(takeVideoIntent)

            try {

                context.startActivityForResult(takeVideoIntent, type)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Some error occurred. Please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    fun openAudioFile(
        context: Activity,
        callBack: ((String?) -> Unit)
    ) {
        type = RequestCode.PICK_AUDIO
        mActivity = context
        imageCallback = callBack
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
            type = "audio/mp3"
            val mimeTypes = arrayOf(
                "audio/mp3",
                "audio/aac",
                "audio/m4a",
                "audio/flac",
                "audio/mpeg",
                "audio/x-flac",
                "audio/x-dsd",
                "audio/dsd",
                "audio/aac-adts"
            )
            if (mimeTypes.isNotEmpty()) {
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        }
//        getContent?.launch(intent)
        try {

            context.startActivityForResult(intent, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Some error occurred. Please try again", Toast.LENGTH_SHORT)
                .show()
        }
//       {"audio/mpeg",".mp3"},
//            {"audio/aac",".aac"},
//            {"audio/x-flac",".flac"},
//            {"audio/x-dsd",".dsd" },
//            {"audio/dsd",".dsd"},
//            {"audio/aac-adts",".aac"}
    }


    fun openDocs(
        context: Activity,
        callBack: ((String?) -> Unit),
        onlyPdf: Boolean? = false,
    ) {


        var mimeTypes = arrayOf<String>()
        if (onlyPdf == true) {
            mimeTypes = arrayOf(
                "application/pdf"
            )
            type = RequestCode.PICK_PDF_DOCUMENT
        } else {
            type = RequestCode.PICK_DOCUMENT

            mimeTypes = arrayOf(
                "application/pdf",
                "application/msword",
                "application/doc",
                "application/ms-doc",
                "application/mspowerpoint",
                "application/powerpoint",
                "application/vnd.ms-powerpoint",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
        }
        mActivity = context
        imageCallback = callBack
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            openRVersionPicker(mimeTypes)
//        } else {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        try {

            context.startActivityForResult(intent, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Some error occurred. Please try again", Toast.LENGTH_SHORT)
                .show()
        }
//        openDoc?.launch(mimeTypes)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openRVersionPicker(mimeTypes: Array<String>) {
        if (Environment.isExternalStorageManager()) {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "application/pdf"
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


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            when (resultCode) {
                Activity.RESULT_OK -> {

//                    data?.data?.also { uri ->
                    val uri = data?.data
                    when (requestCode) {
                        UCrop.REQUEST_CROP -> {
                            var resultUri = data?.let { UCrop.getOutput(it) }
                            imageCallback?.invoke(resultUri?.path)
                        }
//
//
//                        121 -> {
//                            imageCallback?.invoke(uri.toString())
//                        }
                        RequestCode.CAPTURE_IMAGE -> {
//                            if (mImageURI.isNull()) {
//                                val bitmap = data?.extras?.get("data") as Bitmap?
//                                captureImageCallback?.invoke(bitmap)
////                                    imageCallback?.invoke(uri.toString())
//                            } else {
                            Log.d("ImagePickeUtil", "onActivityResult: ${uri}")
                            imageCallback?.invoke(uri?.path)
//                            val path = Uri.fromFile(File((uri?.path)))
//                            mActivity?.let {
//                                val ucrop = UCrop.of(path, path)
//                                    .withMaxResultSize(1080, 1080)
//                                ucrop.start(it)
//                            }

//                            }
//
//                                imageCallback?.invoke(uri.path)
                        }
                        RequestCode.CAPTURE_BANNER_IMAGE -> {
//                            if (mImageURI.isNull()) {
//                                val bitmap = data?.extras?.get("data") as Bitmap?
//                                captureImageCallback?.invoke(bitmap)
////                                    imageCallback?.invoke(uri.toString())
//                            } else {
                            Log.d("ImagePickeUtil2", "onActivityResult2: ${uri}")

                            imageCallback?.invoke(uri?.path)
//                            val path = Uri.fromFile(File((uri?.path)))
//                            mActivity?.let {
//                                val ucrop = UCrop.of(path, path)
//                                    .withMaxResultSize(1080, 1080)
//
//                                    ucrop.withAspectRatio(16f, 9f)
//
//                                ucrop.start(it)
//                            }

//                            }
//
//                                imageCallback?.invoke(uri.path)
                        }
                        RequestCode.CAPTURE_VIDEO, RequestCode.PICK_VIDEO -> {

//                            editVideo(mActivity, uri.toString())
//                            imageCallback?.invoke(uri.toString())

//                            if (uri != null && mActivity!=null) {
//                                editVideo(mActivity, uri)
//                            }else{
                            imageCallback?.invoke(uri.toString())

//                            }

                        }
                        RequestCode.PICK_DOCUMENT, RequestCode.PICK_PDF_DOCUMENT, RequestCode.PICK_AUDIO -> {
                            imageCallback?.invoke(uri.toString())

                        }
//                        4 -> {
////                            editVideo(mActivity, uri.toString())
//
//                            if (uri != null) {
//                                editVideo(mActivity, uri)
//                            }
////                            imageCallback?.invoke(uri.toString())
//                        }
//                        5 -> {
//                            imageCallback?.invoke(uri.toString())
//                        }
//                        6 -> {
//                            imageCallback?.invoke(
//                                uri.toString()
//                            )
//                        }
//                        7 -> {
//                            imageCallback?.invoke(uri.toString())
//                        }

                        RequestCode.PICK_IMAGE -> {
                            imageCallback?.invoke(uri.toString())

//                            val path = Uri.fromFile(File((uri?.path)))
//                            mActivity?.let {
//                                val ucrop = UCrop.of(path, path)
//                                    .withMaxResultSize(1080, 1080)
//                                ucrop.start(it)
//                            }
                        }
                        RequestCode.PICK_BANNER_IMAGE -> {
                            imageCallback?.invoke(uri?.toString())
//                            val path = Uri.fromFile(File((uri?.path)))
//                            mActivity?.let {
//                                val ucrop = UCrop.of(path, path)
//                                    .withMaxResultSize(1080, 1080)
//                                ucrop.withAspectRatio(16f, 9f)
//                                ucrop.start(it)
//                            }
                        }
                        EDITOR_REQUEST_CODE -> {
                            val result = data?.let { EditorSDKResult(it) }
                            when (result?.resultStatus) {
                                EditorSDKResult.Status.CANCELED -> showLog(
                                    "imaglyUri",
                                    "Editor cancelled"
                                )
                                EditorSDKResult.Status.EXPORT_DONE -> {
                                    showLog("imaglyUri", "Result saved at ${result.resultUri}")
                                    result.settingsList.release()
                                    imageCallback?.invoke(result.resultUri.toString())

                                }
                                else -> {
                                    showLog("imaglyUri", "No result found")
                                }
                            }
                        }
//                        else -> {
//                            imageCallback?.invoke(uri.toString())
//                        }
                    }


                    showLog(
                        "IMAGE",
                        "profileUri _result >>> $uri ...... path >>>  ${data?.data?.path}"
                    )

//                    }


                }

//                ImagePicker.RESULT_ERROR -> {
//                    Toast.makeText(mActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT)
//                        .show()
//                }
                else -> {
//                    Toast.makeText(mActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun editVideo(context: Activity, inputPath: Uri) {

        val settingsList = VideoEditorSettingsList(false)
            // Set the source as the Uri of the video to be loaded
            .configure<LoadSettings> {
                it.source = inputPath

            }

        // The min/max length limits are also respected by the VideoCompositionToolPanel.
        settingsList.configure<TrimSettings> {
            // By default the editor does not allow videos shorter than 0.5 seconds. For this example the duration is set,
            // e.g. for a social media application where the posts are not allowed to be shorter than 2 seconds.
            // highlight-min-max
            it.setMinimumVideoLength(2, TimeUnit.SECONDS)
            // By default the editor does not have a maximum duration. For this example the duration is set,
            // e.g. for a social media application where the posts are not allowed to be longer than 5 seconds.
//            it.setMaximumVideoLength(10, TimeUnit.MINUTES)
            // highlight-min-max
//            it.forceTrimMode = TrimSettings.ForceTrim.IF_NEEDED
        }


        // Start the video editor using VideoEditorBuilder
        // The result will be obtained in onActivityResult() corresponding to EDITOR_REQUEST_CODE
        VideoEditorBuilder(context)
            .setSettingsList(settingsList)
            .startActivityForResult(context, EDITOR_REQUEST_CODE)


        // Release the SettingsList once done
        settingsList.release()


//        context?.let {
//            HdEditor.Builder(context).inputPath(inputPath).createIntent { resultIntent ->
//                try {
//
//                    context.startActivityForResult(resultIntent, 121)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Toast.makeText(
//                        context,
//                        "Some error occurred. Please try again",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        } ?: kotlin.run {
//            imageCallback?.invoke(inputPath)
//
//        }
    }

    fun cropImage(
        context: Activity,
        uri: String?,
        isBanner: Boolean,
        callBack: ((String?) -> Unit),
    ) {
        mActivity = context
        imageCallback = callBack
        val path = Uri.fromFile(File((uri)))

        context?.let {
            val ucrop = UCrop.of(path, path)
                .withMaxResultSize(1080, 1080)
            if (isBanner) ucrop.withAspectRatio(16f, 9f)
            ucrop.start(it)
        }
    }

}