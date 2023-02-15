package com.selflearningcoursecreationapp.extensions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.util.PatternsCompat
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.utils.DefaultExt
import com.selflearningcoursecreationapp.utils.DefaultMime
import com.selflearningcoursecreationapp.utils.FileUtils
import com.selflearningcoursecreationapp.utils.ValidationConst
import java.io.File

fun String.isValidEmail(): Boolean {
    return try {
        !TextUtils.isEmpty(this) && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
    } catch (ex: NullPointerException) {
        false
    }
}

fun String.blank(): Boolean {
    return trim().isEmpty()
}

fun String.isPasswordValid(): Boolean {
    return isNotEmpty() && length >= ValidationConst.MIN_PASSWORD_LENGTH
//    val expression = ("^(?=.*[0-9])"
//            + "(?=.*[a-z])(?=.*[A-Z])"
//            + "(?=.*[@#$%^&+=!*])"
//            + "(?=\\S+$).{6,40}$")
//
//    val pattern = Pattern.compile(expression)
//    val matcher = pattern.matcher(this)
//    return matcher.matches()
}

suspend fun call(p1: String): String {
    return FileUtils.getPath(
        SelfLearningApplication.applicationContext(),
        Uri.parse(p1.toString())
    ) ?: p1
}

fun String?.isFileLimitExceed(limit: Int): Boolean {
    if (this.isNullOrEmpty()) {
        return true
    }
    val path = try {
        FileUtils.getPath(
            SelfLearningApplication.applicationContext(),
            Uri.parse(this)
        ) ?: this
    } catch (e: Exception) {
        this
    }

    val file = File(path)
    val fileSizeInBytes = file.length()
    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    val fileSizeInKB: Double = fileSizeInBytes / 1024.0
    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
    val fileSizeInMB: Double = fileSizeInKB / 1024.0
//    val fileSizeInGB = fileSizeInMB / 1024
    Log.d("varun", "isFileLimitExceed: ${fileSizeInKB} ${fileSizeInMB} ${fileSizeInMB > limit}")
    return fileSizeInMB > limit


}

fun String?.isFileLimitExceedWithoutRealPath(limit: Int): Boolean {
    if (this.isNullOrEmpty()) {
        return true
    }
    val file = File(this)
    val fileSizeInBytes = file.length()
    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    val fileSizeInKB = fileSizeInBytes / 1024
    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
    val fileSizeInMB = fileSizeInKB / 1024
//    val fileSizeInGB = fileSizeInMB / 1024
    Log.d("varun", "isFileLimitExceed: ${fileSizeInKB} ${fileSizeInMB} ${fileSizeInMB > limit}")
    return fileSizeInMB > limit

}

fun String?.getValidAudioMimeType(context: Context, withoutType: Boolean = false): String {
    var mime = this?.let {
        "audio/" + it.substringAfterLast(".")
    }

    val mimeTypes = arrayOf(
        "audio/mp3",
        "audio/mpeg",
        "audio/m4a",
        "audio/aac",
        "audio/flac",
        "audio/mpeg",
        "audio/x-flac",
        "audio/x-dsd",
        "audio/dsd",
        "audio/aac-adts"
    )
    if (mimeTypes.contains(mime)) {
        return getAudioMime(withoutType, mime)
    } else {
        val uri = Uri.parse(this)
//Check uri format to avoid null
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            mime = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))

        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            mime = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(this)).toString())

        }
        if (mime?.contains("/") == false) {
            mime = "audio/" + mime
        }

        showLog("MIME_TYPE", "mime 2>>> $mime")

        return if (mimeTypes.contains(mime)) {
            getAudioMime(withoutType, mime)
        } else {
            if (withoutType) DefaultExt.AUDIO else DefaultMime.AUDIO
        }
    }
}

private fun getAudioMime(withoutType: Boolean, mime: String?) = if (withoutType) {
    if (mime?.contains("/") == true) "." + mime.split("/").get(1) else DefaultExt.AUDIO
} else mime ?: DefaultMime.AUDIO

fun String?.getValidVideoMimeType(context: Context, withoutType: Boolean = false): String {
    var mime = this?.let {
        "video/" + it.substringAfterLast(".")


    }

    val mimeTypes = arrayOf(
        "video/webm",
        "video/x-msvideo",
        "video/quicktime",
        "video/mp4",
        "video/mpeg",
        "video/x-matroska",
        "video/x-ms-wmv",
        "video/avi",
        "video/x-flv"
    )
    if (mimeTypes.contains(mime)) {
        return getVideoMime(withoutType, mime)
    } else {
        val uri = Uri.parse(this)
//Check uri format to avoid null
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            mime = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))

        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            mime = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(this)).toString())

        }
        if (mime?.contains("/") == false) {
            mime = "video/" + mime
        }

        showLog("MIME_TYPE", "mime 2>>> $mime")

        if (mimeTypes.contains(mime)) {
            return getVideoMime(withoutType, mime)
        } else {
            return if (withoutType) DefaultExt.VIDEO else DefaultMime.VIDEO
        }
    }
}

private fun getVideoMime(withoutType: Boolean, mime: String?) =
    if (!withoutType) mime ?: DefaultMime.VIDEO else {
        if (mime?.contains("/") == true) "." + mime.split("/").get(1) else DefaultExt.VIDEO
    }