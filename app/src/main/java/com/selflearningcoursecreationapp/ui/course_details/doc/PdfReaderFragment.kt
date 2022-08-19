package com.selflearningcoursecreationapp.ui.course_details.doc

import android.app.DownloadManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.interfaces.OnPageChangedListener
import com.danjdt.pdfviewer.utils.PdfPageQuality
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPdfReaderBinding
import java.io.IOException
import java.util.*


class PdfReaderFragment : BaseFragment<FragmentPdfReaderBinding>(),
    OnErrorListener, OnPageChangedListener,
    com.danjdt.pdfviewer.interfaces.OnErrorListener {
    var manager: DownloadManager? = null
    private val root = Environment.getExternalStorageDirectory().toString()
    private val app_folder = "$root/Self/"
    var date: Date? = null
    private var downloadID: Long = 0
//    var url = ""

    override fun getLayoutRes() = R.layout.fragment_pdf_reader
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        arguments?.let {
            PdfViewer.Builder(binding.root)
                .quality(PdfPageQuality.QUALITY_720)
                .setZoomEnabled(true)
                .setMaxZoom(3f) //zoom multiplier
                .setOnPageChangedListener(this)
                .setOnErrorListener(this)
                .build()
                .load(it.getString("fileUri").toString())

        }


    }

    override fun onApiRetry(apiCode: String) {

    }


    override fun onAttachViewError(e: Exception) {
    }

    override fun onFileLoadError(e: Exception) {
    }

    override fun onPdfRendererError(e: IOException) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onPageChanged(page: Int, total: Int) {
        hideLoading()
//        showToastShort(page.toString() + "::::" + total.toString())
    }

}