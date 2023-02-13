package com.hd.videoEditor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.hd.videoEditor.R
import com.hd.videoEditor.customViews.editor.EditorView
import com.hd.videoEditor.databinding.ActivityHdEditorBinding
import com.hd.videoEditor.ffmpeg.FFmpegStateConst
import com.hd.videoEditor.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern


class HdEditorActivity : AppCompatActivity(), View.OnClickListener, EditorView.OnEditorListener {
    private var totalVideoTime: Long? = null
    private lateinit var binding: ActivityHdEditorBinding
    private val viewModel: HdEditorVM by viewModels()
    val TYPE_TRIM = 1
    val TYPE_FILTER = 2
    val TYPE_WATERMARK = 3
    var regex = Pattern.compile("[$&+,:;=\\\\?@#|'<>^*()%!]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hd_editor)
        getBundleData()
        initClickListeners()
        observeData()
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
    }

    private fun observeData() {
        viewModel.resultLiveData.observe(this, Observer {
            Loge(msg = "state>> ${it.state}")
            when (it.state) {
                FFmpegStateConst.STATE_LOADING -> {
                    binding.loadingG.visibility = View.VISIBLE
                    showProgress(0)
                }
                FFmpegStateConst.STATE_RUNNING -> {
                    showProgress(it.time)
                }
                FFmpegStateConst.STATE_FAILED -> {
                    binding.loadingG.visibility = View.GONE
                    binding.btFinish.isEnabled = true
                    binding.editG.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        "Some error occurred. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                FFmpegStateConst.STATE_COMPLETED -> {
                    binding.loadingG.visibility = View.GONE
                    val path = getCreatedVideoFilePath(
                        this,
                        File(viewModel.outputPath ?: ""),
                        viewModel.outputPath.getMimeType(3, false)
                    ) ?: Uri.parse(viewModel.outputPath)


                    setResult(
                        Activity.RESULT_OK,
                        Intent().setData(path)
                    )
                    finish()
                }
            }
        })
    }

    private fun showProgress(time: Int?) {
        time?.let {
            val percentage = (it).toDouble() / ((totalVideoTime?.toDouble() ?: 0.0) / 100.0)
            binding.tvProcessing.text = String.format("Saving File .. (%.2f %s)", percentage, "%")
        }
    }

    private fun initClickListeners() {
        binding.tvEdit.setOnClickListener(this)
        binding.ivFilter.setOnClickListener(this)
        binding.ivImage.setOnClickListener(this)
        binding.ivText.setOnClickListener(this)
        binding.ivTrim.setOnClickListener(this)
        binding.btFinish.setOnClickListener(this)
        binding.ivWatermark.setOnClickListener(this)

    }

