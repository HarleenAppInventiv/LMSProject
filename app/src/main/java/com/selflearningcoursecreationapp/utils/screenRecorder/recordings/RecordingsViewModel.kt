package com.selflearningcoursecreationapp.utils.screenRecorder.recordings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.selflearningcoursecreationapp.utils.screenRecorder.data.DataManager
import com.selflearningcoursecreationapp.utils.screenRecorder.data.MediaStoreDataSource
import com.selflearningcoursecreationapp.utils.screenRecorder.data.Recording
import com.selflearningcoursecreationapp.utils.screenRecorder.data.SAFDataSource
import com.selflearningcoursecreationapp.utils.screenRecorder.services.RecorderState
import com.selflearningcoursecreationapp.utils.screenRecorder.services.SaveUri
import com.selflearningcoursecreationapp.utils.screenRecorder.services.UriType
import com.selflearningcoursecreationapp.utils.screenRecorder.settings.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn


@ExperimentalCoroutinesApi
class RecordingsViewModel(app: Application) : AndroidViewModel(app) {

    private val context: Context = getApplication()

    private var dataManager: DataManager? = null

    val recorderState = RecorderState.state

    val recordings: LiveData<List<Recording>>

    init {
        val preferences = PreferenceHelper(context)
        recordings = preferences.saveLocationFlow.flatMapLatest {
            dataManager = createDataManager(it)
            dataManager?.recordings() ?: emptyFlow()
        }.flowOn(Dispatchers.IO).combine(preferences.sortOrderOptionsFlow) { recordings, options ->
            processData(recordings, options)
        }.flowOn(Dispatchers.Default).asLiveData()
    }

    private fun createDataManager(saveUri: SaveUri?) = when (saveUri?.type) {
        UriType.MEDIA_STORE -> DataManager(MediaStoreDataSource(context, saveUri.uri))
        UriType.SAF -> DataManager(SAFDataSource(context, saveUri.uri))
        else -> null
    }

    fun rename(recording: Recording, newName: String) {
        dataManager?.rename(recording, newName)
    }

    fun deleteRecording(recording: Recording) {
        dataManager?.delete(recording)
    }

    fun deleteRecordings(recordings: List<Recording>) {
        dataManager?.delete(recordings.map { it.uri })
    }

    private fun processData(
        recordings: List<Recording>,
        options: PreferenceHelper.SortOrderOptions
    ): List<Recording> {
        return recordings.filter { !it.isPending }.run {
            when (options.sortBy) {
                PreferenceHelper.SortBy.NAME -> sortedBy { it.title }
                PreferenceHelper.SortBy.DATE -> sortedBy { it.modified }
                PreferenceHelper.SortBy.DURATION -> sortedBy { it.duration }
                PreferenceHelper.SortBy.SIZE -> sortedBy { it.size }
            }.run {
                when (options.orderBy) {
                    PreferenceHelper.OrderBy.ASCENDING -> this
                    PreferenceHelper.OrderBy.DESCENDING -> reversed()
                }
            }
        }
    }
}