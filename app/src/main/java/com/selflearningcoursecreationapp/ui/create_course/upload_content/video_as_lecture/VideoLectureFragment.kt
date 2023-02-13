package com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentVideoLectureBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.UploadMetaData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadVideoOptionsDialog
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.FileUtils.saveBitmapToFile
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
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit


class VideoLectureFragment : BaseFragment<FragmentVideoLectureBinding>(), (String?) -> Unit,
    Player.Listener, HandleClick, BaseBottomSheetDialog.IDialogClick, View.OnTouchListener {
    private lateinit var uploadTask: CoRoutineUploadTask
    private val viewModel: VideoLessonViewModel by viewModel()
    private val imagePickUtils: ImagePickUtils by inject()
    private lateinit var client: TusClient

    private var uriList: ArrayList<Uri>? = ArrayList()
    var thumbnailList: ArrayList<ThumbnailModel>? = ArrayList()
    private var type = 0

    private val playbackStateListener: Player.Listener =
        playbackStateListener()
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private var player: ExoPlayer? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        arguments?.let {
            val value = VideoLectureFragmentArgs.fromBundle(it)
            viewModel.lessonArgs = value.lessonArgs
        }

        if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {


            showLoading(baseActivity.getString(R.string.processing_file))
            CoroutineScope(Dispatchers.IO).launch {
                delay(200)
                if (!viewModel.lessonArgs?.filePath.isNullOrEmpty()) {
                    viewModel.uri = async {
                        call(viewModel.lessonArgs?.filePath.toString())
                    }.await()
                } else {
                    viewModel.uri = ""
                }
                updateUI(viewModel.uri, viewModel.lessonArgs?.filePath.toString(), true)
            }

        } else {
            viewModel.getLectureDetail()
            binding.btnAddLesson.text = baseActivity.getString(
                R.string.update_lesson
            )
        }
//        if (!viewModel.videoLiveData.value?.mChildPosition.isNullOrNegative()) {
//        }

        binding.edtTitle.setOnTouchListener(this)
        binding.btnAddLesson.text =
            if (viewModel.lessonArgs?.type == Constant.CLICK_EDIT) {
                baseActivity.getString(
                    R.string.update_lesson
                )
            } else {
                baseActivity.getString(
                    R.string.add_lesson
                )
            }
//        binding.edtTitle.doOnTextChanged { text, start, before, count ->
//            if (text.isNullOrEmpty()) {
//
////                binding.card2.gone()
//
//            } else {
//
////                if (viewModel.thumbUri.isNullOrEmpty()) {
//                if (viewModel.lessonArgs?.type == Constant.CLICK_EDIT) {
//                    baseActivity.getString(
//                        R.string.update_lesson
//                    )
//                } else {
//                    baseActivity.getString(
//                        R.string.add_lesson
//                    )
//                }
////                binding.card2.visibleView(!viewModel.docLiveData.value?.thumbNailURl.isNullOrEmpty())
////                binding.btnAddThumbnail.visibleView(viewModel.docLiveData.value?.thumbNailURl.isNullOrEmpty())
//
//
////                } else {
////                    binding.card2.visible()
////                    binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
////                }
//
//
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    private suspend fun updateUI(uri: String, p1: String?, goBack: Boolean = false) {
        withContext(Dispatchers.Main) {
            try {

                val file = File(Uri.parse(p1.toString()).path ?: "")
                Log.d("TextAndroid", "invoke: after path ${file}")


                if (uri.isFileLimitExceed(1024)) {
                    showToastLong(baseActivity.getString(R.string.file_limit_alert_text))
                    if (goBack) findNavController().navigateUp()
                }/* else if (regex.matcher(file.name).find()) {
            showToastShort(baseActivity.getString(R.string.plz_remove_special))
        }*/
                else {
                    viewModel.filePath = p1 ?: ""
                    viewModel.playerUrl = uri
                    viewModel.uri = uri
                    viewModel.uri_tus = p1.toString()

                    initializePlayer()
                    convertToFile()

                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToastShort(getString(R.string.some_error_occure_please_try_again))
                if (goBack) {
                    findNavController().navigateUp()
                }
            }
            hideLoading()

        }
    }

    private fun initUI() {
        initProgressBar()
        callMenu()
        binding.videoLesson = viewModel
        binding.clickHandler = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.card2.gone()
        val buttonview = binding.videoView.findViewById<ImageButton>(R.id.exo_fullscreen)
        buttonview.setImageDrawable(
            ContextCompat.getDrawable(
                baseActivity,
                R.drawable.ic_edit_white
            )
        )

        binding.videoView.findViewById<Group>(R.id.group_video).gone()
        binding.videoView.findViewById<ConstraintLayout>(R.id.layout_header_view).gone()
//        binding.videoView.findViewById<Group>(R.id.group_video).gone()
        buttonview.setOnClickListener {

            UploadVideoOptionsDialog().apply {
                setOnDialogClickListener(this@VideoLectureFragment)
            }.show(childFragmentManager, "sd")
        }
        binding.ivCross.setOnClickListener {
            binding.card2.gone()
        }
    }


    override fun getLayoutRes() = R.layout.fragment_video_lecture


    private fun uploadServer() {
        viewModel.uploadMetaData()
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    viewModel.mContentId = it.id.toString()
                    viewModel.filePath = it.fileUrl ?: ""

                }
            }
            ApiEndPoints.API_UPLOAD_METADATA -> {
                (value as BaseResponse<UploadMetaData>).resource?.let {
                    viewModel.mContentId = it.contentId.toString()
                    uploadFileInPackets(
                        Uri.parse(viewModel.filePath),
                        it.contentId!!
                    )


//                    addFileToPLayer()
//                    initializePlayer()

                }
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()

                    it.thumbNailURl = viewModel.docLiveData.value?.thumbNailURl
//                    if (viewModel.videoLiveData.value?.mModel?.lessonList == null) {
//                        viewModel.videoLiveData.value?.mModel?.lessonList = ArrayList()
//                    }

                    if (viewModel.lessonArgs?.type == Constant.CLICK_EDIT/*viewModel.videoLiveData.value?.mChildPosition != null && viewModel.videoLiveData.value?.mChildPosition != -1 && viewModel.videoLiveData.value?.mModel?.lessonList?.isNotEmpty() == true*/) {
//                        viewModel.videoLiveData.value?.mModel?.lessonList?.set(
//                            viewModel.videoLiveData.value?.mChildPosition ?: 0,
//                            it
//                        )
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))


                    } else {
//                        viewModel.videoLiveData.value?.mModel?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
                    findNavController().popBackStack()
                }

            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {

                (value as BaseResponse<ChildModel>).resource?.let {
                    viewModel.docLiveData.value?.thumbNailURl = it.thumbNailURl
                    binding.edtTitle.setText(it.lectureTitle)
                    viewModel.mContentId = it.lectureContentId.toString()
                    viewModel.playerUrl = it.lectureContentUrl
//                    mFilePath = it.lectureContentUrl.toString()
//                    setupPlayer()
//                    addFileToPLayer()
                    initializePlayer()
                    if (!it.thumbNailURl.isNullOrEmpty()) {
                        binding.card2.visible()
                        binding.ivThumbnailImage.loadImage(
                            it.thumbNailURl,
                            R.drawable.ic_home_default_banner
                        )
                        binding.btnAddThumbnail.visible()
                        binding.btnAddThumbnail.text =
                            baseActivity.getString(R.string.take_from_gallary)
                    }
//                    Glide.with(requireContext()).load(it.thumbNailURl)
//                        .into(binding.ivThumbnailImage)
//                    binding.btnTakeFromGallary.setText("REPLACE THUMBNAIL")


                }
            }
        }
    }

