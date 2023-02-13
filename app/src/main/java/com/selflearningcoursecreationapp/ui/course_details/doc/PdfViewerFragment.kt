package com.selflearningcoursecreationapp.ui.course_details.doc

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPdfReaderBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.offlineCourse.AddOfflineCourseVM
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseVM
import com.selflearningcoursecreationapp.ui.splash.MessageListener
import com.selflearningcoursecreationapp.ui.splash.VideoAudioProgress
import com.selflearningcoursecreationapp.ui.splash.WebSocketManager
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.downloadManager.DOWNLOAD_COMPLETE_BROADCAST
import com.selflearningcoursecreationapp.utils.downloadManager.FileDownloadService
import com.selflearningcoursecreationapp.utils.downloadManager.ThinDownloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*
import kotlin.concurrent.thread


class PdfViewerFragment : BaseFragment<FragmentPdfReaderBinding>()
    /*OnPageChangedListener,
    com.danjdt.pdfviewer.interfaces.OnErrorListener*/,
    MessageListener/*, DownloadStatusListenerV1*/ {
    private var downloadId: Int? = null
    private var isDownloadClicked = false

    private var fileUrl: String = ""
    private lateinit var actionRead: MenuItem
    private lateinit var actionDownload: MenuItem
    private val viewModel: CourseDetailVM by viewModel()
    private val DBViewModel: OfflineCourseVM by viewModel()
    private val addOfflineCourseVM: AddOfflineCourseVM by viewModel()
    private var offlineCourseData: OfflineCourseData? = null
    private var DBCourseData: OfflineCourseData? = null
    var date: Date? = null

    //dowload file
    private var downloadManager: ThinDownloadManager? = null
    private val DOWNLOAD_THREAD_POOL_SIZE = 1


    override fun getLayoutRes() = R.layout.fragment_pdf_reader
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        showLoading()
        WebSocketManager.close()
        arguments?.let {
//            viewModel.sectionId = it.getInt("sectionId")
//          showToastShort(it.getString("fileUri").toString())
            viewModel.lectureId = it.getInt("lessonId")
//            viewModel.modType = it.getInt("type")
            viewModel.modType = it.getInt("modType")
            viewModel.lessonList =
                it.getParcelableArrayList<ChildModel>("lessonList") ?: ArrayList()
            viewModel.sectionList =
                it.getParcelableArrayList<SectionModel>("section") ?: ArrayList()
            if (it.getString("courseId")?.toInt() == null) {
                viewModel.courseId = it.getInt("courseId")

            } else {
                viewModel.courseId = it.getString("courseId")?.toInt() ?: 0
            }
//            viewModel.sectionId= viewModel.lessonList.get(arguments?.getInt("innerPosition")?:0)?.sectionId?:0

//            if (viewModel.lessonList.isNotEmpty()){
//
//                viewModel.lectureId = it.getInt("lectureId")
//                viewModel.courseId = it.getInt("courseId")
//                viewModel.modType = it.getInt("type")
//
//            }

            if (viewModel.modType == MODTYPE.LEARNER) {
                offlineCourseData = it.get("offlineCourseData") as OfflineCourseData?

                viewModel.sectionId =
                    viewModel.lessonList.get(it.getInt("innerPosition"))?.sectionId ?: 0
            }
        }
        initUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
        actionDownload = menu.findItem(R.id.action_download)
        actionRead = menu.findItem(R.id.action_read)
        actionDownload.contentDescription = getString(R.string.download_lesson_for_offline_mode)

        actionDownload.isVisible = false
        actionRead.isVisible = true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_download -> {
                if (!isMyServiceRunning() && !isDownloadClicked) {
                    isDownloadClicked = true
                    downloadFile()
                } else {

                    showToastLong(baseActivity.getString(R.string.downloading_is_in_progrss))
                }
                true
            }
            R.id.action_read -> {
                baseActivity.checkAccessibilityService()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun isMyServiceRunning(): Boolean {
        try {
            val manager: ActivityManager? =
                ContextCompat.getSystemService(
                    baseActivity,
                    FileDownloadService::class.java
                ) as ActivityManager?
            for (service in manager?.getRunningServices(Int.MAX_VALUE)!!) {
                if (FileDownloadService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
//        if (downloadId != null && downloadManager != null && !downloadManager!!.isReleased) {
//            val status = downloadManager?.query(downloadId!!)
//            if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_STARTED || status == DownloadManager.STATUS_RUNNING)
//                return true
//        }
//        return false
    }


    private fun downloadFile() {
        val downloadingUrl = viewModel.lectureLiveData.value?.lectureContentUrl
        if (downloadingUrl?.contains("https") == true) {

            offlineCourseData = viewModel.lessonList.get(arguments?.getInt("innerPosition") ?: 0)
                ?.getOfflineCourseData(
                    viewModel.userProfile?.name ?: "",
                    viewModel.userProfile?.id ?: 0,
                    viewModel.courseId
                )
            try {


                val intent = Intent(activity, FileDownloadService::class.java)
                intent.putExtra("streamingUrl", downloadingUrl)
                intent.putExtra("DBCourseData", DBCourseData)
                intent.putExtra("offlineCourseData", offlineCourseData)
                intent.putExtra("sectionId", viewModel.sectionId)
                baseActivity.startService(intent)
                Toast.makeText(
                    context,
                    getString(R.string.downloading_is_in_progrss),
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                showToastShort(getString(R.string.some_error_occure_please_try_again))
            }
        } else {
            Toast.makeText(context, getString(R.string.invalid_file_url), Toast.LENGTH_LONG).show()
        }
    }

//    private fun downloadFile() {
//        downloadManager =
//            ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE)
//        val retryPolicy: RetryPolicy = DefaultRetryPolicy()
//
//        val filesDir: File? = requireActivity().getExternalFilesDir("")
//
//        val downloadUri = Uri.parse(fileUrl)
//        var name: String = guessFileName(fileUrl, null, null)
//
//        offlineCourseData = viewModel.lessonList.get(arguments?.getInt("innerPosition") ?: 0)?.getOfflineCourseData(
//            viewModel.userProfile?.name?:"",
//            viewModel.userProfile?.id ?: 0,
//            viewModel.courseId
//        )
//        var fileExtention = getFileExtension(name)
//        offlineCourseData?.sectionList?.get(
//            0
//        )?.lessonList?.get(0)?.fileExtention = fileExtention
//        name = getFileNameWithoutExtension(name)
//
//        val destinationUri = Uri.parse("$filesDir/$name")
//        val downloadRequest1: DownloadRequest = DownloadRequest(downloadUri)
//            .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
//            .setRetryPolicy(retryPolicy)
//            .setDownloadContext("Download1")
//            .setStatusListener(this)
//
//        downloadId = downloadManager!!.add(downloadRequest1)
//        showToastLong(baseActivity.getString(R.string.downloading_is_in_progrss))
//
//
//    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_download)
        item.icon?.colorFilter = PorterDuffColorFilter(
            ThemeUtils.getAppColor(baseActivity),
            PorterDuff.Mode.SRC_IN
        )

    }

    fun getFileNameWithoutExtension(filePath: String): String {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val extenPosi: Int = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi: Int = filePath.lastIndexOf(File.separator)
        if (filePosi == -1) {
            return if (extenPosi == -1) filePath else filePath.substring(
                0,
                extenPosi
            )
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1)
        }
        return if (filePosi < extenPosi) filePath.substring(
            filePosi + 1,
            extenPosi
        ) else filePath.substring(filePosi + 1)
    }

    val FILE_EXTENSION_SEPARATOR = "."

    fun getFileExtension(filePath: String): String? {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val extenPosi: Int = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi: Int = filePath.lastIndexOf(File.separator)
        if (filePosi == -1) {
            return if (extenPosi == -1) filePath else filePath.substring(
                extenPosi,
                filePath.length
            )
        }
        return ""

    }

    private fun initUI() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        DBViewModel.getApiResponse().observe(viewLifecycleOwner, this)
        addOfflineCourseVM.getApiResponse().observe(viewLifecycleOwner, this)
        observer()
        viewModel.getSectionContentDetails()
        setHasOptionsMenu(true)
        if (arguments?.getInt("modType") == MODTYPE.LEARNER && viewModel.lessonList.size != 1) {


            if (((arguments?.getInt("position") ?: -1) == 0 && (arguments?.getInt("innerPosition")
                    ?: -1) == 0)
            ) {

                binding.childView.tvPrevious.alpha = .5f
                binding.childView.tvPrevious.isEnabled = false

            } else {

                binding.childView.tvPrevious.alpha = 1f
                binding.childView.tvPrevious.isEnabled = true

            }

            if (viewModel.lessonList.get(
                    arguments?.getInt("innerPosition") ?: -1
                )?.lectureId != viewModel.lessonList.last()?.lectureId
            ) {
                if (arguments?.getBoolean("freezeContent") == true && arguments?.getBoolean("isCompleted") != true) {
                    binding.childView.tvNext.alpha = .5f
                    binding.childView.tvNext.isEnabled = false

                } else {

                    binding.childView.tvNext.alpha = 1f
                    binding.childView.tvNext.isEnabled = true
                }

            } else {

//                binding.childView.tvNext.alpha = .5f
//                binding.childView.tvNext.isEnabled = false
                binding.childView.tvNext.setText(R.string.finish)


            }


            if (arguments?.getInt("innerPosition") == null && arguments?.getInt("courseStatus") == CourseStatus.NOT_ENROLLED) {
                binding.childFrame.gone()

            } else {
                binding.childFrame.visible()

            }

        }






        binding.childView.tvPrevious.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if ((arguments?.getInt("position")
                        ?: -1) == 0 && (arguments?.getInt("innerPosition")
                        ?: -1) == 0
                ) {

                    findNavController().popBackStack(R.id.courseDetailsFragment, false)

                } else {

                    navigationToContent(
                        arguments?.getInt("position") ?: 0,
                        arguments?.getInt("innerPosition")?.minus(1) ?: 0,
                        MODTYPE.LEARNER,
                        true
                    )


                }


            }


        })

        binding.childView.tvNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (viewModel.lessonList.get(
                        arguments?.getInt("innerPosition") ?: -1
                    )?.lectureId == viewModel.lessonList.last()?.lectureId
                ) {
                    findNavController().popBackStack(R.id.courseDetailsFragment, false)


                } else {

                    navigationToContent(
                        arguments?.getInt("position") ?: 0,
                        arguments?.getInt("innerPosition")?.plus(1) ?: 0,
                        MODTYPE.LEARNER,
                        true
                    )
                }
//                    navigationToContent(
//                        arguments?.getInt("position") ?: 0,
//                        arguments?.getInt("innerPosition")?.plus(1) ?: 0,
//                        MODTYPE.LEARNER,
//                        true
//                    )


            }

        })

    }


    fun navigationToContent(position: Int, innerPosition: Int, modType: Int, b: Boolean) {
//        val offlineCourseData: OfflineCourseData =
//            getOfflineCourseData(position, innerPosition)

        when (viewModel.lessonList.get(
            innerPosition
        )?.mediaType) {
            MediaType.QUIZ -> {
                if (modType == MODTYPE.LEARNER) {

                    findNavController().navigateTo(
                        R.id.action_global_quizBaseFragment,
                        bundleOf(
                            "quizId" to viewModel.lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to innerPosition,
                            "position" to position,
                            "section" to viewModel.sectionList,
                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                            "from" to 0,
                            "offlineCourseData" to offlineCourseData,
                            "lessonId" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureId,
                            "isCompleted" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureIsCompleted,
                            "modType" to MODTYPE.LEARNER,
                            "isQuizReport" to true,
                            "courseStatus" to arguments?.getInt("courseStatus"),

                            "freezeContent" to arguments?.getBoolean("freezeContent")


                        )
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.action_global_quiZListForModCreatorFragment,
                        bundleOf(
                            "quizId" to viewModel.lessonList.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to innerPosition,
                            "position" to position,
                            "section" to viewModel.sectionList,
                            "modType" to MODTYPE.LEARNER,

                            "from" to 0,
                            "courseStatus" to arguments?.getInt("courseStatus"),

                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,

                            )
                    )
                }
            }
            MediaType.DOC -> {
                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "quizId" to viewModel.lessonList.get(
                            innerPosition
                        )?.quizId,
                        "title" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureTitle,
                        "offlineCourseData" to offlineCourseData,

                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "from" to 0,
                        "modType" to MODTYPE.LEARNER,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,

                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "isQuizReport" to true,
                        "courseStatus" to arguments?.getInt("courseStatus"),

                        "freezeContent" to arguments?.getBoolean("freezeContent")

                    )
                )
            }
            MediaType.TEXT -> {


                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "quizId" to viewModel.lessonList.get(
                            innerPosition
                        )?.quizId,
                        "title" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureTitle,
                        "modType" to MODTYPE.LEARNER,
                        "offlineCourseData" to offlineCourseData,

                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionId,
                        "from" to 0,
                        "courseStatus" to arguments?.getInt("courseStatus"),


                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "isQuizReport" to true,
                        "freezeContent" to arguments?.getBoolean("freezeContent")

//                        "name" to viewModel.sectionList.value?.get(position)?.lessonList?.get(
//                            innerPosition
//                        )?.lectureTitle,
//                        "duration" to viewModel.sectionList.value?.get(position)?.lessonList?.get(
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
                "courseId" to viewModel.courseId,
                "offlineCourseData" to offlineCourseData,

                "type" to modType,
                "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                "lectureId" to viewModel.lessonList.get(innerPosition)?.lectureId,
                "title" to viewModel.lessonList.get(innerPosition)?.lectureTitle,
                "lessonList" to viewModel.lessonList,
                "innerPosition" to innerPosition,
                "position" to position,
                "section" to viewModel.sectionList,
                "from" to 0,
                "modType" to MODTYPE.LEARNER,
                "lessonId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId,
                "isCompleted" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureIsCompleted,
                "isQuizReport" to true,
                "courseStatus" to arguments?.getInt("courseStatus"),

                "freezeContent" to arguments?.getBoolean("freezeContent")

            )
        )
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)

        if (apiCode == ApiEndPoints.OFFLINE_COURSE) {
            if (value != null)
                DBCourseData = value as OfflineCourseData
            else {
                actionDownload.isVisible = true

            }

            actionDownload.isVisible = (fileUrl != null && fileUrl.isNotEmpty()
                    && !checkIfLessonExist()
                    && viewModel.modType == MODTYPE.LEARNER
                    && fileUrl.contains(".pdf"))


        }


    }

    private fun observer() {
        viewModel.lectureLiveData.observe(viewLifecycleOwner) {
            if (!it?.streamingEndpointUrl.isNullOrEmpty()) {
                loadWebView(it?.streamingEndpointUrl)
                fileUrl = it?.streamingEndpointUrl.toString()
                baseActivity.setToolbar(showToolbar = true, title = it?.lectureTitle)
                DBViewModel.getOfflineCourse(viewModel.courseId)
                hideLoading()
            } else {
                showToastShort(getString(R.string.check_later))
            }

            baseActivity.setToolbar(arguments?.getString("title"))
//
//            binding.childView.tvNext.isEnabled=
//                viewModel.lessonList[arguments?.getInt("innerPosition") ?: -1]?.lectureIsCompleted == true


        }


    }

    private fun initWebSocket() {

        if (!WebSocketManager.isConnect()) {


            thread {
                kotlin.run {
                    WebSocketManager.init(
                        "${ApiEndPoints.WEB_SOCKET_USER_CONTENT_HISTORY}?Token=${baseActivity.tokenFromDataStore()}&LanguageId=${1}&ChannelId=2&Data.CourseId=${viewModel.courseId}&Data.SectionId=${viewModel.sectionId}",
                        this,
                    )
                    WebSocketManager.connect()
                }
            }
        }


    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun sendMessage() {
        if (context != null) {
            WebSocketManager.sendMessage(
                Gson().toJson(
                    VideoAudioProgress(
                        viewModel.lectureId,
                        viewModel.lectureLiveData.value?.lectureContentDuration ?: 0,
                        viewModel.sectionId
                    )
                )
            )
            Log.e(
                "sample_sample_sample", Gson().toJson(
                    VideoAudioProgress(
                        viewModel.lectureId,
                        viewModel.lectureLiveData.value?.lectureContentDuration ?: 0,
                        viewModel.sectionId
                    )
                )
            )
        }

    }


    override fun onConnectSuccess() {
        Log.d("socket", "onConnectSuccess")
//        sendMessage()
    }

    override fun onConnectFailed() {
        Log.d("socket", "onConnectFailed")
    }

    override fun onClose() {
        Log.d("socket", "onClose")
    }

    override fun onMessage(text: String?) {
        Log.d("socket", "onMessage")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        downloadManager?.release()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.modType == MODTYPE.LEARNER) {
            initWebSocket()
        }
    }

    override fun onPause() {
        super.onPause()
        sendMessage()
        WebSocketManager.close()
    }

    private fun loadWebView(streamingEndpointUrl: String?) {
//        binding.childView.tvNext.alpha = .5f
//        binding.childView.tvNext.isEnabled = false

        binding.webview.apply {
            settings.javaScriptEnabled = true
            if (streamingEndpointUrl?.contains(".txt") == true)
                loadUrl("https://www.skillfy.in/public-view/text-viewer?textUrl=$streamingEndpointUrl")
            else if (streamingEndpointUrl?.contains(".pdf") == true) {
                loadUrl("https://www.skillfy.in/public-view/pdf-viewer?docUrl=$streamingEndpointUrl")
            } else {
                loadUrl("https://www.skillfy.in/public-view/doc-viewer?docUrl=$streamingEndpointUrl")
            }

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
                    CoroutineScope(Main).launch {
                        Log.d("varun", "onPageFinished: 1")
                        delay(6000)
                        Log.d("varun", "onPageFinished: 2")

                        binding.childView.tvNext.alpha = 1f
                        binding.childView.tvNext.isEnabled = true

                    }

                    showLog("WEBVIEW", "onPageFinished url >> $url")

                    viewModel.lessonList.forEachIndexed { index, childModel ->
                        if (viewModel.lectureId == childModel?.lectureId) {
                            viewModel.lessonList.set(index, childModel)?.lectureIsCompleted = true
                        }


                    }

                    if (viewModel.modType == MODTYPE.LEARNER) {
                        sendMessage()
                    }


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

//    override fun onDownloadComplete(downloadRequest: DownloadRequest?) {
//        Log.e("download", ">>>>>>completed")
//        actionDownload.isVisible = false
//        saveToDB(downloadRequest?.destinationURI?.path.toString())
//        isDownloadClicked = false
//        downloadId = null
//
//
//        viewModel.lessonList.forEachIndexed { index, childModel ->
//            if (viewModel.lectureId == childModel?.lectureId) {
//                viewModel.lessonList.set(index, childModel)?.lectureIsCompleted = true
//
//            }
//
//
//        }
//
//    }

    private fun saveToDB(path: String?) {
        offlineCourseData?.sectionList?.get(
            0
        )?.lessonList?.get(0)?.filePath = path

        var sectionPosition = 0
        if (DBCourseData != null && DBCourseData!!.sectionList != null && DBCourseData!!.sectionList!!.size > 0) {
            getSectionPositionInDB(DBCourseData!!.sectionList)
            if (sectionPosition > 0) {
                sectionPosition = getSectionPositionInDB(DBCourseData!!.sectionList)
                offlineCourseData?.sectionList?.get(
                    0
                )?.lessonList?.get(0)?.let { offlineLesson ->
                    DBCourseData?.sectionList?.get(sectionPosition)?.lessonList?.add(
                        offlineLesson
                    )
                }
            } else {
                offlineCourseData?.sectionList?.get(0)?.let { DBCourseData?.sectionList?.add(it) }
            }
            addOfflineCourseVM.updateOfflineCourse(DBCourseData!!)
        } else {
            offlineCourseData?.let { addOfflineCourseVM.insertOfflineCourse(it) }
        }
        showToastLong(baseActivity.getString(R.string.download_completed))


    }


    private fun checkIfLessonExist(): Boolean {
        return if (DBCourseData?.sectionList != null)
            checkIfLessonExistInDB(DBCourseData?.sectionList)
        else
            false
    }

    private fun checkIfLessonExistInDB(sectionList: ArrayList<OfflineSectionData>?): Boolean {
        sectionList?.let {
            for (i in 0 until (sectionList?.size ?: 0)) {
                if (sectionList[i].lessonList != null && sectionList[i].sectionId == viewModel.sectionId) {
                    return checkIfContainsLecture(sectionList[i].lessonList!!)
                }
            }
        }

        return false
    }

    private fun getSectionPositionInDB(sectionList: ArrayList<OfflineSectionData>?): Int {
        sectionList?.let {
            for (i in 0 until sectionList.size) {
                if (sectionList[i].sectionId == viewModel.sectionId) {
                    return i
                }
            }
        }

        return 0
    }

    private fun checkIfContainsLecture(sectionList: ArrayList<OfflineLessonData>): Boolean {
        for (i in 0 until sectionList.size) {
            if (sectionList[i].lessonId == viewModel.lectureId) {
                return true
            }
        }
        return false
    }

    //    override fun onDownloadFailed(
//        downloadRequest: DownloadRequest?,
//        errorCode: Int,
//        errorMessage: String?
//    ) {
//        actionDownload.isVisible = true
//        isDownloadClicked = false
//        downloadId = null
//
//        showToastLong(baseActivity.getString(R.string.download_failed))
//
//    }
    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(DOWNLOAD_COMPLETE_BROADCAST)
        try {

            LocalBroadcastManager.getInstance(baseActivity)
                .registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onStop() {
        super.onStop()
        try {


            LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        WebSocketManager.close()


    }

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                DOWNLOAD_COMPLETE_BROADCAST -> {
                    if (intent.extras?.getBoolean("downloadCompleted", false) == true) {
                        Log.e("download", ">>>>>>completed")
                        Toast.makeText(
                            baseActivity,
                            getString(R.string.download_completed),
                            Toast.LENGTH_LONG
                        ).show()
                        actionDownload.isVisible = false
                        isDownloadClicked = false
                    } else {
                        Log.e("download", ">>>>>>error")
                        Toast.makeText(
                            baseActivity,
                            getString(R.string.download_failed),
                            Toast.LENGTH_LONG
                        ).show()
                        actionDownload.isVisible = true
                        isDownloadClicked = false
                    }

                }
            }

        }
    }


//    override fun onProgress(
//        downloadRequest: DownloadRequest?,
//        totalBytes: Long,
//        downloadedBytes: Long,
//        progress: Int
//    ) {
//        Log.e("download", ">>>>>>progress>>> $progress")
//    }

    fun onClickBack() {
        if (arguments?.getInt("modType") == MODTYPE.LEARNER) {
            findNavController().popBackStack(R.id.courseDetailsFragment, false)

        } else {
            findNavController().popBackStack()

        }

    }
}




