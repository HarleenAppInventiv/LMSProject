package com.selflearningcoursecreationapp.utils.richView

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.*
import com.richpath.model.Group
import com.richpath.util.PathUtils
import com.selflearningcoursecreationapp.utils.richView.listener.OnRichPathUpdatedListener
import com.selflearningcoursecreationapp.utils.richView.pathparser.PathDataNode
import com.selflearningcoursecreationapp.utils.richView.pathparser.PathParser
import com.selflearningcoursecreationapp.utils.richView.pathparser.PathParserCompat
import com.selflearningcoursecreationapp.utils.richView.util.XmlParser

class RichPath(private val src: Path) : Path(src) {

    companion object {
        const val TAG_NAME = "path"
    }

    var fillColor = Color.TRANSPARENT
        set(value) {
            field = value
            onPathUpdated()
        }
    var strokeColor = Color.TRANSPARENT
        set(value) {
            field = value
            onPathUpdated()
        }
    var fillAlpha = 1f
        set(value) {
            field = value
            onPathUpdated()
        }
    var strokeAlpha = 1f
        set(value) {
            field = value
            onPathUpdated()
        }
    var strokeWidth = 0f
        set(value) {
            field = value
            onPathUpdated()
        }
    var trimPathStart = 0f
        set(value) {
            field = value
            trim()
            onPathUpdated()
        }
    var trimPathEnd = 1f
        set(value) {
            field = value
            trim()
            onPathUpdated()
        }
    var trimPathOffset = 0f
        set(value) {
            field = value
            trim()
            onPathUpdated()
        }

    var strokeLineCap = Paint.Cap.BUTT
        set(value) {
            field = value
            onPathUpdated()
        }
    var strokeLineJoin = Paint.Join.MITER
        set(value) {
            field = value
            onPathUpdated()
        }

    var strokeMiterLimit = 4f
        set(value) {
            field = value
            onPathUpdated()
        }

    var name: String? = null
    private lateinit var paint: Paint
    var rotation = 0f
        set(value) {
            val deltaValue = value - field
            if (isPivotToCenter) {
                PathUtils.setPathRotation(this, deltaValue)
                PathUtils.setPathRotation(src, deltaValue)
            } else {
                PathUtils.setPathRotation(this, deltaValue, pivotX, pivotY)
                PathUtils.setPathRotation(src, deltaValue, pivotX, pivotY)
            }
            field = value
            onPathUpdated()
        }
    var scaleX = 1f
        set(value) {
            if (isPivotToCenter) {
                //reset scaling
                PathUtils.setPathScaleX(this, 1.0f / field)
                PathUtils.setPathScaleX(src, 1.0f / field)
                //new scaling
                PathUtils.setPathScaleX(this, value)
                PathUtils.setPathScaleX(src, value)
            } else {
                //reset scaling
                PathUtils.setPathScaleX(this, 1.0f / field, pivotX, pivotY)
                PathUtils.setPathScaleX(src, 1.0f / field, pivotX, pivotY)
                //new scaling
                PathUtils.setPathScaleX(this, value, pivotX, pivotY)
                PathUtils.setPathScaleX(src, value, pivotX, pivotY)
            }
            field = value
            onPathUpdated()
        }
    var scaleY = 1f
        set(value) {
            if (isPivotToCenter) { //reset scaling
                PathUtils.setPathScaleY(this, 1.0f / field)
                PathUtils.setPathScaleY(src, 1.0f / field)
                //new scaling
                PathUtils.setPathScaleY(this, value)
                PathUtils.setPathScaleY(src, value)
            } else { //reset scaling
                PathUtils.setPathScaleY(this, 1.0f / field, pivotX, pivotY)
                PathUtils.setPathScaleY(src, 1.0f / field, pivotX, pivotY)
                //new scaling
                PathUtils.setPathScaleY(this, value, pivotX, pivotY)
                PathUtils.setPathScaleY(src, value, pivotX, pivotY)
            }
            field = value
            onPathUpdated()
        }
    var translationX = 0f
        set(value) {
            PathUtils.setPathTranslationX(this, value - field)
            PathUtils.setPathTranslationX(src, value - field)
            field = value
            onPathUpdated()

        }
    var translationY = 0f
        set(value) {
            PathUtils.setPathTranslationY(this, value - field)
            PathUtils.setPathTranslationY(src, value - field)
            field = value
            onPathUpdated()
        }

    var originalWidth = 0f
        private set
    var originalHeight = 0f
        private set

    var pivotX = 0f
    var pivotY = 0f
    var isPivotToCenter = false

    var onRichPathUpdatedListener: OnRichPathUpdatedListener? = null
        internal set

    private var pathMeasure: PathMeasure? = null

    /**
     * [pathDataNodes] couldn't be private.
     * It could called by @see [com.richpathanimator.AnimationBuilder.pathData]
     */
    var pathDataNodes: Array<PathDataNode>? = null
        set(value) {
            value ?: return
            PathUtils.setPathDataNodes(this, value)
            field = value
            for (matrix in matrices) {
                transform(matrix)
            }
            onPathUpdated()
        }

    private lateinit var matrices: ArrayList<Matrix>

