package com.selflearningcoursecreationapp.utils.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.hd.videoEditor.R
import com.hd.videoEditor.customViews.trimmer.UnitConverter
import java.text.DecimalFormat


class RangeSeekBarView : View {
    private val TAG = RangeSeekBarView::class.java.simpleName
    val INVALID_POINTER_ID = 255
    val ACTION_POINTER_INDEX_MASK = 0x0000ff00
    val ACTION_POINTER_INDEX_SHIFT = 8
    private val TextPositionY: Int = UnitConverter.dpToPx(context, 7)
    private val paddingOnTop: Int = UnitConverter.dpToPx(context, 10)
    private var mActivePointerId = INVALID_POINTER_ID
    private var mMinShootTime = 300L
    private var absoluteMinValuePrim = 0.0
    private var absoluteMaxValuePrim = 0.0
    private var normalizedMinValue =
        0.0 //The ratio of point coordinates to the total length, ranging from 0-1
    private var normalizedMaxValue =
        1.0 //The ratio of point coordinates to the total length, ranging from 0-1
    private var normalizedMinValueTime = 0.0
    private var normalizedMaxValueTime =
        1.0 // normalized：Normalized -- the ratio of point coordinates to the total length, ranging from 0-1
    private var mScaledTouchSlop = 0
    private var thumbImageLeft: Bitmap? = null
    private var thumbImageRight: Bitmap? = null
    private var thumbPressedImage: Bitmap? = null
    private var paint: Paint? = null
    private var rectPaint: Paint? = null
    private val mVideoTrimTimePaintL = Paint()
    private val mVideoTrimTimePaintR = Paint()
    private val mShadow = Paint()
    private var thumbWidth = 0
    private var thumbHalfWidth = 0f
    private val padding = 0f
    private var mStartPosition: Long = 0
    private var mEndPosition: Long = 0
    private val thumbPaddingTop = 0f
    private var isTouchDown = false
    private var mDownMotionX = 0f
    private var mIsDragging = false
    private var pressedThumb: Thumb? = null
    private var isMin = false
    private var min_width = 1.0 //Minimum clipping distance

    /**
     * For external activity calls, the control is to print log information when dragging, the default is false not to print
     */
    var isNotifyWhileDragging = false
    private var mRangeSeekBarChangeListener: OnRangeSeekBarChangeListener? = null
    private val whiteColorRes = context.resources.getColor(R.color.white)

    enum class Thumb {
        MIN, MAX
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, absoluteMinValuePrim: Long, absoluteMaxValuePrim: Long) : super(
        context
    ) {
        this.absoluteMinValuePrim = absoluteMinValuePrim.toDouble()
        this.absoluteMaxValuePrim = absoluteMaxValuePrim.toDouble()
        isFocusable = true
        isFocusableInTouchMode = true
        initViews()
    }

    fun setTrimStartIcon(icon: Int): RangeSeekBarView {
        thumbImageLeft = BitmapFactory.decodeResource(resources, icon)
        return this
    }

    fun setTrimEndIcon(icon: Int): RangeSeekBarView {
        thumbImageRight = BitmapFactory.decodeResource(resources, icon)
        return this
    }

    fun setShadowColor(color: Int): RangeSeekBarView {
        mShadow.color = color
        return this
    }

    fun build(): RangeSeekBarView {
        init()
        return this
    }

    private fun initViews() {
        thumbImageLeft = BitmapFactory.decodeResource(resources, R.drawable.trim_start)
        thumbImageRight = BitmapFactory.decodeResource(resources, R.drawable.trim_end)
        val shadowColor = context.resources.getColor(R.color.shadow_color)
        mShadow.color = shadowColor
    }

