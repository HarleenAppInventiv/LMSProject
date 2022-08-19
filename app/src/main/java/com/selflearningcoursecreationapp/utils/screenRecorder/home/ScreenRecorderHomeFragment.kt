package com.selflearningcoursecreationapp.utils.screenRecorder.home

import android.Manifest
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Rational
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.FragmentHomeVideoRecordingBinding
import com.selflearningcoursecreationapp.utils.screenRecorder.MainActivity.Companion.ACTION_TOGGLE_RECORDING
import com.selflearningcoursecreationapp.utils.screenRecorder.data.Recording
import com.selflearningcoursecreationapp.utils.screenRecorder.recordings.RecordingAdapter
import com.selflearningcoursecreationapp.utils.screenRecorder.recordings.RecordingDetailsLookup
import com.selflearningcoursecreationapp.utils.screenRecorder.recordings.RecordingKeyProvider
import com.selflearningcoursecreationapp.utils.screenRecorder.recordings.RecordingsViewModel
import com.selflearningcoursecreationapp.utils.screenRecorder.services.RecorderService
import com.selflearningcoursecreationapp.utils.screenRecorder.services.RecorderState
import com.selflearningcoursecreationapp.utils.screenRecorder.services.UriType
import com.selflearningcoursecreationapp.utils.screenRecorder.settings.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_home_video_recording.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File


open class ScreenRecorderHomeFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var recordingsAdapter: RecordingAdapter

    private lateinit var selectionTracker: SelectionTracker<Recording>

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: RecordingsViewModel by viewModels()
    private lateinit var fab: FloatingActionButton

    private val root = Environment.getExternalStorageDirectory().toString()
    private val app_folder = "$root/SelfApp/"
    private lateinit var recorderState: LiveData<RecorderState.State>

    private lateinit var preferences: PreferenceHelper


    private var isImageSelected: Boolean = false

    private lateinit var binding: FragmentHomeVideoRecordingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeVideoRecordingBinding.inflate(inflater, container, false)

        binding.messageNoVideo.visibility = View.GONE
        isImageSelected = true
        binding.videosList.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            recordingsAdapter = RecordingAdapter()
            adapter = recordingsAdapter
            selectionTracker = SelectionTracker.Builder(
                "recording-selection-id",
                this,
                RecordingKeyProvider(recordingsAdapter),
                RecordingDetailsLookup(this),
                StorageStrategy.createParcelableStorage(Recording::class.java)
            )
                .withOnItemActivatedListener { item, _ ->
                    onRecordingClick(item.selectionKey!!)
                    return@withOnItemActivatedListener true
                }
                .build()
            savedInstanceState?.let { selectionTracker.onRestoreInstanceState(it) }
            recordingsAdapter.selectionTracker = selectionTracker
        }


        return binding.root
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        preferences = PreferenceHelper(requireContext()).apply {
            // preferenceHelper.doPostInitCheck()
            // preferenceHelper.checkOutputDirectory()
            // preferenceHelper.checkRecordAudio()
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                recordAudio = true
            }
            saveLocation?.let { uri ->
                if (uri.type == UriType.SAF) {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri.uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    requireContext().contentResolver.persistedUriPermissions.filter { it.uri == uri.uri }
                        .apply {
                            if (isEmpty()) {
                                resetSaveLocation()
                            }
                        }
                }
            }
        }

        recorderState = viewModel.recorderState

        // Respond to app shortcut
        requireActivity().intent.action?.let {
            when (it) {
                ACTION_TOGGLE_RECORDING -> if (RecorderState.State.STOPPED == recorderState.value)
                    startRecording()
                else {
                    stopRecording()
                    requireActivity().finish()
                }
            }
        }

        view.findViewById<Toolbar?>(R.id.toolbar)?.title = getString(R.string.home_recordings_title)
        // Configure fab
        fab = view.findViewById(R.id.floting_action)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                )
                startActivityForResult(intent, 12345)
            }
        }



        try {
            fab.apply {
                setOnClickListener {


                    when {
                        selectionTracker.hasSelection() -> selectionTracker.clearSelection()
                        isRecording -> stopRecording()
                        else -> {
                            if (preferences.saveLocation == null) {
                                showChooseTreeUri()
                            } else {
                                startRecording()
                            }
                        }
                    }


                }
                setOnLongClickListener {
                    Toast.makeText(
                        requireContext(),
                        R.string.home_fab_record_hint,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    true
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }




        recorderState.observe(viewLifecycleOwner)
        {
            when (it) {
                RecorderState.State.RECORDING -> {
                    onRecording()
                }
                RecorderState.State.PAUSED -> {
                    onRecordingPaused()
                }
                RecorderState.State.STOPPED -> {
                    onRecordingStopped()
                }
                else -> {
                    onRecordingStopped()
                }
            }
        }

        requestPer()


    }

    override fun onResume() {
        super.onResume()
        bar.visibility = View.INVISIBLE
        fab.visibility = View.VISIBLE
    }

    private val isRecording: Boolean
        get() = recorderState.value?.run {
            this != RecorderState.State.STOPPED
        } ?: false

    private fun onRecording() {
        configureFab(true)
    }

    private fun onRecordingStopped() {
        configureFab(false)
    }

    private fun onRecordingPaused() {
        configureFab(true)
    }


    private fun requestPer() {

        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
//                    setupSurfaceHolder()
                    startCamera()

                }


                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {


                }
            }).check()
    }

    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext().applicationContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.ivPreview.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun configureFab(isRecording: Boolean) {
        fab.setImageDrawable(if (isRecording) R.drawable.ic_stop else R.drawable.ic_record)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startRecording() {
        // Request Screen recording permission
        val projectionManager =
            requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            SCREEN_RECORD_REQUEST_CODE
        )


        val intent = Intent(requireContext(), FloatingWidgetService::class.java)
        requireContext().startService(intent)
        openPipMode()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openPipMode() {


        val width: Int = requireActivity().windowWidth
        val height: Int = requireActivity().windowHeight

        val ratio = Rational(width, height)
        val pip_Builder = PictureInPictureParams.Builder()
        pip_Builder.setAspectRatio(ratio).build()
        requireActivity().enterPictureInPictureMode(pip_Builder.build())

        bar.visibility = View.GONE
        fab.visibility = View.GONE
    }

    val Activity.windowHeight: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = requireActivity().windowManager.currentWindowMetrics
                val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
                metrics.bounds.height() - insets.bottom - insets.top
            } else {
                val view = requireActivity().window.decorView
                val insets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                        .getInsets(WindowInsetsCompat.Type.systemBars())
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
                resources.displayMetrics.heightPixels - insets.bottom - insets.top
            }
        }

    val Activity.windowWidth: Int
        @RequiresApi(Build.VERSION_CODES.M)
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = requireActivity().windowManager.currentWindowMetrics
                val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
                metrics.bounds.width() - insets.left - insets.right
            } else {
                val view = requireActivity().window.decorView
                val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.systemBars())
                resources.displayMetrics.widthPixels - insets.left - insets.right
            }
        }

    private fun stopRecording() {
        RecorderService.stop(requireContext())


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SCREEN_RECORD_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    RecorderService.start(requireContext(), resultCode, data)
                }



                requireActivity().intent.action?.takeIf { it == ACTION_TOGGLE_RECORDING }?.let {
                    requireActivity().finish()
                }
            }
            REQUEST_DOCUMENT_TREE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onTreeUriResult(resultCode, data)
                }
                if (preferences.saveLocation != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startRecording()
                    } else {
                        //TODO: handle for lower version
                    }
                }
            }
