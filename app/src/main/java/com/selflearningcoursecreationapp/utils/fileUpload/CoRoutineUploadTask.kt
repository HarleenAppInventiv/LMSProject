package com.selflearningcoursecreationapp.utils.fileUpload

import android.app.Dialog
import android.util.Log
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.CoroutineAsyncTask
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import io.tus.java.client.TusUploader
import java.net.URL


class CoRoutineUploadTask(
    activity: Dialog,
    client: TusClient,
    upload: TusUpload,
    progressCallback: VideoUploadProgress
) :
    CoroutineAsyncTask<Void?, Long?, URL?>() {
    private val activity: Dialog
    private val client: TusClient
    private val upload: TusUpload
    private var progressCallback: VideoUploadProgress = progressCallback
    private var exception: Exception? = null
    override fun onPreExecute() {
        showLog("Upload status>> ", "onPreExecute >> ");

        progressCallback.setStatus("Upload selected...")
        progressCallback.setPauseButtonEnabled(true)
        progressCallback.progressUpdate(0)
    }

    override fun onPostExecute(uploadURL: URL?) {
        showLog("Upload status>> ", "onPostExecute >> $uploadURL");
        progressCallback.setStatus(
            """
                Upload finished!
                """.trimIndent()
        )

        Log.e("uploaded url tus>>>", "uploadURL ?>>$uploadURL")

        uploadURL?.let { progressCallback.uploadedUrl(it) }
        progressCallback.setPauseButtonEnabled(false)
    }

    override fun onCancelled(result: URL?) {
        super.onCancelled(result)
        if (exception != null) {
            Log.e("upload cancelled>> ", exception?.message.toString());
        }
        progressCallback.setPauseButtonEnabled(false)
        progressCallback.onUploadCanceled(exception)
    }

//    override fun onCancelled() {
//
//    }

    override fun onProgressUpdate(vararg updates: Long?) {
        val uploadedBytes = updates[0]?.toDouble() ?: 0.0
        val totalBytes = updates[1] ?: 0
        Log.e(
            "upload progress>>> ",
            (uploadedBytes / totalBytes * 100).toInt().toString()
        )
        progressCallback.setStatus(
            "Uploaded " + (uploadedBytes / totalBytes * 100).toInt()
        )
        progressCallback.progressUpdate((uploadedBytes / totalBytes * 100).toInt())
    }

    override fun doInBackground(vararg p0: Void?): URL? {
        try {
            val uploader: TusUploader = client.resumeOrCreateUpload(upload)
            val totalBytes: Long = upload.size
            var uploadedBytes: Long = uploader.offset

            // Upload file in 1MiB chunks
            uploader.chunkSize = 1024 * 500
            while (!isCancelled && uploader.uploadChunk() > 0) {
                uploadedBytes = uploader.offset
                publishProgress(uploadedBytes, totalBytes)
            }
            uploader.finish()
            return uploader.uploadURL
        } catch (e: Exception) {
            exception = e
            cancel(true)
        }
        return null
    }

    init {
        this.activity = activity
        this.client = client
        this.upload = upload
        this.progressCallback = progressCallback
    }
}
