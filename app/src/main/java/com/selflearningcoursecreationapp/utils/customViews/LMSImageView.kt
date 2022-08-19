package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.selflearningcoursecreationapp.R

class LMSImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        initView(context, attrs, defStyle)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyle: Int? = 0) {
        val themeAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.LMSImageView,
            defStyle!!, 0
        )

        val changeBgType =
            themeAttrs.getInt(
                R.styleable.LMSImageView_changeBackgroundType,
                ThemeConstants.TYPE_NONE
            )
        changeBackground(changeBgType)
        val applyGrayscale =
            themeAttrs.getBoolean(R.styleable.LMSShapeAbleImageView_applyGrayscale, false)

        if (applyGrayscale && ThemeUtils.isViOn()) {
            applyGrayscale()
        }
        themeAttrs.recycle()
    }

    fun applyGrayscale() {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0.0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        colorFilter = filter

    }

    fun changeBackground(changeBgType: Int) {
        when (changeBgType) {
            ThemeConstants.TYPE_TINT -> {

                imageTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                imageTintMode = PorterDuff.Mode.MULTIPLY
            }
            ThemeConstants.TYPE_TINT_SRC -> {

                imageTintList = ColorStateList.valueOf(ThemeUtils.getTintColor(context))
                imageTintMode = PorterDuff.Mode.SRC_IN
            }
            ThemeConstants.TYPE_THEME -> {

                imageTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                imageTintMode = PorterDuff.Mode.SRC_IN
            }
            ThemeConstants.TYPE_BACKGROUND_TINT -> {

                backgroundTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                backgroundTintMode = PorterDuff.Mode.SRC_IN
            }
            ThemeConstants.TYPE_BG_TINT_SRC -> {

                backgroundTintList = ColorStateList.valueOf(ThemeUtils.getTintColor(context))
                backgroundTintMode = PorterDuff.Mode.SRC_IN
            }
            ThemeConstants.TYPE_BACKGROUND -> {
                setBackgroundColor(ThemeUtils.getAppColor(context))
            }

        }
    }

}