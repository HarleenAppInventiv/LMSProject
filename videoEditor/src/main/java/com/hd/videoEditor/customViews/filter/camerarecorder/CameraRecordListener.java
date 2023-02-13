package com.hd.videoEditor.customViews.filter.camerarecorder;


public interface CameraRecordListener {

    void onGetFlashSupport(boolean flashSupport);

    void onRecordComplete();

    void onRecordStart();

    void onError(Exception exception);

    void onCameraThreadFinish();

    /**
     * Is called when native codecs finish to write file.
     */
    void onVideoFileReady();
}
