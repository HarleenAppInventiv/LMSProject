package com.selflearningcoursecreationapp.utils.screenRecorder.recordings

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider

import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.utils.screenRecorder.data.Recording

class RecordingDetails(private val adapterPosition: Int, val recording: Recording) :
    ItemDetailsLookup.ItemDetails<Recording>() {
    override fun getSelectionKey(): Recording? {
        return recording
    }

    override fun getPosition(): Int {
        return adapterPosition
    }
}

class RecordingDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Recording>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Recording>? {
        return recyclerView.findChildViewUnder(e.x, e.y)?.let {
            (recyclerView.getChildViewHolder(it) as? RecordingViewHolder)?.getItemDetails()
        }
    }
}

class RecordingKeyProvider(var adapter: RecordingAdapter) :
    ItemKeyProvider<Recording>(SCOPE_CACHED) {
    override fun getKey(position: Int): Recording? {
        return adapter.data[position]
    }

    override fun getPosition(key: Recording): Int {
        return adapter.data.indexOf(key)
    }
}