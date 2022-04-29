package com.selflearningcoursecreationapp.utils.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication


class LMSMaterialButton : MaterialButton {

    private var mAttrs: AttributeSet? = null
    private var mDefStyle: Int? = null

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
        mAttrs = attrs
        mDefStyle = defStyle

        val themeAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.LMSMaterialButton,
            defStyle!!, 0
        )


        val backgroundType = themeAttrs.getInt(
            R.styleable.LMSMaterialButton_buttonBackground,
            ThemeConstants.TYPE_PRIMARY
        )


//        val isBtnEnabled = themeAttrs.getBoolean(R.styleable.LMSMaterialButton_btnEnabled, true)
//if (!isBtnEnabled && Build.VERSION.SDK_INT<24)
//{
//    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getDisabledColor()))
//}else{
        changeBtnBackground(backgroundType)
//}

        val fontType =
            themeAttrs.getInt(
                R.styleable.LMSMaterialButton_buttonFont,
                ThemeConstants.FONT_MEDIUM
            )
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

    @SuppressLint("RestrictedApi")
    private fun changeBtnBackground(backgroundType: Int) {
        var tintColor = ThemeUtils.getTintColor(context)
        var primaryColor = ThemeUtils.getAppColor(context)

        var disabledColor = getDisabledColor()

        if (backgroundType != ThemeConstants.TYPE_NONE) {
            val strokeArray = intArrayOf(
                tintColor,
                tintColor,
                tintColor,
                tintColor
            )
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_active)
            )
            val colors = intArrayOf(
                primaryColor,
                tintColor,
                disabledColor,
                primaryColor
            )
            when (backgroundType) {
                ThemeConstants.TYPE_SECONDARY -> {
                    colors.set(0, ContextCompat.getColor(context, R.color.white))
                    strokeColor = ColorStateList(states, strokeArray)
                    strokeWidth = context.resources.getDimensionPixelOffset(R.dimen._1sdp)
                }
                ThemeConstants.TYPE_TINT -> {
                    colors.set(0, tintColor)

                }
            }
//
//background.setTintList(ColorStateList(states,colors))
//background.setTintMode(PorterDuff.Mode.MULTIPLY)

//            backgroundTintList = ColorStateList(states, colors)
//            supportBackgroundTintList=ColorStateList(states, colors)
//            backgroundTintMode= PorterDuff.Mode.SRC_IN

            if (Build.VERSION.SDK_INT >= 24)
                ViewCompat.setBackgroundTintList(this, ColorStateList(states, colors))
            else {
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(primaryColor))

            }
        }
    }

    private fun getDisabledColor(): Int {
        val tintColor = ThemeUtils.getTintColor(context)
        var red = (Color.red(tintColor))
        var green = (Color.green(tintColor))


        var blue = (Color.blue(tintColor))


        var disabledColor = Color.parseColor(
            String.format(
                "#%02x%02x%02x%02x",
                100,
                red,
                green,
                blue
            )
        )
        return disabledColor
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

    fun setBtnDisabled(enabled: Boolean) {
        if (Build.VERSION.SDK_INT < 24) {
            if (!enabled) {
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(getDisabledColor()))
            } else {
                if (mAttrs != null && mDefStyle != null) {
                    val themeAttrs = context.obtainStyledAttributes(
                        mAttrs, R.styleable.LMSMaterialButton,
                        mDefStyle!!, 0
                    )

                    val backgroundType = themeAttrs.getInt(
                        R.styleable.LMSMaterialButton_buttonBackground,
                        ThemeConstants.TYPE_PRIMARY
                    )

                    changeBtnBackground(backgroundType)
                    themeAttrs.recycle()
                } else {
                    ViewCompat.setBackgroundTintList(
                        this,
                        ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                    )

                }
            }
        }
    }

}