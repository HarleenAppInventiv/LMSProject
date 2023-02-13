package com.hd.videoEditor.customViews.filter.player

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import com.hd.videoEditor.customViews.filter.egl.GlConfigChooser
import com.hd.videoEditor.customViews.filter.egl.GlContextFactory
import com.hd.videoEditor.customViews.filter.egl.filter.GlFilter

class GPUPlayerView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs), Player.Listener {
    private var renderer: GPUPlayerRenderer? = null
    var player: StyledPlayerView? = null
    private var videoAspect = 1f
    private var playerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH


    fun setSimpleExoPlayer(player: StyledPlayerView?): GPUPlayerView {
        if (this.player != null) {
            this.player?.player?.release()
            this.player = null
        }
        this.player = player
        //        this.player.addListener(this);
        renderer?.setSimpleExoPlayer(player)
        return this
    }

    fun setGlFilter(glFilter: GlFilter?) {
        renderer?.setGlFilter(glFilter)
    }

    fun setPlayerScaleType(playerScaleType: PlayerScaleType) {
        this.playerScaleType = playerScaleType
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val measuredWidth = measuredWidth
//        val measuredHeight = measuredHeight
//        var viewWidth = measuredWidth
//        var viewHeight = measuredHeight
//        when (playerScaleType) {
//            PlayerScaleType.RESIZE_FIT_WIDTH -> viewHeight = (measuredWidth / videoAspect).toInt()
//            PlayerScaleType.RESIZE_FIT_HEIGHT -> viewWidth = (measuredHeight * videoAspect).toInt()
//        }
//
//        // Log.d(TAG, "onMeasure viewWidth = " + viewWidth + " viewHeight = " + viewHeight);
//        setMeasuredDimension(viewWidth, viewHeight)

    }

    override fun onPause() {
        super.onPause()
        renderer?.release()
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener
    override fun onVideoSizeChanged(videoSize: VideoSize) {
        // Log.d(TAG, "width = " + width + " height = " + height + " unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
        videoAspect = videoSize.width.toFloat() / videoSize.height * videoSize.pixelWidthHeightRatio
        // Log.d(TAG, "videoAspect = " + videoAspect);

        requestLayout()
    }

    override fun onRenderedFirstFrame() {
        // do nothing
    }

    companion object {
        private val TAG = GPUPlayerView::class.java.simpleName
    }

    init {
        setEGLContextFactory(GlContextFactory())
        setEGLConfigChooser(GlConfigChooser(false))
        renderer = GPUPlayerRenderer(this)
        setRenderer(renderer)
    }
}