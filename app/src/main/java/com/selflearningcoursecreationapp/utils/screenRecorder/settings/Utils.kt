package com.selflearningcoursecreationapp.utils.screenRecorder.settings

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.selflearningcoursecreationapp.utils.screenRecorder.PreferenceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PreferenceChangedLiveData(
    private val sharedPreferences: SharedPreferences,
    private val keys: List<String>
) : MutableLiveData<String>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onActive() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key in keys) {
            value = key
        }
    }
}

class PreferenceLiveData<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val loadFirst: Boolean = false,
    private val onKeyChanged: (String) -> T
) : MutableLiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onActive() {
        if (loadFirst) {
            value = onKeyChanged(key)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, changed: String) {
        if (key == changed) {
            value = onKeyChanged(key)
        }
    }
}


@ExperimentalCoroutinesApi
fun <T> preferenceFlow(
    sharedPreferences: SharedPreferences,
    key: String,
    loadFirst: Boolean = false,
    onKeyChanged: (String) -> T
): Flow<T> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changed ->
        if (key == changed) {
            trySend(onKeyChanged(key))
//            offer(onKeyChanged(key))
        }
    }
    if (loadFirst) {
//        offer(onKeyChanged(key))
        trySend(onKeyChanged(key))
    }
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    awaitClose {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}

fun <T> PreferenceHelper.preferenceFlow(
    key: String,
    loadFirst: Boolean = false,
    onKeyChanged: (String) -> T
): Flow<T> =
    preferenceFlow(sharedPreferences, key, loadFirst, onKeyChanged)


fun createPreferenceChangedLiveData(
    sharedPreferences: SharedPreferences,
    keys: List<String>
): LiveData<String> {
    return PreferenceChangedLiveData(sharedPreferences, keys)
}


fun Context.closeMedia(uri: Uri) {
    val contentValues = ContentValues()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
    } else {
        contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());

        // DATE HERE
    }

    contentResolver.update(uri, contentValues, null, null)
}

fun <T> createPreferenceLiveData(
    sharedPreferences: SharedPreferences,
    key: String,
    onChanged: (SharedPreferences, String) -> T
): LiveData<T> {
    return PreferenceLiveData(sharedPreferences, key) {
        onChanged(sharedPreferences, it)
    }
}