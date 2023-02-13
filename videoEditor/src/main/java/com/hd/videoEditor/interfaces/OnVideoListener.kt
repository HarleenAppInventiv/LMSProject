package com.hd.videoEditor.interfaces

interface OnVideoListener {
    fun onVideoPrepared()
    fun onProgressChanged()
    fun onVideoCompleted()
    fun onVideoError()
}