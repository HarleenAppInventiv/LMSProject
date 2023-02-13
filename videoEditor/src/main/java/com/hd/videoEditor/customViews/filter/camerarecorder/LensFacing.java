package com.hd.videoEditor.customViews.filter.camerarecorder;

import android.hardware.camera2.CameraCharacteristics;


public enum LensFacing {
    FRONT(CameraCharacteristics.LENS_FACING_FRONT),
    BACK(CameraCharacteristics.LENS_FACING_BACK);

    private int facing;

    LensFacing(int facing) {
        this.facing = facing;
    }

    public int getFacing() {
        return facing;
    }
}
