package com.selflearningcoursecreationapp.utils.screenRecorder.services

import androidx.lifecycle.MutableLiveData

object RecorderState {
    val state: MutableLiveData<State> = MutableLiveData()

    enum class State {
        RECORDING, STOPPED, PAUSED
    }
}