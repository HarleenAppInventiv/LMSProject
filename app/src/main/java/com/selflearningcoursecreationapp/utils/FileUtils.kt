package com.selflearningcoursecreationapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import java.io.*
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*


object FileUtils {

    private var contentUri: Uri? = null

    @SuppressLint("NewApi")
    fun getPathFromUri(context: Context, uri: Uri): String? {
        // check here to is it KITKAT or new version
        val isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        val selection: String? = null
        val selectionArgs: Array<String>? = null
        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            dataFromExternalStorage(uri, context, selection, selectionArgs)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return dataFromCloudDirectory(uri, context)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun dataFromCloudDirectory(
        uri: Uri,
        context: Context,
    ): String? {
        return when {
            isGooglePhotosUri(uri) -> {
                uri.lastPathSegment
            }
            isGoogleDriveUri(uri) -> {
                getDriveFilePath(uri, context)
            }
            isDropboxUri(uri) -> {
                null
            }
            isDropboxDocument(uri) -> {
                getDriveFilePath(uri, context)
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.N -> {
                getMediaFilePathForN(uri, context)
            }
            else -> {
                getDataColumn(context, uri, null, null)
            }
        }
    }

    private fun dataFromExternalStorage(
        uri: Uri,
        context: Context,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val fullPath = getPathFromExtSD(split)
                return if (fullPath !== "") {
                    fullPath
                } else {
                    null
                }
            }
            isDownloadsDocument(uri) -> {
                return getDownloadedFilePath(context, uri)

            }
            isMediaDocument(uri) -> {
                return getMediaFilePath(uri, selection, selectionArgs, context)
            }
            isGoogleDriveUri(uri) -> {
                return getDriveFilePath(uri, context)
            }
            else -> return null
        }
    }

    private fun getMediaFilePath(
        uri: Uri,
        selection1: String?,
        selectionArgs1: Array<String>?,
        context: Context,
    ): String? {
        var selection11 = selection1
        var selectionArgs11 = selectionArgs1
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).toTypedArray()
        val type = split[0]
        var contentUri: Uri? = null
        contentUri = when (type) {
            "image" -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            "video" -> {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            "audio" -> {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            else -> {
                MediaStore.Files.getContentUri("external");
            }
        }
        selection11 = "_id=?"
        selectionArgs11 = arrayOf(split[1])
        return getDataColumn(
            context, contentUri, selection11,
            selectionArgs11
        )
    }

    private fun getDownloadedFilePath(
        context: Context,
        uri: Uri,
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDownloadPathForLowerVersion(context, uri)

        } else {
            getDownloadPathForLowerVersion(context, uri)
        }

    }

    private fun getDownloadPathForLowerVersion(context: Context, uri: Uri): String? {
        val id = DocumentsContract.getDocumentId(uri)
        if (id.startsWith("raw:")) {
            return id.replaceFirst("raw:".toRegex(), "")
        }
        try {
            contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                Long.valueOf(id)
            )
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (contentUri != null) {
            return getDataColumn(context, contentUri, null, null)
        }
        return null
    }

    private fun getDownloadPathForHigherVersion(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                null,
                null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val fileName = cursor.getString(0)
                val path = Environment.getExternalStorageDirectory()
                    .toString() + "/Download/" + fileName
                if (!TextUtils.isEmpty(path)) {
                    return path
                }
            }
        } finally {
            cursor?.close()
        }
        val id: String = DocumentsContract.getDocumentId(uri)
        if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:".toRegex(), "")
            }
            val contentUriPrefixesToTry = arrayOf(
                "content://downloads/public_downloads",
                "content://downloads/my_downloads"
            )
            for (contentUriPrefix in contentUriPrefixesToTry) {
                return try {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        Long.valueOf(id)
                    )
                    getDataColumn(context, contentUri, null, null)
                } catch (e: NumberFormatException) {
                    //In Android 8 and Android P the id is not a number
                    uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                        .replaceFirst("^raw:".toRegex(), "")
                }
            }
        }
        return null
    }

    private fun isVirtualFile(context: Context, uri: Uri): Boolean {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false
        }

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
            null,
            null,
            null
        )

        val flags: Int = cursor?.use {
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        } ?: 0

        return flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
    }

    @Throws(IOException::class)
    private fun getInputStreamForVirtualFile(
        context: Context,
        uri: Uri, mimeTypeFilter: String,
    ): InputStream? {

        val openableMimeTypes: Array<String>? =
            context.contentResolver.getStreamTypes(uri, mimeTypeFilter)

        return if (openableMimeTypes?.isNotEmpty() == true) {
            context.contentResolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                ?.createInputStream()
        } else {
            null
        }
    }


    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "getRealPathFromURI Exception : $e")
            ""
        } finally {
            cursor?.close()
        }
    }


    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    private fun getPathFromExtSD(pathData: Array<String>): String {
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath = ""
        if ("primary".equals(type, ignoreCase = true)) {
            fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    fun getDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()

        } catch (e: Exception) {
            e.message?.let { Log.e("Exception", it) }
        }
        return file.getPath()
    }

    @SuppressLint("Recycle")
    private fun getMediaFilePathForN(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()

        } catch (e: Exception) {
            e.message?.let { Log.e("Exception", it) }
        }
        return file.getPath()
    }


    private fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isDropboxDocument(uri: Uri): Boolean {
        return "com.dropbox.android.FileCache" == uri.authority
    }

    private fun isDropboxUri(uri: Uri): Boolean {
        return "com.dropbox.product.android.dbapp.document_provider.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }


    fun getFileFromFileProvider(mActivity: Activity, file: File?): Uri? {
        return FileProvider.getUriForFile(
            mActivity,
            mActivity.packageName + ".provider",
            file!!
        )
    }

    @Throws(IOException::class)
    fun createImageFile(): File? { // Create an image file name
        var timeStamp = ""
        var imageFileName = ""
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = "JPEG_" + timeStamp + "_"

        val storageDir: File? =
            SelfLearningApplication.applicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_DCIM + "/Camera")
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    fun getPhotoFile(): File? {
        var photoFileUri: Uri? = null
        val fileName: String = getFileName()
        val photoFile = File(fileName)
        photoFileUri = Uri.fromFile(photoFile)
        return photoFile
    }


    private fun getFileName(): String {
        val file =
            File(
                SelfLearningApplication.applicationContext().getExternalFilesDir(null)
                    .toString() + "/STD_Driver"
            )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }


    @Throws(IOException::class)
    fun copy(inputStream: InputStream, outputStream: OutputStream) {
        try {
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }


    fun Uri.getName(context: Context): String? {
        val returnCursor = context.contentResolver.query(this, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val fileName = returnCursor?.getString(nameIndex!!)
        returnCursor?.close()
        return fileName
    }


}