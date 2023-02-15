package com.selflearningcoursecreationapp.ui.create_course.upload_content.editVideo

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEditAudioBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.DefaultMime
import com.selflearningcoursecreationapp.utils.FileUtils
import com.selflearningcoursecreationapp.utils.customViews.RangeSeekBarView
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class EditAudioFragment : BaseFragment<FragmentEditAudioBinding>(),
    SeekBar.OnSeekBarChangeListener,

    RangeSeekBarView.OnRangeSeekBarChangeListener {
    private var args: EditAudioFragmentArgs? = null
    var isPrepared = false

    //    private var fileUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private val viewModel: AudioEditVM by viewModel()
    private var rangeSeekBar: RangeSeekBarView? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_audio
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }

    private fun setSeekBarParams() {
        rangeSeekBar = RangeSeekBarView(context, 0L, (viewModel.duration).toLong())
        rangeSeekBar?.apply {

            setTrimStartIcon(R.drawable.ic_waveform2)
            setTrimEndIcon(R.drawable.ic_waveform2)
            setShadowColor(ThemeUtils.getTintColor(baseActivity))
            build()
            selectedMinValue = 0
            setStartEndTime(0L, (viewModel.duration).toLong())
            selectedMaxValue = viewModel.duration.toLong()
            isNotifyWhileDragging = true
        }

        binding.sbRange.addView(rangeSeekBar)
        rangeSeekBar!!.setOnRangeSeekBarChangeListener(this)
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.btSave.setOnClickListener {


            val difference = viewModel.maxDuration - viewModel.minDuration

            if (difference.isNullOrZero() || difference.isNullOrNegative()) {
                showToastShort(
                    getString(R.string.audio_file_play_time_Should_be_greater_than_2_second),

                    )
            } else if (difference < 2000) {
                showToastShort(
                    getString(R.string.audio_file_play_time_Should_be_greater_than_2_second),

                    )
//                showToastShort(
//                    "Trim seconds should be less than 30 minutes",
//                )
            } else {
                if (viewModel.mIsPlaying) {
                    handlePause()
                }

                viewModel.executeTrimAudio()

            }
        }

        binding.ivPlay.setOnClickListener {
            if (!viewModel.mIsPlaying) {
                binding.ivPlay.setImageResource(R.drawable.ic_pause_audio_black)
                binding.ivPlay.contentDescription = baseActivity.getString(R.string.pause_icon)
            } else {
                binding.ivPlay.setImageResource(R.drawable.ic_play_audio_black)
                binding.ivPlay.contentDescription = baseActivity.getString(R.string.play_icon)

            }
            onPlay(viewModel.mStartPos)
        }

        viewModel.mKeyDown = false


        arguments?.let {
            Log.d("checkValue", "initUi: pre ")
            args = EditAudioFragmentArgs.fromBundle(it)
            viewModel.selectedSaf = FFmpegKitConfig.getSafParameterForRead(
                baseActivity,
                Uri.parse(args?.lessonArgs?.filePath)
            )
            Log.d("checkValue", "initUi: mid ")

            viewModel.mLoadingLastUpdateTime = System.currentTimeMillis()

            initPlayer()
        }
        val metrics = DisplayMetrics()
        baseActivity.windowManager.defaultDisplay.getMetrics(metrics)
        viewModel.mDensity = metrics.density


        binding.seekbar.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true

        }

        updateSeekBarPosition(0)
    }

    //    suspend fun call(): String {