    internal var onPathClickListener: OnPathClickListener? = null

    constructor(pathData: String) : this(PathParser.createPathFromPathData(pathData))

    init {
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }
        matrices = arrayListOf()
        updateOriginalDimens()
    }

    fun setWidth(width: Float) {
        PathUtils.setPathWidth(this, width)
        PathUtils.setPathWidth(src, width)
        onPathUpdated()
    }

    fun getWidth(): Float = PathUtils.getPathWidth(this)

    fun setHeight(height: Float) {
        PathUtils.setPathHeight(this, height)
        PathUtils.setPathWidth(src, height)
        onPathUpdated()
    }

    fun getHeight(): Float = PathUtils.getPathHeight(this)

    internal fun draw(canvas: Canvas) {
        paint.run {
            color = applyAlpha(fillColor, fillAlpha)
            style = Paint.Style.FILL
            canvas.drawPath(this@RichPath, this)

            color = applyAlpha(strokeColor, strokeAlpha)
            style = Paint.Style.STROKE
            canvas.drawPath(this@RichPath, this)
        }
    }

    fun applyGroup(group: Group) {
        mapToMatrix(group.matrix())
        pivotX = group.pivotX
        pivotY = group.pivotY
    }

    internal fun mapToMatrix(matrix: Matrix) {
        matrices.add(matrix)
        transform(matrix)
        src.transform(matrix)
        mapPoints(matrix)
        updateOriginalDimens()
    }

    private fun mapPoints(matrix: Matrix) {
        val src = floatArrayOf(pivotX, pivotY)
        matrix.mapPoints(src)
        pivotX = src[0]
        pivotY = src[1]
    }

    internal fun scaleStrokeWidth(scale: Float) {
        paint.strokeWidth = strokeWidth * scale
    }

    fun setPathData(pathData: String) {
        PathParserCompat.createNodesFromPathData(pathData)?.let {
            pathDataNodes = it
        }
    }

    fun inflate(context: Context, xpp: XmlResourceParser) {
        val pathData = XmlParser.getAttributeString(context, xpp, "pathData", name)

        pathDataNodes = PathParserCompat.createNodesFromPathData(pathData)

        name = XmlParser.getAttributeString(context, xpp, "name", name)

        fillAlpha = XmlParser.getAttributeFloat(xpp, "fillAlpha", fillAlpha)

        fillColor = XmlParser.getAttributeColor(context, xpp, "fillColor", fillColor)

        strokeAlpha = XmlParser.getAttributeFloat(xpp, "strokeAlpha", strokeAlpha)

        strokeColor = XmlParser.getAttributeColor(context, xpp, "strokeColor", strokeColor)

        strokeLineCap = XmlParser.getAttributeStrokeLineCap(xpp, "strokeLineCap", strokeLineCap)

        strokeLineJoin = XmlParser.getAttributeStrokeLineJoin(xpp, "strokeLineJoin", strokeLineJoin)

        strokeMiterLimit = XmlParser.getAttributeFloat(xpp, "strokeMiterLimit", strokeMiterLimit)

        strokeWidth = XmlParser.getAttributeFloat(xpp, "strokeWidth", strokeWidth)

        trimPathStart = XmlParser.getAttributeFloat(xpp, "trimPathStart", trimPathStart)

        trimPathEnd = XmlParser.getAttributeFloat(xpp, "trimPathEnd", trimPathEnd)

        trimPathOffset = XmlParser.getAttributeFloat(xpp, "trimPathOffset", trimPathOffset)

        fillType = XmlParser.getAttributePathFillType(xpp, "fillType", fillType)

        updatePaint()

        trim()
    }

    private fun updateOriginalDimens() {
        originalWidth = PathUtils.getPathWidth(this)
        originalHeight = PathUtils.getPathHeight(this)
    }

    private fun trim() {
        if (trimPathStart != 0.0f || trimPathEnd != 1.0f) {
            var start = (trimPathStart + trimPathOffset) % 1.0f
            var end = (trimPathEnd + trimPathOffset) % 1.0f
            val pathMeasure = pathMeasure ?: PathMeasure()
            pathMeasure.setPath(src, false)
            val len = pathMeasure.length
            start *= len
            end *= len
            reset()
            if (start > end) {
                pathMeasure.getSegment(start, len, this, true)
                pathMeasure.getSegment(0f, end, this, true)
            } else {
                pathMeasure.getSegment(start, end, this, true)
            }
            rLineTo(0f, 0f) // fix bug in measure
        }
    }

    private fun updatePaint() {
        paint.strokeCap = strokeLineCap
        paint.strokeJoin = strokeLineJoin
        paint.strokeMiter = strokeMiterLimit
        paint.strokeWidth = strokeWidth
    }

    private fun onPathUpdated() {
        onRichPathUpdatedListener?.onPathUpdated()
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val alphaBytes = Color.alpha(color)
        var newColor = color and 0x00FFFFFF
        newColor = newColor or ((alphaBytes * alpha).toInt() shl 24)
        return newColor
    }

    interface OnPathClickListener {
        fun onClick(richPath: RichPath)
    }

}