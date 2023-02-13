package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentDocLessonBinding
import com.selflearningcoursecreationapp.extensions.applyPrimaryTint
import com.selflearningcoursecreationapp.extensions.call
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isFileLimitExceed
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.UploadMetaData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import com.selflearningcoursecreationapp.utils.customViews.LMSMaterialButton
import com.selflearningcoursecreationapp.utils.fileUpload.CoRoutineUploadTask
import com.selflearningcoursecreationapp.utils.fileUpload.VideoUploadProgress
import io.tus.android.client.TusAndroidUpload
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.net.URL
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.regex.Pattern


class DocLessonFragment : BaseFragment<FragmentDocLessonBinding>(), (String?) -> Unit,
    View.OnTouchListener {
    private var fileSize_tus: Long = 0L
    private lateinit var mimeType_tus: String
    private var fileName_tus: String = ""
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: DocViewModel by viewModel()
    var regex = Pattern.compile("[$&+,:;=\\\\?@#|'<>^*()%!]")

    //    private var childPosition: Int? = -1
    private lateinit var client: TusClient
    private lateinit var uploadTask: CoRoutineUploadTask

    private var type: Int? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.docs = viewModel
        binding.edtDocTitle.setOnTouchListener(this)

        arguments?.let {
            val lessonData = DocLessonFragmentArgs.fromBundle(it)
            viewModel.lessonArgs = lessonData.lessonArgs
//            childPosition = lessonData.childPosition
            type = lessonData.lessonArgs?.type
//            viewModel.model = lessonData.sendSectionModel
//            viewModel.lectureId = lessonData.lectureId
//            viewModel.courseId = lessonData.courseId
            viewModel.uri_tus = viewModel.lessonArgs?.filePath ?: ""
            showLoading(baseActivity.getString(R.string.processing_file))
            CoroutineScope(IO).launch {
                if (!lessonData.lessonArgs?.filePath.isNullOrEmpty()) {
                    viewModel.uri = async {
                        call(lessonData.lessonArgs?.filePath.toString())
                    }.await()
                } else {
                    viewModel.uri = ""
                }

                if (type == Constant.CLICK_ADD) {
                    updateUI(viewModel.uri, lessonData.lessonArgs?.filePath.toString(), true)
                } else {
                    viewModel.getLectureDetail()
//            binding.btnAddLesson.text = baseActivity.getString(R.string.update_lesson)
                }
            }
        }


//            if (viewModel.uri.isFileLimitExceed(1024)) {
//                showToastLong(baseActivity.getString(R.string.file_limit_alert_text))
//                findNavController().popBackStack()
//            } else if (regex.matcher(viewModel.uri).find()) {
//                showToastShort(baseActivity.getString(R.string.plz_remove_special))
//                findNavController().popBackStack()
//            }
//            else if (checkSpecialCharacter(file.name)) {
//                showToastShort(baseActivity.getString(R.string.plz_remove_special))
//                activity?.onBackPressed()
//
//            }
//            else {
//                setDataFromFile()
//            }
//        } else {
//            viewModel.getLectureDetail()
//            binding.btnAddLesson.text = baseActivity.getString(R.string.update_lesson)
//        }


        binding.ivEditPdf.setOnClickListener {

            openDoc()
        }

        binding.btnAddLesson.setOnClickListener {
            viewModel.docValidations()
        }
        binding.ivSignly.setOnClickListener { showCommingSoonDialog(getString(R.string.coming_soon)) }

    }

    override fun getLayoutRes() = R.layout.fragment_doc_lesson


    private fun setDataFromFile() {

        viewModel.notifyData()
        val file = File(Uri.parse(viewModel.uri).path ?: "")
        viewModel.docLiveData.value?.lectureContentName = file.name
        binding.tvDocSize.text = humanReadableByteCountSI(file.length())
        getHeader(Uri.parse(viewModel.uri_tus), "");
        viewModel.fileName = fileName_tus
        viewModel.fileSize = fileSize_tus
        viewModel.mimeType = mimeType_tus
//        uploadServer()
    }

    fun humanReadableByteCountSI(byte: Long): String {
        var bytes = byte
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }

    override fun invoke(p1: String?) {
//        showToastShort("yes")
        Log.d("TextAndroid", "invoke: pre")
        showLoading(baseActivity.getString(R.string.processing_file))
        CoroutineScope(IO).launch {
            val uri = async { call(p1.toString()) }.await()
            updateUI(uri, p1)
        }
        Log.d("TextAndroid", "invoke: post")
//        }
    }

    private suspend fun updateUI(uri: String, p1: String?, goBack: Boolean = false) {
        withContext(Main) {
            try {


                val file = File(Uri.parse(p1.toString()).path ?: "")
                Log.d("TextAndroid", "invoke: after path ${file}")

                if (uri.endsWith("pdf", true)) {
                    checkingFileSize(100, uri, p1)
                } else {
                    checkingFileSize(25, uri, p1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToastShort(getString(R.string.some_error_occure_please_try_again))
            }
        }
    }

    private fun checkingFileSize(value: Int, uri: String, p1: String?) {
        if (uri.isFileLimitExceed(value)) {
            showToastLong(
                if (value == 100) baseActivity.getString(R.string.pdf_limit_alert_text) else baseActivity.getString(
                    R.string.doc_limit_alert_text
                )
            )
            if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
                setFragmentResult(
                    "onLessonBack",
                    bundleOf("isDialogOpen" to true)
                )
            }
            findNavController().popBackStack()
        }/* else if (regex.matcher(file.name).find()) {
            showToastShort(baseActivity.getString(R.string.plz_remove_special))
        }*/
        else if (regex.matcher(uri).find()) {
            showToastShort(baseActivity.getString(R.string.plz_remove_special))
            if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
                setFragmentResult(
                    "onLessonBack",
                    bundleOf("isDialogOpen" to true)
                )
            }
            findNavController().popBackStack()
        } else {
            viewModel.uri = uri
            viewModel.uri_tus = p1.toString()
            setDataFromFile()
        }
        hideLoading()
    }


    private fun openDoc() {
        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .requestCode(Permission.DOC)
            .getCallBack { b, perms, i ->
                if (b) {
                    imagePickUtils.openDocs(
                        baseActivity,
                        this
                    )
                } else {
                    baseActivity.handlePermissionDenied(perms)
                }

            }
            .build()


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureContentName = binding.edtDocTitle.content()
//                    if (viewModel.model?.lessonList == null) {
//                        viewModel.model?.lessonList = ArrayList()
//                    }
                    if (type == Constant.CLICK_EDIT/*&& viewModel.model?.lessonList?.isNotEmpty() == true*/) {
//                        viewModel.model?.lessonList?.set(childPosition ?: 0, it)
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))

                    } else {
//                        viewModel.model?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
                }
