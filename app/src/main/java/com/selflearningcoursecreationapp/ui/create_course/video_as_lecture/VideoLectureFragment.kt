package com.selflearningcoursecreationapp.ui.create_course.video_as_lecture

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.*
import com.selflearningcoursecreationapp.databinding.FragmentVideoLectureBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadVideoOptionsDialog
import com.selflearningcoursecreationapp.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.util.concurrent.TimeUnit


class VideoLectureFragment : BaseFragment<FragmentVideoLectureBinding>(), (String?) -> Unit,
    Player.Listener, HandleClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: VideoLessonViewModel by inject()
    private val imagePickUtils: ImagePickUtils by inject()
    var mContentId = ""
    var mThumbId = ""

    var mDuration = 0L
    var uriList: ArrayList<Uri>? = ArrayList()
    var thumbnailList: ArrayList<ThumbnailModel>? = ArrayList()
    private lateinit var player: ExoPlayer

    //    var thumnailAdapter: ThumbnailAdapter? = null
    var type = 0
    var thumbUri: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        arguments?.let {
//            (it as VideoModel)
            val value = VideoLectureFragmentArgs.fromBundle(it)
//            viewModel.videoLiveData.value. = it
            viewModel.videoLiveData.value?.mFilePath = value.filePath
            viewModel.videoLiveData.value?.mLectureId = value.lectureId
            viewModel.videoLiveData.value?.mCourseId = value.courseId
            viewModel.videoLiveData.value?.mModel = value.sendSectionModel
            viewModel.videoLiveData.value?.mSectionId =
                viewModel.videoLiveData.value?.mModel!!.sectionId
            viewModel.videoLiveData.value?.mChildPosition = value.childPosition
            viewModel.videoLiveData.value?.mType = value.type
        }

//        Log.d("varun1", "onViewCreated: ${mFilePath}")
        setupPlayer()

        if (viewModel.videoLiveData.value?.mType == Constant.CLICK_ADD) convertToFile() else viewModel.getLectureDetail(
            viewModel.videoLiveData.value?.mLectureId!!
        )
        if (!viewModel.videoLiveData.value?.mChildPosition.isNullOrNegative()) {
            binding.btnAddLesson.setText(
                baseActivity.getString(
                    R.string.update_lesson
                )
            )
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
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun getLayoutRes() = R.layout.fragment_video_lecture


    fun uploadServer(fileName: String, file: File, durationFromVideo: String?) {
        viewModel.uploadContent(
            viewModel.videoLiveData.value?.mCourseId,
            viewModel.videoLiveData.value?.mSectionId,
            viewModel.videoLiveData.value?.mLectureId!!,
            fileName,
            file,
            MEDIA_TYPE.VIDEO,
            "",
            durationFromVideo!!.toInt()
        )
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    mContentId = it.id.toString()
                    addFileToPLayer(it.fileUrl)

                }
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()


                    if (viewModel.videoLiveData.value?.mChildPosition != null && viewModel.videoLiveData.value?.mChildPosition != -1) {
                        viewModel.videoLiveData.value?.mModel?.lessonList?.set(
                            viewModel.videoLiveData.value?.mChildPosition!!,
                            it
                        )
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))


                    } else {
                        viewModel.videoLiveData.value?.mModel?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
//                        /findNavController().popBackStack(R.id.preVideoSelectFragment, true)
                    }
                    findNavController().popBackStack()
                }

            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    binding.edtTitle.setText(it.lectureTitle)
                    mContentId = it.lectureContentId.toString()