//        return FileUtils.getPath(
//            SelfLearningApplication.applicationContext(),
//            Uri.parse(viewModel.selectedPath)
//        ).toString()
//    }
//    suspend fun call(toString: String) {
//        return FileUtils.getPath(
//            SelfLearningApplication.applicationContext(),
//            Uri.parse(p1.toString())
//        ) ?: p1
//    }

    private fun initPlayer() {
        viewModel.mLoadingKeepGoing = true
        showLog("checkValue", "initPlayer")
        baseActivity.showProgressBar(baseActivity.getString(R.string.processing_file))
        baseActivity.lifecycleScope.launch {
            CoroutineScope(IO).launch {
                viewModel.selectedPath =
                    async { call(args?.lessonArgs?.filePath.toString()) }.await()
//                FileUtils.getPath(
//                SelfLearningApplication.applicationContext(),
//                Uri.parse(args?.lessonArgs?.filePath)
//            ) ?: args?.lessonArgs?.filePath
                withContext(Main) {

                    viewModel.mimeType = viewModel.selectedPath?.getValidAudioMimeType(baseActivity)
                    viewModel.selectedSaf =
                        viewModel.selectedPath?.getValidAudioMimeType(baseActivity, true)?.let {
                            FileUtils.copyFile(
                                baseActivity,
                                it, true, Uri.parse(args?.lessonArgs?.filePath)
                            )
                        }

                    showLog("MIME_TYPE", "mime path >>>  ${viewModel.mimeType}")

                    var audiofile: File? = null
                    try {
                        audiofile = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
                                    + "/AUD_${System.currentTimeMillis()}_recording.${
                                viewModel.mimeType?.substringAfter(
                                    "/"
                                ) ?: "mp3"
                            }"
                        )

                    } catch (e: IOException) {
                        showLog("TAG", "external storage access error")

                    }
                    viewModel.outputPath = audiofile?.absolutePath
//                    delay(200)
//            viewModel.duration = FileUtils.getAudioDuration(viewModel.selectedPath)

                    baseActivity.runOnUiThread {
                        setMediaPlayer()


                    }
                }
            }
