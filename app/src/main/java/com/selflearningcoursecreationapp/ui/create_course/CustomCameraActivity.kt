package com.selflearningcoursecreationapp.ui.create_course

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ActivityCustomCameraBinding
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.ui.custom_camera.CameraArAdapter
import com.selflearningcoursecreationapp.utils.FileUtils
import kotlinx.android.synthetic.main.activity_custom_camera.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CustomCameraActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCustomCameraBinding
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null


    private var flashMode = ImageCapture.FLASH_MODE_AUTO
    private var camera: Camera? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var clickType = -1
    private var flashModeVal = 0 // 0 for auto, 1 for on, 2 for off

    companion object {
        private const val TAG = "CustomCameraX"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 1002
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private val REQUIRED_PERMISSIONS2 = arrayOf(Manifest.permission.CAMERA)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_camera)
        if (allPermissionsGranted()) {
            startCamera(cameraSelector)
        } else {
            ActivityCompat.requestPermissions(
                this@CustomCameraActivity,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) REQUIRED_PERMISSIONS2 else REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        // Setup the listener for take photo button

//        setAdapter()
        setClickListeners()

//        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setAdapter() {
        //Adapter for flash light
        binding.lFlashLayout.tvRatio.text = getString(R.string.flash)
        binding.lFlashLayout.rvRatio.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.lFlashLayout.rvRatio.adapter =
            CameraArAdapter(viewType = 1) {
                // click listener
                when (it) {
                    0 -> {
                        flashMode = ImageCapture.FLASH_MODE_AUTO
                        imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                            startCamera(cameraSelector)

                        }
                    }
                    1 -> {
                        flashMode = ImageCapture.FLASH_MODE_ON
                        imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                            startCamera(cameraSelector)

                        }
                    }
                    2 -> {
                        flashMode = ImageCapture.FLASH_MODE_OFF
                        imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                            startCamera(cameraSelector)

                        }
                    }
                }
            }
    }

    private fun setClickListeners() {
//        binding.lCameraOption.ivCross.setOnClickListener(this)
        binding.switchCamera.setOnClickListener(this)
        binding.btnCapture.setOnClickListener(this)
        binding.lCameraOption.ivFlash.setOnClickListener(this)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(cameraSelector)
            } else {
                Toast.makeText(
                    this@CustomCameraActivity,
                    getString(R.string.permission_not_granted_by_the_user),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun allPermissionsGranted() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS2.all {
                ContextCompat.checkSelfPermission(
                    baseContext, it
                ) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    baseContext, it
                ) == PackageManager.PERMISSION_GRANTED
            }

        }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@CustomCameraActivity)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(previewViewCamera.surfaceProvider) }
            imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()
//            imageCapture?.targetRotation= Surface.ROTATION_180
//            preview.targetRotation = Surface.ROTATION_180


            // Select ic_see_more camera
//            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this@CustomCameraActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this@CustomCameraActivity))
    }

    private fun takePhoto() {

//        val animator: ObjectAnimator = ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f)
//        animator.setDuration(1000)
//        animator.setRepeatCount(ObjectAnimator.INFINITE)
//        animator.setRepeatMode(ObjectAnimator.REVERSE)
//        animator.start()

        val anim: Animation = ScaleAnimation(
            1f, 1.1f,  // Start and end values for the X axis scaling
            1f, 1.1f,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling

        anim.setFillAfter(false) // Needed to keep the result of the animation

        anim.setDuration(800)
        binding.btnCapture.startAnimation(anim)


        // Get a stable reference of the
        // modifiable image capture use case
        showLog("CUSTOM_CAMERA", "takePhoto")
        val imageCapture = imageCapture ?: return
        showLog("CUSTOM_CAMERA", "takePhoto next")

        val timestamp = System.currentTimeMillis()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Skillfy")
            }
        }
