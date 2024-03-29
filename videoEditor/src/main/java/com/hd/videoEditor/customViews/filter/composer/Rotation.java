package com.hd.videoEditor.customViews.filter.composer;

public enum Rotation {
    NORMAL(0),
    ROTATION_90(90),
    ROTATION_180(180),
    ROTATION_270(270);

    private final int rotation;

    Rotation(int rotation) {
        this.rotation = rotation;
    }

    public static Rotation fromInt(int rotate) {
        for (Rotation rotation : Rotation.values()) {
            if (rotate == rotation.getRotation()) return rotation;
        }

        return NORMAL;
    }

    public int getRotation() {
        return rotation;
    }

}
