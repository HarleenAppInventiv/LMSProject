package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRecordAudioBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.FileUtils
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RecordAudioFragment : BaseFragment<FragmentRecordAudioBinding>(), HandleClick {
    private lateinit var fragArgs: RecordAudioFragmentArgs
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var recordingStopped: Boolean = false
    private var recordingStatus = 0
//    var lectureId: Int? = null
//    var courseId: Int? = null
//    var model: SectionModel? = null
//    var childPosition: Int? = -1
//    var sectionId: Int? = null
//    var from: Int? = null

    private var mLastStopTime = 0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        baseActivity.supportActionBar?.hide()
        binding.handleClick = this
        permission()
        blurButton()
        getDatFromArg()
    }

    override fun onResume() {
        super.onResume()
//        initUI()
    }

    override fun getLayoutRes() = R.layout.fragment_record_audio

    private fun getDatFromArg() {
        arguments?.let {
            fragArgs = RecordAudioFragmentArgs.fromBundle(it)
//            lectureId = value.lectureId
//            courseId = value.courseId
//            childPosition = value.childPosition
//            model = value.sendSectionModel
//            sectionId = model?.sectionId
//            from = value.from
        }
    }

    private fun blurButton() {
        binding.ivAudioDelete.apply {
            alpha = 0.5f
            isEnabled = false
        }
        binding.ivAudioSave.apply {
            alpha = 0.5f
            isEnabled = false
        }
    }

    private fun showButton() {
        binding.ivAudioDelete.apply {
            alpha = 1f
            isEnabled = true
        }
        binding.ivAudioSave.apply {
            alpha = 1f
            isEnabled = true
        }
    }

    private fun handleTimer() {
        binding.tvRecordingTime.apply {
            if (mLastStopTime == 0L) {
                base = SystemClock.elapsedRealtime()
            } else {
                val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
                base += intervalOnPause
            }
            start()
        }
    }

    override fun onPause() {
        super.onPause()
//        pauseRecording()
    }

    private fun permission() {
        PermissionUtilClass.builder(baseActivity).requestExternalStorage()
            .getCallBack { b, strings, _ ->
                if (b) {
                    initMediaRecorder()

                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()


    }


    private fun initMediaRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaRecorder = MediaRecorder(baseActivity)
        } else {
            mediaRecorder = MediaRecorder()

        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        var audiofile: File
        try {
            audiofile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
                        + "/AUD_${System.currentTimeMillis()}_recording.aac"
            )

        } catch (e: IOException) {
            Log.e("TAG", "external storage access error")
            return
        }

        output = audiofile.absolutePath
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/${System.currentTimeMillis()}_recording.mp3"
        try {

            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        } catch (e: Exception) {
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        }
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

    }

    private fun startRecording() {
        try {
            binding.ivAudioWaveImg.gone()
            binding.ivAudioWaveImage.visible()
            binding.ivAudioWaveImage.setFreezesAnimation(true)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            handleTimer()
            showButton()
            recordingStatus = 1
            binding.ivAudioPlay.setImageResource(R.drawable.ic_pause)
            binding.ivAudioPlay.contentDescription = baseActivity.getString(R.string.pause_icon)

            binding.tvRecording.text = baseActivity.getString(R.string.recording__)
            state = true
        } catch (e: IllegalStateException) {
            showException(e)
        } catch (e: IOException) {
            showException(e)

        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (state) {
            binding.ivAudioWaveImg.visible()
            binding.ivAudioWaveImage.gone()
            recordingStatus = 2
            countDownTimer?.cancel()
            binding.tvRecording.text = getString(R.string.recording_paused_message)
            binding.tvRecordingTime.stop()
            mLastStopTime = SystemClock.elapsedRealtime()
            binding.ivAudioPlay.setImageResource(R.drawable.ic_record)
            binding.ivAudioPlay.contentDescription =
                baseActivity.getString(R.string.record_audio_icon)

            binding.ivAudioSave.apply {
                alpha = 1f
                isEnabled = true
            }
            mediaRecorder?.pause()
            recordingStopped = true
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        try {
            binding.ivAudioWaveImg.gone()
            binding.ivAudioWaveImage.visible()
            recordingStatus = 1
            handleTimer()
            binding.tvRecording.text = getString(R.string.recording_dots)
            mediaRecorder?.resume()
            binding.ivAudioPlay.setImageResource(R.drawable.ic_pause)
            binding.ivAudioPlay.contentDescription = baseActivity.getString(R.string.pause_icon)

            recordingStopped = false
        } catch (e: Exception) {
            deleteAudio()
            startRecording()
        }
    }

    private fun stopRecording() {
        if (state) {
            binding.ivAudioWaveImg.visible()
            binding.ivAudioWaveImage.gone()
            mediaRecorder?.stop()
            mediaRecorder?.release()
            binding.tvRecording.text = baseActivity.getString(R.string.recording_stopped)

            state = false
        } else {
            showToastShort(getString(R.string.you_are_not_recording_right_now))
//            Toast.makeText(
//                baseActivity,
//                getString(R.string.you_are_not_recording_right_now),
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recordingStatus = 0
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    override fun onHandleClick(vararg items: Any) {
        val view = items[0] as View
        when (view.id) {
            R.id.iv_close -> {
                if (mediaRecorder != null) {
                    mediaRecorder?.let {

                        it.release()
                        mediaRecorder = null
                    }
                    onClickBack()

                } else {
                    onClickBack()

                }


            }
            R.id.iv_audio_save -> {
                stopRecording()
                output = FileUtils.getCreatedFilePath(baseActivity, File(output), "audio/aac")
                    ?.toString() ?: output


                if (!state) {

                    if (fragArgs.fromLesson) {
                        setFragmentResult(
                            "onRecordCallback",
                            bundleOf("recordedUri" to output, "fromRecord" to true)
                        )
                        findNavController().popBackStack()
                    } else {
                        fragArgs.lessonArgs?.filePath = output
                        showLog("VIDEO_COMMAND", "file output >> $output")
                        val action =
                            RecordAudioFragmentDirections.actionRecordAudioFragmentToEditAudioFragment(
                                fragArgs.lessonArgs
                            )
                        findNavController().navigateTo(action)
                    }


                }
            }
            R.id.iv_audio_delete -> {
                deleteAudio()

            }
            R.id.iv_audio_play -> {

                when (recordingStatus) {
                    0 -> {
                        startRecording()

                    }
                    1 -> {
                        pauseRecording()
                    }
                    else -> {
                        resumeRecording()
                    }
                }
            }

        }
    }

    private fun deleteAudio() {
        binding.ivAudioWaveImg.visible()
        binding.ivAudioWaveImage.gone()
        mLastStopTime = 0L
        recordingStatus = 0
        binding.tvRecordingTime.stop()
        binding.tvRecordingTime.base = SystemClock.elapsedRealtime()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_record)
        binding.ivAudioPlay.contentDescription = baseActivity.getString(R.string.record_audio_icon)

        try {


            mediaRecorder?.let {
                it.stop()
                it.reset()
                it.release()
                mediaRecorder = null
            }
        } catch (e: Exception) {
            showException(e)
        }
        binding.tvRecording.text = baseActivity.getString(R.string.recording)
        initMediaRecorder()
        blurButton()
    }

    override fun onApiRetry(apiCode: String) {

    }

    fun onClickBack(isOpen: Boolean = true) {
//        setFragmentResult(
//            "onLessonBack",
//            bundleOf("isDialogOpen" to isOpen)
//        )
//        findNavController().popBackStack()
        if (fragArgs?.fromLesson) {
//            setFragmentResult(
//                "onLessonBack",
//                bundleOf("isDialogOpen" to isOpen)
//            )
//                    }
            findNavController().popBackStack()
        } else {
            confirmationPopUp(isOpen)
        }
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
//                    if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
                    setFragmentResult(
                        "onLessonBack",
                        bundleOf("isDialogOpen" to isOpen)
                    )
//                    }
                    findNavController().popBackStack()
                }

            }.notCancellable(false).icon(R.drawable.ic_alert_title)
            .build()
    }

//    private fun registerForCallback(registry: ActivityResultRegistry) {
//        getContent = registry.register(
//            "file_selection",
//            ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//
//        }
//


    override fun onDestroy() {
        super.onDestroy()


    }

}