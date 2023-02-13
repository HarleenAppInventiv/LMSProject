package com.selflearningcoursecreationapp.utils.screenRecorder

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.ACTION_RECORDER_BROADCAST
import com.selflearningcoursecreationapp.utils.RequestCode
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import com.selflearningcoursecreationapp.utils.customViews.LMSImageView
import com.selflearningcoursecreationapp.utils.screenRecorder.home.FloatingWidgetService
import com.selflearningcoursecreationapp.utils.screen_recording_v1.ScreenRecordService
import com.selflearningcoursecreationapp.utils.screen_recording_v1.screenHightNew
import com.selflearningcoursecreationapp.utils.screen_recording_v1.screenWidthNew
import kotlinx.android.synthetic.main.activity_screen_recording.*

@RequiresApi(Build.VERSION_CODES.O)
class ScreenRecordingActivity : BaseActivity() {

    private var hasPermissions = false

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE =
        PERMISSION_REQ_ID_RECORD_AUDIO + 1

    private var type = 2


    //Start/Stop Button
    private var startbtn: Button? = null
    private var ivIcon: LMSImageView? = null
    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_RECORDER_BROADCAST -> {
                    isRecordingStart = false
                    val status = intent.extras?.getInt("recordingStatus")

                    when (status) {
                        3 -> {
                            runOnUiThread {
                                startbtn?.text = getString(R.string.start_recording)
                            }
                            isRecordingStart = false
                            stopService(Intent(context, FloatingWidgetService::class.java))

//
                            finish()
                        }

                        4 -> {
                            runOnUiThread {
                                isRecordingStart = true
                                initViews()
                            }


                        }

                    }

                }

            }

        }
    }


    override fun onStop() {
        super.onStop()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // Max file size in K

    private val mediaProjectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private var isRecordingStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_recording)

        checkForPermission()
    }

    private fun openPipMode() {

        if (!isMyServiceRunning(TimerWidgetService::class.java) && isMyServiceRunning(
                ScreenRecordService::class.java
            )
        ) {
            startbtn?.isEnabled = true
//            startbtn?.alpha = 1f
            startbtn?.isFocusable = false

            startbtn?.setText(R.string.stop)
            startbtn?.alpha = 1f
        }


        if (isRecordingStart && type == 1) {

            val ratio = Rational(1, 1)
            val pip_Builder = PictureInPictureParams.Builder()
            pip_Builder.setAspectRatio(ratio).build()
            enterPictureInPictureMode(pip_Builder.build())

            startbtn?.visibility = View.GONE


//        }
        }

    }


    //Init Views
    private fun initViews() {
        startbtn = findViewById(R.id.button_start)
        ivIcon = findViewById(R.id.iv_icon)
//        countDown = findViewById(R.id.pulseCountDown)

        if (isMyServiceRunning(ScreenRecordService::class.java)) {
            startbtn?.setText(R.string.stop)
            startbtn?.alpha = 1f
            startbtn?.isEnabled = true
//            startbtn?.alpha = 1f
            startbtn?.isFocusable = true
        }

        type = intent.getIntExtra("type", 2)

        if (type == 1) {
            startCamera()
        }
        ivIcon?.visibleView(type != 1)


    }

    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(applicationContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(iv_preview.surfaceProvider)
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

        }, ContextCompat.getMainExecutor(this))
    }

    //Start Button OnClickListener
    private fun setOnClickListeners() {
        startbtn?.setOnClickListener {

            if (!isMyServiceRunning(TimerWidgetService::class.java)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkSelfPermission(
                            Manifest.permission.RECORD_AUDIO,
                            PERMISSION_REQ_ID_RECORD_AUDIO
                        )
                    ) {
                        hasPermissions = true
                    }
                } else {
                    if (checkSelfPermission(
                            Manifest.permission.RECORD_AUDIO,
                            PERMISSION_REQ_ID_RECORD_AUDIO
                        ) && checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        hasPermissions = true
                    }
                }
                if (hasPermissions) {
                    if (isRecordingStart) {
                        startbtn?.setText(R.string.start_recording)
                        val intent2 = Intent(this, TimerWidgetService()::class.java)
                        intent2.putExtra("command", 3)
                        stopService(intent2)
                        val intent1 = Intent(this, ScreenRecordService::class.java)
                        intent2.putExtra("command", 3)
                        stopService(intent1)
//                        showToastLong(getString(R.string.video_saved))
                        stopService(Intent(this, FloatingWidgetService::class.java))
                    } else {

                        if (!Settings.canDrawOverlays(this)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
//                        startActivityForResult(intent, 1234)
//                        countDown?.start()

                            startActivityForResult(intent, RequestCode.OVERLAY_PERMISSION)
//                        countDown?.start {
//
//                        }
                        } else {
                            if (!isMyServiceRunning(ScreenRecordService::class.java) && !isMyServiceRunning(
                                    TimerWidgetService::class.java
                                )
                            ) {
                                val captureIntent =
                                    mediaProjectionManager.createScreenCaptureIntent()
                                startActivityForResult(captureIntent, RequestCode.RECORD_PERMISSION)
                            }
                        }

                    }
                }
            }
            //first check if permissions was granted

        }
    }


    override fun onPause() {
        super.onPause()
        openPipMode()
    }


    //Check if permissions was granted
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    override fun onResume() {

//        if (this@ScreenRecordingActivity.isInPictureInPictureMode) {
//            val startIntent = Intent(
//                this@ScreenRecordingActivity,
//                ScreenRecordingActivity::class.java
//            )
//            startIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//            this@ScreenRecordingActivity.startActivity(startIntent)


//        }
        super.onResume()

        startbtn?.visibility = View.VISIBLE
//        if (isRecordingStart) {
//            startbtn?.text = getString(R.string.stop_recording)
//        }
        if (isMyServiceRunning(ScreenRecordService::class.java)) {
            startbtn?.setText(R.string.stop)
            isRecordingStart = true
            initViews()
        }
        try {
            val intentFilter = IntentFilter(ACTION_RECORDER_BROADCAST)
            LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                RequestCode.RECORD_PERMISSION -> {
                    isRecordingStart = true
                    startbtn?.alpha = .5f
                    startbtn?.isEnabled = false
                    val intent2 = Intent(this, TimerWidgetService::class.java)
                    intent2.putExtra("command", 1)
                    intent2.putExtra("largeIconId", R.drawable.ic_notification_icon)
                    intent2.putExtra("smallIconId", R.drawable.ic_notification_icon)
                    intent2.putExtra("width", R.drawable.ic_notification_icon)
                    intent2.putExtra("width", screenWidthNew)
                    intent2.putExtra("height", screenHightNew)
                    intent2.putExtra("data", data)
                    startService(intent2)
                    openPipMode()

//                    if (type != 1) {
//                        this.moveTaskToBack(true);
//                    }

                    if (type == 2) {
                        finish()
                    }
                }

            }
        }
    }

    private fun checkForPermission() {
        PermissionUtilClass.builder(this)
            .requestPermissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.CAMERA)
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO

                    )
                }

            )
            .getCallBack { b, _, _ ->
                if (b) {
                    initViews()
                    setOnClickListeners()

                } else {

                    showToastLong(getString(R.string.settings))

                }
            }.build()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}
