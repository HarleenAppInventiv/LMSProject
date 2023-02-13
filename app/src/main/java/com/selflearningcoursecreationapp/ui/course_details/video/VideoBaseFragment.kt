package com.selflearningcoursecreationapp.ui.course_details.video


import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionOverrides
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentVideoBaseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.offlineCourse.AddOfflineCourseVM
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseVM
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.ui.splash.MessageListener
import com.selflearningcoursecreationapp.ui.splash.VideoAudioProgress
import com.selflearningcoursecreationapp.ui.splash.WebSocketManager
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.downloadManager.DOWNLOAD_COMPLETE_BROADCAST
import com.selflearningcoursecreationapp.utils.downloadManager.FileDownloadService
import com.selflearningcoursecreationapp.utils.richView.util.Utils
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class VideoBaseFragment : BaseFragment<FragmentVideoBaseBinding>(), MessageListener,
    Player.Listener {
    private var job: Job? = null
    private var downloadingUrl: String? = ""
    private lateinit var trackSelector: DefaultTrackSelector
    var qualityList = ArrayList<Pair<String, TrackSelectionOverrides.Builder>>()
    var speedArray = arrayOf("1x", "2x", "3x")
    private var qualityPopUp: PopupMenu? = null
    private var isDownloadClicked = false
    private var changeSpeed: PopupMenu? = null
    private var player: ExoPlayer? = null
    private var childModel: ChildModel? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var exoHeight = 250
    private val viewModel: ContentDetailViewModel by viewModel()
    private var streamingUrl: String? = null
    private lateinit var timer: Timer
    var fullscreen: Boolean = false
    lateinit var window: Window
    private var playbackPosition = 0L
    private val DBViewModel: OfflineCourseVM by viewModel()
    private val addOfflineCourseVM: AddOfflineCourseVM by viewModel()
    private var offlineCourseData: OfflineCourseData? = null
    private var DBCourseData: OfflineCourseData? = null
    private lateinit var actionRead: MenuItem
    private lateinit var actionDownload: MenuItem
    private var title: String = ""
    private var expandClick: ImageButton? = null

    override fun getLayoutRes() = R.layout.fragment_video_base
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    offlineCourseData =
                        viewModel.lessonList.get(arguments?.getInt("innerPosition") ?: 0)
                            ?.getOfflineCourseData(
                                viewModel.userProfile?.name ?: "",
                                viewModel.userProfile?.id ?: 0,
                                viewModel.courseId
                            )
                    downloadFile()
                } else
                    Toast.makeText(
                        context,
                        getString(R.string.downloading_is_in_progrss),
                        Toast.LENGTH_LONG
                    ).show()
                true
            }
            R.id.action_read -> {
                baseActivity.checkAccessibilityService()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_download)
        item.icon?.colorFilter = PorterDuffColorFilter(
            ThemeUtils.getAppColor(baseActivity),
            PorterDuff.Mode.SRC_IN
        )

    }


    private fun initUI() {
        initProgressBar()
        if (arguments?.getInt("modType") == MODTYPE.LEARNER && viewModel.lessonList.size != 1) {

            if (arguments?.getInt("innerPosition") == null && arguments?.getInt("courseStatus") == CourseStatus.NOT_ENROLLED) {
                binding.childFrame.gone()

            } else {
                binding.childFrame.visible()

            }
        }

        "section" to viewModel.sectionList

        arguments.let {
            viewModel.lectureId = it?.getInt("lectureId") ?: 0
            viewModel.courseId = it?.getInt("courseId") ?: 0


            if (it?.getInt("courseId") == 0) {
                viewModel.courseId = it.getString("courseId")?.toInt() ?: 0
            } else {
                viewModel.courseId = it?.getInt("courseId") ?: 0
            }
            title = it?.getString("title") ?: ""
//            viewModel.sectionId = it?.getInt("sectionId") ?: 0
            baseActivity.setToolbar(it?.getString("title"))
            viewModel.modType = it?.getInt("type") ?: 0

            viewModel.lessonList =
                it?.getParcelableArrayList<ChildModel>("lessonList") ?: ArrayList()
            viewModel.sectionList =
                it?.getParcelableArrayList<SectionModel>("section") ?: ArrayList()



            if (viewModel.modType == MODTYPE.LEARNER) {
                viewModel.sectionId =
                    viewModel.lessonList.get(it?.getInt("innerPosition") ?: 0)?.sectionId ?: 0
                offlineCourseData = it?.get("offlineCourseData") as OfflineCourseData?

            }

        }
        window = baseActivity.window

//        if ()


        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        DBViewModel.getApiResponse().observe(viewLifecycleOwner, this)
        addOfflineCourseVM.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getSectionContentDetails()
        setHasOptionsMenu(true)
        val list = arrayListOf<Fragment>(
            TranscriptFragment(),
            SignLanguageFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.transcript),
            baseActivity.getString(R.string.sign_language)
        )
        viewModel.getSectionContentDetails()
        binding.viewPager.isUserInputEnabled = false

        binding.viewPager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = nameArray[position]
        }.attach()
        binding.tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        viewModel.playSignVideoLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                viewModel.isSignVideoPlaying = true
                releasePlayer()
                signPlayer()
            }


        }

        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {

                    (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT_BOLD
                    (tab?.customView as TextView?)?.isAllCaps = false
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT
                    (tab?.customView as TextView?)?.isAllCaps = false

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    Log.d("main", "")

                }
            })


        binding.childView.tvPrevious.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                showLog("WEB_SOCKET", "previous click")
                sendData()
                player?.pause()
                WebSocketManager.close()

                if ((arguments?.getInt("position")
                        ?: -1) == 0 && (arguments?.getInt("innerPosition")
                        ?: -1) == 0
                )
                    return

                navigationToContent(
                    arguments?.getInt("position") ?: 0,
                    arguments?.getInt("innerPosition")?.minus(1) ?: 0,
                    MODTYPE.LEARNER,
                    true
                )


            }


        })



        binding.childView.tvNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
