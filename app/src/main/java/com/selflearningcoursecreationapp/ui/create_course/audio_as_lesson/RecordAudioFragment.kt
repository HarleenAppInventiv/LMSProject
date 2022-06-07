package com.selflearningcoursecreationapp.ui.create_course.audio_as_lesson

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRecordAudioBinding
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import java.io.IOException


class RecordAudioFragment : BaseFragment<FragmentRecordAudioBinding>(), HandleClick {
    private var output: String? = null
    private var getContent: ActivityResultLauncher<Intent>? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var recordingStopped: Boolean = false
    var recordingStatus = 0
    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null
    var childPosition: Int? = 0
    var sectionId: Int? = null
    var from: Int? = null

    var mLastStopTime = 0L


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

    fun handleTimer() {
        binding.tvRecordingTime.apply {
            if (mLastStopTime == 0L) {
                base = SystemClock.elapsedRealtime()
            } else {
                val intervalOnPause = SystemClock.elapsedRealtime() - mLastStopTime
                base = base + intervalOnPause
            }
            start()
        }
    }

    fun permission() {

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
        output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
    }

    private fun startRecording() {
        try {
            binding.ivAudioWaveImage.setFreezesAnimation(true)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            handleTimer()
            showButton()
            recordingStatus = 1
            binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)

            binding.tvRecording.text = "Recording..."
            state = true
        } catch (e: IllegalStateException) {
            Log.d("varun", "startRecording: ${e.message}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("varun", "startRecording2: ${e.message}")

        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {

        if (state) {
            if (!recordingStopped) {
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
            } else {
                resumeRecording()

            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        recordingStatus = 1
        handleTimer()
        binding.tvRecording.text = "Recording..."
        mediaRecorder?.resume()
        binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        recordingStopped = false
    }

    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            binding.tvRecording.text = "Recording Stopped"

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
        var view = items[0] as View
        when (view.id) {
            R.id.iv_close -> {
                if (mediaRecorder != null) {
                    mediaRecorder?.let {

                        it.release();
                        mediaRecorder = null;
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
                            childPosition = childPosition!!,
                            type = Constant.CLICK_ADD,
                            from = from!!
                        )
                    findNavController().navigate(action)

                }
            }
            R.id.iv_audio_delete -> {

                mLastStopTime = 0L
                recordingStatus = 0
                binding.tvRecordingTime.stop()
                binding.tvRecordingTime.base = SystemClock.elapsedRealtime()
                binding.ivAudioPlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                mediaRecorder?.let {
                    it.stop()
                    it.reset();
                    it.release();
                    mediaRecorder = null;
                }
                binding.tvRecording.text = "Start Recording"
                initMediaRecorder()
                blurButton()

            }
            R.id.iv_audio_play -> {

                if (recordingStatus == 0) {
                    startRecording()

                } else if (recordingStatus == 1) {
                    pauseRecording()
                } else {
                    resumeRecording()
                }
            }

        }
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