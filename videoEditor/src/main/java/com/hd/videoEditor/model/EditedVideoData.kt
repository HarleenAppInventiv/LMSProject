package com.hd.videoEditor.model


import com.hd.videoEditor.customViews.filter.egl.filter.GlFilter


data class EditedVideoData(
    var orgStartPosition: Long = 0,
    var orgEndPosition: Long = 0,
    var endPosition: Long = 0,
    var startPosition: Long = 0,
    var filter: GlFilter? = null,
    var watermarkList: ArrayList<WatermarkData>? = null

)