//                if (binding.childView.tvNext.text.equals(R.string.finish)) {
//                } else {
                showLog("WEB_SOCKET", "next click")
                sendData()
                player?.pause()
                WebSocketManager.close()

                if (viewModel.lessonList.get(
                        arguments?.getInt("innerPosition") ?: -1
                    )?.lectureId != viewModel.lessonList.last()?.lectureId
                ) {
                    navigationToContent(
                        arguments?.getInt("position") ?: 0,
                        arguments?.getInt("innerPosition")?.plus(1) ?: 0,
                        MODTYPE.LEARNER,
                        true
                    )


                } else {

                    findNavController().popBackStack(R.id.courseDetailsFragment, false)

                }


            }

        })


    }


    private fun signPlayer() {

        WebSocketManager.close()
        player?.pause()
        player?.release()

        if (childModel?.mediaType != 2) {

            binding.videoView.findViewById<Group>(R.id.group_video).visibility = View.VISIBLE
            binding.videoView.findViewById<ImageButton>(R.id.exo_fullscreen).visibility =
                View.VISIBLE
        } else {
            binding.videoView.findViewById<Group>(R.id.group_video).visibility = View.GONE
            binding.videoView.findViewById<ImageButton>(R.id.exo_fullscreen).visibility = View.GONE


        }

        trackSelector = DefaultTrackSelector(baseActivity, AdaptiveTrackSelection.Factory())
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                MediaItem.fromUri(
                    childModel?.signLanguageStreamingEndpointUrl ?: ""
                )
            )
        playbackPosition = 0L
        binding.videoView.findViewById<TextView>(R.id.header_tv).text =
            arguments?.getString("title")
        expandClick = binding.videoView.findViewById(R.id.exo_fullscreen)
        expandClick?.setOnClickListener {
            if (fullscreen) {
                expandClick?.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseActivity,
                        R.drawable.ic_expand
                    )
                )

                baseActivity.setToolbar(showToolbar = true, title = title)
                baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                showSystemUI()
                val params: ConstraintLayout.LayoutParams =
                    binding.videoView.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = 600
                binding.videoView.layoutParams = params
