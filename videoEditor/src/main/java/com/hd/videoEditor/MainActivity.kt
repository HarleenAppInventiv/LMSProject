package com.hd.videoEditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hd.videoEditor.databinding.ActivityMainBinding
import com.hd.videoEditor.utils.PermissionUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        binding.btUpload.setOnClickListener {
//            PermissionUtil.checkPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ),
//                121
//            ) {
//
//
//                if (it) {
//                    cacheDir.delete()
//                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//                        type = "video/*"
//                    }
//                    startActivityForResult(intent, 101)
//
//
//                } else {
//                    Toast.makeText(this, "No Permission Accepted", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
//                101 -> {
//                    lifecycleScope.launch {
//                        var inputPath =
//                            data?.data?.let { getPath(context = this@MainActivity, it) } ?: ""
//
//                        data?.data?.let {
//                            inputPath =
//                                copyFile(this@MainActivity, inputPath.getMimeType(3), true, it)
//                        }
//                        val outputPath =
//                            createFile(this@MainActivity, inputPath.getMimeType(3), false)
//                        runOnUiThread {
//                            HdEditor.Builder(this@MainActivity).inputPath(inputPath)
//                                .outputPath(outputPath.absolutePath).createIntent { resultIntent ->
//                                    startActivityForResult(resultIntent, 121)
//                                }
//
//                        }
//                    }
//
//
//                }
            }
        }
    }
}