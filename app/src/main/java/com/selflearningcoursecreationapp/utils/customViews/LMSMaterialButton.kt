package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication


class LMSMaterialButton : MaterialButton {

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
            attrs, R.styleable.LMSMaterialButton,
            defStyle!!, 0
        )


        var tintColor = ThemeUtils.getTintColor(context)
        var primaryColor = ThemeUtils.getAppColor(context)
        val backgroundType = themeAttrs.getInt(
            R.styleable.LMSMaterialButton_buttonBackground,
            ThemeConstants.TYPE_PRIMARY
        )

        if (backgroundType != -1) {
            val strokeArray = intArrayOf(
                tintColor,
                tintColor
            )
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed)
            )
            val colors = intArrayOf(
                primaryColor,
                tintColor
            )
            when (backgroundType) {
                ThemeConstants.TYPE_SECONDARY -> {
                    colors.set(0, ContextCompat.getColor(context, R.color.white))
                    strokeColor = ColorStateList(states, strokeArray)
                    strokeWidth = context.resources.getDimensionPixelOffset(R.dimen._1sdp)
                }
            }



            backgroundTintList = ColorStateList(states, colors)
        }


        val fontType =
            themeAttrs.getInt(R.styleable.LMSMaterialButton_buttonFont, ThemeConstants.FONT_MEDIUM)
        if (fontType >= 0) {
            typeface = ResourcesCompat.getFont(
                context,
                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        }
        val textColorType =
            themeAttrs.getInt(
                R.styleable.LMSMaterialButton_btnTextColor,
                ThemeConstants.TYPE_PRIMARY
            )
        if (textColorType >= 0)
            changeTextColor(textColorType)

        themeAttrs.recycle()
    }

    private fun changeTextColor(textColorType: Int) {
        val colorValue = when (textColorType) {

            ThemeConstants.TYPE_PRIMARY -> {
                ContextCompat.getColor(context, R.color.white)
            }
            ThemeConstants.TYPE_HEADING -> {
                ContextCompat.getColor(context, R.color.heading_color_262626)
            }
            ThemeConstants.TYPE_BODY -> {
                ContextCompat.getColor(context, R.color.hint_color_929292)
            }

            else -> {
                ThemeUtils.getAppColor(context)
            }

        }
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
        )
        val colors = intArrayOf(
            colorValue
        )
        iconTint = ColorStateList(states, colors)

        setTextColor(colorValue)
    }

}