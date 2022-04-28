package com.selflearningcoursecreationapp.ui.bottom_more.settings.staticPages

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_BACK
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPrivacyBinding
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick


class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>(), HandleClick {
    var title = ""
    var url = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun init() {
        binding.privacy = this
        arguments?.let {
            title = it.getString("title").toString()
            url = it.getString("url").toString()
            Log.d("pp", "init: ${ApiEndPoints.BASE_URL_INTERNAL + url}")
        }

        val finalUrl = ApiEndPoints.BASE_URL_INTERNAL + "" + url
        binding.tvTitle.text = title
        binding.wvPrivecyPolicy.apply {
            settings.javaScriptEnabled = true
            loadUrl(finalUrl)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoading()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoading()
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                }
            }
            setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KEYCODE_BACK && event.action == MotionEvent.ACTION_UP
                    && canGoBack()
                ) {
                    goBack()
                    return@OnKeyListener true
                }
                return@OnKeyListener false
            })
        }
    }


    override fun getLayoutRes() = R.layout.fragment_privacy
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var view = items[0] as View
            when (view.id) {
                R.id.img_back -> {
                    findNavController().popBackStack()
//                    activity?.startActivity(Intent(activity!!, InitialActivity::class.java))
//                    activity?.finish()


                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("signup", "onDestroy: privacy")
    }

}