//                binding.videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                binding.invalidateAll()
                fullscreen = false
                binding.childFrame.visible()

            } else {
                expandClick?.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseActivity,
                        R.drawable.ic_collapse
                    )
                )


                hideSystemUI()
//
//                if (baseActivity.supportActionBar != null) {
//                    baseActivity.supportActionBar?.hide()
//                }
                baseActivity.setToolbar(showToolbar = false)
                baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params: ConstraintLayout.LayoutParams =
                    binding.videoView.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.videoView.layoutParams = params

                binding.childFrame.gone()

                fullscreen = true
            }

        }

        player = ExoPlayer.Builder(baseActivity)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                exoPlayer.setMediaSource(hlsMediaSource)
                exoPlayer.playWhenReady = playWhenReady
                if (childModel?.lectureThumbnailUrl.isNullOrEmpty())
                    binding.videoView.defaultArtwork =
                        Utils.drawableFromUrl(childModel?.lectureThumbnailUrl)
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(this)
//                    exoPlayer.videoSize.height
                exoPlayer.trackSelectionParameters =
                    (binding.videoView.player as ExoPlayer).trackSelectionParameters

                exoPlayer.prepare()
            }

        if (childModel?.mediaType == 2) {
//            binding.videoView.setShutterBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryColor))
            binding.videoView.setShutterBackgroundColor(ThemeUtils.getAppColor(baseActivity))

        }
        binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).max =
            childModel?.lectureSignLanguageContentDuration?.toInt() ?: 0

        binding.videoView.findViewById<TextView>(R.id.exo_duration1).text =
            childModel?.lectureSignLanguageContentDuration?.getTime(baseActivity, showHrs = false)

        if (fullscreen) {
//            expandClick?.setImageDrawable(
//                ContextCompat.getDrawable(
//                    baseActivity,
//                    R.drawable.ic_collapse
//                )
//            )
            hideSystemUI()
            baseActivity.setToolbar(showToolbar = false)
            baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val params: ConstraintLayout.LayoutParams =
                binding.videoView.layoutParams as ConstraintLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.videoView.layoutParams = params


        }
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        if (apiCode == ApiEndPoints.API_GET_DETAIL_OF_CONTENT || apiCode == ApiEndPoints.API_GET_DETAIL_OF_CONTENT_MODE_CREATOR) {
            val response = value as BaseResponse<ChildModel>
            streamingUrl = response.resource?.streamingEndpointUrl
            downloadingUrl = response.resource?.lectureContentUrl
            childModel = response.resource
            viewModel.transcriptLiveData.value = childModel?.lectureTranscriptUrl ?: ""
            viewModel.lectureLiveData.value = childModel
            initializePlayer()
            if (viewModel.modType == MODTYPE.LEARNER) {
                showLog("WEB_SOCKET", "response success initWebSocket")
                if (!WebSocketManager.isConnect()) {
                    initWebSocket()
                }
            }
            DBViewModel.getOfflineCourse(viewModel.courseId)


        } else if (apiCode == ApiEndPoints.OFFLINE_COURSE) {


            if (value != null)
                DBCourseData = value as OfflineCourseData

            if (!streamingUrl.isNullOrEmpty()
                && !checkIfLessonExist()
                && viewModel.modType == MODTYPE.LEARNER

            ) {
                actionDownload.isVisible = true

            }
        } /*else if (apiCode == ApiEndPoints.API_USER_COURSE_CONTENT_HISTORY) {
            sfwefwefw
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        sendData()

        baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    override fun onResume() {
        super.onResume()
//        fullscreen = true
        if (Util.SDK_INT <= 23 || player == null && streamingUrl?.isNotEmpty() == true) {
            if (viewModel.isSignVideoPlaying) {

                releasePlayer()
                signPlayer()
            } else {
                initializePlayer()
                initWebSocket()
            }
        }
        showLog("WEB_SOCKET", "onResume >> initWebSocket")

    }

    override fun onPause() {
        super.onPause()
        showLog("WEB_SOCKET", "on pause")

        sendData()
        baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        timer?.cancel()
        WebSocketManager.close()
        job?.cancel()
        job = null
        if (::timer.isInitialized) {
            timer.cancel()
        }
//        if (Util.SDK_INT <= 23 && streamingUrl?.isNotEmpty() == true) {
//
//            releasePlayer()
//        }

    }


    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(DOWNLOAD_COMPLETE_BROADCAST)
        try {
            LocalBroadcastManager.getInstance(baseActivity).registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23 && streamingUrl?.isNotEmpty() == true) {
            try {

                LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            releasePlayer()

            WebSocketManager.close()

            if (::timer.isInitialized) {
                timer.cancel()
            }
        }

    }


    private fun initializePlayer() {
        WebSocketManager.close()


        if (arguments?.getInt("modType") == MODTYPE.LEARNER) {


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
                binding.childView.tvNext.setText(R.string.finish)


            }
        }


        if (childModel?.mediaType != 2) {
            binding.videoView.findViewById<Group>(R.id.group_video).visibility = View.VISIBLE
            binding.videoView.findViewById<ImageButton>(R.id.exo_fullscreen).visibility =
                View.VISIBLE
        } else {
            binding.videoView.findViewById<Group>(R.id.group_video).visibility = View.GONE
            binding.videoView.findViewById<ImageButton>(R.id.exo_fullscreen).visibility = View.GONE
        }
        expandClick = binding.videoView.findViewById(R.id.exo_fullscreen)

        trackSelector = DefaultTrackSelector(baseActivity, AdaptiveTrackSelection.Factory())
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(childModel?.streamingEndpointUrl ?: ""))
        playbackPosition = childModel?.lectureLastPlayedTime ?: 0L
        binding.videoView.findViewById<TextView>(R.id.header_tv).text =
            arguments?.getString("title")
        expandClick = binding.videoView.findViewById(R.id.exo_fullscreen)
        expandClick?.setOnClickListener {
            if (fullscreen) {


                collapsedMode()
            } else {
                expandClick?.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseActivity,
                        R.drawable.ic_collapse
                    )
                )
                hideSystemUI()
                baseActivity.setToolbar(showToolbar = false)
                baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params: ConstraintLayout.LayoutParams =
                    binding.videoView.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.videoView.layoutParams = params

                fullscreen = true
            }

        }
        player?.release()
        player = null

        player = ExoPlayer.Builder(baseActivity)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->

                binding.videoView.player = exoPlayer
                exoPlayer.setMediaSource(hlsMediaSource)
                exoPlayer.playWhenReady = playWhenReady
                if (childModel?.lectureThumbnailUrl.isNullOrEmpty())
                    binding.videoView.defaultArtwork =
                        Utils.drawableFromUrl(childModel?.lectureThumbnailUrl)
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(this)
                exoPlayer.trackSelectionParameters =
                    (binding.videoView.player as ExoPlayer).trackSelectionParameters


                exoPlayer.prepare()
            }

        if (childModel?.mediaType == 2) {
//            binding.videoView.setShutterBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primaryColor))
            binding.videoView.setShutterBackgroundColor(ThemeUtils.getAppColor(baseActivity))

        }
        binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).max =
            childModel?.lectureContentDuration?.toInt() ?: 0

        binding.videoView.findViewById<TextView>(R.id.exo_duration1).text =
            childModel?.lectureContentDuration?.getTime(baseActivity, showHrs = false)
        binding.videoView.doOnLayout {

            exoHeight = it.measuredHeight
        }
        if (fullscreen) {
//            expandClick?.setImageDrawable(
//                ContextCompat.getDrawable(
//                    baseActivity,
//                    R.drawable.ic_collapse
//                )
//            )
            hideSystemUI()
            baseActivity.setToolbar(showToolbar = false)
            baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val params: ConstraintLayout.LayoutParams =
                binding.videoView.layoutParams as ConstraintLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.videoView.layoutParams = params


        } else {
//            collapsedMode()
        }

    }

    private fun collapsedMode() {


        expandClick?.setImageDrawable(
            ContextCompat.getDrawable(
                baseActivity,
                R.drawable.ic_expand
            )
        )


        baseActivity.setToolbar(showToolbar = true, title = title)
        baseActivity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        showSystemUI()
        val params: ConstraintLayout.LayoutParams =
            binding.videoView.layoutParams as ConstraintLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = exoHeight
        binding.videoView.layoutParams = params
        binding.invalidateAll()
        fullscreen = false
        baseActivity.setTransparentLightStatusBar()


    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(this)
            baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            exoPlayer.release()
        }

        player = null
//        WebSocketManager.
        WebSocketManager.close()
        if (::timer.isInitialized)
            timer.cancel()
    }

//    @SuppressLint("InlinedApi")
//    private fun hideSystemUi() {
//        WindowCompat.setDecorFitsSystemWindows(baseActivity.window, false)
//        WindowInsetsControllerCompat(
//            baseActivity.window,
//            binding.videoView
//        ).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }

    private fun initWebSocket() {
        WebSocketManager.close()

        if (!WebSocketManager.isConnect() && !viewModel.sectionId.isNullOrZero() && !viewModel.courseId.isNullOrZero()) {
            baseActivity.runOnUiThread {
                WebSocketManager.init(
                    "${ApiEndPoints.WEB_SOCKET_USER_CONTENT_HISTORY}?Token=${baseActivity.tokenFromDataStore()}&LanguageId=${1}&ChannelId=2&Data.CourseId=${
                        viewModel.courseId
                    }&Data.SectionId=${viewModel.sectionId}",
                    this@VideoBaseFragment,
                )
                WebSocketManager.connect()
            }
            initTimer()

        }

    }

    private fun initTimer() {
        job?.cancel()
        job = null
        job = lifecycleScope.launch {
            delay(10000)
            baseActivity.runOnUiThread {
                showLog("WEB_SOCKET", "timer callback")

                sendData()
                if (isAdded && isVisible) {
                    initTimer()
                }

            }
        }
    }


    private fun sendData() {
        try {


            if (context != null && isAdded && isVisible) {
                baseActivity.runOnUiThread {
                    val msg = Gson().toJson(
                        VideoAudioProgress(
                            viewModel.lectureId,
                            player?.currentPosition ?: 0L,
                            viewModel.sectionId
                        )
                    )
                    showLog("WEB_SOCKET", "send message >> $msg")
                    WebSocketManager.sendMessage(
                        msg
                    )
                    childModel?.lectureLastPlayedTime = player?.currentPosition ?: 0L
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun sendMaxDurationData() {
        try {


            if (context != null && isAdded && isVisible) {


                baseActivity.runOnUiThread {
                    WebSocketManager.sendMessage(
                        Gson().toJson(
                            VideoAudioProgress(
                                viewModel.lectureId,
                                childModel?.lectureContentDuration ?: 0L,
                                viewModel.sectionId

                            )
                        )
                    )

                    Log.e(
                        "sample_sample_sample", Gson().toJson(
                            VideoAudioProgress(
                                viewModel.lectureId,
                                childModel?.lectureContentDuration ?: 0L,
                                viewModel.sectionId

                            )
                        )
                    )
                    childModel?.lectureLastPlayedTime = childModel?.lectureContentDuration ?: 0L


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun onConnectSuccess() {
        showLog("WEB_SOCKET", "onConnectSuccess")

        sendData()

    }

    override fun onConnectFailed() {


    }

    override fun onClose() {


    }

    override fun onMessage(text: String?) {
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) {
            sendMaxDurationData()
            if (viewModel.lessonList[arguments?.getInt("innerPosition")
                    ?: -1]?.lectureIsCompleted != true
            ) {
                viewModel.postVideoAudioTime(arguments?.getInt("innerPosition") ?: -1)
            }

            binding.childView.tvNext.alpha = 1f
            binding.childView.tvNext.isEnabled = true

            viewModel.lessonList.forEachIndexed { index, childModel ->
                if (viewModel.lectureId == childModel?.lectureId) {
                    viewModel.lessonList.set(index, childModel)?.lectureIsCompleted = true
                }
            }
        }


        trackSelector.generateQualityList().let {
            qualityList = it


            try {
                setUpQualityList()


            } catch (e: Exception) {

            }
        }

        try {
            setUpSpeedOfVideo()
        } catch (e: Exception) {


        }

    }

    private fun setUpQualityList() {
        qualityPopUp = PopupMenu(baseActivity, binding.videoView.findViewById(R.id.img_setting))
        val filteredData = qualityList.distinctBy { it.first }
        filteredData.let {
            for ((i, videoQuality) in it.withIndex()) {

                val name = videoQuality.first.split("x")
                qualityPopUp?.menu?.add(0, i, 0, "${name[1]}p")
            }
        }
        qualityPopUp?.setOnMenuItemClickListener { menuItem ->
            filteredData[menuItem.itemId].let {
                trackSelector.parameters = trackSelector.parameters
                    .buildUpon()
                    .setTrackSelectionOverrides(it.second.build())
                    .setTunnelingEnabled(true)
                    .build()
            }
            true
        }

        binding.videoView.findViewById<ImageView>(R.id.img_setting).setOnClickListener {
            qualityPopUp?.show()
        }


    }

    private fun setUpSpeedOfVideo() {

        changeSpeed = PopupMenu(baseActivity, binding.videoView.findViewById(R.id.img_speed))
        val filteredData = speedArray
        filteredData.let {
            speedArray.forEachIndexed { index, _ ->
                changeSpeed?.menu?.add(0, index, 0, speedArray[index])

            }

        }
        changeSpeed?.setOnMenuItemClickListener { menuItem ->
            filteredData[menuItem.itemId].let {
                binding.videoView.findViewById<TextView>(R.id.img_speed).text = menuItem.title

                val title = menuItem.title?.split("x")
                val param = PlaybackParameters(title?.get(0)?.toInt()?.toFloat() ?: 0f)

                player?.let {
                    it.playbackParameters = param

                }
            }
            true
        }


        binding.videoView.findViewById<TextView>(R.id.img_speed).setOnClickListener {
            changeSpeed?.show()
        }


    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)

        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.childView.constChild.gone()
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.root
        ).show(WindowInsetsCompat.Type.systemBars())
        binding.childView.constChild.visible()


    }

    private fun isMyServiceRunning(): Boolean {
        try {
            val manager: ActivityManager? =
                getSystemService(
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
    }

    private fun downloadFile() {
        if (downloadingUrl?.contains("https") == true) {
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
            }
        } else {
            Toast.makeText(context, getString(R.string.invalid_file_url), Toast.LENGTH_LONG).show()
        }
    }


    private fun checkIfLessonExist(): Boolean {
        return if (DBCourseData?.sectionList != null)
            checkIfLessonExistInDB(DBCourseData?.sectionList)
        else
            false
    }

    private fun checkIfLessonExistInDB(sectionList: ArrayList<OfflineSectionData>?): Boolean {
        sectionList?.let {
            for (i in 0 until sectionList.size) {
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


    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                DOWNLOAD_COMPLETE_BROADCAST -> {
                    if (intent.extras?.getBoolean("downloadCompleted", false) == true) {
                        Toast.makeText(
                            baseActivity,
                            getString(R.string.download_completed),
                            Toast.LENGTH_LONG
                        ).show()
                        actionDownload.isVisible = false
                        isDownloadClicked = false
                    } else {
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


    fun navigationToContent(position: Int, innerPosition: Int, modType: Int, b: Boolean) {


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
                            "offlineCourseData" to offlineCourseData,

                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to innerPosition,
                            "position" to position,
                            "section" to viewModel.sectionList,
                            "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                            "from" to 0,
                            "modType" to arguments?.getInt("modType"),

                            "lessonId" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureId,
                            "courseStatus" to arguments?.getInt("courseStatus"),


                            "isCompleted" to viewModel.lessonList.get(
                                innerPosition
                            )?.lectureIsCompleted,
                            "isQuizReport" to true,
                            "freezeContent" to arguments?.getBoolean("freezeContent")
                        )
                    )

//
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
                            "courseStatus" to arguments?.getInt("courseStatus"),

                            "section" to viewModel.sectionList,
                            "from" to 0,
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
                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "offlineCourseData" to offlineCourseData,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "courseStatus" to arguments?.getInt("courseStatus"),

                        "innerPosition" to innerPosition,
                        "position" to position,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "modType" to arguments?.getInt("modType"),

                        "from" to 0,
                        "isQuizReport" to true,
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
                        "courseStatus" to arguments?.getInt("courseStatus"),

                        "offlineCourseData" to offlineCourseData,
                        "isCompleted" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "courseId" to viewModel.courseId,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "section" to viewModel.sectionList,
                        "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                        "modType" to arguments?.getInt("modType"),

                        "lessonId" to viewModel.lessonList.get(
                            innerPosition
                        )?.lectureId,
                        "from" to 0,
                        "isQuizReport" to true,
                        "freezeContent" to arguments?.getBoolean("freezeContent")

                    )
                )
            }
            MediaType.VIDEO, MediaType.AUDIO -> {
                playVideo(position, innerPosition, modType)

            }
            Constant.CLICK_LESSON -> {

//                if (baseActivity.tokenFromDataStore().isEmpty()) {
//                    baseActivity.guestUserPopUp()
//                }
            }
        }
    }

    fun playVideo(position: Int, innerPosition: Int, modType: Int) {
        findNavController().navigateTo(
            R.id.action_global_videoBaseFragment,
            bundleOf(
                "courseId" to viewModel.courseId,
                "status" to viewModel.status,
                "offlineCourseData" to offlineCourseData,
                "type" to modType,
                "sectionId" to viewModel.sectionList.get(position)?.sectionId,
                "lectureId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId,
                "title" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureTitle,
                "courseStatus" to arguments?.getInt("courseStatus"),

                "offlineCourseData" to offlineCourseData,
                "isCompleted" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureIsCompleted,
                "lessonList" to viewModel.lessonList,
                "innerPosition" to innerPosition,
                "position" to position,
                "section" to viewModel.sectionList,
                "modType" to arguments?.getInt("modType"),

                "lessonId" to viewModel.lessonList.get(
                    innerPosition
                )?.lectureId,
                "from" to 0,
                "isQuizReport" to true,
                "freezeContent" to arguments?.getBoolean("freezeContent")


            )

        )
    }

    fun onClickBack() {

        if (fullscreen) {
            collapsedMode()
        } else {
            if (arguments?.getInt("modType") == MODTYPE.LEARNER) {
                showLog("WEB_SOCKET", "onClickBackk")

                sendData()

                findNavController().popBackStack(R.id.courseDetailsFragment, false)

            } else {
                findNavController().popBackStack()

            }
        }


    }

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

                            player?.seekTo(p1?.toLong())
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




