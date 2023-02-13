package com.hd.videoEditor.customViews.editor

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.hd.videoEditor.R
import com.hd.videoEditor.customViews.FilterAdapter
import com.hd.videoEditor.customViews.VideoPlayer
import com.hd.videoEditor.customViews.filter.egl.newFilter.NewFilterType
import com.hd.videoEditor.customViews.filter.player.GPUPlayerView
import com.hd.videoEditor.customViews.resizeableViews.ScaleableImageView
import com.hd.videoEditor.customViews.trimmer.RangeSeekBarView
import com.hd.videoEditor.databinding.LayoutEditorBinding
import com.hd.videoEditor.interfaces.OnVideoListener
import com.hd.videoEditor.model.EditedVideoData
import com.hd.videoEditor.model.VideoData
import com.hd.videoEditor.model.WatermarkData
import com.hd.videoEditor.utils.Logd
import com.hd.videoEditor.utils.Loge
import com.hd.videoEditor.utils.fetchVideoInfo
import com.hd.videoEditor.utils.getTime
import kotlinx.coroutines.*

class EditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnVideoListener,
    RangeSeekBarView.OnRangeSeekBarChangeListener {
    private var mDuration: Long = 0
    private var rangeSeekBar: RangeSeekBarView? = null
    private var mMinDuration: Int = -1
    private var mMaxDuration: Int = -1
    private lateinit var binding: LayoutEditorBinding
    private var mVideoPlayer: VideoPlayer? = null
    private var gpuPlayerView: GPUPlayerView? = null
    private var videoData: VideoData? = null
    private var filePath: String? = null
    val TAG = "EDITOR_VIEW"
    private var resultData = EditedVideoData()
    private var filterAdapter: FilterAdapter? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_editor, this, true)
        setUpListeners()
        binding.ivPlay.setOnClickListener {
            onClickVideoPlayPause()
        }
        binding.tvTime.setOnClickListener {
            onClickVideoPlayPause()
        }

        binding.btUploadWatermark.setOnClickListener {
            mEditorListener?.onUploadWatermark()
        }
        binding.btRemoveWatermark.setOnClickListener {
            resultData.watermarkList?.forEach {
                binding.container.removeView(it.watermarkView)
            }
            binding.watermarkG.visibility = View.GONE
            binding.btUploadWatermark.text = context.getString(R.string.upload_watermark)
        }

        binding.sbOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                resultData.watermarkList?.forEach {
                    it.opacity = p1.toFloat() / 100f
                    it.watermarkView?.alpha = it.opacity ?: 1f
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

    }


    private fun setUpListeners() {
        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    onClickVideoPlayPause()
                    return true
                }
            })
        binding.container.setOnTouchListener { view, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            true
        }
        binding.handlerTop.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                onPlayerIndicatorSeekChanged(p1, p2)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

