package com.selflearningcoursecreationapp.utils.screenRecorder.recordings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.selflearningcoursecreationapp.utils.screenRecorder.data.Recording


abstract class RecordingListFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(layoutRes, container, false)
//
//        messageView = root.findViewById(R.id.message_no_video)
//        messageView.visibility = View.GONE
//
//        root.findViewById<RecyclerView>(R.id.videos_list).apply {
//            val linearLayoutManager = LinearLayoutManager(context)
//            layoutManager = linearLayoutManager
//            recordingsAdapter = RecordingAdapter()
//            adapter = recordingsAdapter
//            selectionTracker = SelectionTracker.Builder(
//                "recording-selection-id",
//                this,
//                RecordingKeyProvider(recordingsAdapter),
//                RecordingDetailsLookup(this),
//                StorageStrategy.createParcelableStorage(Recording::class.java)
//            )
//                .withOnItemActivatedListener { item, _ ->
//                    onRecordingClick(item.selectionKey!!)
//                    return@withOnItemActivatedListener true
//                }
//                .build()
//            savedInstanceState?.let { selectionTracker.onRestoreInstanceState(it) }
//            recordingsAdapter.selectionTracker = selectionTracker
//        }
//
//        viewModel.recordings.observe(viewLifecycleOwner, Observer {
//            onDataLoaded(it)
//        })

        return root
    }

    abstract val layoutRes: Int

    protected open fun onRecordingClick(recording: Recording) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(
                recording.uri,
                requireContext().contentResolver.getType(recording.uri)
            )
        startActivity(intent)
    }

//    private fun onDataLoaded(data: List<Recording>) {
//        messageView.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
//        recordingsAdapter.updateData(data)
//    }
//
//    protected fun rename(recording: Recording, newName: String) {
//        viewModel.rename(recording, newName)
//    }
//
//    protected fun delete(recording: Recording) {
//        viewModel.deleteRecording(recording)
//    }
//
//    protected fun delete(recordings: List<Recording>) {
//        viewModel.deleteRecordings(recordings)
//    }
}
