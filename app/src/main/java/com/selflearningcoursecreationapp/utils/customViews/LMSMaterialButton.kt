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
import com.selflearningcoursecreationapp.extensions.getAttrColor


class LMSMaterialButton : MaterialButton {

    private var mAttrs: AttributeSet? = null
    private var mDefStyle: Int? = null
    private var buttonType = ThemeConstants.TYPE_PRIMARY
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
                ThemeConstants.FONT_SEMI_BOLD
            )
        if (fontType > 0) {
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

        buttonType = backgroundType
        val tintColor = ThemeUtils.getBtnTintColor(context)
        val primaryColor = ThemeUtils.getBtnBgColor(context)

        val disabledColor = getDisabledColor()

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
                    colors[0] = ContextCompat.getColor(context, R.color.white)
                    strokeColor = ColorStateList(states, strokeArray)
                    strokeWidth = context.resources.getDimensionPixelOffset(R.dimen._1sdp)
                }
                ThemeConstants.TYPE_TINT -> {
                    colors[0] = tintColor

                }
                ThemeConstants.TYPE_BACKGROUND_TINT -> {
                    colors[0] = tintColor
                    strokeArray[0] = primaryColor

                    strokeColor = ColorStateList(states, strokeArray)
                    strokeWidth = context.resources.getDimensionPixelOffset(R.dimen._1sdp)

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
        val tintColor = ThemeUtils.getBtnTintColor(context)
        val red = (Color.red(tintColor))
        val green = (Color.green(tintColor))


        val blue = (Color.blue(tintColor))


        return Color.parseColor(
            String.format(
                "#%02x%02x%02x%02x",
                100,
                red,
                green,
                blue
            )
        )
    }

    private fun changeTextColor(textColorType: Int) {
        if (textColorType != ThemeConstants.TYPE_NONE) {
            val colorValue = when (textColorType) {

                ThemeConstants.TYPE_PRIMARY -> {
                    ContextCompat.getColor(context, context.getAttrColor(R.attr.buttonTextColor))

                }
                ThemeConstants.TYPE_HEADING -> {
                    ContextCompat.getColor(context, context.getAttrColor(R.attr.headingTextColor))
                }
                ThemeConstants.TYPE_BODY -> {
                    ContextCompat.getColor(context, context.getAttrColor(R.attr.bodyTextColor))
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

    fun setSecondaryBtnDisabled(enabled: Boolean) {
        if (enabled) {
            changeBtnBackground(ThemeConstants.TYPE_SECONDARY)
            changeTextColor(ThemeConstants.TYPE_SECONDARY)

        } else {
            ViewCompat.setBackgroundTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            )
            setTextColor(ContextCompat.getColor(context, R.color.progress_bg))
            strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.progress_bg))
            iconTint =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.progress_bg))
        }
    }

    fun setBtnDisabled(enabled: Boolean, typeSecondary: Boolean = false) {

        var buttonData = Pair(ThemeConstants.TYPE_PRIMARY, ThemeConstants.TYPE_PRIMARY)
        if (mAttrs != null && mDefStyle != null) {
            val themeAttrs = context.obtainStyledAttributes(
                mAttrs, R.styleable.LMSMaterialButton,
                mDefStyle!!, 0
            )

            val backgroundType = themeAttrs.getInt(
                R.styleable.LMSMaterialButton_buttonBackground,
                ThemeConstants.TYPE_PRIMARY
            )

            val textType = themeAttrs.getInt(
                R.styleable.LMSMaterialButton_btnTextColor,
                ThemeConstants.TYPE_PRIMARY
            )
            buttonData = Pair(backgroundType, textType)



            themeAttrs.recycle()
        }

        if (Build.VERSION.SDK_INT < 24) {
            if (!enabled) {
                ViewCompat.setBackgroundTintList(
                    this,
                    /*if (typeSecondary) ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.offWhite_f6f6f6)
                    ) else*/ ColorStateList.valueOf(getDisabledColor())
                )
                /*if (typeSecondary) {
                    setTextColor(getDisabledColor())
                }*/

            } else {
                changeBtnBackground(buttonData.first)
                changeTextColor(buttonData.second)
//                if (mAttrs != null && mDefStyle != null) {
//                    val themeAttrs = context.obtainStyledAttributes(
//                        mAttrs, R.styleable.LMSMaterialButton,
//                        mDefStyle!!, 0
//                    )
//
//                    val backgroundType = themeAttrs.getInt(
//                        R.styleable.LMSMaterialButton_buttonBackground,
//                        ThemeConstants.TYPE_PRIMARY
//                    )
//
//                    val textType = themeAttrs.getInt(
//                        R.styleable.LMSMaterialButton_btnTextColor,
//                        ThemeConstants.TYPE_PRIMARY
//                    )
//
//
//                    themeAttrs.recycle()
//                } else {
//                    ViewCompat.setBackgroundTintList(
//                        this,
//                        ColorStateList.valueOf(ThemeUtils.getAppColor(context))
//                    )
//
//                }
            }
        }
    }

}