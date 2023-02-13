package com.hd.videoEditor.customViews.resizeableViews

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class ScaleableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener {

    private var isFirstTime: Boolean = true
    private var oldDist: Float = 1f
    private var lastEvent: FloatArray? = null
    var finalX: Float = 0f
    var finalY: Float = 0f
    private var mPosY: Float = 0f
    private var mPosX: Float = 0f
    private var mLastTouchY: Float = 0f
    private var mLastTouchX: Float = 0f
    private var mActivePointerId = MotionEvent.INVALID_POINTER_ID
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1f
    private var mScaleFactorF = 1f

    // we can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private var isDraggingEnable = true

    init {
        initView()

    }


    private fun initView() {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())

        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.scale(mScaleFactor, mScaleFactor)
        // onDraw() code goes here
//        scaleX = mScaleFactor
//        scaleY = mScaleFactor
        if (mScaleFactor != 1f) {
            setNewHeightWidth(rotation)
//            val newHeight = height * mScaleFactor
//            val newWidth = width * mScaleFactor

        } else if (isFirstTime) {
            isFirstTime = false
            setNewHeightWidth(rotation)
        }
        canvas?.restore()
    }

    private var rotation = 0
    fun setNewHeightWidth(rotation: Int) {
        this.rotation = rotation
        (parent as View).let { parentV ->
            var newHeight = height * mScaleFactor
            var newWidth = width * mScaleFactor

            if (rotation == 0 || rotation == 90) {
                val heightProp = newHeight / parentV.height
                val widthProp = newWidth / parentV.width
                if (heightProp > 0.9) {
                    newHeight = (0.9f).times(parentV.height)
                } else if (heightProp < 0.2) {
                    newHeight = (0.2f).times(parentV.height)
                }
                if (widthProp > 0.9) {
                    newWidth = (0.9f).times(parentV.width)
                } else if (widthProp < 0.2) {
                    newWidth = (0.2f).times(parentV.width)
                }
            } else {
                val heightProp = newHeight / parentV.width
                val widthProp = newWidth / parentV.height
                if (heightProp > 0.9) {
                    newHeight = (0.9f).times(parentV.width)
                } else if (heightProp < 0.2) {
                    newHeight = (0.2f).times(parentV.width)
                }
                if (widthProp > 0.9) {
                    newWidth = (0.9f).times(parentV.height)
                } else if (widthProp < 0.2) {
                    newWidth = (0.2f).times(parentV.height)
                }
            }
            layoutParams = layoutParams?.apply {
                height = newHeight.toInt()
                width = newWidth.toInt()
                mScaleFactor = 1f
            }
        }
    }

/*
    override fun onTouch(p0: View?, ev: MotionEvent?): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        // Let the ScaleGestureDetector inspect all events.
      if (ev==null)
      {
          return true
      }
        mScaleDetector?.onTouchEvent(ev)

        val action = ev?.getActionMasked()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                ev?.getActionIndex()?.also { pointerIndex ->
                    // Remember where we started (for dragging)
                    mLastTouchX = ev.getX( pointerIndex)
                    mLastTouchY = ev.getY( pointerIndex)
                }

                // Save the ID of this pointer (for dragging)
                mActivePointerId = ev.getPointerId( 0)


            }
            MotionEvent.ACTION_MOVE -> {

                // Find the index of the active pointer and fetch its position
                val (x1: Float, y1: Float) = ev.findPointerIndex( mActivePointerId).let { pointerIndex ->
                        // Calculate the distance moved
                    ev.getX( pointerIndex) to
                            ev.getY( pointerIndex)
                    }

                mPosX += x1 - mLastTouchX
                mPosY += y1 - mLastTouchY

                invalidate()

            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                ev.getActionIndex().also { pointerIndex ->
                    ev.getPointerId( pointerIndex)
                        .takeIf { it == mActivePointerId }
                        ?.run {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            val newPointerIndex = if (pointerIndex == 0) 1 else 0
                            mLastTouchX = ev.getX( newPointerIndex)
                            mLastTouchY = ev.getY( newPointerIndex)
                            mActivePointerId = ev.getPointerId( newPointerIndex)
                        }
                }
            }






        }
        return true
    }
*/

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            var factor1 = mScaleFactor
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.8f, Math.min(mScaleFactor, 3.0f))