//            111 -> {
//
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        //Image Uri will not be null for RESULT_OK
//                        val uri: Uri = data?.data!!
//
//                        // Use Uri object instead of File to avoid storage permissions
//                        viewModel.recordings.observe(viewLifecycleOwner) {
//                            onDataLoaded(it, uri)
//
//
//                        }
//
//                    }
//                    ImagePicker.RESULT_ERROR -> {
//                        Toast.makeText(
//                            requireContext(),
//                            ImagePicker.getError(data),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    else -> {
//                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
        }
    }

    private fun onTreeUriResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data!!.data!!
            requireContext().contentResolver.apply {
                takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                persistedUriPermissions.filter { it.uri == uri }.apply {
                    if (isNotEmpty()) {
                        PreferenceHelper(requireContext()).setSaveLocation(uri, UriType.SAF)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        if (isRecording) {

            openPipMode()
            //TODO: check in version lower than o
        }

    }

    private fun showChooseTreeUri() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_location_dialog_title)
            .setPositiveButton(R.string.choose_location_action) { _, _ ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.putExtra(
                    "android.provider.extra.INITIAL_URI",
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, REQUEST_DOCUMENT_TREE)
            }
            .create().show()
    }


//    private fun showRenameRecordingDialog(context: Context, recording: Recording) {
//        val inflater = LayoutInflater.from(context)
//
//        @SuppressLint("InflateParams")
//        val view = inflater.inflate(R.layout.dialog_rename_file, null)
//        val input = view.findViewById<EditText>(R.id.input)
//        input.setText(recording.title.filename)
//        input.selectAll()
//        view.findViewById<TextView>(R.id.extension).text = recording.title.extension
//
//        MaterialAlertDialogBuilder(context)
//            .setTitle(R.string.dialog_title_rename)
//            .setPositiveButton(android.R.string.ok) { dialog, _ ->
//                val newName = input.text.toString().trim { it <= ' ' } + recording.title.extension
//                rename(recording, newName)
//                selectionTracker.clearSelection()
//                dialog.cancel()
//            }
//            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
//            .setView(view)
//            .show()
//    }

    private fun showDeleteRecordingDialog(recording: Recording) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_file_msg)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                delete(recording)
                selectionTracker.clearSelection()
                dialog.cancel()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showDeleteRecordingsDialog(recordings: List<Recording>) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_all_msg)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                delete(recordings)
                selectionTracker.clearSelection()
                dialog.cancel()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.cancel() }
            .show()
    }
