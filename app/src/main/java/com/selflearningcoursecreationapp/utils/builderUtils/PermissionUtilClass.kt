package com.selflearningcoursecreationapp.utils.builderUtils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.utils.Constant

class PermissionUtilClass {
    companion object {
        private var requestCode: Int = Constant.CLICK_UPLOAD
        private var permCallBack: ((Boolean, Array<String>, Int) -> Unit)? = null
        private lateinit var permissionArray: Array<String>
        fun builder(context: Activity): Builder {
            return Builder(context)
        }

        fun onRequestPermissionResult(
            code: Int,
            perms: Array<out String>,
            grantResult: IntArray,
        ) {
            if (requestCode == code) {
                Log.d("checkValue", "checkPermissions: code")

                val notGrantedList = grantResult.filter { it == PackageManager.PERMISSION_DENIED }
                permCallBack?.invoke(notGrantedList.isEmpty(), permissionArray, requestCode)
            }
        }

    }

    class Builder(private var context: Activity) {
        private var isExternalStorage: Boolean = false

        fun requestExternalStorage(): Builder {
            isExternalStorage = true
            if (Build.VERSION.SDK_INT >= TIRAMISU) {
                permissionArray = arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            } else {
                permissionArray = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

            return this
        }

        @RequiresApi(Build.VERSION_CODES.R)
        private fun requestRPermissions() {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")

                val uri = Uri.fromParts("package", context.applicationContext.packageName, null)
                intent.data = uri

//                intent.data = Uri.parse(
//                    String.format(
//                        "package:%s",
//                        context.applicationContext.packageName
//                    )
//                )
                context.startActivityForResult(intent, requestCode)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivityForResult(intent, requestCode)
            }
        }

        fun requestStoragePermission(): Builder {
            permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,

                    )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            return this
        }

        fun requestPermissions(array: Array<String>): Builder {
            permissionArray = array
            return this
        }

        fun requestCode(code: Int): Builder {
            requestCode = code
            return this
        }

        fun getCallBack(callBack: ((Boolean, Array<String>, Int) -> Unit)): Builder {
            permCallBack = callBack
            return this
        }

        fun build() {
//            if (isExternalStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    permCallBack?.invoke(true, permissionArray, requestCode)
//                } else {
//                    requestRPermissions()
//                }
//            } else {
            checkPermissions()
//            }
        }

        private fun checkPermissions() {
            val permissionList = permissionArray.filter {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
            }
            if (permissionList.isEmpty()) {
                Log.d("checkValue", "checkPermissions: ac")
                permCallBack?.invoke(true, permissionArray, requestCode)
            } else {
                Log.d("checkValue", "checkPermissions: dn")

                ActivityCompat.requestPermissions(
                    context,
                    permissionList.toTypedArray(),
                    requestCode
                )
            }
        }
    }
}