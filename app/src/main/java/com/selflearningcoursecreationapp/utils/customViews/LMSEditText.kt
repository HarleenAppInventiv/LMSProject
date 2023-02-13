package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.isNullOrZero


class LMSEditText : AppCompatEditText {

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
            attrs, R.styleable.LMSEditText,
            defStyle ?: 0, 0
        )

        val drawablePos =
            themeAttrs.getInt(R.styleable.LMSEditText_primaryIconTint, ThemeConstants.TYPE_NONE)
        val textBgColor =
            themeAttrs.getInt(R.styleable.LMSEditText_textBgColor, ThemeConstants.TYPE_NONE)

        val textBgMode =
            themeAttrs.getInt(R.styleable.LMSEditText_textBgMode, ThemeConstants.TYPE_BACKGROUND)
        if (textBgMode == ThemeConstants.TYPE_TINT) {
            changeBackgroundTint(textBgColor)
        } else {
            changeBackgroundColor(textBgColor)
        }
        changeDrawableTint(drawablePos)

        val fontType =
            themeAttrs.getInt(R.styleable.LMSEditText_fontType, ThemeConstants.FONT_REGULAR)
        if (fontType > 0) {
            typeface = ResourcesCompat.getFont(
                context,
                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        }
        val textColorType =
            themeAttrs.getInt(R.styleable.LMSEditText_textColorType, ThemeConstants.TYPE_SECONDARY)
        if (textColorType >= 0)
            changeTextColor(textColorType)

        themeAttrs.recycle()
    }

    private fun changeDrawableTint(drawablePos: Int) {
        if (drawablePos >= 0 && drawablePos < compoundDrawablesRelative.size) {

            compoundDrawablesRelative[drawablePos]?.let {

                it.colorFilter =
                    PorterDuffColorFilter(
                        ThemeUtils.getAppColor(context),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }



    private fun changeTextColor(textColorType: Int) {
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
            ThemeConstants.TYPE_BLACK -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.blackTextColor))
            }
            ThemeConstants.TYPE_WHITE -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.whiteTextColor))
            }
            else -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.primaryTextColor))
            }

        }

        setTextColor(colorValue)
    }

    private fun changeBackgroundColor(backgroundTint: Int) {
        val color = getBgColor(backgroundTint)
        if (!color.isNullOrZero()) {
            setBackgroundColor(color!!)
        }
    }

    private fun changeBackgroundTint(backgroundTint: Int) {
        val color = getBgColor(backgroundTint)
        if (!color.isNullOrZero()) {
            background.colorFilter = PorterDuffColorFilter(
                color!!,
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun getBgColor(backgroundTint: Int): Int? {
        return if (backgroundTint >= 0) {


            when (backgroundTint) {
                ThemeConstants.TYPE_THEME -> {

                    ThemeUtils.getAppColor(context)
                }
                ThemeConstants.TYPE_TINT -> {
                    ThemeUtils.getTintColor(context)

                }
                ThemeConstants.TYPE_SECONDARY -> {
                    ContextCompat.getColor(
                        context,
                        context.getAttrColor(R.attr.viewSecondaryBgColor)
                    )
                }
                else -> {
                    null
                }

            }


        } else {
            null
        }
    }


}