//                    mFilePath = it.lectureContentUrl.toString()

                    addFileToPLayer(it.lectureContentUrl.toString())
                }
            }
            ApiEndPoints.API_THUMBNAIL_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    mThumbId = it.id.toString()


                }

            }
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(baseActivity).build()
        binding.videoView.player = player
        player.addListener(this)

    }

    private fun addFileToPLayer(fileUrl: String?) {
        val mediaItem = MediaItem.fromUri(fileUrl.toString())
        player.addMediaItem(mediaItem)
        player.prepare()
//        durationFromVideo(fileUrl)
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }


    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                binding.progressBar.visible()

            }
            Player.STATE_READY -> {
                binding.progressBar.gone()
                mDuration = player.getDuration()
                val totalSec =
                    TimeUnit.SECONDS.convert(player.getDuration(), TimeUnit.MILLISECONDS)
                binding.tvTimer.text = DateUtils.formatElapsedTime(totalSec) + " mins"
            }
            Player.STATE_ENDED -> {
                binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
                player.seekTo(0);
                player.setPlayWhenReady(false)

            }
        }
    }

    fun uploadThumb(name: String, file: File) {
        viewModel.uploadThumbnail(
            viewModel.videoLiveData.value?.mCourseId,
            viewModel.videoLiveData.value?.mSectionId,
            viewModel.videoLiveData.value?.mLectureId!!,
            name,
            file
        )
    }

    override fun onHandleClick(vararg items: Any) {
        var view = items[0] as View
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
                    viewModel.docValidations(
                        viewModel.videoLiveData.value?.mLectureId,
                        viewModel.videoLiveData.value?.mSectionId,
                        viewModel.videoLiveData.value?.mCourseId,
                        binding.edtTitle.content(),
                        mContentId,
                        MEDIA_TYPE.VIDEO,
                        mThumbId,
                        mDuration.toString()
                    )
                } else {
                    type = 0
                    val file = File(Uri.parse(thumbUri).path)
                    uploadThumb(file.name, file)
                    binding.btnAddLesson.setText("Add Lesson")
                    binding.btnTakeFromGallary.setText("REPLACE THUMBNAIL")
                }

            }
            R.id.btn_add_thumbnail -> {

                binding.btnAddThumbnail.gone()
                binding.btnTakeFromGallary.visible()


            }
            R.id.btn_take_from_gallary -> {
                changeVideoFunctionality()
            }

        }
    }

    private fun changeVideoFunctionality() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!PermissionUtil.checkPermissions(requireActivity())) {
                requestPermission()
            } else {
                imagePickUtils.openGallery(
                    baseActivity,
                    this,
                    registry = requireActivity().activityResultRegistry
                )
            }
        } else {
            PermissionUtil.checkPermissions(
                baseActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                Permission.GALLERY
            ) {
                if (it) {
                    imagePickUtils.openGallery(
                        baseActivity,
                        this,
                        registry = baseActivity.activityResultRegistry
                    )

                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    ) {
                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))

                    } else {
                        baseActivity.permissionDenied()
                    }
                }
            }
        }
    }

    private fun handlePlayFunctionality() {
        if (player.isPlaying) {

            binding.ivPlayVideo.setImageResource(R.drawable.ic_audio_indicaor)
            player.pause()

        } else {

            player.play()
            binding.ivPlayVideo.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

        }
    }

    private fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(
                String.format(
                    "package:%s",
                    requireContext().getApplicationContext().getPackageName()
                )
            )
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, 100)
        }

    }

    override fun invoke(
        p1: String?,
    ) {
        binding.card2.visible()
        viewModel.docLiveData.value?.thumbNailURl = p1
        binding.ivThumbnailImage.setImageURI(Uri.parse(p1))
        binding.btnAddLesson.setText("Add Thumbnail")
        type = 1
        thumbUri = p1
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

    fun convertToFile() {
        val file = File(Uri.parse(viewModel.videoLiveData.value?.mFilePath).path)
        uriList?.clear()
        uriList?.add(Uri.parse(viewModel.videoLiveData.value?.mFilePath))
        uploadServer(file.name, file, "0")
//    processVideo(duration)

//                uploadServer (file.name, file, durationFromVideo(mFilePath))


    }


    fun durationFromVideo(
        fileUrl: String?,
    )
            : String
    ? {

        var retriever = MediaMetadataRetriever();
        retriever.setDataSource(fileUrl);
        var duration =
            (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        retriever.release();
        mDuration = duration!!.toLong()
        val totalSec =
            TimeUnit.SECONDS.convert(duration.toLong(), TimeUnit.MILLISECONDS)
        binding.tvTimer.text = DateUtils.formatElapsedTime(totalSec) + " mins"
        return duration
    }

    override fun onDialogClick(vararg items: Any) {
        var type = items[0] as Int
        viewModel.videoLiveData.value?.mFilePath = items[1] as String
        when (type) {
            MEDIA_TYPE.VIDEO -> {
                if (isFileLessThan5MB(File(viewModel.videoLiveData.value?.mFilePath))) {
                    showToastShort("File size is more than 5 MB, not able to upload. Please select another file")
                } else {
                    setupPlayer()
                    convertToFile()
                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun processVideo(duration: String?) {

        GlobalScope.launch {
            VideoCompressor.start(
                context = SelfLearningApplication.applicationContext(),
                uris = uriList!!,
                isStreamable = true,
                saveAt = Environment.DIRECTORY_MOVIES,
                listener = object : CompressionListener {
                    override fun onProgress(index: Int, percent: Float) {
//TODO:ffd
                    }

                    override fun onStart(index: Int) {
                        baseActivity.showProgressBar()
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        val file = File(path)
                        FileUtils.getDriveFilePath(Uri.parse(path), requireContext())
                        Log.d(
                            "varun1",
                            "onViewCreated: ${
                                FileUtils.getRealPathFromURI(
                                    requireContext(),
                                    Uri.parse(path)
                                )
                            }"
                        )
//                        durationFromVideo(Uri.parse(file.path).toString())
                        uploadServer(file.name, file, duration)
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        baseActivity.hideProgressBar()


                    }

                    override fun onCancelled(index: Int) {
                        baseActivity.hideProgressBar()
                        // make UI changes, cleanup, etc
                    }
                },
                configureWith = Configuration(
                    quality = VideoQuality.LOW,
                    isMinBitrateCheckEnabled = false,
                )
            )
        }
    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }

//    fun thumbnailAdapter() {
//
//        thumnailAdapter?.notifyDataSetChanged() ?: kotlin.run {
//            thumnailAdapter = ThumbnailAdapter(thumbnailList)
//            binding.recylerThumbnail.apply {
//                visible()
//                adapter = thumnailAdapter
//
//            }
//
//        }
//        thumnailAdapter?.setOnAdapterItemClickListener(this)
//    }
//
//    override fun onItemClick(vararg items: Any) {
//        val type = items[0] as Int
//        val position = items[1] as Int
//        when (type) {
//            Constant.CLICK_ADD -> {
//                binding.btnAddLesson.text = " Add Thumbnail"
//
//            }
//            Constant.CLICK_DELETE -> {
//                thumbnailList?.removeAt(position)
//                thumnailAdapter?.notifyDataSetChanged()
//            }
//
//        }
//
//    }
}