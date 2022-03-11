package com.selflearningcoursecreationapp.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.selflearningcoursecreationapp.extensions.showLog

class ImagePickUtils {

    private var getContent: ActivityResultLauncher<Intent>? = null
    private var imageCallback: ((String?) -> Unit)? = null
    private var mActivity: Activity? = null


    fun captureImage(
        context: Activity,
        callBack: ((String?) -> Unit),
        doCrop: Boolean = true,
        doCompress: Boolean = true,
        registry: ActivityResultRegistry,
    ) {
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
        registry: ActivityResultRegistry
    ) {
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

    private fun registerForCallback(registry: ActivityResultRegistry) {
        getContent = registry.register(
            "file_selection",
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                imageCallback?.invoke(fileUri.path)

                showLog(
                    "IMAGE",
                    "profileUri >>> $fileUri ...... path >>>  ${fileUri.path}"
                )
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(mActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(mActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


}