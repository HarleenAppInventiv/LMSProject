package com.selflearningcoursecreationapp.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.showLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object FileUtils {


    fun getAlternatePath(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri =
//                    ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"),
//                        java.lang.Long.valueOf(id)
//                    )
//                return getDataColumn(context, contentUri, null, null)

                val fileName: String = getFilePath_(context, uri)
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                }

                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:".toRegex(), "")
                    val file = File(id)
                    if (file.exists()) return id
                }

                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)


            } else if (isMediaDocument(uri)) {
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
                        MediaStore.Files.getContentUri("external")
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isGoogleDriveUri(uri)) {
//                Toast.makeText(context, "google", Toast.LENGTH_SHORT).show()
                val returnCursor = context.contentResolver.query(uri, null, null, null, null)
                val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                val name = returnCursor.getString(nameIndex)
                val file = File(context.cacheDir, name)
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    var read: Int
                    val maxBufferSize = 1 * 1024 * 1024
                    val bytesAvailable: Int = inputStream!!.available()

                    //int bufferSize = 1024;
                    val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                    val buffers = ByteArray(bufferSize)
                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.write(buffers, 0, read)
                    }
                    inputStream.close()
                    outputStream.close()

                } catch (e: Exception) {
                    e.message?.let { Log.e("Exception", it) }
                }
                returnCursor.close()

                return file.path
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        uri?.let {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context.contentResolver
                    .query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                cursor?.close()
            }
            return null

        } ?: kotlin.run { return null }
    }


    fun getFilePath_(context: Context, uri: Uri?): String {
        uri?.let {
            var cursor: Cursor? = null
            val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            try {
                cursor = context.contentResolver.query(
                    uri, projection, null, null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    return cursor.getString(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            } finally {
                cursor?.close()
            }
            return ""
        } ?: kotlin.run { return "" }
    }

    fun dumpImageMetaData(context: Context, uri: Uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = context.contentResolver.query(
            uri, null, null, null, null, null
        )
        try {
            cursor?.use {
                // moveToFirst() returns false if the cursor has 0 rows. Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (it.moveToFirst()) {

                    // Note it's called "Display Name". This is
                    // provider-specific, and might not necessarily be the file name.
                    val displayName: String =
                        it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    Log.i("FILE_UTIL", "Display Name: $displayName")

                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    // If the size is unknown, the value stored is null. But because an
                    // int can't be null, the behavior is implementation-specific,
                    // and unpredictable. So as
                    // a rule, check if it's null before assigning to an int. This will
                    // happen often: The storage API allows for remote files, whose
                    // size might not be locally known.
                    val size: String = if (!it.isNull(sizeIndex)) {
                        // Technically the column stores an int, but cursor.getString()
                        // will do the conversion automatically.
                        it.getString(sizeIndex)
                    } else {
                        "Unknown"
                    }
                    Log.i("FILE_UTIL", "Size: $size")
                }
            }
        } catch (e: Exception) {
            showException(e)
        } finally {
            cursor?.close()
        }
    }

    fun getDriveFilePath(uri: Uri, context: Context): String? {
        try {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)


            val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor?.moveToFirst()
            val name = nameIndex?.let { returnCursor.getString(it) } ?: ""
            val file = File(context.cacheDir, name)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            if (inputStream != null) {
                val bytesAvailable: Int = inputStream.available()

                //int bufferSize = 1024;
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
            }
            inputStream?.close()
            outputStream.close()
            returnCursor?.close()
            return file.path
        } catch (e: Exception) {
            e.message?.let { Log.e("Exception", it) }
            return ""
        }

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

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    fun Bitmap?.getFilePath(context: Context): String {
        if (this == null) {
            return ""
        }
        // Assume block needs to be inside a Try/Catch block.
        // Assume block needs to be inside a Try/Catch block.

        try {

            val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()
            } else {
                Environment.getExternalStorageDirectory().toString().toString()
            }
            var fOut: OutputStream? = null
            val counter = System.currentTimeMillis().toString()
            val file = File(
                path,
                "FitnessGirl$counter.jpg"
            ) // the File to save , append increasing numeric counter to prevent files from getting overwritten.

            fOut = FileOutputStream(file)


            compress(
                Bitmap.CompressFormat.JPEG,
                85,
                fOut
            ) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate

            fOut.flush() // Not really required

            fOut.close() // do not forget to close the stream


            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                file.name,
                file.name
            )
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun Bitmap.saveBitmapToFile(context: Context): String {
        return try {
            val filePath = createFile(context, ".png")
            val fileOutputStream: FileOutputStream =
                FileOutputStream(filePath)
            this.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
            filePath?.absolutePath ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun createFile(context: Context, fileType: String, cache: Boolean = true): File? {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Skillfy")
        val filePrefix = "LMS_${System.currentTimeMillis()}"
        try {

            var dirFile = if (cache)
                File(context.cacheDir, "Skillfy")
            else directory

            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }

//        var dest = if (cache)
//            File(context.cacheDir, filePrefix + fileType) else File(
//            directory,
//            filePrefix + fileType
//        )

            var dest = File(dirFile, filePrefix + fileType)
            var fileNo = 0
//        while (dest.exists()) {
//            fileNo++
//            dest =  File(dirFile,filePrefix+fileNo+fileType)
////            dest = if (cache) File(context.cacheDir, filePrefix + fileNo + fileType) else File(
////                directory,
////                filePrefix + fileNo + fileType
////            )
//        }
            return dest
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun copyFile(
        context: Context,
        fileType: String,
        cache: Boolean = true,
        uri: Uri
    ): String {
        val file = createFile(context, fileType, cache)
        var outputPath = ""
        outputPath = CoroutineScope(Dispatchers.IO).async {
            var inputPath = getPath(context, uri) ?: uri.path ?: ""
            try {

                val `in`: InputStream = FileInputStream(File(inputPath))
                val out: OutputStream = FileOutputStream(file)

                // Copy the bits from instream to outstream

                // Copy the bits from instream to outstream
                val buf = ByteArray(1024)
                var len: Int

                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }

                `in`.close()
                out.close()
                file?.absolutePath ?: inputPath
            } catch (e: Exception) {
                e.printStackTrace()
                return@async inputPath
            }
        }.await()
        return outputPath
    }

    @SuppressLint("Range")
    fun getCreatedFilePath(
        context: Context,
        imageFile: File,
        mimeType: String = DefaultMime.AUDIO
    ): Uri? {
        val filePath = imageFile.absolutePath
        try {


            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Media._ID),
                MediaStore.Audio.Media.DATA + "=? ", arrayOf(filePath), null
            )
            return if (cursor != null && cursor.moveToFirst()) {
                val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                cursor.close()
                Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id)
            } else {
                if (imageFile.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    val resolver: ContentResolver = context.getContentResolver()
//                    val picCollection = MediaStore.Audio.Media
//                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//                    val picDetail = ContentValues()
//                    picDetail.put(MediaStore.Audio.Media.DISPLAY_NAME, imageFile.name)
//                    picDetail.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
//                    picDetail.put(
//                        MediaStore.Audio.Media.RELATIVE_PATH,
//                        "audio/" + UUID.randomUUID().toString()
//                    )
//                    picDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
//                    val finaluri = resolver.insert(picCollection, picDetail)
////                    picDetail.clear()
////                    picDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
//                    resolver.update(picCollection, picDetail, null, null)
//                    finaluri

                        saveAudio(context, filePath, mimeType)
                    } else {
                        val values = ContentValues()
                        values.put(MediaStore.Audio.Media.DATA, filePath)
                        context.contentResolver.insert(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values
                        )
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveAudio(ctx: Context, filePath: String, mimeType: String): Uri? {
        var audioFile: File = File(filePath)
        try {


            var values: ContentValues = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, audioFile.absolutePath)
            values.put(MediaStore.MediaColumns.TITLE, audioFile.name)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            values.put(MediaStore.MediaColumns.SIZE, audioFile.length())
            values.put(MediaStore.Audio.Media.ARTIST, "")
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            // Now set some extra features it depend on you
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            values.put(MediaStore.Audio.Media.IS_ALARM, false)
            values.put(MediaStore.Audio.Media.IS_MUSIC, false)

            var uri: Uri? = MediaStore.Audio.Media.getContentUriForPath(audioFile.absolutePath)
            var uri2: Uri? = uri?.let { ctx.contentResolver.insert(it, values) }

            if (TextUtils.isEmpty(uri2.toString())) {
                Log.w("", "Something went wrong while inserting data to content resolver")
            }

            return uri2
        } catch (e: Exception) {
            e.printStackTrace()
            val resolver: ContentResolver = ctx.contentResolver
            val picCollection = MediaStore.Audio.Media
                .getContentUri(MediaStore.VOLUME_EXTERNAL)
            val picDetail = ContentValues()
            picDetail.put(MediaStore.Audio.Media.DISPLAY_NAME, audioFile.name)
            picDetail.put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
            picDetail.put(
                MediaStore.Audio.Media.DATA,
                audioFile.absolutePath
            )
            picDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
            val finaluri = resolver.insert(picCollection, picDetail)
//                    picDetail.clear()
//                    picDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
            resolver.update(picCollection, picDetail, null, null)
            return finaluri
        }
    }

    @SuppressLint("Range")
    fun getCreatedImageFilePath(
        context: Context,
        imageFile: File,
        mimeType: String = "image/jpg"
    ): Uri? {
        val filePath = imageFile.absolutePath
        try {


            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null
            )
            return if (cursor != null && cursor.moveToFirst()) {
                val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                cursor.close()
                Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
            } else {
                if (imageFile.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    val resolver: ContentResolver = context.getContentResolver()
//                    val picCollection = MediaStore.Images.Media
//                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//                    val picDetail = ContentValues()
//                    picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
//                    picDetail.put(MediaStore.Images.Media.MIME_TYPE, "audio/mp3")
//                    picDetail.put(
//                        MediaStore.Images.Media.RELATIVE_PATH,
//                        "audio/" + UUID.randomUUID().toString()
//                    )
//                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0)
//                    val finaluri = resolver.insert(picCollection, picDetail)
////                    picDetail.clear()
////                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0)
//                    resolver.update(picCollection, picDetail, null, null)
//                    finaluri

                        saveImages(context, filePath, mimeType)
                    } else {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.DATA, filePath)
                        context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveImages(ctx: Context, filePath: String, mimeType: String): Uri? {
        var audioFile: File = File(filePath)
        try {


            var values: ContentValues = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, audioFile.absolutePath)
            values.put(MediaStore.MediaColumns.TITLE, audioFile.name)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            values.put(MediaStore.MediaColumns.SIZE, audioFile.length())
            values.put(MediaStore.Images.Media.ARTIST, "")
//            values.put(MediaStore.Images.Media.IS_RINGTONE, false)
//            // Now set some extra features it depend on you
//            values.put(MediaStore.Images.Media.IS_NOTIFICATION, false)
//            values.put(MediaStore.Images.Media.IS_ALARM, false)
//            values.put(MediaStore.Images.Media.IS_MUSIC, false)

            var uri: Uri? = MediaStore.Images.Media.getContentUri(audioFile.absolutePath)
            var uri2: Uri? = uri?.let { ctx.contentResolver.insert(it, values) }

            if (TextUtils.isEmpty(uri2.toString())) {
                Log.w("", "Something went wrong while inserting data to content resolver")
            }

            return uri2
        } catch (e: Exception) {
            e.printStackTrace()
            val resolver: ContentResolver = ctx.contentResolver
            val picCollection = MediaStore.Images.Media
                .getContentUri(MediaStore.VOLUME_EXTERNAL)
            val picDetail = ContentValues()
            picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, audioFile.name)
            picDetail.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            picDetail.put(
                MediaStore.Images.Media.DATA,
                audioFile.absolutePath
            )
            picDetail.put(MediaStore.Images.Media.IS_PENDING, 0)
            val finaluri = resolver.insert(picCollection, picDetail)
//                    picDetail.clear()
//                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(picCollection, picDetail, null, null)
            return finaluri
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            try {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                returnCursor.moveToFirst()
                val name = returnCursor.getString(nameIndex)
//            val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
                val file = File(context.filesDir, name)

                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                if (inputStream != null) {
                    val bytesAvailable = inputStream.available()

                    //int bufferSize = 1024;
                    val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    val buffers = ByteArray(bufferSize)
                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.write(buffers, 0, read)
                    }
                    Log.e("File Size", "Size " + file.length())
                }
                inputStream?.close()
                outputStream.close()
                Log.e("File Path", "Path " + file.path)
                Log.e("File Size", "Size " + file.length())
                return file.path
            } catch (e: java.lang.Exception) {
                Log.e("Exception", e.message ?: "NO message")
                return getAlternatePath(context, uri)
            }

        } else return null
    }

    fun getDisplayName(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)

            return name
        } else return "file"
    }

    fun getAudioDuration(path: String?): Long {
        var extractor = MediaExtractor()
        var fis: FileInputStream? = null
        var format: MediaFormat? = null
        try {
            fis = FileInputStream(path)
            val fd = fis.fd
            extractor.setDataSource(fd)
            val numTracks = extractor.trackCount
            // find and select the first audio track present in the file.
            showLog("SOUND_FILE", "ReadFile numTracks>> ${numTracks} ")
            var i = 0
            while (i < numTracks) {

                format = extractor.getTrackFormat(i)
                if (format.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                    showLog("SOUND_FILE", "ReadFile mime extract>> ${format} ")
                    extractor.selectTrack(i)
                    break
                }
                i++
            }

            if (i == numTracks) {
                throw Exception("No audio track found in $path")
            }
            return (format?.getLong(MediaFormat.KEY_DURATION) ?: 0L) / 1000
        } catch (e: Exception) {
            showException(e)
            return 0L
        }
    }

    fun compressImage(context: Context, filePath: String?, rotateImage: Boolean = false): Uri? {
//        val contentUri = Uri.parse(imageUri)
//        val filePath: String = FileUriUtils.getRealPath(context, contentUri).toString()
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()

        if (rotateImage) {
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        } else {
            scaleMatrix.setScale(1f, -1f, middleX, middleY)

        }
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath.toString())
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()