//                onPlayerIndicatorSeekStart()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
//                onPlayerIndicatorSeekStop()
            }

        })

    }


    private fun onPlayerIndicatorSeekChanged(progress: Int, fromUser: Boolean) {
        val duration = progress
        /** 1000L*/
        if (fromUser) {
            if (duration < resultData.startPosition) {
                setProgressBarPosition(resultData.startPosition)
            } else if (duration > resultData.endPosition) {
                setProgressBarPosition(resultData.endPosition)
            } else {
                mVideoPlayer?.seekTo(duration.toLong())
                mVideoPlayer?.pause()
//                binding.ivPlay.visibility = View.VISIBLE
                binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_play,
                    0,
                    0,
                    0
                )
            }
        }

    }

    private fun setProgressBarPosition(position: Long) {
        binding.handlerTop.progress = (position /*/ 1000L*/).toInt()
        mVideoPlayer?.seekTo(position)
        mVideoPlayer?.pause()
    }


    private fun onPlayerIndicatorSeekStart() {

        mVideoPlayer?.pause()
//        binding.ivPlay.visibility = View.VISIBLE
        binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0)

    }


    private fun onPlayerIndicatorSeekStop() {

        mVideoPlayer?.pause()
//        binding.ivPlay.visibility = View.VISIBLE
        binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0)
        val duration = (mDuration * binding.handlerTop.progress / 1000L)
        mVideoPlayer?.seekTo(duration)
    }


    private fun onClickVideoPlayPause() {
        binding.handlerTop.visibility = View.VISIBLE

        //show play icon
        //pause resume video
        if (mVideoPlayer?.isPlaying() == true) {
//            binding.ivPlay.visibility = View.VISIBLE
            binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_play,
                0,
                0,
                0
            )

            mVideoPlayer?.pause()
        } else {
//            binding.ivPlay.visibility = View.GONE
            binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_pause_new,
                0,
                0,
                0
            )

            mVideoPlayer?.play()
        }
        setSeekBarPosition()
    }

    fun setVideoPath(path: String?): EditorView {
        this.filePath = path
        if (!path.isNullOrEmpty()) {
            videoData = path.fetchVideoInfo()
            if (videoData?.width == 0 || videoData?.height == 0) {
                mEditorListener?.onVideoError()
            }
            setVideoData()
            binding.timeLineView.setVideo(Uri.parse(filePath))
            initVideoPlayer()
            setSeekBarParams()
            initFilterRV()
        }
        return this

    }

    private fun setSeekBarParams() {
        rangeSeekBar = RangeSeekBarView(context, 0L, (videoData!!.duration).toLong())
        rangeSeekBar!!.selectedMinValue = 0
        rangeSeekBar!!.setStartEndTime(0L, (videoData!!.duration).toLong())
        rangeSeekBar!!.selectedMaxValue = videoData!!.duration.toLong()
        rangeSeekBar!!.isNotifyWhileDragging = true
        binding.sbRange.addView(rangeSeekBar)
        rangeSeekBar!!.setOnRangeSeekBarChangeListener(this)
    }

    private fun setVideoData() {
        mDuration = videoData?.duration?.toLong() ?: 0
        resultData.startPosition = 0
        resultData.orgStartPosition = 0
        resultData.endPosition = videoData?.duration?.toLong() ?: 0
        resultData.orgEndPosition = videoData?.duration?.toLong() ?: 0
        binding.handlerTop.max = (mDuration/* / 1000L*/).toInt()
    }

    private fun initVideoPlayer() {
        mVideoPlayer = VideoPlayer(context)
        mVideoPlayer?.setOnVideoListener(this)
        filePath?.let { mVideoPlayer?.setVideoPath(it) }
        gpuPlayerView = GPUPlayerView(context)
        gpuPlayerView?.setSimpleExoPlayer(mVideoPlayer?.getPlayerView())

        binding.container.addView(gpuPlayerView)

    }

    override fun onVideoPrepared() {
        setVideoLayoutParams()

    }

    override fun onProgressChanged() {

        setSeekBarPosition()
    }

    override fun onVideoCompleted() {
        mVideoPlayer?.seekTo(resultData.startPosition)
        mVideoPlayer?.pause()
        if (mVideoPlayer?.isPlaying() == true) {
//            binding.ivPlay.visibility = View.GONE
            binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_pause_new,
                0,
                0,
                0
            )

        } else {
            binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_play,
                0,
                0,
                0
            )