//
//    private fun showShareRecordingDialog(recording: Recording) {
//        startActivity(
//            Intent.createChooser(
//                createShareIntent(recording.uri),
//                getString(R.string.notification_finish_title)
//            )
//        )
//    }

    private fun createShareIntent(uri: Uri) = Intent()
        .setAction(Intent.ACTION_SEND)
        .setType("video/*")
        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        .putExtra(Intent.EXTRA_STREAM, uri)

//    private fun shareVideos(recordings: List<Recording>) {
//        val videoList = ArrayList<Uri>()
//        for (recording in recordings) {
//            videoList.add(recording.uri)
//        }
//        val shareIntent = Intent()
//            .setAction(Intent.ACTION_SEND_MULTIPLE)
//            .setType("video/*")
//            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            .putParcelableArrayListExtra(Intent.EXTRA_STREAM, videoList)
//        startActivity(
//            Intent.createChooser(
//                shareIntent,
//                getString(R.string.notification_finish_title)
//            )
//        )
//    }

    private fun FloatingActionButton.setImageDrawable(res: Int) {
        this.setImageDrawable(ContextCompat.getDrawable(requireContext(), res))
    }

    companion object {
        const val SCREEN_RECORD_REQUEST_CODE = 1003
        const val REQUEST_DOCUMENT_TREE = 22
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }


    protected open fun onRecordingClick(recording: Recording) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(
                recording.uri,
                requireContext().contentResolver.getType(recording.uri)
            )
        startActivity(intent)
    }

    private fun onDataLoaded(data: List<Recording>, uri: Uri) {
//        binding.messageNoVideo.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
//        recordingsAdapter.updateData(data)

//        if (isImageSelected) {
//            if (data.isNotEmpty()) {
//
//
//                addWaterMarkProcess(
//                    FileUtils.getPathFromUri(requireContext(), data[0].uri).toString(),
//                    getPath(requireContext(), uri), data
//                )
//
//
//            }
//        }


    }


    private fun getPath(context: Context, uri: Uri): String {
        var realPath = String()
        uri.path?.let { path ->

            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = context.contentResolver.query(
                    databaseUri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        return realPath
    }

    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName)
            .also {
                if (!it.exists()) {
                    it.outputStream().use { cache ->
                        context.assets.open(fileName).use { inputStream ->
                            inputStream.copyTo(cache)
                        }
                    }
                }
            }


//    private fun addWaterMarkProcess(
//        video: String,
//        image: String,
//        data: List<Recording>
//    ) {
//
//        var filePrefix = "VIDEO"
//        var fileExtn = ".mp4"
//        var filePath = ""
//        showProgressBar()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val valuesvideos = ContentValues()
//            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Shubham")
//            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix + System.currentTimeMillis())
//            valuesvideos.put(
//                MediaStore.Video.Media.DISPLAY_NAME,
//                filePrefix + System.currentTimeMillis() + fileExtn
//            )
//            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
//            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
//            val uri: Uri? = requireContext().contentResolver.insert(
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                valuesvideos
//            )
//            val file: File =
//                com.selflearningcoursecreationapp.utils.screenRecorder.settings.FileUtils.getFileFromUri(
//                    requireContext(),
//                    uri!!
//                )
//            filePath = file.absolutePath
//        } else {
//            filePrefix = "reverse"
//            fileExtn = ".mp4"
//            var dest = File(File(app_folder), filePrefix + fileExtn)
//            var fileNo = 0
//            while (dest.exists()) {
//                fileNo++
//                dest = File(File(app_folder), filePrefix + fileNo + fileExtn)
//            }
//            filePath = dest.absolutePath
//
//        }
//
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(video)
//        val videWidth =
//            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
//        val videoHeight =
//            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
//        retriever.release()
//
//
//        val xPos = videWidth.div(2).toFloat()
//        val yPos = videoHeight.div(2).toFloat()
//        isImageSelected = false
//
//        hideProgressBar()
//
//        val complexCommand = arrayOf(
//            "ffmpeg",
//            "-y",
//            "-i",
//            video,
//            "-strict",
//            "experimental",
//            "-vf",
//            "movie=$image [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]",
//            "-s",
//            "320x240",
//            "-r",
//            "30",
//            "-b",
//            "15496k",
//            "-vcodec",
//            "mpeg4",
//            "-ab",
//            "48000",
//            "-ac",
//            "2",
//            "-ar",
//            "22050",
//            filePath
//        )
////
////        val query:Array<String> = arrayOf("ffmpeg", "-i", "$video", "-i", "$image" ,"-filter_complex", "overlay=1500:1000", "$filePath")
////        CallBackOfQuery().callQuery(complexCommand, object : FFmpegCallBack {
////            override fun statisticsProcess(statistics: Statistics) {
////                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber.toString())
////            }
////
////            override fun process(logMessage: LogMessage) {
////
////
////                Log.i("FFMPEG LOG : ", logMessage.text)
////            }
////
////            override fun success() {
////            }
////
////            override fun cancel() {
////            }
////
////            override fun failed() {
////            }
////        })
//
//
//        val videoFile: File = File(video)
//        var retrievesr = MediaMetadataRetriever()
//        retrievesr.setDataSource(requireContext(), Uri.fromFile(videoFile))
//        val time = retrievesr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        val timeInMillisec = time!!.toLong()
//        var timeInSec = (timeInMillisec / 1000).toInt()
//
//
//        val query1 = FFmpegQueryExtension().addVideoWaterMark(video, image, xPos, yPos, filePath)
//        CallBackOfQuery().callQuery(query1, object : FFmpegCallBack {
//            override fun process(logMessage: LogMessage) {
//
//                Toast.makeText(requireContext(), "Converting", Toast.LENGTH_LONG).show()
//
//                Log.e("ScreenRecorderHomeFragment", "process" + Gson().toJson(logMessage))
//
////            ImagePicker.with(requireActivity()).saveDir(File((outputPath)))
//
//
//            }
//
//
//            override fun success() {
//
//
//                hideProgressBar()
////                delete(data)
//
//                Toast.makeText(requireContext(), "success", Toast.LENGTH_LONG).show()
//
//
//            }
//
//            override fun cancel() {
//                hideProgressBar()
//                Toast.makeText(requireContext(), "cancel", Toast.LENGTH_LONG).show()
//
//                Log.e("ScreenRecorderHomeFragment", "cancel")
//
//            }
//
//            override fun failed() {
//                hideProgressBar()
//                Toast.makeText(requireContext(), "failed", Toast.LENGTH_LONG).show()
//
//                Log.e("ScreenRecorderHomeFragment", "failed")
//
//            }
//        })
////
////        val startTimeString = "00:01:00" (HH:MM:SS)
////        val endTimeString = "00:02:00" (HH:MM:SS)
//
//        /*  Log.e("ScreenRecorderHomeFragment", "first${File(filePath).length()}")
//          val query:Array<String> = FFmpegQueryExtension().addVideoWaterMark(video, image, xPos, yPos, output = filePath)
//          CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
//              override fun statisticsProcess(statistics: Statistics) {
//                 Log.e("ScreenRecorderHomeFragment", statistics.size.toString())
//              }
//
//              override fun process(logMessage: LogMessage) {
//               Log.e("ScreenRecorderHomeFragment", logMessage.text)
//
//              }
//
//              override fun success() {
//  //                writeFileOnInternalStorage(requireContext(),filePath)
//
//                  Log.e("ScreenRecorderHomeFragment", "size${File(filePath).length()/1024}")
//                  //Output = outputPath
//              }
//
//              override fun cancel() {
//                  Log.e("ScreenRecorderHomeFragment", "cancel")
//              }
//
//              override fun failed() {
//                  Log.e("ScreenRecorderHomeFragment", "failed")
//              }
//          })*/
//    }


    protected fun rename(recording: Recording, newName: String) {
        viewModel.rename(recording, newName)
    }

    protected fun delete(recording: Recording) {
        viewModel.deleteRecording(recording)
    }

    protected fun delete(recordings: List<Recording>) {
        viewModel.deleteRecordings(recordings)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(requireContext(), FloatingWidgetService::class.java)
        requireContext().stopService(intent)
    }


}