//                viewModel.model?.notifyPropertyChanged(BR.uploadLesson)
//                lifecycleScope.launch {
//                    binding.btnAddLesson.isEnabled
//                    delay(3000)
//                    baseActivity?.runOnUiThread {
//                     findNavController().navigateUp()
//                    }
//                }

//                findNavController().popBackStack(R.id.addCourseBaseFragment, false)
                findNavController().navigateUp()
//                baseActivity.onBackPressed()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
//                super.onResponseSuccess(value, apiCode)
                (value as BaseResponse<ImageResponse>).resource?.let {
                    viewModel.contentId = it.id.toString()
                }
            }
            ApiEndPoints.API_UPLOAD_METADATA -> {
                (value as BaseResponse<UploadMetaData>).resource?.let {
                    viewModel.contentId = it.contentId.toString()
                    uploadFileInPackets(
                        Uri.parse(viewModel.uri_tus),
                        it.contentId!!
                    )


//                    addFileToPLayer()
//                    initializePlayer()

                }
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
//                super.onResponseSuccess(value, apiCode)
                (value as BaseResponse<ChildModel>).resource?.let {
                    viewModel.contentId = it.lectureContentId.toString()

                    binding.tvDocName.text = it.lectureContentName
                    binding.edtDocTitle.setText(it.lectureTitle)
                    val min = (it.lectureContentDuration)?.div(60000)
                    binding.edtReadTime.setText(min.toString())
//                    binding.tvDocSize.text = android.text.format.Formatter.formatFileSize(
//                        requireActivity(),
//                        it.lectureContentSize!!
//                    )
                    binding.tvDocSize.text = humanReadableByteCountSI(
                        it.lectureContentSize!!
                    )
                }
            }
        }
    }

    private fun uploadServer() {
//        viewModel.uploadContent()
//        getHeader(Uri.parse(viewModel.uri_tus), "");
//        viewModel.fileName = fileName_tus
//        viewModel.fileSize = fileSize_tus
//        viewModel.mimeType = mimeType_tus
//        viewModel.uploadMetaData()
    }

    private fun getHeader(uri: Uri?, contentId: String): HashMap<String, String> {
        val headers: HashMap<String, String> = HashMap()
        fileName_tus = getFileName(uri!!)
        val cR = activity?.contentResolver
        val type = cR?.getType(uri!!)
        mimeType_tus = type ?: ""
        val upload: TusUpload = TusAndroidUpload(uri, baseActivity)
        fileSize_tus = upload.size

        headers["name"] = fileName_tus
        headers["contentType"] = "$type || application/octet-stream"
        headers["emptyMetaKey"] = ""
        headers["Upload-Type"] = MediaType.AUDIO.toString()
        headers["X-Content-Id"] = contentId
        return headers
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = activity?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    private fun uploadFileInPackets(fileUri: Uri?, contentId: String) {
        val dialog = Dialog(baseActivity, R.style.DialogTransparent)
        dialog.setCancelable(false)

        try {
            val pref: SharedPreferences = baseActivity.getSharedPreferences("tus", 0)
            client = TusClient()
            client.uploadCreationURL =
                URL(BuildConfig.UPLOAD_FILE_URL)
            client.enableResuming(TusPreferencesURLStore(pref))
            client.enableRemoveFingerprintOnSuccess()
            client.headers = getAuthHeader()

        } catch (e: java.lang.Exception) {
            showError(e)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.show_video_progress_dialog)
        val pauseButton: LMSMaterialButton = dialog.findViewById(R.id.pause_button)
        val progressBar: ContentLoadingProgressBar = dialog.findViewById(R.id.progressBar)
        val status: TextView = dialog.findViewById(R.id.status)
        val cancelIV: ImageView = dialog.findViewById(R.id.cancel_IV)
        val resumeButton: LMSMaterialButton = dialog.findViewById(R.id.resume_button)
        progressBar.applyPrimaryTint()
        val callback = object : VideoUploadProgress {
            override fun progressUpdate(progress: Int) {
                baseActivity.runOnUiThread {

                    progressBar.progress = progress

                }
            }

            override fun setStatus(text: String) {
                baseActivity.runOnUiThread {

                    status.text = text + "%"
                    if (text.toString().contains("99")) {
                        pauseButton.isEnabled = false
                        resumeButton.isEnabled = false
                        pauseButton.setSecondaryBtnDisabled(false)
                        resumeButton.setBtnDisabled(false)
                    }
                }

            }

            override fun uploadedUrl(url: URL) {
                baseActivity.runOnUiThread {

                    dialog.dismiss()
                    viewModel.addPatchLecture()
                }
            }

            override fun onUploadCanceled(exception: Exception?) {
                baseActivity.runOnUiThread {
//                Log.e("upload canceled", exception.toString())
                    if (exception != null)
                        showError(exception)
                }
            }

            override fun setPauseButtonEnabled(enabled: Boolean) {
                baseActivity.runOnUiThread {
                    hideLoading()
                    pauseButton.isEnabled = enabled
                    resumeButton.isEnabled = !enabled
                    pauseButton.setSecondaryBtnDisabled(enabled)
                    resumeButton.setBtnDisabled(!enabled)
                }
            }

        }

        pauseButton.setOnClickListener {
            showLoading()
            pauseUpload(uploadTask)
        }
        cancelIV.setOnClickListener {
            pauseUpload(uploadTask)
            dialog.dismiss()
            activity?.onBackPressed()
        }

        resumeButton.setOnClickListener {
            resumeUpload(dialog, client, callback, fileUri, contentId)
        }

        dialog.setOnShowListener {
            try {
                val upload: TusUpload = TusAndroidUpload(fileUri, baseActivity)
                val pref: SharedPreferences = baseActivity.getSharedPreferences("tus", 0)
                TusPreferencesURLStore(pref).remove(upload.fingerprint)

                upload.metadata = getHeader(fileUri, contentId)
                uploadTask = CoRoutineUploadTask(dialog, client, upload, callback)
                uploadTask.execute(*arrayOfNulls<Void>(0))
            } catch (e: Exception) {
                showError(e)
            }
        }


        dialog.show()

    }

    private fun getAuthHeader(): HashMap<String, String>? {
        val headers: HashMap<String, String> = HashMap()
//        headers["Upload-Length"] = viewModel.videoLiveData.value?.fileSize.toString()
        headers["Authorization"] = "Bearer ${SelfLearningApplication.token}"
        headers["Upload-Type"] = MediaType.DOC.toString()
        headers["Upload-Extension"] = "creation"
        return headers
    }

    private fun showError(e: java.lang.Exception) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(e.message)
        val dialog = builder.create()
        dialog.show()
        e.printStackTrace()
    }

    private fun pauseUpload(uploadTask: CoRoutineUploadTask?) {
        uploadTask?.cancel(false)
    }

    private fun resumeUpload(
        dialog: Dialog,
        client: TusClient,
        progressCallback: VideoUploadProgress, fileUri: Uri?, contentId: String
    ) {
        try {
            val upload: TusUpload = TusAndroidUpload(fileUri, baseActivity)
            upload.metadata = getHeader(fileUri, contentId)
            uploadTask = CoRoutineUploadTask(dialog, client, upload, progressCallback)
            uploadTask.execute(*arrayOfNulls<Void>(0))
        } catch (e: java.lang.Exception) {
            showError(e)
        }
    }


    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.NO_CONTENT -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .apply {

                        description(baseActivity.getString(R.string.the_content_not_found))

                    }
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        baseActivity.onBackPressed()
                    }
                    .build()
            }
            HTTPCode.CO_AUTHOR_ACCESS_DENIED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_rejected_account)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)
                    }
                    .build()
            }
            HTTPCode.CONTENT_DELETED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .apply {

                        description(exception.message ?: "")

                    }
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        baseActivity.onBackPressed()
                    }
                    .build()
            }

            HTTPCode.FORBIDDEN -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)
                    }
                    .build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }

    }


    private fun checkSpecialCharacter(fileName: String): Boolean {
        fileName.contains("~") ||
                fileName.contains("@") ||
                fileName.contains("#") ||
                fileName.contains("|") ||
                fileName.contains("^") ||
                fileName.contains("$") ||
                fileName.contains("%") ||
                fileName.contains("&") ||
                fileName.contains("!") ||
                fileName.contains("*")

        return false

    }

    fun onClickBack(isOpen: Boolean = true) {
//        if (type == Constant.CLICK_ADD) {
//            setFragmentResult(
//                "onLessonBack",
//                bundleOf("isDialogOpen" to isOpen)
//            )
//        }
//        findNavController().popBackStack()
        confirmationPopUp(isOpen)
    }

    private fun confirmationPopUp(isOpen: Boolean) {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(false)
            .positiveBtnText(baseActivity.getString(R.string.yes))
            .negativeBtnText(baseActivity.getString(R.string.no))
            .title(baseActivity.getString(R.string.alerte))
            .description(getString(R.string.are_you_do_not_want_to_save_lesson))
            .getCallback {
                if (it) {
                    if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
                        setFragmentResult(
                            "onLessonBack",
                            bundleOf("isDialogOpen" to isOpen)
                        )
                    }
                    findNavController().popBackStack()
                }

            }.notCancellable(false).icon(R.drawable.ic_alert_title)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}



