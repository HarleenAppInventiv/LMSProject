package com.selflearningcoursecreationapp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    private var requestPermCode = 0
    private var permissionCallBack: ((Boolean) -> Unit)? = null
    fun checkPermissions(
        context: Activity,
        perms: Array<String>,
        requestCode: Int,
        callback: ((Boolean) -> Unit),
    ) {
        permissionCallBack = callback
        requestPermCode = requestCode
        val permissionList = perms.filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
        }
        if (permissionList.isEmpty()) {
            permissionCallBack?.invoke(true)
        } else {
            ActivityCompat.requestPermissions(
                context,
                permissionList.toTypedArray(),
                requestPermCode
            )
        }
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        perms: Array<out String>,
        grantResult: IntArray,
    ) {
        if (requestCode == requestPermCode) {
            val notGrantedList = grantResult.filter { it == PackageManager.PERMISSION_DENIED }
            permissionCallBack?.invoke(notGrantedList.isEmpty())
        }
    }

    fun checkPermissionss(context: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val result1 =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }


//    fun pemi(
//        context: Activity,
//        perms: Array<String>,
//        requestCode: Int,
//        callback: ((Boolean) -> Unit),
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s"))
//                startActivityForResult(intent, 2296)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent, 2296)
//            }
//        } else {
//
//        }

//    }


}