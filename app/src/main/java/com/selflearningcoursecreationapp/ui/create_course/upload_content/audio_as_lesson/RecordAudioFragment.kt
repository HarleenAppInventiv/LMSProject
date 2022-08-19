package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.MediaRecorder
import android.os.*
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRecordAudioBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import java.io.IOException


class RecordAudioFragment : BaseFragment<FragmentRecordAudioBinding>(), HandleClick {
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var recordingStopped: Boolean = false
    private var recordingStatus = 0
    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null
    var childPosition: Int? = -1
    var sectionId: Int? = null
    var from: Int? = null

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

    override fun getLayoutRes() = R.layout.fragment_record_audio

    private fun getDatFromArg() {
        arguments?.let {
            val value = RecordAudioFragmentArgs.fromBundle(it)
            lectureId = value.lectureId
            courseId = value.courseId
            childPosition = value.childPosition
            model = value.sendSectionModel
            sectionId = model?.sectionId
            from = value.from
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

    private fun permission() {

//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data =
//                    Uri.parse(String.format("package:%s", getAppContext().packageName))
//                getContent?.launch(intent)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                getContent?.launch(intent)
//            }
//
//        }


//
//            PermissionUtil.checkPermissions(
//                requireActivity(),
//                arrayOf(
//                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
//
//                ),
//                Permission.RECORD_AUDIO
//            ) {
//                if (it) {
        initMediaRecorder()
//                } else {
//                    if (shouldShowRequestPermissionRationale(
//                            Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//
//                    ) {
//                        showToastShort(baseActivity.getString(R.string.no_permission_accepted))
//
//                    } else {
//                        baseActivity.permissionDenied()
//                    }
//
//                }
//            }
////
//        } else {

//            initMediaRecorder()

//        }
    }


    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder()
        output =
            Environment.getExternalStorageDirectory().absolutePath + "/${System.currentTimeMillis()}_recording.mp3"
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
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
            binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)

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
            binding.tvRecording.text = "Recording Paused"
            binding.tvRecordingTime.stop()
            mLastStopTime = SystemClock.elapsedRealtime()
            binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
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
        binding.ivAudioWaveImg.gone()
        binding.ivAudioWaveImage.visible()
        recordingStatus = 1
        handleTimer()
        binding.tvRecording.text = "Recording..."
        mediaRecorder?.resume()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        recordingStopped = false
    }

    private fun stopRecording() {
        if (state) {
            binding.ivAudioWaveImg.visible()
            binding.ivAudioWaveImage.gone()
            mediaRecorder?.stop()
            mediaRecorder?.release()
            binding.tvRecording.text = getString(R.string.recording_stopped)

            state = false
        } else {
            Toast.makeText(
                baseActivity,
                "You are not recording right now!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recordingStatus = 0
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
                    findNavController().navigateUp()

                } else {
                    findNavController().navigateUp()

                }


            }
            R.id.iv_audio_save -> {
                stopRecording()
                if (!state) {
                    val action =
                        RecordAudioFragmentDirections.actionRecordAudioFragmentToAudioLectureFragment(
                            sendSectionModel = model,
                            filePath = output.toString(),
                            courseId = courseId!!,
                            lectureId = lectureId!!,
                            childPosition = childPosition ?: -1,
                            type = Constant.CLICK_ADD,
                            from = from!!
                        )
                    findNavController().navigate(action)

                }
            }
            R.id.iv_audio_delete -> {
                binding.ivAudioWaveImg.visible()
                binding.ivAudioWaveImage.gone()
                mLastStopTime = 0L
                recordingStatus = 0
                binding.tvRecordingTime.stop()
                binding.tvRecordingTime.base = SystemClock.elapsedRealtime()
                binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                mediaRecorder?.let {
                    it.stop()
                    it.reset()
                    it.release()
                    mediaRecorder = null
                }
                binding.tvRecording.text = baseActivity.getString(R.string.start_recording)
                initMediaRecorder()
                blurButton()

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

    override fun onApiRetry(apiCode: String) {

    }

//    private fun registerForCallback(registry: ActivityResultRegistry) {
//        getContent = registry.register(
//            "file_selection",
//            ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//
//        }
//    }

}