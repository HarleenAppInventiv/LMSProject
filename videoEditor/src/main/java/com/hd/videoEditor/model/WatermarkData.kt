package com.hd.videoEditor.model


import android.graphics.Rect
import com.hd.videoEditor.customViews.resizeableViews.ScaleableImageView

data class WatermarkData(
    var filePath: String? = "",
    var watermarkView: ScaleableImageView? = null,
    var watermarkRect: Rect? = null,
    var watermarkSize: Pair<Float, Float>? = null,
    var opacity: Float? = 1f
)