package com.selflearningcoursecreationapp.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
                val notGrantedList = grantResult.filter { it == PackageManager.PERMISSION_DENIED }
                permCallBack?.invoke(notGrantedList.isEmpty(), permissionArray, requestCode)
            }
        }

    }

    class Builder(private var context: Activity) {
        private var isExternalStorage: Boolean = false

        fun requestExternalStorage(): Builder {
            isExternalStorage = true
            permissionArray = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )


            return this
        }

        @RequiresApi(Build.VERSION_CODES.R)
        private fun requestRPermissions() {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(
                    String.format(
                        "package:%s",
                        context.applicationContext.packageName
                    )
                )
                context.startActivityForResult(intent, requestCode)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivityForResult(intent, requestCode)
            }
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
            if (isExternalStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    permCallBack?.invoke(true, permissionArray, requestCode)

                } else {
                    requestRPermissions()
                }
            } else {
                checkPermissions()
            }


        }

        private fun checkPermissions() {
            val permissionList = permissionArray.filter {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
            }
            if (permissionList.isEmpty()) {
                permCallBack?.invoke(true, permissionArray, requestCode)
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    permissionList.toTypedArray(),
                    requestCode
                )
            }
        }


    }
}