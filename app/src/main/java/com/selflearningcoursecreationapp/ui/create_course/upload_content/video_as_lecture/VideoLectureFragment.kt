package com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentVideoLectureBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadVideoOptionsDialog
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.FileUtils.getFilePath
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit


class VideoLectureFragment : BaseFragment<FragmentVideoLectureBinding>(), (String?) -> Unit,
    /*Player.Listener,*/ HandleClick, BaseBottomSheetDialog.IDialogClick, View.OnTouchListener {
    private val viewModel: VideoLessonViewModel by viewModel()
    private val imagePickUtils: ImagePickUtils by inject()

    private var uriList: ArrayList<Uri>? = ArrayList()
    var thumbnailList: ArrayList<ThumbnailModel>? = ArrayList()
    private var type = 0

    private val playbackStateListener: androidx.media3.common.Player.Listener =
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
            viewModel.videoLiveData.value?.mFilePath = value.filePath
            viewModel.videoLiveData.value?.mLectureId = value.lectureId
            viewModel.videoLiveData.value?.mCourseId = value.courseId
            viewModel.videoLiveData.value?.mModel = value.sendSectionModel
            viewModel.videoLiveData.value?.mSectionId =
                viewModel.videoLiveData.value?.mModel!!.sectionId
            viewModel.videoLiveData.value?.mChildPosition = value.childPosition
            viewModel.videoLiveData.value?.mType = value.type
        }

        if (viewModel.videoLiveData.value?.mType == Constant.CLICK_ADD) {
            convertToFile()
        } else viewModel.getLectureDetail()
        if (!viewModel.videoLiveData.value?.mChildPosition.isNullOrNegative()) {
            binding.btnAddLesson.text = baseActivity.getString(
                R.string.update_lesson
            )
        }

        binding.edtTitle.setOnTouchListener(this)

        binding.edtTitle.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.btnAddLesson.text =
                    if (!viewModel.videoLiveData.value?.mChildPosition.isNullOrNegative()) {
                        baseActivity.getString(
                            R.string.update_lesson
                        )
                    } else {
                        baseActivity.getString(
                            R.string.add_lesson
                        )
                    }
                binding.card2.gone()

            } else {

                if (viewModel.thumbUri.isNullOrEmpty()) {
                    if (!viewModel.videoLiveData.value?.mChildPosition.isNullOrNegative()) {
                        baseActivity.getString(
                            R.string.update_lesson
                        )
                    } else {
                        baseActivity.getString(
                            R.string.add_lesson
                        )
                    }
                    binding.card2.visibleView(!viewModel.docLiveData.value?.thumbNailURl.isNullOrEmpty())

                } else {
                    binding.card2.visible()
                    binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
                }


            }
        }
    }


    private fun initUI() {
        setHasOptionsMenu(true)
        binding.videoLesson = viewModel
        binding.clickHandler = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.card2.gone()
        binding.ivCross.setOnClickListener {
            binding.card2.gone()
            Glide.with(baseActivity).clear(binding.ivThumbnailImage)
//            binding.btnAddThumbnail.visible()
//            binding.btnTakeFromGallary.gone()
//            binding.btnAddThumbnail.text = baseActivity.getString(R.string.add_thumbnail)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun getLayoutRes() = R.layout.fragment_video_lecture


    private fun uploadServer() {
        viewModel.uploadContent()
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    viewModel.mContentId = it.id.toString()
                    viewModel.videoLiveData.value?.mFilePath = it.fileUrl

//                    addFileToPLayer()
//                    initializePlayer()

                }
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()

                    it.thumbNailURl = viewModel.docLiveData.value?.thumbNailURl
                    if (viewModel.videoLiveData.value?.mChildPosition != null && viewModel.videoLiveData.value?.mChildPosition != -1) {
                        viewModel.videoLiveData.value?.mModel?.lessonList?.set(
                            viewModel.videoLiveData.value?.mChildPosition!!,
                            it
                        )
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))


                    } else {
                        viewModel.videoLiveData.value?.mModel?.lessonList?.add(it)
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
                    viewModel.videoLiveData.value?.mFilePath = it.lectureContentUrl
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
                if (type == 0) {
                    viewModel.docValidations()
                } else {
                    type = 0
                    uploadThumb()
                    binding.btnAddLesson.text = baseActivity.getString(R.string.add_lecture)
                    binding.btnTakeFromGallary.text =
                        baseActivity.getString(R.string.replace_thumbnail)
                }

            }
            R.id.btn_add_thumbnail -> {
//                binding.btnAddThumbnail.gone()
                changeVideoFunctionality()

//                binding.btnTakeFromGallary.visible()
            }
            R.id.btn_take_from_gallary -> {
                changeVideoFunctionality()
            }

        }
    }

    private fun changeVideoFunctionality() {

        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .getCallBack { b, strings, _ ->
                if (b) {
                    imagePickUtils.openGallery(
                        baseActivity,
                        this,
                        registry = baseActivity.activityResultRegistry
                    )
                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private fun handlePlayFunctionality() {
        if (player!!.isPlaying) {

            binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
            player!!.pause()

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
        binding.ivThumbnailImage.setImageURI(Uri.parse(p1))
        binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
//        binding.btnTakeFromGallary.gone()
//        binding.btnAddThumbnail.visible()
//        binding.btnAddThumbnail.text = baseActivity.getString(R.string.replace_thumbnail)
        type = 1
        viewModel.thumbUri = p1
//        thumbnailList?.add(ThumbnailModel(Uri.parse(p1)))
//        thumbnailAdapter()


//        showToastShort(p1.toString())

//        viewModel.videoLiveData.value?.mFilePath = p1.toString()
//        val file = File(Uri.parse(p1).path)

//        if (isFileLessThan5MB(file)) {
//            showToastShort("File size is more than 5 MB, not able to upload. Please select another file")
//        } else {
//        uploadThumb(file.name, file)
//        }

    }

    private fun convertToFile() {
        val file = File(Uri.parse(viewModel.videoLiveData.value?.mFilePath).path ?: "")
        uriList?.clear()
        uriList?.add(Uri.parse(viewModel.videoLiveData.value?.mFilePath))
        durationFromVideo(file.path)
        uploadServer()


    }


    private fun durationFromVideo(fileUrl: String?)
            : Long {

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(fileUrl)
        val duration =
            (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        val centreIndex = duration?.toLongOrNull()?.div(2)
        val bitmap = /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            retriever.primaryImage
        }else{*/
            retriever.getFrameAtTime(centreIndex ?: 0)
//        }

        setThumbnail(bitmap?.getFilePath(baseActivity))
        retriever.release()
        viewModel.mDuration = duration?.toLongOrNull() ?: 0L
        val totalSec =
            TimeUnit.SECONDS.convert(viewModel.mDuration, TimeUnit.MILLISECONDS)
        binding.tvTimer.text = viewModel.mDuration?.getTime(baseActivity)
        return viewModel.mDuration
    }

    private fun setThumbnail(filePath: String?) {
        filePath?.let { path ->
//          binding.card2.visible()
            viewModel.docLiveData.value?.thumbNailURl = path
            binding.ivThumbnailImage.setImageURI(Uri.parse(path))
//            binding.btnAddLesson.text = baseActivity.getString(R.string.add_thumbnail)
            type = 1
            viewModel.thumbUri = path
        }
    }

    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        viewModel.videoLiveData.value?.mFilePath = items[1] as String
        when (type) {
            MediaType.VIDEO -> {
                if (isFileLessThan5MB(File(viewModel.videoLiveData.value?.mFilePath ?: ""))) {
                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                } else {
//                    setupPlayer()
                    initializePlayer()
                    convertToFile()
                }
            }
        }

    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }


    override fun onResume() {
        super.onResume()
//        Log.d("varun2", "onResume: ${viewModel.videoLiveData.value}")
        initializePlayer()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    private fun initializePlayer() {
        viewModel.videoLiveData.value?.mFilePath?.let {
            player = ExoPlayer.Builder(requireContext())
                .build()
                .also { exoPlayer ->
                    binding.videoView.player = exoPlayer
                    val mediaItem =
                        MediaItem.fromUri(viewModel.videoLiveData.value?.mFilePath!!)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = false
                    exoPlayer.seekTo(currentItem, playbackPosition)
                    exoPlayer.addListener(playbackStateListener)
                    exoPlayer.prepare()
                }
        }
    }


//    override fun onStart() {
//        super.onStart()
//        if (Util.SDK_INT > 23) {
//            initializePlayer()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
////        hideSystemUi()
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            initializePlayer()
//        }
//    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
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
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
            exoPlayer.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                }
                ExoPlayer.STATE_BUFFERING -> {
                    binding.progressBar.visible()

                }
                ExoPlayer.STATE_READY -> {
                    binding.progressBar.gone()
                    viewModel.mDuration = player!!.duration
                    val totalSec =
                        TimeUnit.SECONDS.convert(player!!.duration, TimeUnit.MILLISECONDS)
//                    binding.tvTimer.text = baseActivity.getQuantityString(
//                        R.plurals.min_quantity_small,
//                        DateUtils.formatElapsedTime(totalSec)?.toIntOrNull() ?: 0
//                    )
                    binding.tvTimer.text = viewModel.mDuration?.getTime(baseActivity)

                }
                ExoPlayer.STATE_ENDED -> {
                    binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
                    player!!.seekTo(0)
                    player!!.playWhenReady = false
                }
                else -> "UNKNOWN_STATE             -"
            }
//            Log.d(TAG, "changed state to $stateString")
        }
    }
}