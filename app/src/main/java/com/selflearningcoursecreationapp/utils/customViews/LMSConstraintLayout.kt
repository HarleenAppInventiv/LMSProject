package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.selflearningcoursecreationapp.R

class LMSConstraintLayout : ConstraintLayout {
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
            attrs, R.styleable.LMSConstraintLayout,
            defStyle!!, 0
        )

        val changeBgType =
            themeAttrs.getInt(
                R.styleable.LMSConstraintLayout_viewBackground,
                ThemeConstants.TYPE_NONE
            )
        when (changeBgType) {
            ThemeConstants.TYPE_TINT -> {

                backgroundTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
            }
            ThemeConstants.TYPE_BACKGROUND -> {
                setBackgroundColor(ThemeUtils.getAppColor(context))
            }
            ThemeConstants.TYPE_PRIMARY -> {
                setBackgroundColor(ThemeUtils.getPrimaryBgColor(context))

            }
            ThemeConstants.TYPE_SECONDARY -> {
                setBackgroundColor(ThemeUtils.getSecondaryBgColor(context))

            }
        }
        themeAttrs.recycle()
    }


}