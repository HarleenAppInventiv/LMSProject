package com.selflearningcoursecreationapp.utils.screenRecorder.data

import android.R
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.di.getAppContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File


class MediaStoreDataSource(val context: Context, val uri: Uri) : DataSource {

    override fun create(
        folderUri: Uri,
        name: String,
        mimeType: String,
        extra: ContentValues?
    ): Uri? {
        val values = ContentValues()
        //values.put(MediaStore.Video.Media.TITLE, fileTitle)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name)
        val now = System.currentTimeMillis()
        // DATE_ADDED is in milliseconds
        // DATE_MODIFIED is in seconds
        values.put(MediaStore.Video.Media.DATE_ADDED, now)
        values.put(MediaStore.Video.Media.DATE_MODIFIED, now / 1000)
        values.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.IS_PENDING, 1)
            extra?.apply {
                if (containsKey(MediaStore.Video.Media.RELATIVE_PATH))
                    values.put(
                        MediaStore.Video.Media.RELATIVE_PATH,
                        extra.getAsString(MediaStore.Video.Media.RELATIVE_PATH)
                    )
            }
        }

//        else{
//            var dest = File(File(app_folder), filePrefix + fileExtn)
//            var fileNo = 0
//            while (dest.exists()) {
//                fileNo++
//                dest = File(File(app_folder), filePrefix + fileNo + fileExtn)
//
//            }
//            folderUri = FileProvider.getUriForFile(
//                getAppContext(),
//                "${BuildConfig.APPLICATION_ID}.provider",
//                dest
//            )
//        }

        return context.contentResolver.insert(folderUri, values)
    }

    fun saveFileLocally(

    ): Uri {
        val root = Environment.getExternalStorageDirectory().toString()

        val app_folder = "$root/Self/"
        val uri: Uri

        val filePrefix = "Self_"
        val fileExtn = ".pdf"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                filePrefix + System.currentTimeMillis() + fileExtn
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, R.attr.mimeType)
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/Self"
            )
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())



            uri = getAppContext().contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )!!
        } else {

            var dest = File(File(app_folder), filePrefix + fileExtn)
            var fileNo = 0
            while (dest.exists()) {
                fileNo++
                dest = File(File(app_folder), filePrefix + fileNo + fileExtn)

            }
            uri = FileProvider.getUriForFile(
                getAppContext(),
                "${BuildConfig.APPLICATION_ID}.provider",
                dest
            )


        }

        return uri
    }

    override fun delete(uri: Uri) {
        context.contentResolver.delete(uri, null, null)
    }

    override fun delete(uris: List<Uri>) {
        val operations = ArrayList<ContentProviderOperation>(uris.size)
        uris.forEach { operations.add(ContentProviderOperation.newDelete(it).build()) }
        context.contentResolver.applyBatch(MediaStore.AUTHORITY, operations)
    }

    override fun rename(uri: Uri, newName: String) {
        val values = ContentValues()
        //values.put(MediaStore.Video.Media.TITLE, newName)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, newName)
        // DATE_MODIFIED is in secondsContentProviderOperation
        //values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        context.contentResolver.update(uri, values, null, null)
    }

    fun fetchRecordings(): List<Recording> {
        val recordings = ArrayList<Recording>()

        @SuppressLint("InlinedApi")
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.IS_PENDING
        )

        // Note: newUri works also with DocumentFile uris. Otherwise this is not necessary.
        //val newUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
        context.contentResolver.query(
            uri,
            projection, null, null, null
        )?.apply {
            if (moveToFirst()) {
                do {
                    val isPending: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        getInt(getColumnIndexOrThrow(MediaStore.Video.Media.IS_PENDING)) == 1
                    } else {
                        false
                    }
                    val displayName = getString(
                        getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    )
                    val modified = getLong(
                        getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                    )
                    val size = getLong(
                        getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    )
                    val duration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        getInt(getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    } else {
                        0
                    }
                    val fileUri = ContentUris.withAppendedId(
                        uri,
                        getLong(getColumnIndex(MediaStore.Video.Media._ID))
                    )
                    // DATE_MODIFIED is in seconds
                    recordings.add(
                        Recording(
                            fileUri,
                            displayName,
                            duration,
                            size,
                            modified * 1000,
                            isPending
                        )
                    )
                } while (moveToNext())
            }
            close()
        }
        return recordings
    }

    override fun update(uri: Uri, values: ContentValues) {
        context.contentResolver.update(uri, values, null, null)
    }

    @ExperimentalCoroutinesApi
    override fun recordings(): Flow<List<Recording>> = callbackFlow {
        val thread = HandlerThread("ContentObserverHandler")
        thread.start()
        val contentObserver = object : ContentObserver(Handler(thread.looper)) {
            override fun onChange(selfChange: Boolean) {
                onChange(selfChange, null)
            }

            override fun onChange(selfChange: Boolean, uri: Uri?) {
//                offer(fetchRecordings())
                trySend(fetchRecordings())
            }

            override fun deliverSelfNotifications() = true
        }
        context.contentResolver.registerContentObserver(uri, true, contentObserver)
//        offer(fetchRecordings())
        trySend(fetchRecordings())
        awaitClose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }
}