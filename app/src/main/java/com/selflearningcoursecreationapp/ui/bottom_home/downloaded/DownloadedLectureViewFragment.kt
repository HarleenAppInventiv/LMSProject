package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.interfaces.OnErrorListener
import com.danjdt.pdfviewer.view.PdfViewerRecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDownloadedLecutureViewBinding
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.downloadManager.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [DownloadedLectureViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadedLectureViewFragment : BaseFragment<FragmentDownloadedLecutureViewBinding>(),
    Player.Listener, OnErrorListener {
    private var changeSpeed: PopupMenu? = null

    private lateinit var offlineLessonSelected: OfflineLessonData
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    override fun getLayoutRes() = R.layout.fragment_downloaded_lecuture_view
    var fullscreen: Boolean = false
    lateinit var window: Window
    private var exoHeight = 250
    private var expandClick: ImageButton? = null
    var speedArray = arrayOf("1x", "2x", "3x")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        arguments?.let {
            offlineLessonSelected = it.get("offlineLessonSelected") as OfflineLessonData
        }

        baseActivity.setToolbar(showToolbar = true, title = offlineLessonSelected.title)
//        Toast.makeText(context, offlineLessonSelected.fileExtention.toString(), Toast.LENGTH_LONG)
//            .show()
        if (offlineLessonSelected.type == MediaType.VIDEO || offlineLessonSelected.type == MediaType.AUDIO) {
            initProgressBar()
            initializePlayer(offlineLessonSelected.filePath.toString())
        } else {
            initializeWebView(offlineLessonSelected.filePath.toString())
        }
    }

    private fun initializeWebView(uri: String) {
        binding.videoView.visibility = View.GONE
        binding.pdfViewRL.visibility = View.VISIBLE
        val file = File(uri)
        PdfViewer.Builder(binding.rootView)
            .view(PdfViewerRecyclerView(baseActivity))
            .build()
            .load(file)
    }

    fun initializePlayer(uri: String) {
        window = baseActivity.window

        binding.videoView.doOnLayout {
            exoHeight = it.measuredHeight
        }
        window = requireActivity().window

        player = ExoPlayer.Builder(baseActivity).build()
        binding.videoView.visibility = View.VISIBLE
        binding.pdfViewRL.visibility = View.GONE
        binding.videoView.player = player

        val mediaItem: MediaItem = MediaItem.fromUri(uri)

//        player!!.setMediaItem(mediaItem)

//        player!!.prepare()
//
//        player!!.play()


        player = ExoPlayer.Builder(baseActivity)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(0, 0L)
                exoPlayer.addListener(this)
//                    exoPlayer.videoSize.height
                exoPlayer.trackSelectionParameters =
                    (binding.videoView.player as ExoPlayer).trackSelectionParameters
                exoPlayer.prepare()
            }
        binding.videoView.findViewById<AppCompatSeekBar>(R.id.exo_progress1).max =
            offlineLessonSelected.duration?.toInt() ?: 0

        binding.videoView.findViewById<TextView>(R.id.exo_duration1).text =
            offlineLessonSelected.duration?.getTime(baseActivity, showHrs = false)
        binding.videoView.findViewById<TextView>(R.id.header_tv).text =
            offlineLessonSelected.title

        expandClick = binding.videoView.findViewById(R.id.exo_fullscreen)
        setUpSpeedOfVideo()
        expandClick?.setOnClickListener {
            if (fullscreen) {


//                if (baseActivity.supportActionBar != null) {
//                    baseActivity.supportActionBar?.show()
//                }

                collapsed()

            } else {
                expandClick?.setImageDrawable(
                    ContextCompat.getDrawable(
                        baseActivity,
                        R.drawable.ic_collapse
                    )
                )


                hideSystemUI()

//                if (baseActivity.supportActionBar != null) {
//                    baseActivity.supportActionBar?.hide()
//                }
                baseActivity.setToolbar(showToolbar = false)
                baseActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params: RelativeLayout.LayoutParams =
                    binding.videoView.layoutParams as RelativeLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.videoView.layoutParams = params

                fullscreen = true
            }

        }

    }

    private fun collapsed() {
        expandClick?.setImageDrawable(
            ContextCompat.getDrawable(
                baseActivity,
                R.drawable.ic_expand
            )
        )
        baseActivity.setToolbar(showToolbar = true, title = offlineLessonSelected.title)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        showSystemUI()
        val params: RelativeLayout.LayoutParams =
            binding.videoView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = exoHeight
        binding.videoView.layoutParams = params
        //                binding.videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        binding.invalidateAll()
        fullscreen = false
        baseActivity.setTransparentLightStatusBar()
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

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 23 && offlineLessonSelected.filePath?.isNotEmpty() == true) {

            if (player != null) {
                player?.pause()
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

//    override fun onStop() {
//        super.onStop()
//        if (Util.SDK_INT > 23 && offlineLessonSelected.filePath?.isNotEmpty() == true) {
//
//            releasePlayer()
//        }
//    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(this)

            exoPlayer.release()
        }

        player = null
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.root
        ).show(WindowInsetsCompat.Type.systemBars())
    }


    override fun onApiRetry(apiCode: String) {
    }

    override fun onAttachViewError(e: Exception) {
        Log.e("onAttachViewError", e.message)
    }

    override fun onFileLoadError(e: Exception) {
        Log.e("onFileLoadError", e.message)
    }

    override fun onPdfRendererError(e: IOException) {
        Log.e("onPdfRendererError", e.message)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        binding.videoView.findViewById<Group>(R.id.group_video).visibility = View.GONE


    }


    fun onClickBack() {

        if (fullscreen) {
            collapsed()
        } else {
            findNavController().popBackStack()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (Util.SDK_INT > 23 && offlineLessonSelected.filePath?.isNotEmpty() == true) {
            releasePlayer()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (Util.SDK_INT > 23 && offlineLessonSelected.filePath?.isNotEmpty() == true) {
            releasePlayer()
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
            kotlinx.coroutines.delay(delay)
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
}