//        val metadata = ImageCapture.Metadata()
//        metadata.isReversedVertical = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
//        metadata.isReversedHorizontal = true
//        metadata.isReversedHorizontalSet =

        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(
                this@CustomCameraActivity.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build(),
            ContextCompat.getMainExecutor(this@CustomCameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
//                    animator.end()

                    showLog("CUSTOM_CAMERA", "exec ${exc.message}")

                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    animator.end()
                    val uri =
                        output.savedUri?.let { FileUtils.getPath(this@CustomCameraActivity, it) }
                    val savedUri = FileUtils.compressImage(
                        this@CustomCameraActivity,
                        uri,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    // set the saved uri to the image view
                    if (savedUri != null) {
                        showLog("CUSTOM_CAMERA", "uri not null")


                        val msg = "Photo capture succeeded: $savedUri"
                        //contentResolver.takePersistableUriPermission(savedUri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val intent = Intent()
                        intent.data = savedUri
                        // intent.putExtra("data", savedUri)
                        setResult(Activity.RESULT_OK, intent)
                        //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                        finish()

//                        setFragmentResult(
//                            "imageData",
//                            bundleOf(
//                                "fileUri" to savedUri.path
//                            )
//                        )
//                        findNavController().navigateUp()
//                        if (imageList.isEmpty()) {
//                            showRetakeDialog(savedUri.toString())
//                        }
//                        else {
//                            imageList.add(savedUri.toString())
//                            adapter?.notifyDataSetChanged()
//
//                            if (imageList.size >= (maxImage ?: 10)) {
//                                binding.btnCapture.inVisible()
//                                AppUtils.showInfoBottomToast(
//                                    requireContext(),
//                                    getString(R.string.you_can_add_upto_10_images)
//                                )
//                            } else {
//                                binding.btnCapture.visible()
//                            }
//
//                            if (imageList.size >= 2) {
//                                if (SharedPreferencesManager.get().getBoolean(
//                                        AppConstants.PreferenceConstants.ALREADY_SAW_DRAG_HINT,
//                                        false
//                                    )
//                                ) {
//                                    if (binding.popHint.visibility != View.VISIBLE)
//                                        binding.popHint.gone()
//                                } else {
//                                    binding.popHint.visible()
//                                    SharedPreferencesManager.get().save(
//                                        AppConstants.PreferenceConstants.ALREADY_SAW_DRAG_HINT,
//                                        true
//                                    )
//                                }
//                            }
//                        }
                    } else {
                        showLog("CUSTOM_CAMERA", "uri null")
                    }
//                    binding.btnCapture.isEnabled = true
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun showHideView(type: Int) {
        clickType = type
        binding.typeOfClick = type
        binding.executePendingBindings()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            R.id.ivCross -> {
//                finish()
//            }

            R.id.switchCamera -> {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                startCamera(cameraSelector)
            }

            R.id.btnCapture -> {
                takePhoto()
            }

            R.id.ivFlash -> {
                if (flashModeVal == 2)
                    flashModeVal = 0
                else
                    flashModeVal += 1

                changeFlash(flashModeVal)

            }
        }
    }

    private fun changeFlash(flashType: Int) {
        when (flashType) {
            0 -> {
                binding.lCameraOption.ivFlash.setImageResource(R.drawable.ic_flash_auto)
                flashMode = ImageCapture.FLASH_MODE_AUTO
                imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                    startCamera(cameraSelector)

                }
            }
            1 -> {
                binding.lCameraOption.ivFlash.setImageResource(R.drawable.ic_flash_on)
                flashMode = ImageCapture.FLASH_MODE_ON
                imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                    startCamera(cameraSelector)

                }
            }
            2 -> {
                binding.lCameraOption.ivFlash.setImageResource(R.drawable.ic_flash_off)
                flashMode = ImageCapture.FLASH_MODE_OFF
                imageCapture?.let { it.flashMode = flashMode } ?: kotlin.run {
                    startCamera(cameraSelector)

                }
            }
        }
    }
}