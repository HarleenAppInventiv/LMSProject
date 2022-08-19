package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentAudioLectureBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadAudioOptionsDialog
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit


class AudioLectureFragment : BaseFragment<FragmentAudioLectureBinding>(),
    HandleClick, BaseBottomSheetDialog.IDialogClick, View.OnTouchListener {
    private val viewModel: AudioLessonViewModel by viewModel()


    private var childPosition: Int? = -1
    private var filePath = ""
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
        setHasOptionsMenu(true)

        binding.seekbar.progressDrawable?.setColorFilter(
            ThemeUtils.getAppColor(baseActivity),
            PorterDuff.Mode.SRC_IN
        )


        binding.audioLesson = viewModel
        binding.handleClick = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        getBundleData()

        if (type == Constant.CLICK_ADD) {
            duration()
        } else {
            viewModel.getLectureDetail()
        }

        if (!childPosition.isNullOrNegative()) {
            binding.btnAddLesson.text = baseActivity.getString(R.string.update_lesson)
        }
        binding.edtTitle.setOnTouchListener(this)


    }

    private fun getBundleData() {
        arguments?.let {
            val value = AudioLectureFragmentArgs.fromBundle(it)
            filePath = value.filePath
            viewModel.lectureId = value.lectureId
            viewModel.courseId = value.courseId
            viewModel.model = value.sendSectionModel
            viewModel.sectionId = viewModel.model?.sectionId
            childPosition = value.childPosition
            type = value.type
            mediaFrom = value.from
        }
    }


    override fun getLayoutRes() = R.layout.fragment_audio_lecture
//

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


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
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            mediaPlayer?.setDataSource(requireContext(), Uri.parse(filePath))
            mediaPlayer?.prepare()
            val millis = mediaPlayer?.duration
            val totalSec = TimeUnit.SECONDS.convert(millis!!.toLong(), TimeUnit.MILLISECONDS)
            viewModel.milliSecond = millis?.toLong()
            countTime = millis?.toLong()
            binding.tvTimerMax.text = String.format("/%s", DateUtils.formatElapsedTime(totalSec))
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
    }

    private fun uploadServer(file: File, millisecond: Long) {
        viewModel.uploadContent(
            file,
            millisecond
        )
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
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()

                    if (viewModel.model?.lessonList == null) {
                        viewModel.model?.lessonList = ArrayList()
                    }


                    if (!childPosition.isNullOrNegative() && viewModel.model?.lessonList?.isNotEmpty() == true) {
                        viewModel.model?.lessonList?.set(childPosition ?: 0, it)
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))

                    } else {
                        viewModel.model?.lessonList?.add(it)
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

        if (filePath.endsWith("mp3")) {
            val file = File(Uri.parse(filePath).path ?: "")

            uploadServer(file, millisecond)
        } else {
            showToastShort(baseActivity.getString(R.string.please_upload_file_of_mp3_extension))
        }

    }

    override fun onHandleClick(vararg items: Any) {
        val view = items[0] as View
        when (view.id) {
            R.id.iv_audio_edit -> {
                resetTimer()
                UploadAudioOptionsDialog().apply {
                    setOnDialogClickListener(this@AudioLectureFragment)
                }.show(childFragmentManager, "sd")
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

    override fun onDialogClick(vararg items: Any) {
        when (val type = items[0] as Int) {
            MediaType.AUDIO -> {
                filePath = items[1] as String

                if (isFileLessThan5MB(File(filePath))) {
                    showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                } else {
                    duration()
                }


            }
            MediaFrom.RECORDING -> {
                val action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToRecordAudioFragment(
                        childPosition = childPosition!!,
                        type = type,
                        from = mediaFrom,
                        sendSectionModel = viewModel.model,
                        courseId = viewModel.courseId!!,
                        lectureId = viewModel.lectureId!!
                    )
                findNavController().navigate(action)
            }

        }
    }

    private fun startTimer() {
        binding.tvTimer.apply {
            base = if (mLastStopTime == 0L) {
                SystemClock.elapsedRealtime()
            } else {
                val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
                base + intervalOnPause
            }
            start()
        }

        mediaPlayer?.start()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

        if (mCountDownTimer != null) {
            mCountDownTimer?.cancel()
        }
        mCountDownTimer = object : CountDownTimer(countTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isAdded && isVisible) {
                    binding.seekbar.progress = mediaPlayer?.currentPosition ?: 0
                    countTime = millisUntilFinished
                }
            }

            override fun onFinish() {
//                binding.seekbar.progress = 100
//                timerRunning = false
//                binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                resetTimer()
            }
        }
        mCountDownTimer?.start()
        timerRunning = true

    }


    private fun pauseTimer() {
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        mediaPlayer!!.pause()
        binding.tvTimer.stop()
        mLastStopTime = SystemClock.elapsedRealtime()
        mCountDownTimer!!.cancel()
        timerRunning = false

    }


    private fun resetTimer() {

        mLastStopTime = 0L
        binding.seekbar.progress = 0
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        timerRunning = false
        binding.tvTimer.apply {
            stop()
            base = SystemClock.elapsedRealtime()
        }
        mediaPlayer?.reset()
        setMediaPlayer()
    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }

    fun duration() {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(requireContext(), Uri.parse(filePath))
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        convertToFile(durationStr?.toLongOrNull() ?: 0)
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}