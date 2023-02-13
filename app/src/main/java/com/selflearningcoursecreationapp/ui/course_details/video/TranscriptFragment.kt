package com.selflearningcoursecreationapp.ui.course_details.video

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTranscriptBinding
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visibleView

class TranscriptFragment : BaseFragment<FragmentTranscriptBinding>() {
    private val viewModel: ContentDetailViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    override fun getLayoutRes() = R.layout.fragment_transcript
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewModel.transcriptLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                loadWebView(it)
            }
            binding.noDataTV.visibleView(it.isNullOrEmpty())

        }

    }

    override fun onApiRetry(apiCode: String) {
    }

    private fun loadWebView(streamingEndpointUrl: String?) {
        binding.webview.apply {

            settings.javaScriptEnabled = true
            loadUrl("https://selflearnqaweb.appskeeper.in/public-view/text-viewer?textUrl=$streamingEndpointUrl")

            //            loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$streamingEndpointUrl")
            webViewClient = object : WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceivedError(

                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    showLog(
                        "WEBVIEW",
                        "onReceivedError request >> ${request?.url} .... error >> ${error?.description?.toString()}"
                    )

                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)

                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLog("WEBVIEW", "onPageStarted url >> $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    showLog("WEBVIEW", "onPageFinished url >> $url")
//                    if (viewModel.modType == MODTYPE.LEARNER) {
//                        initWebSocket()
//                    }
                    Handler(Looper.myLooper()!!).postDelayed({
                        hideLoading()
                    }, 2000)

                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)

                }
            }

            setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP
                    && canGoBack()
                ) {
                    goBack()
                    return@OnKeyListener true
                }
                return@OnKeyListener false
            })
        }
    }


}