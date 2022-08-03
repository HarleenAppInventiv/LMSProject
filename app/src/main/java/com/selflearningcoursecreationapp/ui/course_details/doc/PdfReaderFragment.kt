package com.selflearningcoursecreationapp.ui.course_details.doc

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPdfReaderBinding
import java.io.File
import java.util.*


class PdfReaderFragment : BaseFragment<FragmentPdfReaderBinding>(), OnPageChangeListener,
    OnLoadCompleteListener, OnPageErrorListener, OnErrorListener {
    var manager: DownloadManager? = null
    private val root = Environment.getExternalStorageDirectory().toString()
    private val app_folder = "$root/Self/"
    var date: Date? = null
    override fun getLayoutRes() = R.layout.fragment_pdf_reader
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
//            displayFromAsset(it.getString("fileUri").toString())
//            showToastShort(it.getString("fileUri").toString())
            download(it.getString("fileUri").toString())
        }

    }

    override fun onApiRetry(apiCode: String) {

    }

    fun download(url: String) {
        val file = File(url)
        manager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setMimeType("application/pdf")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            file.name
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        manager?.enqueue(request)
    }

//    private fun displayFromAsset(uri: String) {
////        val fileName = getFileName(Uri.parse(uri))
//        binding.pdfView.from(fileName)
//            .pages(1)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
//            .enableDoubletap(true)
//            .defaultPage(0)
////            .onDraw(onDrawListener)
////            .onDrawAll(onDrawListener)
////            .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
//            .onPageChange(this)
////            .onPageScroll(h)
//            .onError(this)
//            .onPageError(this)
////            .onRender(onRenderListener)
////            .onTap(onTapListener)
//            .enableAnnotationRendering(false)
//            .password(null)
//            .scrollHandle(null)
//            .enableAntialiasing(true)
//            .spacing(0)
//            .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
//            .load();
//    }


    override fun loadComplete(nbPages: Int) {
    }

    override fun onError(t: Throwable?) {
        Log.d("error", "onError:${t?.message.toString()} ")
    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {
        Log.d("error", "onError:${t?.message.toString()} ")

    }


    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                requireActivity().getContentResolver().query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }


}