//            binding.ivPlay.visibility = View.VISIBLE

        }
    }

    override fun onVideoError() {
        mEditorListener?.onVideoError()
    }

    private fun setSeekBarPosition() {


        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            withContext(Dispatchers.Main)
            {
                if ((mVideoPlayer?.getCurrentPosition() ?: 0L) > resultData.endPosition) {
                    mVideoPlayer?.seekTo(resultData.startPosition)
                    mVideoPlayer?.pause()
//                    binding.ivPlay.visibility = View.VISIBLE
                    binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_play,
                        0,
                        0,
                        0
                    )

                } else if ((mVideoPlayer?.getCurrentPosition() ?: 0L) < resultData.startPosition) {
                    mVideoPlayer?.seekTo(resultData.startPosition)
                    mVideoPlayer?.pause()
//                    binding.ivPlay.visibility = View.VISIBLE
                    binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_play,
                        0,
                        0,
                        0
                    )

                }
                binding.handlerTop.progress =
                    ((mVideoPlayer?.getCurrentPosition() ?: 0L) /*/ 1000L*/).toInt()
                binding.tvTime.text = String.format(
                    "%s/%s",
                    mVideoPlayer?.getCurrentPosition()?.getTime(context) ?: "00:00:00",
                    resultData.endPosition.toLong().getTime(context)
                )
                if (mVideoPlayer?.isPlaying() == true) {

                    setSeekBarPosition()
                }
            }
        }
    }

    private fun setVideoLayoutParams() {
        videoData?.let { data ->
            val videoWidth = data.width
            val videoHeight = data.height

            val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
            Loge(
                TAG,
                " video width >> ${videoWidth} ... height >>> $videoHeight ...  video videoProportion >> ${videoProportion} "
            )

            val playerWidth = gpuPlayerView?.width ?: 0
            val playerHeight = gpuPlayerView?.height ?: 0
            Loge(
                TAG,
                " playerWidth width >> ${playerWidth} ... height >>> $playerHeight"
            )

            val playerProportion = playerWidth.toFloat() / playerHeight.toFloat()
            Loge(TAG, " video playerProportion >> ${playerProportion} ")
            binding.container.layoutParams = binding.container.layoutParams?.apply {


                if (data.rotation == 90 || data.rotation == 270) {

                    val prop = (videoHeight.toFloat() / videoWidth.toFloat())


                    if (prop < playerProportion) {
                        width = (prop * playerHeight.toFloat()).toInt()
                        height = playerHeight
                    } else {
                        width = playerWidth
                        height = (playerWidth.toFloat() / prop).toInt()
                    }
                    Loge(
                        TAG,
                        "condition >> ${prop < playerProportion} rotation >> ${data.rotation}  width >> ${width} ... height >>> $height"
                    )
                } else {
                    if (videoProportion > playerProportion) {
                        width = playerWidth
                        height = (playerWidth.toFloat() / videoProportion).toInt()

                    } else {
                        width = (videoProportion * playerHeight.toFloat()).toInt()
                        height = playerHeight

                    }

                    Loge(
                        TAG,
                        "condition >> ${videoProportion > playerProportion} rotation >> ${data.rotation}  width >> ${width} ... height >>> $height"
                    )
                }
            }

        }

    }

    fun showTrimmer() {
        if (binding.timeLineFrame.visibility == View.VISIBLE) {
            binding.timeLineFrame.visibility = View.GONE

        } else {
            binding.timeLineFrame.visibility = View.VISIBLE

        }
        binding.root.invalidate()
    }

    fun hideTrimmer() {
        binding.timeLineFrame.visibility = View.GONE

    }

    fun showFilter() {
        if (binding.rvFilter.visibility == View.VISIBLE) {
            binding.rvFilter.visibility = View.GONE

        } else {
            binding.rvFilter.visibility = View.VISIBLE

        }
        binding.root.invalidate()

    }

    fun hideFilter() {
        binding.rvFilter.visibility = View.GONE

    }

    fun setMaxDuration(maxDuration: Int): EditorView {
        mMaxDuration = maxDuration * 1000
        return this
    }

    fun setMinDuration(minDuration: Int): EditorView {
        mMinDuration = minDuration * 1000
        return this
    }


    override fun onRangeSeekBarValuesChanged(
        bar: RangeSeekBarView?,
        minValue: Long,
        maxValue: Long,
        action: Int,
        isMin: Boolean,
        pressedThumb: RangeSeekBarView.Thumb?
    ) {
        resultData.startPosition = minValue
        resultData.endPosition = maxValue

        when (action) {
            MotionEvent.ACTION_MOVE -> {
                mVideoPlayer?.seekTo((if (pressedThumb === RangeSeekBarView.Thumb.MIN) minValue else maxValue))

            }
            MotionEvent.ACTION_UP -> {
                mVideoPlayer?.seekTo(minValue)
            }
            else -> {

            }
        }
        rangeSeekBar!!.setStartEndTime(minValue, maxValue)
    }

    private fun initFilterRV() {
        val list = NewFilterType.createFilterList()
//        val path = videoData?.bitmap?.saveBitmapToFile(context) ?: ""
//val adapter= FilterArrayAdapter(context,list,path,videoData?.bitmap)
//        binding.rvFilter.adapter=adapter
        filterAdapter?.notifyDataSetChanged() ?: kotlin.run {

            filterAdapter = FilterAdapter(list, videoData?.bitmap) {
                gpuPlayerView?.setGlFilter(NewFilterType.createGlFilter(list[it].filter, context))
                resultData.filter = NewFilterType.createGlFilter(list[it].filter, context)
                list.forEach { it.isSelected = false }
                list[it].isSelected = true
                filterAdapter?.notifyDataSetChanged()
            }
            binding.rvFilter.adapter = filterAdapter

        }
    }

    fun getResultedVideo(): EditedVideoData {
        return resultData
    }

    fun addImage(imagePath: String?, rotation: Int) {

        val watermarkView = ScaleableImageView(context)
        watermarkView.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

        }
        watermarkView.scaleType = ImageView.ScaleType.FIT_XY
        watermarkView.setImageURI(Uri.parse(imagePath))

        watermarkView.tag = resultData.watermarkList?.size ?: 0
        watermarkView.id = View.generateViewId()


        val watermark = WatermarkData(imagePath, watermarkView)
        if (resultData.watermarkList == null) {
            resultData.watermarkList = ArrayList()
        } else {
            if (resultData.watermarkList!!.isNotEmpty()) {

                resultData.watermarkList?.forEach {
                    binding.container.removeView(it.watermarkView)
                }
                resultData.watermarkList?.clear()

            }
        }
        binding.container.addView(watermarkView)
        watermarkView.setFirstTimeHeightWidth(rotation)
        binding.watermarkG.visibility = View.VISIBLE
        binding.btUploadWatermark.text = context.getString(R.string.replace_watermark)
        binding.sbOpacity.progress = 100
        resultData.watermarkList?.add(watermark)
    }


    fun pause() {
        binding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0)

        mVideoPlayer?.pause()
    }

    fun hideWatermark() {
        binding.clWatermark.visibility = View.GONE
        resultData.watermarkList?.forEach {
            it.watermarkView?.disableDragging()
        }
    }

    fun showWatermark() {
        if (binding.clWatermark.visibility == View.VISIBLE) {
            binding.clWatermark.visibility = View.GONE
            resultData.watermarkList?.forEach {
                it.watermarkView?.disableDragging()
            }
        } else {
            binding.clWatermark.visibility = View.VISIBLE
            resultData.watermarkList?.forEach {
                it.watermarkView?.enableDragging()
            }
        }
        binding.root.invalidate()
    }

    fun setOnEditorListener(listener: OnEditorListener): EditorView {
        mEditorListener = listener
        return this
    }

    private var mEditorListener: OnEditorListener? = null

    interface OnEditorListener {
        fun onUploadWatermark()
        fun onVideoError()
    }


    fun computeWatermarkInfo() {
        resultData.watermarkList?.forEach {
            val mWatermarkView = it.watermarkView
            var pair = getWatermarkSize(mWatermarkView)
            var rect = getWatermarkRect(mWatermarkView)
            it.watermarkRect = rect
            it.watermarkSize = pair

        }
    }

    private fun getWatermarkSize(mWatermarkView: ScaleableImageView?): Pair<Float, Float> {
        val mHeight = (mWatermarkView?.height ?: 0) * (mWatermarkView?.scaleY ?: 1f)
        val mWidth = (mWatermarkView?.width ?: 0) * (mWatermarkView?.scaleX ?: 1f)

        var first = 0f
        var second = 0f
        val result = Rect()
        if (videoData?.rotation == 90 || videoData?.rotation == 270) {

            result.top = (mHeight * (videoData?.width ?: 0) / binding.container.height).toInt()

            result.left = (mWidth * (videoData?.height?.toFloat()
                ?: 0f) / binding.container.width.toFloat()).toInt()

            first = result.left.toFloat().div(videoData?.height ?: 0)
            second = result.top.toFloat().div(videoData?.width ?: 0)
        } else {
            result.left =
                (mWidth * (videoData?.width ?: 0) / binding.container.width).toInt()
            result.top =
                (mHeight * (videoData?.height ?: 0) / binding.container.height).toInt()
            first = result.left.toFloat().div(videoData?.width ?: 0)
            second = result.top.toFloat().div(videoData?.height ?: 0)
        }
        Loge(
            "WATERMARK_RECT",
            "watermark rect size >> ${result}"
        )


        var pair = Pair(
            String.format("%.2f", first).toFloat(),
            String.format("%.2f", second).toFloat()
        )

        Logd(
            "WATERMARK_RECT",
            "watermark size first >> ${pair.first} ,.. posY >> ${pair.second}}"
        )
        return pair
    }

    private fun getWatermarkRect(mWatermarkView: ScaleableImageView?): Rect {
        val result = Rect()
        val mHeight = (mWatermarkView?.height ?: 0) * (mWatermarkView?.scaleY ?: 1f)
        val mWidth = (mWatermarkView?.width ?: 0) * (mWatermarkView?.scaleX ?: 1f)
        val matrix = mWatermarkView?.matrix
        mWatermarkView?.requestLayout()


        val posX = mWatermarkView?.finalX ?: 0f
        val posY = mWatermarkView?.finalY ?: 0f

        val videoX = binding.container.x
        val videoY = binding.container.y




        Loge(
            "WATERMARK_RECT",
            "watermark height >> $mHeight , .. width >> $mWidth  , .. posX >> ${posX} ,.. posY >> ${posY}"
        )



        Loge(
            "WATERMARK_RECT",
            "player  float values posX >> ${videoX} ,.. posY >> ${videoY}}"
        )


        if (videoData?.rotation == 90 || videoData?.rotation == 270) {

            result.top = (posY * (videoData?.width ?: 0) / binding.container.height).toInt()

            result.left = (posX * (videoData?.height?.toFloat()
                ?: 0f) / binding.container.width.toFloat()).toInt()


        } else {
            result.left = (posX * (videoData?.width ?: 0) / binding.container.width).toInt()
            result.top =
                (posY * (videoData?.height ?: 0) / binding.container.height).toInt()

        }



        return result
    }


//colorchannelmixer=rr:rg:rb:ra:gr:gg:gb:ga:br:bg:bb:ba:ar:ag:ab:aa.
}