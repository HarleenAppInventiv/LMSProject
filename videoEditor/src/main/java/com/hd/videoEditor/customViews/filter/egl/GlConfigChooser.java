package com.hd.videoEditor.customViews.filter.egl;


public class GlConfigChooser extends DefaultConfigChooser {

    private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

    public GlConfigChooser(final boolean withDepthBuffer) {
        super(withDepthBuffer, EGL_CONTEXT_CLIENT_VERSION);
    }

}
