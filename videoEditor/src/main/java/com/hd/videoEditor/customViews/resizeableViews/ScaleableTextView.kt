package com.hd.videoEditor.customViews.resizeableViews

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class ScaleableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), View.OnTouchListener {

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
    private var viewParent: View? = null

    // we can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE

    init {
        initView()

    }

    fun setViewParent(view: View) {
        viewParent = view
    }

    private fun initView() {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())

        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.scale(mScaleFactor, mScaleFactor)

        if (mScaleFactor != 1f) {
            val newHeight = height * mScaleFactor
            val newWidth = width * mScaleFactor
            layoutParams = layoutParams?.apply {
                height = newHeight.toInt()
                width = newWidth.toInt()
                mScaleFactor = 1f
            }
        }
        canvas?.restore()
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))

            invalidate()
            return true
        }
    }

    override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {
        if (motionEvent == null)
            return true
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

                    Log.e(
                        "SCALEABLE_VIEW",
                        "new values >> ${newX} ,.. newY >> $newY "
                    )


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
                }
                return true // Consumed
            }
            MotionEvent.ACTION_UP -> {


                return false
            }

            else -> return true
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
}