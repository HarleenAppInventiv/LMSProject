package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentAudioLectureBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.models.course.UploadMetaData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadAudioOptionsDialog
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.LMSMaterialButton
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.fileUpload.CoRoutineUploadTask
import com.selflearningcoursecreationapp.utils.fileUpload.VideoUploadProgress
import io.tus.android.client.TusAndroidUpload
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit


class AudioLectureFragment : BaseFragment<FragmentAudioLectureBinding>(),
    HandleClick, BaseBottomSheetDialog.IDialogClick, View.OnTouchListener,
    SeekBar.OnSeekBarChangeListener {
    private var job: Job? = null
    private var fileSize_tus: Long = 0L
    private lateinit var mimeType_tus: String
    private lateinit var fileName_tus: String
    private lateinit var client: TusClient
    private lateinit var uploadTask: CoRoutineUploadTask

    private val viewModel: AudioLessonViewModel by viewModel()
    private var fromListener = false

    //    private var childPosition: Int? = -1
    private var filePath = ""
    private var filePath_tus = ""
    private var mediaFrom = 0

    private var mediaPlayer: MediaPlayer? = null

    private var mCountDownTimer: CountDownTimer? = null
    private var type: Int? = null
    private var timerRunning = false

    private var mLastStopTime = 0L
    private var countTime = 0L
    private var mute = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        callMenu()
        binding.seekbar.progressDrawable?.setColorFilter(
            ThemeUtils.getAppColor(baseActivity),
            PorterDuff.Mode.SRC_IN
        )

        activityResultListener()
        binding.audioLesson = viewModel
        binding.handleClick = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        getBundleData()
        binding.seekbar.setOnSeekBarChangeListener(this)

        binding.seekbar.apply {
            progressTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(baseActivity))
            progressTintMode = PorterDuff.Mode.SRC_IN
//            indeterminateTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(baseActivity))
//            indeterminateTintMode = PorterDuff.Mode.SRC_IN
            backgroundTintList = ColorStateList.valueOf(
                ThemeUtils.getLightestColor(
                    ThemeUtils.getAppColor(baseActivity)
                )
            )
            backgroundTintMode = PorterDuff.Mode.ADD
        }

        showLoading()

        if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
            if (!fromListener) {
                job = CoroutineScope(IO).launch {
                    Log.d("check", "initUI: pre")
                    filePath = async { call(filePath_tus) }.await()
                    Log.d("check", "initUI: mid")

                    updateUI(1)
//                if (filePath.isFileLimitExceed(1024)) {
//                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
//                    findNavController().popBackStack()
////                activity?.onBackPressed()
//                } else {
//                    duration()
//                }
                }
            } else {
                hideLoading()
            }
        } else {

            if (!fromListener) {
                fromListener = true
                viewModel.getLectureDetail()
            }
            binding.btnAddLesson.text = baseActivity.getString(R.string.update_lesson)


        }

//        if (!childPosition.isNullOrNegative()) {
//        }
        binding.edtTitle.setOnTouchListener(this)


    }

    private fun getBundleData() {
        arguments?.let {

            val value = AudioLectureFragmentArgs.fromBundle(it)
            viewModel.lessonArgs = value.lessonArgs
            type = value.lessonArgs?.type
            if (filePath_tus.isNullOrEmpty())
                filePath_tus = value.lessonArgs?.filePath ?: ""

//            if (viewModel.lessonArgs?.type == Constant.CLICK_ADD && filePath.isNullOrEmpty()) {
//                filePath = try {
//                    FileUtils.getPath(
//                        SelfLearningApplication.applicationContext(),
//                        Uri.parse(value.lessonArgs?.filePath)
//                    )!!
//                } catch (e: Exception) {
//                    value.lessonArgs?.filePath ?: ""
//                }
//                filePath_tus = value.lessonArgs?.filePath ?: ""
//            }


//            filePath = try {
//                FileUtils.getPath(
//                    SelfLearningApplication.applicationContext(),
//                    Uri.parse(value.filePath)
//                )!!
//            } catch (e: Exception) {
//                value.filePath
//            }

//            viewModel.lectureId = value.lectureId
//            viewModel.courseId = value.courseId
//            viewModel.model = value.sendSectionModel
//            viewModel.sectionId = viewModel.model?.sectionId
//            childPosition = value.childPosition
//            mediaFrom = value.from
        }
    }


    override fun getLayoutRes() = R.layout.fragment_audio_lecture
