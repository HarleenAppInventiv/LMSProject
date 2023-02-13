package com.selflearningcoursecreationapp.ui.course_details.doc

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPdfReaderBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.MediaType


class ViewQualificationFragment : BaseFragment<FragmentPdfReaderBinding>() {
    var isLoaded = false

    var courseId: Int = 0
    var lectureId: Int = 0
    var status: Int = 0
    var modType: Int = 0
    var sectionId: Int = 0
    var duration: Int = 0
    var mediaType: Int = 0


    var lessonList = ArrayList<ChildModel?>()

    override fun getLayoutRes() = R.layout.fragment_pdf_reader
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        arguments?.let {
            loadWebView(it.getString("fileUri").toString())
            baseActivity.setToolbar(title = it.getString("fileName"))
            lectureId = it?.getInt("lectureId")
            courseId = it?.getInt("courseId")
            sectionId = it?.getInt("sectionId")
            baseActivity.setToolbar(it?.getString("title"))
            modType = it?.getInt("type")
            modType = it?.getInt("modType")

            lessonList =
                it?.getParcelableArrayList<ChildModel>("lessonList") ?: ArrayList()
        }

        binding.childView.tvPrevious.setOnClickListener {
            navigationToContent(0, (arguments?.getInt("innerPosition") ?: 0) - 1, MODTYPE.LEARNER)

        }

        binding.childView.tvNext.setOnClickListener {
            navigationToContent(0, (arguments?.getInt("innerPosition") ?: 0) + 1, MODTYPE.LEARNER)

        }


    }


    override fun onApiRetry(apiCode: String) {

    }

    fun navigationToContent(position: Int, innerPosition: Int, modType: Int) {
        when (lessonList.get(
            innerPosition
        )?.mediaType) {
            MediaType.QUIZ -> {
                if (modType == MODTYPE.LEARNER) {
                    findNavController().navigateTo(
                        R.id.action_global_quizBaseFragment,
                        bundleOf(
                            "quizId" to lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to courseId,
                            "lessonList" to lessonList,
                            "innerPosition" to innerPosition,
                            "modType" to MODTYPE.LEARNER,


                            )
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.action_global_quiZListForModCreatorFragment,
                        bundleOf(
                            "quizId" to lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to courseId,
                            "lessonList" to lessonList,
                            "innerPosition" to innerPosition,
                            "modType" to MODTYPE.LEARNER,


                            )
                    )
                }
            }
            MediaType.DOC -> {


                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "offlineCourseData" to arguments?.get("offlineCourseData") as OfflineCourseData?,
                        "courseId" to courseId,
                        "sectionId" to sectionId,
                        "lessonId" to lectureId,
                        "lessonList" to lessonList,
                        "innerPosition" to innerPosition,
                        "modType" to MODTYPE.LEARNER
                    )
                )
            }
            MediaType.TEXT -> {


                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "offlineCourseData" to arguments?.get("offlineCourseData") as OfflineCourseData?,
                        "courseId" to courseId,
                        "sectionId" to sectionId,
                        "lessonId" to lectureId,
                        "lessonList" to lessonList,
                        "innerPosition" to innerPosition,
                        "modType" to MODTYPE.LEARNER,

//                        "name" to sectionData.value?.get(position)?.lessonList?.get(
//                            innerPosition
//                        )?.lectureTitle,
//                        "duration" to sectionData.value?.get(position)?.lessonList?.get(
//                            innerPosition
//                        )?.lectureContentDuration?.toInt()
                    )
                )
            }
            MediaType.VIDEO, MediaType.AUDIO -> {
                playVideo(position, innerPosition, modType)
            }
            Constant.CLICK_LESSON -> {
            }
        }
    }


    fun playVideo(position: Int, innerPosition: Int, modType: Int) {
        findNavController().navigateTo(
            R.id.action_global_videoBaseFragment,
            bundleOf(
                "courseId" to arguments?.getInt("courseId"),
                "status" to arguments?.getInt("status"),
                "offlineCourseData" to arguments?.get("offlineCourseData") as OfflineCourseData?,
                "type" to modType,
                "sectionId" to arguments?.getInt("sectionId"),
                "lectureId" to lessonList.get(
                    innerPosition
                )?.lectureId,
                "title" to
                        lessonList.get(innerPosition)?.lectureTitle,
                "lessonList" to lessonList,
                "innerPosition" to innerPosition,
                "modType" to MODTYPE.LEARNER,


                )
        )
    }


    private fun loadWebView(streamingEndpointUrl: String?) {
        binding.webview.apply {
            settings.javaScriptEnabled = true
            loadUrl("https://www.skillfy.in/public-view/pdf-viewer?docUrl=$streamingEndpointUrl")
            webViewClient = object : WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    hideLoading()
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
                    hideLoading()

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