package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication

class LMSCheckBox : MaterialCheckBox {

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
            attrs, R.styleable.LMSCheckBox,
            defStyle!!, 0
        )

        var primaryColor = ThemeUtils.getAppColor(context)
        var tintColor = ThemeUtils.getTintColor(context)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
        )

        val colors = intArrayOf(
            tintColor,
            primaryColor,
            ContextCompat.getColor(context, R.color.et_dash_color)
        )
        buttonTintList = ColorStateList(states, colors)

        val fontType =
            themeAttrs.getInt(R.styleable.LMSCheckBox_compoundFont, ThemeConstants.FONT_REGULAR)
        if (fontType > 0) {
            typeface = ResourcesCompat.getFont(
                context,
                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        }
        val textColorType =
            themeAttrs.getInt(
                R.styleable.LMSCheckBox_compoundTextColor,
                ThemeConstants.TYPE_PRIMARY
            )
        if (textColorType >= 0)
            changeTextColor(textColorType)


        themeAttrs.recycle()
    }

    private fun changeTextColor(textColorType: Int) {
        val colorValue = when (textColorType) {

            ThemeConstants.TYPE_SECONDARY -> {
                ContextCompat.getColor(context, R.color.heading_color_262626)
            }
            ThemeConstants.TYPE_HEADING -> {
                ContextCompat.getColor(context, R.color.heading_color_262626)
            }
            ThemeConstants.TYPE_BODY -> {
                ContextCompat.getColor(context, R.color.hint_color_929292)
            }
            ThemeConstants.TYPE_THEME -> {
                ThemeUtils.getAppColor(context)
            }
            else -> {
                ContextCompat.getColor(context, R.color.text_color_black_131414)
            }

        }

        setTextColor(colorValue)
    }
}