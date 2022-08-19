package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.isNullOrZero


class LMSTextView : AppCompatTextView {

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
            attrs, R.styleable.LMSTextView,
            defStyle!!, 0
        )

        val drawablePos =
            themeAttrs.getInt(R.styleable.LMSTextView_primaryIconTint, ThemeConstants.TYPE_NONE)
        val drawableTintType =
            themeAttrs.getInt(R.styleable.LMSTextView_drawableTintType, ThemeConstants.TYPE_THEME)


        setDrawableColor(drawablePos, drawableTintType)

        val fontType =
            themeAttrs.getInt(R.styleable.LMSTextView_fontType, ThemeConstants.FONT_REGULAR)
        if (fontType > 0) {
            changeFontType(fontType)
        }

        val textColorType =
            themeAttrs.getInt(R.styleable.LMSTextView_textColorType, ThemeConstants.TYPE_PRIMARY)
        if (textColorType >= 0)
            changeTextColor(textColorType)

        val textBgColor =
            themeAttrs.getInt(R.styleable.LMSTextView_textBgColor, ThemeConstants.TYPE_NONE)
        val textBgMode =
            themeAttrs.getInt(R.styleable.LMSTextView_textBgMode, ThemeConstants.TYPE_BACKGROUND)
        if (textBgMode == ThemeConstants.TYPE_TINT) {
            changeBackgroundTint(textBgColor)
        } else {

            changeBackgroundColor(textBgColor)
        }

        themeAttrs.recycle()
    }

    fun changeFontType(fontType: Int) {
        typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.resources.getFont(

                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        } else {
            ResourcesCompat.getFont(
                context,
                ThemeUtils.getFont(SelfLearningApplication.fontId, fontType)
            )
        }
    }

    fun setDrawableColor(
        drawablePos: Int,
        drawableTintType: Int
    ) {
        if (drawablePos >= 0 && drawablePos < compoundDrawablesRelative.size) {
            val drawableColor = when (drawableTintType) {
                ThemeConstants.TYPE_THEME -> {
                    ThemeUtils.getAppColor(context)
                }
                ThemeConstants.TYPE_TINT -> {
                    ThemeUtils.getTintColor(context)
                }
                else -> {
                    null
                }
            }
            if (!drawableColor.isNullOrZero()) {
                compoundDrawablesRelative[drawablePos]?.let {
                    it.colorFilter =
                        PorterDuffColorFilter(
                            drawableColor!!,
                            PorterDuff.Mode.SRC_IN
                        )
                }
            }
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
            ThemeConstants.TYPE_BLACK -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.blackTextColor))
            }
            ThemeConstants.TYPE_WHITE -> {
                ContextCompat.getColor(context, context.getAttrColor(R.attr.whiteTextColor))
            }
            ThemeConstants.TYPE_THEME -> {
                ThemeUtils.getAppColor(context)
            }
            else -> {
                ThemeUtils.getPrimaryTextColor(context)
            }

        }

        setTextColor(colorValue)
    }

    fun changeBackgroundColor(backgroundTint: Int) {
        val color = getBgColor(backgroundTint)
        if (!color.isNullOrZero()) {
            setBackgroundColor(color!!)
        }
    }

    fun changeBackgroundTint(backgroundTint: Int) {
        val color = getBgColor(backgroundTint)
        if (!color.isNullOrZero()) {
            backgroundTintList = ColorStateList.valueOf(color!!)
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
                ThemeConstants.TYPE_BODY -> {
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