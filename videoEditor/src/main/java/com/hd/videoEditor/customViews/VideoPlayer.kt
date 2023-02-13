package com.hd.videoEditor.customViews

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.hd.videoEditor.interfaces.OnVideoListener


class VideoPlayer(private var context: Context) {

    private var player: ExoPlayer? = null
    private var mPlayerView: StyledPlayerView? = null
    private var videoUrl: String? = null
    private var mListener: OnVideoListener? = null
    private var videoListener: Player.Listener = playbackStateListener()

    init {
        initPlayer()
    }

    fun setOnVideoListener(listener: OnVideoListener) {
        mListener = listener
    }

    private fun initPlayer() {
        player?.removeListener(videoListener)
        mPlayerView?.player = null
        mPlayerView = null
        player = null
        val trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())

        player = ExoPlayer.Builder(context)
//            .setTrackSelector(trackSelector)
            .build()
        mPlayerView = StyledPlayerView(context)
        mPlayerView?.player = player
        player?.addListener(videoListener)


//mPlayerView?.set
    }

    fun setVideoPath(url: String) {
        this.videoUrl = url
        if (player == null) {
            initPlayer()
        }
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl ?: ""))
        val mediaItem =
            MediaItem.fromUri(url)
//        player?.setMediaSource(hlsMediaSource)

        player?.setMediaItem(mediaItem)
        player?.seekTo(0)
        player?.prepare()


    }

    fun getPlayerView(): StyledPlayerView? {
        return mPlayerView
    }

    fun pause() {
        player?.pause()
    }


    fun play() {
        player?.play()
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }

    fun release() {
        player?.pause()
        player?.release()
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e("PLAYER_ERROR", "onPlayerError >> ${error.errorCode}")
            Log.e("PLAYER_ERROR", "onPlayerError >> ${error.errorCodeName}")
            Log.e("PLAYER_ERROR", "onPlayerError >> ${error.message}")
            mListener?.onVideoError()

            when (error.errorCode) {
                PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> {

                }

            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                }
                ExoPlayer.STATE_BUFFERING -> {

                }
                ExoPlayer.STATE_READY -> {
                    mListener?.onVideoPrepared()
                    mListener?.onProgressChanged()

                }
                ExoPlayer.STATE_ENDED -> {
                    mListener?.onVideoCompleted()

                }

                else -> {}
            }
        }
    }


    fun seekTo(duration: Long) {
        player?.seekTo(duration.toLong())
    }


}