//


//    override fun invoke(p1: String?) {
//        if (mediaPlayer != null) {
//            pauseTimer()
//
//        }
//        filePath = p1.toString()
//        convertToFile()
//
//    }

    private fun setMediaPlayer() {
        binding.ivAudioVolume.setImageResource(R.drawable.ic_baseline_volume_up_24)
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer = MediaPlayer()

//        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            mediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mediaPlayer?.setDataSource(baseActivity, Uri.parse(filePath))
            mediaPlayer?.prepare()
            val millis = mediaPlayer?.duration
            val totalSec = TimeUnit.SECONDS.convert(millis!!.toLong(), TimeUnit.MILLISECONDS)
            viewModel.milliSecond = millis.toLong()
            countTime = millis.toLong()
            binding.tvTimerMax.text = String.format("/%s", millis.toLong().getTime(baseActivity))
            binding.seekbar.apply {
                max = millis
                progress = 0
            }
        } catch (e: Exception) {
            showException(e)
        }

    }


//


    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        mCountDownTimer?.cancel()
        mCountDownTimer = null
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideLoading()
    }

    private fun uploadServer(file: File, millisecond: Long) {
        getHeader(Uri.parse(filePath_tus), "")
        viewModel.milliSecond = millisecond
        viewModel.fileName = fileName_tus
        viewModel.fileSize = fileSize_tus
        viewModel.mimeType = mimeType_tus
//        viewModel.uploadMetaData()


//        viewModel.uploadContent(
//            file,
//            millisecond
//        )
    }

    private fun getHeader(uri: Uri?, contentId: String): HashMap<String, String> {
        val headers: HashMap<String, String> = HashMap()
        fileName_tus = getFileName(uri!!)
        val cR = activity?.contentResolver
        val type = filePath?.let {
            "audio/" + it.substringAfterLast(".")


        } ?: kotlin.run { "audio/mp3" }
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
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CONTENT_UPLOAD -> {

                (value as BaseResponse<ImageResponse>).resource?.let {
                    setMediaPlayer()
                    viewModel.contentId = it.id.toString()
                }
            }
            ApiEndPoints.API_UPLOAD_METADATA -> {
                (value as BaseResponse<UploadMetaData>).resource?.let {
                    viewModel.contentId = it.contentId.toString()
                    uploadFileInPackets(
                        Uri.parse(filePath_tus),
                        it.contentId!!
                    )


//                    addFileToPLayer()
//                    initializePlayer()

                }
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()

//                    if (viewModel.model?.lessonList == null) {
//                        viewModel.model?.lessonList = ArrayList()
//                    }


                    if (/*!childPosition.isNullOrNegative() && viewModel.model?.lessonList?.isNotEmpty() == true*/ type == Constant.CLICK_EDIT) {
//                        viewModel.model?.lessonList?.set(childPosition ?: 0, it)
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))

                    } else {
//                        viewModel.model?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
//                    if (mediaFrom == MEDIA_FROM.RECORDING) {
//                        findNavController().popBackStack(R.id.recordAudioFragment, true)
//
//                    } else {
                    findNavController().popBackStack(R.id.addCourseBaseFragment, false)

//                    }

                }

            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    binding.edtTitle.setText(it.lectureTitle)
                    viewModel.contentId = it.lectureContentId.toString()
                    filePath = it.lectureContentUrl.toString()
                    setMediaPlayer()
                }
            }
        }
    }

    private fun convertToFile(millisecond: Long) {
        val file = File(Uri.parse(filePath).path ?: "")

        uploadServer(file, millisecond)

//        if (filePath.endsWith("mp3")) {
//
//        } else {
//            showToastShort(baseActivity.getString(R.string.please_upload_file_of_mp3_extension))
//        }

    }

    override fun onHandleClick(vararg items: Any) {
        val view = items[0] as View
        when (view.id) {
            R.id.iv_audio_edit -> {
                UploadAudioOptionsDialog().apply {
                    setOnDialogClickListener(this@AudioLectureFragment)
                }.show(childFragmentManager, "sd")
                resetTimer()
            }
            R.id.iv_audio_volume -> {
                if (mute) {
                    mute = false
                    mediaPlayer?.setVolume(1f, 1f)
                    binding.ivAudioVolume.setImageResource(R.drawable.ic_baseline_volume_up_24)
                } else {
                    mute = true
                    mediaPlayer?.setVolume(0f, 0f)
                    binding.ivAudioVolume.setImageResource(R.drawable.ic_baseline_volume_off_24)
                }
            }
            R.id.btn_add_lesson -> {
                if (timerRunning) {
                    pauseTimer()
                }
                viewModel.docValidations()

            }
            R.id.iv_audio_play -> {
                if (timerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }

        }
    }

    suspend fun call(p1: String): String {
        return try {

            FileUtils.getPath(
                SelfLearningApplication.applicationContext(),
                Uri.parse(p1)
            ) ?: p1
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private suspend fun updateUI(i: Int) {
        withContext(Main) {
            try {
                Log.d("check", "initUI: post")

                if (filePath.isFileLimitExceed(1024)) {
                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                    if (i == 1) {
                        findNavController().popBackStack()
                    }
                } else {
                    duration()
                }
                hideLoading()
            } catch (e: Exception) {
                Log.d("check", "initUI: expe")

                e.printStackTrace()
                hideLoading()
            }
        }


    }

    override fun onDialogClick(vararg items: Any) {
        when (items[0] as Int) {
            MediaType.AUDIO -> {

                var filePath_ = items[1] as String

                val action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToEditAudioFragment(
                        fromLesson = true,
                        lessonArgs = LessonArgs(filePath = filePath_)
                    )
                findNavController().navigateTo(action)

            }
            MediaFrom.EDITING -> {
                val action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToEditAudioFragment(
                        fromLesson = true,
                        lessonArgs = LessonArgs(filePath = filePath_tus)
                    )
                findNavController().navigateTo(action)
            }
            MediaFrom.RECORDING -> {
//                val lessonArgs= LessonArgs(
//                    courseId = viewModel.lessonArgs?.courseId,
//                    sectionId = viewModel.lessonArgs?.sectionId,
//
//                )

                val action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToRecordAudioFragment(
                        fromLesson = true
                    )
                findNavController().navigateTo(action)
            }

        }
    }

    private fun startTimer() {
//        binding.tvTimer.apply {
//            base = if (mLastStopTime == 0L) {
//                SystemClock.elapsedRealtime()
//            } else {
//                val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
//                base + intervalOnPause
//            }
//            start()
//        }
        mediaPlayer?.start()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

        showRunningProgress()

//        if (mCountDownTimer != null) {
//            mCountDownTimer?.cancel()
//        }
//        mCountDownTimer = object : CountDownTimer(countTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                if (isAdded && isVisible) {
//                    binding.seekbar.progress = mediaPlayer?.currentPosition ?: 0
//                    countTime = millisUntilFinished
//                }
//            }
//
//            override fun onFinish() {
////                binding.seekbar.progress = 100
////                timerRunning = false
////                binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
//                resetTimer()
//            }
//        }
//        mCountDownTimer?.start()
        timerRunning = true

    }

    private fun showRunningProgress() {
        lifecycleScope.launch {
            delay(1000)
            baseActivity.runOnUiThread {
                binding.seekbar.progress = mediaPlayer?.currentPosition ?: 0
                binding.tvTimer.text =
                    if (mediaPlayer?.currentPosition.isNullOrZero()) "00:00:00" else {
                        (mediaPlayer?.currentPosition ?: 0).toLong()
                            .getTime(baseActivity, showHrs = true)
                    }
                if (mediaPlayer?.currentPosition ?: 0 < mediaPlayer?.duration ?: 0) {
                    showRunningProgress()
                } else {
                    resetTimer()
                }
            }
        }
    }


    private fun pauseTimer() {
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        mediaPlayer?.pause()
//        binding.tvTimer.stop()
        mLastStopTime = SystemClock.elapsedRealtime()
        mCountDownTimer?.cancel()
        timerRunning = false

    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    private fun resetTimer() {

        mLastStopTime = 0L
        binding.seekbar.progress = 0
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        timerRunning = false
//        binding.tvTimer.apply {
//            stop()
//            base = SystemClock.elapsedRealtime()
//        }
        mediaPlayer?.reset()
        setMediaPlayer()
    }


    fun duration() {
        setMediaPlayer()
        val millis = mediaPlayer?.duration?.toLong()
        convertToFile(millis ?: 0L)
//        val millis =if (!filePath.isNullOrEmpty()) {
//  try {
//
//
//      val retriever = MediaMetadataRetriever()
//      retriever.setDataSource(filePath)
//      (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))?.toLongOrNull() ?: 0
//  }catch (e:Exception)
//  {
//      showException(e)
//      mediaPlayer?.duration?.toLong()
//
//  }
//}else {
//        val millis = mediaPlayer?.duration?.toLong()
////}
//
//        convertToFile(millis ?: 0L)

//

//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(requireContext(), Uri.parse(filePath))
//        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        convertToFile(durationStr?.toLongOrNull() ?: 0)
//}
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    private fun getAuthHeader(): HashMap<String, String>? {
        val headers: HashMap<String, String> = HashMap()
//        headers["Upload-Length"] = viewModel.videoLiveData.value?.fileSize.toString()
        headers["Authorization"] = "Bearer ${SelfLearningApplication.token}"
        headers["Upload-Type"] = MediaType.AUDIO.toString()
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
                    Log.e("uploaded url tus>>>", url.toString())
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
            findNavController().popBackStack()
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
                    .description(baseActivity.getString(R.string.creator_revoked_access_text))
                    .title("")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
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
                when (apiCode) {
                    ApiEndPoints.API_UPLOAD_METADATA ->
                        if (exception.statusCode == 400) {
                            findNavController().popBackStack()


                        }
                }
            }
        }

    }

    private fun activityResultListener() {
        setFragmentResultListener(
            "onRecordCallback"
        ) { _, bundle ->
            val value = bundle.getString("recordedUri")
            val fromRecord = bundle.getBoolean("fromRecord")
            showLog("RESULT_LISTENER", "value >> $value")
            Log.d("text", "activityResultListener: yes")
            fromListener = true
            if (fromRecord) {
                val action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToEditAudioFragment(
                        fromLesson = true,
                        lessonArgs = LessonArgs(filePath = value)
                    )
                findNavController().navigateTo(action)
                fromListener = false
            } else {
                if (value.isFileLimitExceed(1024)) {
                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                } else {
                    job?.cancel()
                    showLoading()
                    CoroutineScope(IO).launch {
                        filePath = async { call(value.toString()) }.await()
                        filePath_tus = value ?: ""
                        withContext(Main) {
                            duration()
                            hideLoading()
                            fromListener = false

                        }

                    }


                }

            }


        }

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

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        showLog("SEEKBAR", "p1 >> $p1 ... p2 >> $p2")
        if (p2) {
            mediaPlayer?.seekTo(p1)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

}