//    private fun setupPlayer() {
//        player = ExoPlayer.Builder(baseActivity).build()
//        binding.videoView.player = player
//        player.addListener(this)
//
//    }
//
//    private fun addFileToPLayer() {
////        showToastShort(viewModel.videoLiveData.value?.mFilePath!!)
//        val mediaItem = MediaItem.fromUri(viewModel.videoLiveData.value?.mFilePath!!)
//        player.addMediaItem(mediaItem)
//        player.prepare()
//        durationFromVideo(fileUrl)
//    }
////
//    override fun onStop() {
//        super.onStop()
//        player.release()
//    }


//    override fun onPlaybackStateChanged(state: Int) {
//        when (state) {
//            Player.STATE_BUFFERING -> {
//                binding.progressBar.visible()
//
//            }
//            Player.STATE_READY -> {
//                binding.progressBar.gone()
//                viewModel.mDuration = player!!.duration
//                val totalSec =
//                    TimeUnit.SECONDS.convert(player!!.duration, TimeUnit.MILLISECONDS)
//                binding.tvTimer.text = baseActivity.getQuantityString(
//                    R.plurals.min_quantity_small,
//                    DateUtils.formatElapsedTime(totalSec)?.toIntOrNull() ?: 0
//                )
//            }
//            Player.STATE_ENDED -> {
//                binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
//                player!!.seekTo(0)
//                player!!.playWhenReady = false
//
//            }
//            Player.STATE_IDLE -> {
//                //implement functionality for idle state
//            }
//        }
//    }

    private fun uploadThumb() {
        viewModel.uploadThumbnail()
    }

    override fun onHandleClick(vararg items: Any) {
        val view = items[0] as View
        when (view.id) {
            R.id.iv_play_video -> {
                handlePlayFunctionality()
            }
            R.id.iv_edit_video -> {
                UploadVideoOptionsDialog().apply {
                    setOnDialogClickListener(this@VideoLectureFragment)
                }.show(childFragmentManager, "sd")
            }
            R.id.btn_add_lesson -> {
//                if (type == 0) {
                try {

                    player?.stop()
                    player?.removeListener(playbackStateListener)
                    player?.release()
                    player = null
                    viewModel.docValidations()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToastShort(getString(R.string.some_error_occure_please_try_again))
                }
//                } else {
//                    type = 0
//                    uploadThumb()
//                    binding.btnAddLesson.text = baseActivity.getString(R.string.add_lecture)
//                    binding.btnTakeFromGallary.text =
//                        baseActivity.getString(R.string.replace_thumbnail)
//                }

            }
            R.id.btn_add_thumbnail -> {
//                binding.btnAddThumbnail.gone()
                changeVideoFunctionality()

//                binding.btnTakeFromGallary.visible()
            }
//            R.id.btn_take_from_gallary -> {
//                changeVideoFunctionality()
//            }
            R.id.ivSignly -> {
                showCommingSoonDialog(getString(R.string.coming_soon))
            }

        }
    }

    private fun changeVideoFunctionality() {

        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .getCallBack { b, strings, _ ->
                if (b) {
                    imagePickUtils.openGallery(
                        baseActivity,
                        {
                            baseActivity.showProgressBar()
                            CoroutineScope(Dispatchers.IO).launch {
                                val path = FileUtils.getPath(baseActivity, Uri.parse(it))
                                withContext(Dispatchers.Main)
                                {
                                    baseActivity.hideProgressBar()

                                    imagePickUtils.cropImage(
                                        baseActivity,
                                        path,
                                        type == DialogType.CLICK_BANNER, this@VideoLectureFragment
                                    )
                                }
                            }
                        }
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private fun handlePlayFunctionality() {
        if (player?.isPlaying == true) {

            binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
            player?.pause()

        } else {
            player?.play()
            binding.ivPlayVideo.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

        }
    }


    override fun invoke(
        p1: String?,
    ) {
        binding.card2.visible()
        viewModel.docLiveData.value?.thumbNailURl = p1
//        binding.ivThumbnailImage.setImageURI(Uri.parse(p1))
        binding.ivThumbnailImage.loadImage(
            Uri.parse(p1).toString(),
            R.drawable.ic_home_default_banner
        )
        binding.btnAddThumbnail.visible()
        binding.btnAddThumbnail.text =
            baseActivity.getString(R.string.take_from_gallary)
//        binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
        type = 1
        viewModel.thumbUri = p1
        if (viewModel.thumbUri.isNullOrEmpty()) {
            binding.btnAddThumbnail.text = baseActivity.getString(R.string.add_thumbnail)
        } else {
            binding.btnAddThumbnail.text =
                baseActivity.getString(R.string.take_from_gallary)
        }


    }

//    suspend fun call(p1: String?): String {
//        return FileUtils.getPath(
//            SelfLearningApplication.applicationContext(), Uri.parse(p1)
//        ).toString()
//    }

    private fun convertToFile() {
//        hideLoading()
//        val file = File(Uri.parse(viewModel.filePath).path ?: "")
        uriList?.clear()
        uriList?.add(Uri.parse(viewModel.filePath))

//        var filePath = try {
//            FileUtils.getPath(
//                SelfLearningApplication.applicationContext(),
//                Uri.parse(viewModel.filePath)
//            )!!
//        } catch (e: Exception) {
//            viewModel.filePath
//        }
        try {
            durationFromVideo(viewModel.uri, viewModel.uri_tus)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        getDurationFromFleUri(Uri.parse(viewModel.videoLiveData.value?.mFilePath))
        getHeader(Uri.parse(viewModel.filePath), "")
//        uploadServer()

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
        progressBar.applyPrimaryTint()
        val status: TextView = dialog.findViewById(R.id.status)
        val cancelIV: ImageView = dialog.findViewById(R.id.cancel_IV)
        val resumeButton: LMSMaterialButton = dialog.findViewById(R.id.resume_button)
        val callback = object : VideoUploadProgress {
            override fun progressUpdate(progress: Int) {
                baseActivity.runOnUiThread {
                    progressBar.progress = progress

                }

            }

            override fun setStatus(text: String) {
                baseActivity.runOnUiThread {

                    status.text = text + "%"
                    if (text.contains("99")) {
                        pauseButton.isEnabled = false
                        resumeButton.isEnabled = false
                        pauseButton.setSecondaryBtnDisabled(false)
                        resumeButton.setBtnDisabled(false)
                    } else if (text.contains("100")) {
                        dialog.dismiss()
                        showLoading()
                    }
                }

            }


            override fun uploadedUrl(url: URL) {
                baseActivity.runOnUiThread {
                    viewModel.filePath = url.toString()
                    viewModel.playerUrl = url.toString()
                    viewModel.uploadThumbnail()
                    try {
                        dialog.dismiss()
                    } catch (e: Exception) {
                        showLog("close dialog", e.message.toString())
                    }
                }
            }

            override fun onUploadCanceled(exception: Exception?) {
//                Log.e("upload canceled", exception.toString())
                baseActivity.runOnUiThread {
                    if (exception != null)
                        showError(exception)
                }
            }

            override fun setPauseButtonEnabled(enabled: Boolean) {
                baseActivity.runOnUiThread {
                    pauseButton.setSecondaryBtnDisabled(enabled)
                    pauseButton.isEnabled = enabled
                    resumeButton.isEnabled = !enabled
                    resumeButton.setBtnDisabled(!enabled)
                    hideLoading()
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
            showLoading()
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
        headers["Upload-Type"] = MediaType.VIDEO.toString()
        headers["Upload-Extension"] = "creation"
        return headers
    }

    private fun getHeader(uri: Uri?, contentId: String): HashMap<String, String> {
        val headers: HashMap<String, String> = HashMap()
        val fileName: String = getFileName(uri)
        viewModel.fileName = fileName
        val cR = activity?.contentResolver
        val type = uri?.getMimeType(baseActivity)
        viewModel.mimeType = type ?: ""
//        showToastShort(type.toString())
        try {

            val upload: TusUpload = TusAndroidUpload(uri, baseActivity)
            viewModel.fileSize = upload.size
        } catch (e: Exception) {
            e.printStackTrace()
            showToastShort(getString(R.string.some_error_occure_please_try_again))
        }
        headers["name"] = fileName
        headers["contentType"] = "$type || application/octet-stream"
        headers["emptyMetaKey"] = ""
        headers["Upload-Type"] = MediaType.VIDEO.toString()
        headers["X-Content-Id"] = contentId
        return headers
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri?): String {
        var result: String? = null
        if (uri == null) {
            showToastShort(getString(R.string.some_error_occure_please_try_again))
            return ""
        }
        try {


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
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
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

    private fun showError(e: java.lang.Exception) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(e.message)
        val dialog = builder.create()
        dialog.show()
        e.printStackTrace()
    }

    //    private fun getDurationFromFleUri(uri: Uri): Long {
//        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = context?.contentResolver?.query(uri, filePathColumn, null, null, null)
//        cursor?.moveToFirst()
//
//        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
//        val picturePath = cursor?.getString(columnIndex!!)
//        cursor?.close()
//
//        val bitmap = ThumbnailUtils.createVideoThumbnail(
//            picturePath.toString(),
//            MediaStore.Video.Thumbnails.MICRO_KIND
//        )
//        setThumbnail(bitmap?.getFilePath(baseActivity))
//
//        var durationTime: Long
//        MediaPlayer.create(context, uri).also {
//            durationTime = (it.duration / 1000).toLong()
//            it.reset()
//            it.release()
//
//            viewModel.mDuration = durationTime
//            binding.tvTimer.text = viewModel.mDuration.getTime(baseActivity)
//            return viewModel.mDuration
//        }
//
//    }
    @Throws(Exception::class)
    private fun durationFromVideo(fileUrl: String?, uriTus: String)
            : Long {
        var inputStream: FileInputStream? = null

        val retriever = MediaMetadataRetriever()
        try {

            retriever.setDataSource(fileUrl)
        } catch (e: Exception) {
            val resolver = baseActivity.contentResolver
            val fd =
                resolver.openFileDescriptor(Uri.parse(uriTus), "r") ?: throw FileNotFoundException()
            try {
                retriever.setDataSource(fd.fileDescriptor)
                fd.close()
            } catch (e: IOException) {
                Log.e("TusAndroidUpload", "unable to close ParcelFileDescriptor", e)
            }


//            inputStream=  resolver.openInputStream(Uri.parse(uriTus))
//            inputStream = FileInputStream(File(fileUrl).absolutePath);

        }
        val duration =
            (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        val centreIndex = duration?.toLongOrNull()?.div(2)
//        val bitmap = retriever.getFrameAtTime(centreIndex ?: 0)
        val bitmap = retriever.getFrameAtTime(0)
        if (bitmap != null)
            setThumbnail(bitmap.saveBitmapToFile(baseActivity), bitmap)
        retriever.release()
        inputStream?.close()
        viewModel.mDuration = duration?.toLongOrNull() ?: 0L
        val totalSec =
            TimeUnit.SECONDS.convert(viewModel.mDuration, TimeUnit.MILLISECONDS)
        binding.tvTimer.text = viewModel.mDuration.getTime(baseActivity)
        return viewModel.mDuration
    }

    private fun setThumbnail(filePath: String?, bitmap: Bitmap) {
        filePath?.let { path ->
//          binding.card2.visible()
            viewModel.docLiveData.value?.thumbNailURl = path
//            binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
            type = 1
            viewModel.thumbUri = path
//            binding.ivThumbnailImage.setImageURI(Uri.parse(path))
            binding.ivThumbnailImage.setImageBitmap(bitmap)
            binding.btnAddThumbnail.text = baseActivity.getString(R.string.take_from_gallary)

            binding.card2.visible()

        }
    }

    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int

        when (type) {
            MediaType.VIDEO -> {
                CoroutineScope(IO).launch {
//                    val uri = async { com.selflearningcoursecreationapp.extensions.call(filePath.toString()) }.await()
                    updateUI(items[1] as String, items[2] as String)
                }
//                if (viewModel.filePath.isFileLimitExceed(1024)) {
//                    showToastLong(baseActivity.getString(R.string.file_limit_alert_text))
//                } else {
////                    setupPlayer()
//                    initializePlayer()
//                    convertToFile()
//                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onException(
        isNetworkAvailable: Boolean,
        exception: ApiError,
        apiCode: String
    ) {
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
                if (apiCode == ApiEndPoints.API_UPLOAD_METADATA) {
                    showToastShort(exception.message.toString())
                    findNavController().popBackStack()
                }
            }
        }

    }

    private fun initializePlayer() {
        try {
            viewModel.playerUrl?.let { url ->
                player?.stop()
                player?.removeListener(playbackStateListener)
                player?.release()
                player = null
                player = ExoPlayer.Builder(baseActivity)
                    .build()
                    .also { exoPlayer ->
                        binding.videoView.player = exoPlayer
                        val mediaItem =
                            MediaItem.fromUri(url)
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.playWhenReady = false
                        exoPlayer.seekTo(currentItem, playbackPosition)
                        exoPlayer.addListener(playbackStateListener)
                        exoPlayer.addListener(object : Player.EventListener {
                            override fun onPlayerError(error: PlaybackException) {
                                super.onPlayerError(error)
                                if (isAdded) {
                                    binding.fileFormatNotSupportedTV.visibility = View.VISIBLE
                                    binding.fileFormatNotSupportedTV.text =
                                        getString(R.string.file_format_is_not_supported)
                                }


                            }


                        })
                        exoPlayer.prepare()
                    }


            }
        } catch (e: Exception) {
            showLog("load", e.stackTrace.toString())
        }

    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            player?.pause()
        }
    }

    override fun onDetach() {
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.stop()
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {


        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
            super.onSeekForwardIncrementChanged(seekForwardIncrementMs)
            binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).progress =
                player?.contentPosition?.toInt() ?: 0

        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
            super.onSeekBackIncrementChanged(seekBackIncrementMs)
            binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).progress =
                player?.contentPosition?.toInt() ?: 0
        }


        override fun onEvents(player: Player, events: Player.Events) {
            binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).progress =
                player.contentPosition.toInt()

//        Log.d("varun", "onEvents: yo")


//        if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) || events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
//            if (WebSocketManager.isConnect()) {
//                sendData()
//            } else if (isAdded && isVisible) {
//                initWebSocket()
//            }
//        }

        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                setVideoProgress()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                }
                ExoPlayer.STATE_BUFFERING -> {
                    binding.progressBar.visible()

                }
                ExoPlayer.STATE_READY -> {
                    binding.progressBar.gone()

                    player?.let {
                        viewModel.mDuration = player!!.duration
                        val totalSec =
                            TimeUnit.SECONDS.convert(player!!.duration, TimeUnit.MILLISECONDS)
//                    binding.tvTimer.text = baseActivity.getQuantityString(
//                        R.plurals.min_quantity_small,
//                        DateUtils.formatElapsedTime(totalSec)?.toIntOrNull() ?: 0
//                    )
                        binding.tvTimer.text = viewModel.mDuration.getTime(baseActivity)
                        binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).max =
                            viewModel.mDuration.toInt()

                        binding.videoView.findViewById<TextView>(R.id.exo_duration1).text =
                            viewModel.mDuration.getTime(baseActivity, showHrs = false)
                    }


                }
                ExoPlayer.STATE_ENDED -> {
                    binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
                    player?.seekTo(0)
                    player?.playWhenReady = false
                }
                else -> getString(R.string.unknown_state)
            }
//            Log.d(TAG, "changed state to $stateString")
        }
    }


    fun onClickBack(isOpen: Boolean = true) {
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

    private fun setVideoProgress() {
        val speed = player?.playbackParameters?.speed
        val delay = when (speed) {
            1f -> {
                1000L
            }
            2f -> {
                500L
            }
            else -> {
                250L
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(delay)
            withContext(Dispatchers.Main)
            {
                binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).progress =
                    player?.currentPosition?.toInt() ?: 0
                if (player?.isPlaying == true) {
                    setVideoProgress()
                }
            }
        }
    }

    fun initProgressBar() {
        binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).apply {
            progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.live_red))
            progressTintMode = PorterDuff.Mode.SRC_IN
            thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.live_red))
            thumbTintMode = PorterDuff.Mode.SRC_IN
            indeterminateTintList =
                ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.live_red))
            indeterminateTintMode = PorterDuff.Mode.SRC_IN

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        try {

                            player?.seekTo(p1.toLong())
                        } catch (e: Exception) {
                            showException(e)
                        }
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
        }


    }
}


fun Uri.getMimeType(context: Context): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> context.contentResolver.getType(this)
        ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(toString()).lowercase(Locale.US)
        )
        else -> null
    }
}