//            showLog("checkValue", "mime path >>>  ${viewModel.selectedPath}")


        }

    }

    private fun setMediaPlayer() {
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
            mediaPlayer?.setDataSource(baseActivity, Uri.parse(viewModel.selectedPath))
            mediaPlayer?.setOnPreparedListener {
                Log.d("check", "setMediaPlayer: pre check ")

                if (!isPrepared) {
                    Log.d("check", "setMediaPlayer:mid check ")

                    isPrepared = true
                    val millis = mediaPlayer?.duration
                    val totalSec =
                        TimeUnit.SECONDS.convert(millis!!.toLong(), TimeUnit.MILLISECONDS)
                    viewModel.duration = millis.toLong()
                    viewModel.maxDuration = millis.toLong()

                    binding.tvEndTime.text =
                        String.format("%s", millis.toLong().getTime(baseActivity))
                    binding.tvStartTime.text = "00:00:00"
                    binding.seekbar.apply {
                        max = millis
                        progress = 0
                    }
                    if (viewModel.duration.isNullOrZero() || viewModel.duration < 1000) {
                        showToastLong(getString(R.string.duration_should_be_greater_than_0))

                        onClickBack()

                    }
                    setSeekBarParams()
                    hideLoading()
                    it.start()
                    binding.ivPlay.setImageResource(R.drawable.ic_pause_audio_black)
                    onPlay(0)

                }
            }
            Log.d("check", "setMediaPlayer: later check ")

            mediaPlayer?.prepare()

            mediaPlayer?.setOnErrorListener { mediaPlayer, what, extra ->
                Log.d("check", "setMediaPlayer: error check ")

                if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN || what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    showToastShort(getString(R.string.some_error_occure_please_try_again))

                }

                hideLoading()

                return@setOnErrorListener true
            }

        } catch (e: Exception) {
            showException(e)
        }

    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            "trimAudio" -> {
                val path = FileUtils.getCreatedFilePath(
                    baseActivity,
                    File(viewModel.outputPath ?: ""),
                    viewModel.mimeType ?: DefaultMime.AUDIO
                )?.toString() ?: viewModel.outputPath
                if (args?.fromLesson == true) {
                    setFragmentResult(
                        "onRecordCallback",
                        bundleOf("recordedUri" to path, "fromRecord" to false)
                    )
                    findNavController().popBackStack()

                } else {
                    val lessonArgs = args?.lessonArgs
                    lessonArgs?.filePath = path

                    showLog("VIDEO_COMMAND", "outputUrl >> ${lessonArgs?.filePath}")
                    showLog("VIDEO_COMMAND", "outputUrl >> ${viewModel.outputPath}")
                    val action =
                        EditAudioFragmentDirections.actionEditAudioFragmentToAudioLectureFragment(
                            lessonArgs
                        )
                    findNavController().navigateTo(action)
                }
            }
        }
    }


    @Synchronized
    private fun handlePause() {
        binding.ivPlay.setImageResource(R.drawable.ic_play_audio_black)

        mediaPlayer?.pause()
        viewModel.mIsPlaying = false
    }


    private fun showStartEndTime() {
        binding.tvStartTime.text =
            if (viewModel.minDuration.isNullOrZero()) "00:00:00" else viewModel.minDuration.getTime(
                baseActivity
            )
        binding.tvEndTime.text = viewModel.maxDuration.getTime(baseActivity)
    }


    @Synchronized
    private fun onPlay(startPosition: Int) {
        if (viewModel.mIsPlaying) {
            handlePause()
            return
        }
        if (mediaPlayer == null) {
            setMediaPlayer()
        }
        try {
            if ((mediaPlayer?.currentPosition ?: 0) > viewModel.maxDuration) {
                mediaPlayer?.seekTo(viewModel.minDuration.toInt())
            } else if ((mediaPlayer?.currentPosition ?: 0) < viewModel.minDuration) {
                mediaPlayer?.seekTo(viewModel.minDuration.toInt())
            }
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener {
                handlePause()
            }
            mediaPlayer?.setOnInfoListener { mediaPlayer, i, i2 ->

                showLog("MEDIA_PLAER", "i >>> $i , ... i2 >>> $i2")
                return@setOnInfoListener false
            }
            viewModel.mIsPlaying = true
            updateSeekBarPosition(mediaPlayer?.currentPosition ?: viewModel.minDuration.toInt())

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateSeekBarPosition(secs: Int) {
        binding.seekbar.progress = secs

        baseActivity.lifecycleScope.launch {
            delay(500)
            baseActivity.runOnUiThread {
                binding.seekbar.progress = secs

                if (mediaPlayer?.isPlaying == true) {
                    if ((mediaPlayer?.currentPosition ?: 0) > viewModel.maxDuration) {
                        mediaPlayer?.seekTo(viewModel.minDuration.toInt())
                        mediaPlayer?.pause()
                    } else if ((mediaPlayer?.currentPosition ?: 0) < viewModel.minDuration) {
                        mediaPlayer?.seekTo(viewModel.minDuration.toInt())
                        mediaPlayer?.pause()
                    }
                    updateSeekBarPosition((mediaPlayer?.currentPosition ?: 0))

                } else {
                    binding.seekbar.progress = viewModel.minDuration.toInt()
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()

        if (viewModel.mIsPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_play_audio_black)
            onPlay(viewModel.mStartPos)
        }
    }


    fun onClickBack(isOpen: Boolean = true) {
        setFragmentResult(
            "onLessonBack",
            bundleOf("isDialogOpen" to isOpen)
        )

        findNavController().popBackStack()
    }

    override fun onRangeSeekBarValuesChanged(
        bar: RangeSeekBarView?,
        minValue: Long,
        maxValue: Long,
        action: Int,
        isMin: Boolean,
        pressedThumb: RangeSeekBarView.Thumb?
    ) {
        viewModel.minDuration = minValue
        viewModel.maxDuration = maxValue

        when (action) {

            MotionEvent.ACTION_MOVE -> {
//              mediaPlayer?.seekTo((if (pressedThumb === RangeSeekBarView.Thumb.MIN) minValue.toInt() else maxValue.toInt()))

            }
            MotionEvent.ACTION_UP -> {
                mediaPlayer?.seekTo(minValue.toInt())
            }
            else -> {

            }
        }
        rangeSeekBar?.setStartEndTime(minValue, maxValue)
        showStartEndTime()
        handlePause()
        lifecycleScope.launch {
            delay(1000)
            baseActivity.runOnUiThread {
                binding.seekbar.progress = minValue.toInt()

            }
        }
//        updateSeekBarPosition(mediaPlayer?.currentPosition ?: viewModel.minDuration.toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}