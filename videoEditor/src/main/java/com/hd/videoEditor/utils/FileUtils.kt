package com.hd.videoEditor.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


suspend fun getPath(context: Context, uri: Uri): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    if (returnCursor != null) {
        try {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
//        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
            val file = File(context.filesDir, name)

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream?.available()

            //int bufferSize = 1024;
            val bufferSize = if (bytesAvailable != null)
                Math.min(bytesAvailable, maxBufferSize) else 1024
            val buffers = ByteArray(bufferSize)
            if (inputStream != null) {
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
            return file.path
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message ?: "No message")
            return null
        }
    } else return null

}

fun getImageRotation(path: String): Int {
    var exif: ExifInterface? = null
    try {
        exif = ExifInterface(path)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return exif?.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    ) ?: 0
}

fun createFile(context: Context, fileType: String, cache: Boolean = true): File {
    val directory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePrefix = "LMS_${System.currentTimeMillis()}"

    var dest = if (cache)
        File(context.cacheDir, filePrefix + fileType) else File(
        directory,
        filePrefix + fileType
    )
    var fileNo = 0
    while (dest.exists()) {
        fileNo++
        dest = if (cache) File(context.cacheDir, filePrefix + fileNo + fileType) else File(
            directory,
            filePrefix + fileNo + fileType
        )
    }

    return dest
}

suspend fun copyFile(context: Context, fileType: String, cache: Boolean = true, uri: Uri): String {
    val file = createFile(context, fileType, cache)
    var outputPath = ""
    outputPath = CoroutineScope(Dispatchers.IO).async {
        try {

            var source: FileChannel? = null
            var destination: FileChannel? = null


            source = FileInputStream(
                File(
                    getPath(
                        context,
                        uri
                    ) ?: uri.path
                )
            ).channel
            destination = FileOutputStream(file).channel
            destination.transferFrom(source, 0, source.size())
            file.path

        } catch (e: IOException) {
            Log.d("TAG", "Error Occured" + e.message)
            getPath(context, uri) ?: ""
        }
    }.await()
    return outputPath
}

fun String?.getMimeType(type: Int, withoutType: Boolean = true): String {
    var mime = ""
    var mType = when (type) {
        1 -> "image/"
        2 -> "audio/"
        3 -> "video/"
        else -> "image/"
    }
    if (this.isNullOrEmpty()) {
        mime = when (type) {
            1 -> "png"
            2 -> "mp3"
            3 -> "mp4"
            else -> "png"
        }
        if (withoutType) {
            mime = ".$mime"
        } else {
            mime = mType + mime
        }

    } else {

        mime = this.let {
            it.substringAfterLast(".")
        }
        if (withoutType) {
            mime = ".$mime"
        } else {
            mime = mType + mime
        }
    }
    return mime
}

fun String?.getImageMimeType(context: Context, withoutType: Boolean = true): String {
    var mime = ""
    var mType = "image/"
    val mimeTypes = arrayOf(
        "image/jpg",
        "image/jpeg",
        "image/png"
    )
    if (this.isNullOrEmpty()) {
        mime = "png"

        if (withoutType) {
            mime = ".$mime"
        } else {
            mime = mType + mime
        }

    } else {

        mime = this.let {
            it.substringAfterLast(".")
        }
        if (mimeTypes.contains("image/" + mime)) {
            if (withoutType) {
                mime = ".$mime"
            } else {
                mime = mType + mime
            }
        } else {
            val uri = Uri.parse(this)
//Check uri format to avoid null
            if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                //If scheme is a content
                mime = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: "png"

            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                mime = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(this)).toString())

            }

            if (mime.isNullOrEmpty()) {
                mime = "png"
            }
            if (withoutType) {
                if (mime.contains("/")) {

                    mime = "." + mime.split("/").get(1)
                } else {
                    mime = ".$mime"
                }
            } else {
                if (!mime.contains("/")) {

                    mime = "image/" + mime

                }

            }


        }
    }
    return mime
}

fun Bitmap.saveBitmapToFile(context: Context): String {
    try {
        val filePath = createFile(context, ".png")
        val fileOutputStream: FileOutputStream =
            FileOutputStream(filePath)
        this.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        return filePath.absolutePath
    } catch (e: Exception) {
        Log.d(
            "checkValue", "saveBitmapToFile: ${e.message}"
        )
        e.printStackTrace()
        return ""
    }
}


@SuppressLint("Range")
fun getCreatedVideoFilePath(
    context: Context,
    imageFile: File,
    mimeType: String = "video/mp4"
): Uri? {
    val filePath = imageFile.absolutePath
    try {


        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    saveFile(context, filePath, mimeType)
                } else {
                    val values = ContentValues()

                    values.put(MediaStore.Video.Media.DATA, filePath)

                    context.contentResolver.insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values
                    )
                }
            } else {
                null
            }
        }
    } catch (e: Exception) {
        Log.d("checkValue", "third" + e.message.toString())
        showException(e)
        return null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFile(ctx: Context, filePath: String, mimeType: String): Uri? {
    var audioFile: File = File(filePath)
    try {
        var values: ContentValues = ContentValues()
        values.put(MediaStore.Video.Media.DATA, audioFile.absolutePath)
        values.put(MediaStore.Video.Media.TITLE, audioFile.name)
        values.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Video.Media.SIZE, audioFile.length())
//    values.put(MediaStore.Video.Media.ARTIST, "")


        var uri: Uri = MediaStore.Video.Media.getContentUri(audioFile.absolutePath)
        var uri2: Uri? = ctx.contentResolver.insert(uri, values)

        if (TextUtils.isEmpty(uri2.toString())) {
            Log.w("", "Something went wrong while inserting data to content resolver")
        }

        return uri2
    } catch (e: Exception) {
        Log.d("checkValue", e.message.toString())

        showException(e)
        try {
            val resolver: ContentResolver = ctx.contentResolver
            val picCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val picDetail = ContentValues()
            picDetail.put(MediaStore.Video.Media.DISPLAY_NAME, audioFile.name)
            picDetail.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
            picDetail.put(
                MediaStore.Video.Media.DATA,
                audioFile.absolutePath
            )
            picDetail.put(MediaStore.Video.Media.IS_PENDING, 0)
            val finaluri = resolver.insert(picCollection, picDetail)
//                    picDetail.clear()
//                    picDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
            resolver.update(picCollection, picDetail, null, null)
            return finaluri
        } catch (e: Exception) {
            Log.d("checkValue", "another" + e.message.toString())
            val resolver: ContentResolver = ctx.contentResolver
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                audioFile.name
            ) //It automatically adds .jpg
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Failed to create new MediaStore record.")

//            val stream: OutputStream? = resolver.openOutputStream(uri)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            Log.d("checkValue", "moveTemplateToDownloads: Successfully moved to downloads.")
            return uri
        }
    }
}


fun compressImage(context: Context, filePath: String?): Uri? {
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
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
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
            ExifInterface.TAG_ORIENTATION, 0
        )
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 3) {
            matrix.postRotate(180f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 8) {
            matrix.postRotate(270f)
            Log.d("EXIF", "Exif: $orientation")
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
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
        scaledBitmap!!.compress(Bitmap.CompressFormat.PNG, 70, out)
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
