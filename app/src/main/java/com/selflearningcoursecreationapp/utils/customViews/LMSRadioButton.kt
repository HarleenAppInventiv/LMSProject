package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.radiobutton.MaterialRadioButton
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.getAttrColor

class LMSRadioButton : MaterialRadioButton {

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
            attrs, R.styleable.LMSRadioButton,
            defStyle ?: 0, 0
        )
//
//        val btnTint = themeAttrs.getInt(R.styleable.LMSCheckBox_btnTint, ThemeConstants.TYPE_PRIMARY)
        val primaryColor = ThemeUtils.getAppColor(context)
        val tintColor = ThemeUtils.getTintColor(context)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
        )


//        when(btnTint)
//        {
//            ThemeConstants.TYPE_SECONDARY->{
//                primaryColor=ContextCompat.getColor(context,R.color.white)
//            }
//        }
        val colors = intArrayOf(
            tintColor,
            primaryColor,
            ContextCompat.getColor(context, context.getAttrColor(R.attr.radioUnselectedColor))
        )
        buttonTintList = ColorStateList(states, colors)

        val fontType =
            themeAttrs.getInt(R.styleable.LMSCheckBox_compoundFont, ThemeConstants.FONT_REGULAR)
        changeFontType(fontType)

        val textColorType =
            themeAttrs.getInt(
                R.styleable.LMSCheckBox_compoundTextColor,
                ThemeConstants.TYPE_PRIMARY
            )
        if (textColorType >= 0)
            changeTextColor(textColorType)

        themeAttrs.recycle()
    }

    fun changeFontType(fontType: Int) {
        if (fontType > 0) {
            typeface = ResourcesCompat.getFont(
                context,
                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        }
    }

    fun changeTextColor(textColorType: Int) {
        val colorValue = when (textColorType) {

            ThemeConstants.TYPE_SECONDARY -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.secondaryTextColor))
            }
            ThemeConstants.TYPE_HEADING -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.headingTextColor))
            }
            ThemeConstants.TYPE_BODY -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.bodyTextColor))
            }
            ThemeConstants.TYPE_THEME -> {
                ThemeUtils.getAppColor(context)
            }
            else -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.primaryTextColor))
            }

        }

        setTextColor(colorValue)
    }
}