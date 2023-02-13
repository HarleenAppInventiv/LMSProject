package com.hd.videoEditor.customViews.filter;


import com.hd.videoEditor.customViews.filter.egl.filter.GlFilter;

public interface MixAdjuster {
    public void adjustBrightness(GlFilter filter, int percentage);

    public void adjustContrast(GlFilter filter, int percentage);
}