    private fun init() {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        val width = thumbImageLeft?.width ?: 0
        val height = this.thumbImageLeft?.height ?: 0
        val rWidth = thumbImageLeft?.width ?: 0
        val rHeight = this.thumbImageLeft?.height ?: 0
        val newWidth: Int =
            UnitConverter.dpToPx(context, context.resources.getDimensionPixelSize(R.dimen._5sdp))
        val newHeight: Int =
            UnitConverter.dpToPx(context, context.resources.getDimensionPixelSize(R.dimen._80sdp))

        val scaleWidth = newWidth * 1.0f / width
        val scaleHeight = newHeight * 1.0f / height
        val scaleRWidth = newWidth * 1.0f / rWidth
        val scaleRHeight = newHeight * 1.0f / rHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val rMatrix = Matrix()
        rMatrix.postScale(scaleRWidth, scaleRHeight)
        if (thumbImageLeft != null)
            thumbImageLeft =
                Bitmap.createBitmap(thumbImageLeft!!, 0, 0, width, height, matrix, true)
        if (thumbImageRight != null)
            thumbImageRight =
                Bitmap.createBitmap(thumbImageRight!!, 0, 0, rWidth, rHeight, rMatrix, true)
        thumbPressedImage = thumbImageLeft
        thumbWidth = newWidth
        thumbHalfWidth = (thumbWidth / 2).toFloat()
        mShadow.isAntiAlias = true
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint?.style = Paint.Style.FILL
        rectPaint?.color = whiteColorRes
//        mVideoTrimTimePaintL.strokeWidth = 3f
//        mVideoTrimTimePaintL.setARGB(255, 51, 51, 51)
//        mVideoTrimTimePaintL.textSize = 22f
//        mVideoTrimTimePaintL.isAntiAlias = true
//        mVideoTrimTimePaintL.color = whiteColorRes
//        mVideoTrimTimePaintL.textAlign = Paint.Align.LEFT
//        mVideoTrimTimePaintR.strokeWidth = 3f
//        mVideoTrimTimePaintR.setARGB(255, 51, 51, 51)
//        mVideoTrimTimePaintR.textSize = 22f
//        mVideoTrimTimePaintR.isAntiAlias = true
//        mVideoTrimTimePaintR.color = whiteColorRes
//        mVideoTrimTimePaintR.textAlign = Paint.Align.RIGHT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 300
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = 120
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bg_middle_left = 0f
        val bg_middle_right = (width - paddingRight).toFloat()
        val rangeL = normalizedToScreen(normalizedMinValue)
        val rangeR = normalizedToScreen(normalizedMaxValue)
        val leftRect = Rect(bg_middle_left.toInt(), height, rangeL.toInt(), 0)
        val rightRect = Rect(rangeR.toInt(), height, bg_middle_right.toInt(), 0)
        canvas.drawRect(leftRect, mShadow)
        canvas.drawRect(rightRect, mShadow)
//        if (rectPaint != null) {
//            canvas.drawRect(
//                rangeL,
//                thumbPaddingTop + paddingOnTop,
//                rangeR,
//                thumbPaddingTop + UnitConverter.dpToPx(context, 2) + paddingOnTop,
//                rectPaint!!
//            )
//            canvas.drawRect(
//                rangeL,
//                height - UnitConverter.dpToPx(context, 2).toFloat(),
//                rangeR,
//                height.toFloat(),
//                rectPaint!!
//            )
//        }
        drawThumb(normalizedToScreen(normalizedMinValue), false, canvas, true)
        drawThumb(normalizedToScreen(normalizedMaxValue), false, canvas, false)
        drawVideoTrimTimeText(canvas)
    }

    private fun drawThumb(screenCoord: Float, pressed: Boolean, canvas: Canvas, isLeft: Boolean) {
        val bm =
            (if (pressed) thumbPressedImage else if (isLeft) thumbImageLeft else thumbImageRight)
        if (bm != null) {
            canvas.drawBitmap(
                bm,
                screenCoord - if (isLeft) 0 else thumbWidth,
                0f,
                paint
            )
        }
    }

