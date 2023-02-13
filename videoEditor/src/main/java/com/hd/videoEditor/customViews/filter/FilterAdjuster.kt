package com.hd.videoEditor.customViews.filter

import com.hd.videoEditor.customViews.filter.egl.filter.GlFilter


interface FilterAdjuster {
    fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int)
}