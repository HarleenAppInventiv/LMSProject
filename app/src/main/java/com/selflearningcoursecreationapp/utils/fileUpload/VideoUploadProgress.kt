package com.selflearningcoursecreationapp.utils.fileUpload

import java.net.URL

interface VideoUploadProgress {

    fun progressUpdate(progress: Int)
    fun setStatus(status: String)
    fun uploadedUrl(url: URL)
    fun onUploadCanceled(exception: Exception?)
    fun setPauseButtonEnabled(enabled: Boolean)

}