    private fun getBundleData() {
        intent?.extras?.let {
            lifecycleScope.launch {

                viewModel.originalPath = it.getString(HdEditor.EXTRA_INPUT_PATH)
                delay(200)
                withContext(Dispatchers.IO) {
                    val path = getPath(
                        this@HdEditorActivity,
                        Uri.parse(it.getString(HdEditor.EXTRA_INPUT_PATH))
                    ) ?: ""
                    if (path.isNullOrEmpty()) {

                        runOnUiThread {
                            Toast.makeText(
                                this@HdEditorActivity,
                                "Some error occurred. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_CANCELED, Intent())
                            finish()
                        }
                    } else if (regex.matcher(path).find()) {
                        runOnUiThread {
                            Toast.makeText(
                                this@HdEditorActivity,
                                "Please remove special characters (#^|\\$%&*! ) from file name.",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_CANCELED, Intent())
                            finish()
                        }
                    } else {
//                viewModel.originalPath=path
                        viewModel.inputPath = copyFile(
                            this@HdEditorActivity,
                            path.getMimeType(3),
                            uri = Uri.parse(path)
                        )
                        if (it.containsKey(HdEditor.EXTRA_OUTPUT_PATH)) {
                            viewModel.outputPath = it.getString(HdEditor.EXTRA_OUTPUT_PATH)
                        } else {
//                    viewModel.outputPath = createFile(
//                        this@HdEditorActivity,
//                        viewModel.inputPath.getMimeType(3),
//                        false
//                    ).absolutePath

                            var outMime = viewModel.inputPath?.getMimeType(3) ?: ".mp4"
                            if (outMime.endsWith("webm")) {
                                outMime = ".mp4"
                            }
//                        outMime= ".mp4"
                            viewModel.outputPath = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
                                        + "/VID_${System.currentTimeMillis()}_edited${
                                    outMime
                                }"
                            ).absolutePath
                        }
                        delay(200)

                        runOnUiThread {
                            binding.editor.setOnEditorListener(this@HdEditorActivity)
                                .setVideoPath(viewModel.inputPath)
                                .setMaxDuration(5 * 60)
                                .setMinDuration(5)

                            binding.loadingG.visibility = View.GONE
                            binding.mainG.visibility = View.VISIBLE
                        }
                    }
                }

            }
        }


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_edit -> {
                reset()
                binding.editG.visibility = View.VISIBLE
                binding.tvEdit.visibility = View.GONE
                binding.tvOriginal.visibility = View.GONE
                viewModel.editType = TYPE_TRIM

                binding.editor.showTrimmer()
                binding.ivTrim.apply {
                    imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@HdEditorActivity,
                            R.color.purple_500
                        )
                    )
                    imageTintMode = PorterDuff.Mode.SRC_IN
                }
                binding.btFinish.text = getString(R.string.done)
            }

            R.id.iv_trim -> {
                reset()
                if (viewModel.editType == 0) {
                    viewModel.editType = TYPE_TRIM

                    binding.editor.showTrimmer()
                    binding.ivTrim.apply {
                        imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@HdEditorActivity,
                                R.color.purple_500
                            )
                        )
                        imageTintMode = PorterDuff.Mode.SRC_IN
                    }
                    binding.btFinish.text = getString(R.string.done)
                }


            }
            R.id.iv_watermark -> {
                reset()
                if (viewModel.editType == 0) {
                    viewModel.editType = TYPE_WATERMARK

                    binding.editor.showWatermark()
                    binding.ivWatermark.apply {
                        imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@HdEditorActivity,
                                R.color.purple_500
                            )
                        )
                        imageTintMode = PorterDuff.Mode.SRC_IN
                    }
                    binding.btFinish.text = getString(R.string.done)
                }


            }
            R.id.iv_image -> {
                uploadImage()
            }
            R.id.iv_filter -> {
                reset()
                if (viewModel.editType == 0) {
                    viewModel.editType = TYPE_FILTER
                    binding.editor.showFilter()
                    binding.ivFilter.apply {
                        imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@HdEditorActivity,
                                R.color.purple_500
                            )
                        )
                        imageTintMode = PorterDuff.Mode.SRC_IN
                    }
                    binding.btFinish.text = getString(R.string.done)
                }
            }
            R.id.bt_finish -> {
                if (binding.tvOriginal.visibility == View.VISIBLE) {
//                    val path= getCreatedFilePath(this,
//                        File( viewModel.originalPath),viewModel.originalPath.getMimeType(3,false))

//                    val path = getCreatedVideoFilePath(
//                        this,
//                        File(viewModel.originalPath ?: ""),
//                        viewModel.originalPath.getMimeType(3,false) ?: "video/mp4"
//                    )
                    totalVideoTime =
                        binding.editor.getResultedVideo().endPosition - binding.editor.getResultedVideo().startPosition

                    if (totalVideoTime ?: 0L < 2000L) {
                        Toast.makeText(
                            this,
                            "Video file play time should be greater than 2 seconds.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        setResult(
                            Activity.RESULT_OK,
                            Intent().setData(Uri.parse(viewModel.originalPath))
                        )
                        finish()
                    }
                } else if (viewModel.editType != 0) {
                    reset()
                } else {
                    totalVideoTime =
                        binding.editor.getResultedVideo().endPosition - binding.editor.getResultedVideo().startPosition

                    if (totalVideoTime ?: 0L < 2000L) {
                        Toast.makeText(
                            this,
                            "Video file play time should be greater than 2 seconds.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.btFinish.isEnabled = false
                        binding.editG.visibility = View.GONE
                        binding.editor.computeWatermarkInfo()
                        viewModel.saveVideo(binding.editor.getResultedVideo())
                    }
                }
            }
        }
    }

    private fun uploadImage() {
        PermissionUtil.checkPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1001
        ) {
            if (it) {
                try {
//                    val intent1 = Intent(Intent.ACTION_GET_CONTENT)
//
////                    intent1.addCategory(Intent.CATEGORY_OPENABLE)
//                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    intent1.type = "image/*"
////                    intent1.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
////                    intent1.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//                    intent1.putExtra(
//                        Intent.EXTRA_MIME_TYPES,
//                        arrayListOf<String>("image/jpg", "image/png", "image/jpeg")
//                    )
//                  this.  startActivityForResult(
//                        intent1,
//                        150
//                    )

                    val i = Intent()
                    i.type = "image/*"
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    i.action = Intent.ACTION_GET_CONTENT
//                    i.putExtra(
//                        Intent.EXTRA_MIME_TYPES,
//                        arrayListOf<String>("image/jpg", "image/png", "image/jpeg")
//                    )
                    // pass the constant to compare it
                    // with the returned requestCode

                    // pass the constant to compare it
                    // with the returned requestCode
                    startActivityForResult(
                        Intent.createChooser(i, "Select Picture"),
                        150
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(this, "No Permission Accepted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reset() {
        viewModel.editType = 0
        binding.ivFilter.apply {
            imageTintList = null
        }
        binding.ivText.apply {
            imageTintList = null
        }
        binding.ivImage.apply {
            imageTintList = null
        }
        binding.ivTrim.apply {
            imageTintList = null
        }
        binding.ivWatermark.apply {
            imageTintList = null
        }
        binding.editor.hideFilter()
        binding.editor.hideTrimmer()
        binding.editor.hideWatermark()
        binding.btFinish.text = getString(R.string.finish)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == 150) {
                    lifecycleScope.launch {
                        data?.data?.let {
                            val path = getPath(this@HdEditorActivity, it)
                            if (!path.isNullOrEmpty()) {
                                val imagePath = copyFile(
                                    this@HdEditorActivity,
                                    path.getImageMimeType(this@HdEditorActivity),
                                    uri = it
                                )
                                if (!imagePath.isNullOrEmpty()) {
                                    val rotation = getImageRotation(imagePath)
                                    binding.editor.addImage(imagePath, rotation)
                                }
                            }
                        }
                    }


                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.editor.pause()
    }

    override fun onUploadWatermark() {
        uploadImage()
    }

    override fun onVideoError() {
        Toast.makeText(
            this,
            "Either this file is corrupted or format not supported.",
            Toast.LENGTH_SHORT
        ).show()
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

}