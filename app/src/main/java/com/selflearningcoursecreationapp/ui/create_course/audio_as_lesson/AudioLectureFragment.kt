package com.selflearningcoursecreationapp.ui.create_course.audio_as_lesson

import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
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
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.dialog.UploadAudioOptionsDialog
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit


class AudioLectureFragment : BaseFragment<FragmentAudioLectureBinding>(),
    HandleClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: AudioLessonViewModel by viewModel()

    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null
    var childPosition: Int? = 0
    var sectionId: Int? = null
    var filePath = ""
    var mediaFrom = 0

    var mediaPlayer: MediaPlayer? = null

    var mCountDownTimer: CountDownTimer? = null
    var type: Int? = null
    private var timerRunning = false
    var milliSecond = 0
    var contentId = ""

    var mLastStopTime = 0L
    var mute = false
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
        arguments?.let {
            val value = AudioLectureFragmentArgs.fromBundle(it)
            filePath = value.filePath
            lectureId = value.lectureId
            courseId = value.courseId
            model = value.sendSectionModel
            sectionId = model?.sectionId
            childPosition = value.childPosition
            type = value.type
            mediaFrom = value.from
        }
//      /  Log.d("varun", "initUI: ${FileUtils.getDriveFilePath(Uri.parse(filePath),requireContext())}")

        if (type == Constant.CLICK_ADD) {
            convertToFile()
        } else {
            viewModel.getLectureDetail(lectureId!!)
        }

        if (!childPosition.isNullOrNegative()) {
            binding.btnAddLesson.setText(baseActivity.getString(R.string.update_lesson))
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

    fun setMediaPlayer() {
        binding.ivAudioVolume.setImageResource(R.drawable.ic_baseline_volume_up_24)
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer?.setDataSource(requireContext(), Uri.parse(filePath))
            mediaPlayer?.prepare()
            val millis = mediaPlayer?.duration
            val totalSec = TimeUnit.SECONDS.convert(millis!!.toLong(), TimeUnit.MILLISECONDS)
            milliSecond = mediaPlayer?.duration!!
            binding.tvTimerMax.text = "/" + DateUtils.formatElapsedTime(totalSec)
            binding.seekbar.apply {
                max = millis ?: 0
                progress = 0
            }
        } catch (e: Exception) {
            Log.d("varun", "initUI: ${e.message}")
        }

    }


//


    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        mCountDownTimer?.cancel()
    }

    fun uploadServer(fileName: String, file: File) {
        viewModel.uploadContent(
            courseId,
            sectionId,
            lectureId!!,
            fileName,
            file,
            MEDIA_TYPE.AUDIO,
            "",
            0
        )
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    setMediaPlayer()
                    contentId = it.id.toString()
                }
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureTitle = binding.edtTitle.content()


                    if (childPosition != null && childPosition != -1) {
                        model?.lessonList?.set(childPosition!!, it)
                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))

                    } else {
                        model?.lessonList?.add(it)
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
                    contentId = it.lectureContentId.toString()
                    filePath = it.lectureContentUrl.toString()
                    setMediaPlayer()
                }
            }
        }
    }

    fun convertToFile() {

        if (filePath.endsWith("mp3")) {
            val file = File(Uri.parse(filePath).path)

            uploadServer(file.name, file)
        } else {
            showToastShort("Not able to upload this file, please select another")
        }

    }

    override fun onHandleClick(vararg items: Any) {
        var view = items[0] as View
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
                viewModel.docValidations(
                    lectureId,
                    sectionId,
                    courseId,
                    binding.edtTitle.content(),
                    contentId,
                    MEDIA_TYPE.AUDIO,
                    milliSecond
                )

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
        var type = items[0] as Int
        when (type) {
            MEDIA_TYPE.AUDIO -> {
                filePath = items[1] as String
                Log.d("varun", "initUI: ${filePath}")

                if (isFileLessThan5MB(File(filePath))) {
                    showToastShort("File size is more than 5 MB, not able to upload. Please select another file")
                } else {
                    convertToFile()
                }


            }
            MEDIA_FROM.RECORDING -> {
                var action =
                    AudioLectureFragmentDirections.actionAudioLectureFragmentToRecordAudioFragment(
                        childPosition = childPosition!!,
                        type = type,
                        from = mediaFrom,
                        sendSectionModel = model,
                        courseId = courseId!!,
                        lectureId = lectureId!!
                    )
                findNavController().navigate(action)
            }

        }
    }

    private fun startTimer() {
        binding.tvTimer.apply {
            if (mLastStopTime == 0L) {
                base = SystemClock.elapsedRealtime()
            } else {
                val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
                base = base + intervalOnPause
            }
            start()
        }

        mediaPlayer?.start()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)

        if (mCountDownTimer != null) {
            mCountDownTimer?.cancel();
        }
        mCountDownTimer = object : CountDownTimer(milliSecond.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isAdded && isVisible) {
                    binding.seekbar.progress = mediaPlayer?.currentPosition ?: 0
                    milliSecond = millisUntilFinished.toInt()
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


}