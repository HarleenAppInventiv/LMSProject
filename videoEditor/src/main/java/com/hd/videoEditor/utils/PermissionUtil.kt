package com.hd.videoEditor.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    private var requestPermCode = 0
    private var permissionCallBack: ((Boolean) -> Unit)? = null
    fun checkPermissions(
        context: Activity,
        perms: Array<String>,
        requestCode: Int,
        callback: ((Boolean) -> Unit)
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
        grantResult: IntArray
    ) {
        if (requestCode == requestPermCode) {
            val notGrantedList = grantResult.filter { it == PackageManager.PERMISSION_DENIED }
            permissionCallBack?.invoke(notGrantedList.isEmpty())
        }
    }
}