//            if (!rotateImage) {
            when (orientation) {
                6 -> {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                3 -> {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                8 -> {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
            }
//            } else {
//                matrix.postRotate(270f)
//            }
//           scaleMatrix.setScale(1f, -1f, middleX, middleY)

            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                false
            )
//            scaledBitmap=            modifyOrientation(scaledBitmap,filePath)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
//        val filename: String = getFilename()
        val photoFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + File.separator
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            File(
                imagesDir,
                SimpleDateFormat(
                    "yyyy_MM_dd_hhmmss",
                    Locale.US
                ).format(System.currentTimeMillis()) + ".png"
            )
        } else {
            File(
                getOutputDirectory(),
                SimpleDateFormat(
                    "yyyy_MM_dd_hhmmss",
                    Locale.US
                ).format(System.currentTimeMillis()) + ".png"
            )

        }

        try {
            out = FileOutputStream(photoFile)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.PNG, 70, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            //display
//            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,Uri.fromFile(photoFile))
//            imgPath = filename
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(photoFile)

    }


    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    //creates a folder inside internal storage
    private fun getOutputDirectory(): File {

        val dir: File = if (Build.VERSION_CODES.R < Build.VERSION.SDK_INT) {
            File(
                Environment.getExternalStorageDirectory().path
                        + "//Skillfy"
            )
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path
                        + "//Skillfy"
            )
        }

        if (!dir.exists())
            dir.mkdir()
        return dir
    }

//    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap? {
//        val matrix = Matrix()
//        matrix.postScale(
//            if (xFlip) -1 else 1,
//            if (yFlip) -1 else 1,
//            source.width / 2f,
//            source.height / 2f
//        )
//        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
//    }


    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap? {
        val ei = image_absolute_path?.let { ExifInterface(it) }
        val orientation =
            ei?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return flip(bitmap, true, false)


//        when (orientation) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
//            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, false)
//            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, true)
//            else -> bitmap
//        }
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap? {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}