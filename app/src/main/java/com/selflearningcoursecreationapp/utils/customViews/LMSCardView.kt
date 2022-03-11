package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.selflearningcoursecreationapp.R

class LMSCardView : MaterialCardView {
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
            attrs, R.styleable.LMSCardView,
            defStyle!!, 0
        )

        val changeBgType =
            themeAttrs.getInt(
                R.styleable.LMSCardView_cardBackground,
                ThemeConstants.TYPE_NONE
            )

        if (changeBgType>=0) {

            val color = when (changeBgType) {
                ThemeConstants.TYPE_TINT -> {

                    ThemeUtils.getAppColor(context)
                }
                ThemeConstants.TYPE_BACKGROUND -> {

                    ThemeUtils.getAppColor(context)
                }

                ThemeConstants.TYPE_SECONDARY -> {
                    ThemeUtils.getSecondaryBgColor(context)

                }
                else -> {
                    ThemeUtils.getPrimaryBgColor(context)

                }
            }

            setCardBackgroundColor( ColorStateList.valueOf(color))
        }

        themeAttrs.recycle()
    }


}