    private fun drawVideoTrimTimeText(canvas: Canvas) {
//        val leftThumbsTime: String = UnitConverter.convertSecondsToTime(mStartPosition) ?: ""
//        val rightThumbsTime: String = UnitConverter.convertSecondsToTime(mEndPosition) ?: ""
//        canvas.drawText(
//            leftThumbsTime,
//            normalizedToScreen(normalizedMinValue),
//            TextPositionY.toFloat(),
//            mVideoTrimTimePaintL
//        )
//        canvas.drawText(
//            rightThumbsTime,
//            normalizedToScreen(normalizedMaxValue),
//            TextPositionY.toFloat(),
//            mVideoTrimTimePaintR
//        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e(TAG, "onTouchEvent: " + event.action + " x: " + event.x)

        if (isTouchDown) {
            Log.e(TAG, "onTouchEvent: isTouchDown")
            return super.onTouchEvent(event)
        }
        if (event.pointerCount > 1) {
            Log.e(TAG, "onTouchEvent: pointerCount ")
            return super.onTouchEvent(event)
        }
        if (!isEnabled) {
            Log.e(TAG, "onTouchEvent: isEnabled ")
            return false
        }
//        if (absoluteMaxValuePrim <= mMinShootTime) {
//            Log.e(TAG, "onTouchEvent: absoluteMaxValuePrim ")
//            return super.onTouchEvent(event)
//        }
        val pointerIndex: Int // record the index of the click point
        val action = event.action
        Log.e(TAG, "onTouchEvent: action ")
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //Remember the coordinate x of the point where the last finger clicked on the screen, mDownMotionX
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                mDownMotionX = event.getX(pointerIndex)
                //Determine whether the touch is the maximum value thumb or the minimum value thumb
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) return super.onTouchEvent(event)
                isPressed = true // Set the control to be pressed
                onStartTrackingTouch() // Set mIsDragging to true and start tracking touch events
                trackTouchEvent(event)
                attemptClaimDrag()


                val width = Resources.getSystem().displayMetrics.widthPixels
//val value=
                if (mRangeSeekBarChangeListener != null) {
                    mRangeSeekBarChangeListener?.onRangeSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_DOWN, isMin,
                        pressedThumb
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    // Scroll to follow the motion event
                    pointerIndex = event.findPointerIndex(mActivePointerId)
                    val x =
                        event.getX(pointerIndex) // The X coordinate of the finger on the control
                    // The finger is not on the min and max, and there is a swipe event on the control
                    if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                        isPressed = true
                        Log.e(
                            TAG,
                            "Not dragging the maximum and minimum"
                        ) // 一Will it not be implemented?
                        invalidate()
                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                if (isNotifyWhileDragging && mRangeSeekBarChangeListener != null) {
                    mRangeSeekBarChangeListener?.onRangeSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_MOVE,
                        isMin, pressedThumb
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                invalidate()
                if (mRangeSeekBarChangeListener != null) {
                    mRangeSeekBarChangeListener?.onRangeSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_UP, isMin,
                        pressedThumb
                    )
                }
                pressedThumb = null // When the finger is lifted, the touched thumb is set to null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.pointerCount - 1
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index)
                mActivePointerId = event.getPointerId(index)
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
            else -> {
            }
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and ACTION_POINTER_INDEX_MASK shr ACTION_POINTER_INDEX_SHIFT
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mDownMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun trackTouchEvent(event: MotionEvent) {
        Log.e(TAG, "trackTouchEvent: " + event.action + " x: " + event.x)
        if (event.pointerCount > 1) return
        Log.e(TAG, "trackTouchEvent: " + event.action + " x: " + event.x)
        val pointerIndex =
            event.findPointerIndex(mActivePointerId) // get the index of the pressed point
        Log.e(TAG, "trackTouchEvent: pointerIndex $pointerIndex ")


        var x = 0f
        x = try {
            event.getX(pointerIndex)

        } catch (e: Exception) {
            return
        }

        Log.e(TAG, "trackTouchEvent: x $x ")

        if (Thumb.MIN == pressedThumb) {
            // screenToNormalized(x)-->get normalized 0-1 value
            setNormalizedMinValue(screenToNormalized(x, 0))
        } else if (Thumb.MAX == pressedThumb) {
            setNormalizedMaxValue(screenToNormalized(x, 1))
        }
    }

