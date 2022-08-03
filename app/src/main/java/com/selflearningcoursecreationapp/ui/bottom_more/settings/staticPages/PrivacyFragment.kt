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
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.STATIC_PAGES_TYPE


class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>(), HandleClick {
    //        var type = 0
//    private var url = ""
    var finalUrl = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        init()

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun init() {
        binding.privacy = this
        arguments?.let {
            initView(it.getInt("type"))
        }


    }


    override fun getLayoutRes() = R.layout.fragment_privacy
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
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


    override fun onApiRetry(apiCode: String) {

    }

    fun initView(type: Int) {
        when (type) {
            STATIC_PAGES_TYPE.PRIVACY -> {
                binding.tvTitle.text = getString(R.string.privacy_policy)
                binding.appCompatImageView.loadImage(R.drawable.ic_privacy_header)
                finalUrl = ApiEndPoints.BASE_URL_INTERNAL + "" + ApiEndPoints.LINK_PRIVACY_POL
            }
            STATIC_PAGES_TYPE.TERMS -> {
                binding.tvTitle.text = getString(R.string.terms_amp_conditions)
                binding.appCompatImageView.loadImage(R.drawable.ic_terms_header)
                finalUrl = ApiEndPoints.BASE_URL_INTERNAL + "" + ApiEndPoints.LINK_TERM_COND
            }
            STATIC_PAGES_TYPE.HELP -> {
                binding.tvTitle.text = getString(R.string.question_we_ve_got_instant_answers)
                binding.appCompatImageView.loadImage(R.drawable.ic_help_header)
                finalUrl = ApiEndPoints.BASE_URL_INTERNAL + "" + ApiEndPoints.LINK_FAQ
            }
            STATIC_PAGES_TYPE.ABOUT_US -> {
                binding.tvTitle.text = getString(R.string.about_us)
                binding.appCompatImageView.loadImage(R.drawable.ic_privacy_header)
                finalUrl = ApiEndPoints.BASE_URL_INTERNAL + "" + ApiEndPoints.LINK_ABOUT_US
            }
        }
        loadWebView()
    }

    fun loadWebView() {
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
//                    showLoading()
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

}