//            if (mScaleFactorF > 1f && mScaleFactorF < 2f) {
//                mScaleFactor = factor1
//                mScaleFactorF = factor1
//            } else {
//                mScaleFactor = mScaleFactorF
//            }
            invalidate()
            return true
        }
    }

    override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {
        if (motionEvent == null)
            return true
        if (isDraggingEnable) {
            mScaleDetector?.onTouchEvent(motionEvent)
            when (motionEvent.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    mLastTouchX = motionEvent.rawX
                    mLastTouchY = motionEvent.rawY
                    mPosX = x - mLastTouchX
                    mPosY = y - mLastTouchY
                    mode = DRAG
                    return true // Consumed
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(motionEvent)
                    if (oldDist > 10f) {
                        mode = ZOOM
                    }
                    lastEvent = FloatArray(4).apply {
                        set(0, motionEvent.getX(0))
                        set(1, motionEvent.getX(1))
                        set(2, motionEvent.getY(0))
                        set(3, motionEvent.getY(1))
                    }

                    return true
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                    lastEvent = null
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        var newX: Float = motionEvent.rawX + mPosX
                        newX = Math.max(
                            0f,
                            newX
                        )
                        var newY: Float = motionEvent.rawY + mPosY
                        newY =
                            Math.max(0f, newY)

//                    Log.e(
//                        "SCALEABLE_VIEW",
//                        "x values >> ${motionEvent.rawX} ,.. posX >> $mPosX ... lastX >> $mLastTouchX ,... x >>>$x"
//                    )
//                    Log.e(
//                        "SCALEABLE_VIEW",
//                        "y values >> ${motionEvent.rawY} ,.. posX >> $mPosY ... lastX >> $mLastTouchY ,... x >>>$y"
//                    )
                        Log.e(
                            "SCALEABLE_VIEW",
                            "new values >> ${newX} ,.. newY >> $newY "
                        )


//                    animate()
//                        .x(newX)
//                        .y(newY)
//                        .setDuration(0)
//                        .start()

//
//


                        (parent as View).let { parentV ->
                            val newHeight = height * scaleY
                            val newWidth = width * scaleX

                            if (newY > parentV.height - newHeight) {
                                finalY = (parentV.bottom - newHeight).toFloat()
                                finalY = Math.min(
                                    (parentV.height - newHeight).toFloat(),
                                    finalY
                                )
                            } else {
                                finalY = newY

                            }

                            if (newX > parentV.width - newWidth) {
                                finalX = (parentV.width - newWidth).toFloat()
                                finalX = Math.min(
                                    (parentV.width - newWidth).toFloat(),
                                    newX
                                )
                            } else {
                                finalX = newX
                            }
                        }
//
                        animate()
                            .x(finalX)

                            .y(finalY)
                            .setDuration(0)
                            .start()
                    } else if (mode == ZOOM) {
//                    val newDist = spacing(motionEvent)
//                    if (newDist > 20f) {
//                        val scale = newDist.div(oldDist)
//                        scaleX = scale
//                        scaleY = scale
//                        invalidate()
//                        animate()
//                            .scaleX(scale)
//                            .scaleY(scale)
//                            .setDuration(10)
//                            .start()
//                    }
                    }
                    return true // Consumed
                }
                MotionEvent.ACTION_UP -> {


                    return false
                }

                else -> return true
            }
        } else {
            return true
        }
    }


    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    fun disableDragging() {
        isDraggingEnable = false
    }

    fun enableDragging() {
        isDraggingEnable = true

    }

    fun setFirstTimeHeightWidth(rotation: Int) {
        this.rotation = rotation

    }
}