    private fun screenToNormalized(screenCoord: Float, position: Int): Double {
        val width = width
        return if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            isMin = false
            var current_width = screenCoord.toDouble()
            val rangeL = normalizedToScreen(normalizedMinValue)
            val rangeR = normalizedToScreen(normalizedMaxValue)
            val min =
                mMinShootTime / (absoluteMaxValuePrim - absoluteMinValuePrim) * (width - thumbWidth * 2)
            min_width =
                if (absoluteMaxValuePrim > 5 * 60 * 1000) { //Exact four decimal places greater than 5 minutes
                    val df = DecimalFormat("0.0000")
                    df.format(min).toDouble()
                } else {
                    Math.round(min + 0.5).toDouble()
                }
            if (position == 0) {
                if (isInThumbRangeLeft(screenCoord, normalizedMinValue, 0.5)) {
                    return normalizedMinValue
                }
                val rightPosition: Float =
                    if (getWidth() - rangeR >= 0) getWidth() - rangeR else 0f
                val left_length = valueLength - (rightPosition + min_width)
                if (current_width > rangeL) {
                    current_width = rangeL + (current_width - rangeL)
                } else if (current_width <= rangeL) {
                    current_width = rangeL - (rangeL - current_width)
                }
                if (current_width > left_length) {
                    isMin = true
                    current_width = left_length
                }
                if (current_width < thumbWidth * 2 / 3) {
                    current_width = 0.0
                }
                val resultTime = (current_width - padding) / (width - 2 * thumbWidth)
                normalizedMinValueTime = Math.min(1.0, Math.max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                Math.min(
                    1.0,
                    Math.max(0.0, result)
                ) // The value is guaranteed to be between 0 and 1, but when is this judgment useful?
            } else {
                if (isInThumbRange(screenCoord, normalizedMaxValue, 0.5)) {
                    return normalizedMaxValue
                }
                val right_length = valueLength - (rangeL + min_width)
                if (current_width > rangeR) {
                    current_width = rangeR + (current_width - rangeR)
                } else if (current_width <= rangeR) {
                    current_width = rangeR - (rangeR - current_width)
                }
                var paddingRight = getWidth() - current_width
                if (paddingRight > right_length) {
                    isMin = true
                    current_width = getWidth() - right_length
                    paddingRight = right_length
                }
                if (paddingRight < thumbWidth * 2 / 3) {
                    current_width = getWidth().toDouble()
                    paddingRight = 0.0
                }
                var resultTime = (paddingRight - padding) / (width - 2 * thumbWidth)
                resultTime = 1 - resultTime
                normalizedMaxValueTime = Math.min(1.0, Math.max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                Math.min(
                    1.0,
                    Math.max(0.0, result)
                ) // The value is guaranteed to be between 0 and 1, but when is this judgment useful?
            }
        }
    }

    private val valueLength: Int
        private get() = width - 2 * thumbWidth

    /**
     * Calculate in which Thumb
     *
     * @param touchX touchX
     * @return Whether the touched one is empty or the maximum value or the minimum value
     */
    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(
            touchX,
            normalizedMinValue,
            2.0
        ) // Whether the touch point is within the minimum image range
        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue, 2.0)
        if (minThumbPressed && maxThumbPressed) {
            // If two thumbs overlap, and you cannot judge which one to drag, do the following
            // If the touch point is on the right side of the screen, it is judged that the touch has reached the minimum value thumb, otherwise it is judged that the touch has reached the maximum value thumb
            result = if (touchX / width > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        return result
    }

    private fun isInThumbRange(
        touchX: Float,
        normalizedThumbValue: Double,
        scale: Double
    ): Boolean {
        // The difference between the X coordinate of the current touch point and the X coordinate of the center point of the minimum picture on the screen <= the width of the minimum point picture is generally
        // That is, it is judged whether the touch point is within a circle with the center of the minimum image as the origin and half the width as the radius.
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth * scale
    }

    private fun isInThumbRangeLeft(
        touchX: Float,
        normalizedThumbValue: Double,
        scale: Double
    ): Boolean {
        // The difference between the X coordinate of the current touch point and the X coordinate of the center point of the minimum picture on the screen <= the width of the minimum point picture is generally
        // That is, it is judged whether the touch point is within a circle with the center of the minimum image as the origin and half the width as the radius.
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue) - thumbWidth) <= thumbHalfWidth * scale
    }

    /**
     * Trying to tell the parent view not to intercept the drag of the child control
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    fun onStartTrackingTouch() {
        mIsDragging = true
    }

    fun onStopTrackingTouch() {
        mIsDragging = false
    }

    fun setMinShootTime(min_cut_time: Long) {
        mMinShootTime = min_cut_time
    }

    private fun normalizedToScreen(normalizedCoord: Double): Float {
        return (paddingLeft + normalizedCoord * (width - paddingLeft - paddingRight)).toFloat()
    }

    private fun valueToNormalized(value: Long): Double {
        return if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            0.0
        } else (value - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim)
    }

    fun setStartEndTime(start: Long, end: Long) {
        mStartPosition = start / 1000
        mEndPosition = end / 1000
    }

    fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = Math.max(0.0, Math.min(1.0, Math.min(value, normalizedMaxValue)))
        invalidate() // 重新绘制此view
    }

    fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = Math.max(0.0, Math.min(1.0, Math.max(value, normalizedMinValue)))
        invalidate() // 重新绘制此view
    }

    var selectedMinValue: Long
        get() = normalizedToValue(normalizedMinValueTime)
        set(value) {
            if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
                setNormalizedMinValue(0.0)
            } else {
                setNormalizedMinValue(valueToNormalized(value))
            }
        }
    var selectedMaxValue: Long
        get() = normalizedToValue(normalizedMaxValueTime)
        set(value) {
            if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
                setNormalizedMaxValue(1.0)
            } else {
                setNormalizedMaxValue(valueToNormalized(value))
            }
        }

    private fun normalizedToValue(normalized: Double): Long {
        Log.e(
            TAG,
            "normalizedToValue: $normalized >>> absoluteMinValuePrim  >> $absoluteMinValuePrim .... absoluteMaxValuePrim $absoluteMaxValuePrim "
        )

        return (absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim)).toLong()
    }

    fun setTouchDown(touchDown: Boolean) {
        isTouchDown = touchDown
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("SUPER", super.onSaveInstanceState())
        bundle.putDouble("MIN", normalizedMinValue)
        bundle.putDouble("MAX", normalizedMaxValue)
        bundle.putDouble("MIN_TIME", normalizedMinValueTime)
        bundle.putDouble("MAX_TIME", normalizedMaxValueTime)
        return bundle
    }

    override fun onRestoreInstanceState(parcel: Parcelable) {
        val bundle = parcel as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"))
        normalizedMinValue = bundle.getDouble("MIN")
        normalizedMaxValue = bundle.getDouble("MAX")
        normalizedMinValueTime = bundle.getDouble("MIN_TIME")
        normalizedMaxValueTime = bundle.getDouble("MAX_TIME")
    }

    interface OnRangeSeekBarChangeListener {
        fun onRangeSeekBarValuesChanged(
            bar: RangeSeekBarView?,
            minValue: Long,
            maxValue: Long,
            action: Int,
            isMin: Boolean,
            pressedThumb: Thumb?
        )
    }

    fun setOnRangeSeekBarChangeListener(listener: OnRangeSeekBarChangeListener?) {
        mRangeSeekBarChangeListener = listener
    }


}